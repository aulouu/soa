package itmo.soa.service2.external;

import itmo.soa.service2.dto.HumanBeingRequest;
import itmo.soa.service2.dto.HumanBeingResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
public class HumanBeingClient {

  private static final String BASE_URL =
    "https://localhost:8443/api/human-beings";

  public List<HumanBeingResponse> getAllHumanBeings() {
    List<HumanBeingResponse> result = new ArrayList<>();
    int page = 0;
    int size = 100;
    boolean hasNext = true;

    try {
      Client client = ClientBuilder.newClient();
      while (hasNext) {
        Response response = client
          .target(BASE_URL + "?page=" + page + "&size=" + size)
          .request(MediaType.APPLICATION_JSON)
          .get();

        if (response.getStatus() != 200) {
          throw new WebApplicationException(response);
        }

        Map<String, Object> pageBody = response.readEntity(
          new GenericType<Map<String, Object>>() {}
        );
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> content = (List<
            Map<String, Object>
          >) pageBody.get("content");
        if (content == null) {
          break;
        }

        for (Map<String, Object> item : content) {
          HumanBeingResponse hb = mapToHumanBeingResponse(item);
          result.add(hb);
        }

        Boolean last = (Boolean) pageBody.get("last");
        hasNext = last != null ? !last : false;
        page++;
      }
      client.close();
    } catch (ProcessingException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  public void deleteHumanBeingById(long id) {
    try {
      Client client = ClientBuilder.newClient();
      Response response = client.target(BASE_URL + "/" + id).request().delete();
      if (response.getStatus() >= 400) {
        throw new WebApplicationException(response);
      }
      client.close();
    } catch (ProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public HumanBeingResponse updateHumanBeing(long id, HumanBeingRequest body) {
    try {
      Client client = ClientBuilder.newClient();
      Response response = client
        .target(BASE_URL + "/" + id)
        .request(MediaType.APPLICATION_JSON)
        .put(Entity.entity(body, MediaType.APPLICATION_JSON));
      if (response.getStatus() != 200) {
        throw new WebApplicationException(response);
      }
      HumanBeingResponse updated = response.readEntity(
        HumanBeingResponse.class
      );
      client.close();
      return updated;
    } catch (ProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private static HumanBeingResponse mapToHumanBeingResponse(
    Map<String, Object> item
  ) {
    HumanBeingResponse hb = new HumanBeingResponse();
    Object id = item.get("id");
    if (id instanceof Number) hb.setId(((Number) id).longValue());
    hb.setName((String) item.get("name"));
    // Shallow mapping: we only need fields used by filters/updates below
    Object hasToothpick = item.get("hasToothpick");
    if (hasToothpick instanceof Boolean) hb.setHasToothpick(
      (Boolean) hasToothpick
    );
    Object impactSpeed = item.get("impactSpeed");
    if (impactSpeed instanceof Number) hb.setImpactSpeed(
      ((Number) impactSpeed).intValue()
    );
    Object car = item.get("car");
    if (car != null) {
      // presence means car not null; actual value not required for our operations
      hb.setCar(new itmo.soa.service2.model.Car(true));
    }
    return hb;
  }
}
