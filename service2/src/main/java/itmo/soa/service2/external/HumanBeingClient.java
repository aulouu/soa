package itmo.soa.service2.external;

import itmo.soa.service2.dto.HumanBeingRequest;
import itmo.soa.service2.dto.HumanBeingResponse;
import itmo.soa.service2.model.Coordinates;
import itmo.soa.service2.model.Mood;
import itmo.soa.service2.model.WeaponType;

import javax.ejb.Stateless;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Stateless
public class HumanBeingClient {

    private static final String BASE_URL =
            "https://localhost:37449/api/human-beings";

    private static HumanBeingResponse mapToHumanBeingResponse(
            Map<String, Object> item
    ) {
        HumanBeingResponse hb = new HumanBeingResponse();
        Object id = item.get("id");
        if (id instanceof Number) hb.setId(((Number) id).longValue());

        hb.setName((String) item.get("name"));

        Object coordsObj = item.get("coordinates");
        if (coordsObj instanceof Map) {
            Map coordsMap = (Map) coordsObj;
            Coordinates coordinates = new Coordinates();
            Object x = coordsMap.get("x");
            if (x instanceof Number) coordinates.setX(((Number) x).longValue());
            Object y = coordsMap.get("y");
            if (y instanceof Number) coordinates.setY(((Number) y).intValue());
            hb.setCoordinates(coordinates);
        }

        Object realHero = item.get("realHero");
        if (realHero instanceof Boolean) hb.setRealHero((Boolean) realHero);

        Object hasToothpick = item.get("hasToothpick");
        if (hasToothpick instanceof Boolean) hb.setHasToothpick(
                (Boolean) hasToothpick
        );

        Object impactSpeed = item.get("impactSpeed");
        if (impactSpeed instanceof Number) hb.setImpactSpeed(
                ((Number) impactSpeed).intValue()
        );

        Object weaponType = item.get("weaponType");
        if (weaponType instanceof String) {
            hb.setWeaponType(WeaponType.valueOf((String) weaponType));
        }

        Object mood = item.get("mood");
        if (mood instanceof String) {
            hb.setMood(Mood.valueOf((String) mood));
        }

        Object carObj = item.get("car");
        if (carObj instanceof Map) {
            Map carMap = (Map) carObj;
            Object cool = carMap.get("cool");
            if (cool instanceof Boolean) {
                hb.setCar(new itmo.soa.service2.model.Car((Boolean) cool));
            }
        }

        return hb;
    }

    public List<HumanBeingResponse> getAllHumanBeings(Long teamId) {
        List<HumanBeingResponse> result = new ArrayList<>();
        int page = 0;
        int size = 100;
        boolean hasNext = true;

        try {
            Client client = ClientBuilder.newClient();
            while (hasNext) {
                String url = BASE_URL + "?page=" + page + "&size=" + size;
                if (teamId != null) {
                    url += "&teamId=" + teamId;
                }

                // ЛОГИРУЕМ ЗАПРОС
                System.out.println("[HumanBeingClient] Sending GET request to: " + url);

                Response response = client
                        .target(url)
                        .request(MediaType.APPLICATION_JSON)
                        .get();

                // ЛОГИРУЕМ СТАТУС ОТВЕТА
                System.out.println(
                        "[HumanBeingClient] Received GET response with status: " +
                                response.getStatus()
                );

                if (response.getStatus() != 200) {
                    throw new WebApplicationException(response);
                }

                Map<String, Object> pageBody = response.readEntity(
                        new GenericType<Map<String, Object>>() {
                        }
                );
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> content = (List<
                        Map<String, Object>
                        >) pageBody.get("content");
                if (content == null || content.isEmpty()) {
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

    public HumanBeingResponse updateHumanBeing(long id, HumanBeingRequest body) {
        try {
            Client client = ClientBuilder.newClient();
            String url = BASE_URL + "/" + id;
            // ЛОГИРУЕМ ЗАПРОС
            System.out.println("[HumanBeingClient] Sending PUT request to: " + url);

            Response response = client
                    .target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(body, MediaType.APPLICATION_JSON));

            // ЛОГИРУЕМ СТАТУС ОТВЕТА
            System.out.println(
                    "[HumanBeingClient] Received PUT response with status: " +
                            response.getStatus()
            );

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

    // Остальные методы (delete, mapToHumanBeingResponse) оставляем без изменений из предыдущего ответа
    public void deleteHumanBeingById(long id) {
        try {
            Client client = ClientBuilder.newClient();
            String url = BASE_URL + "/" + id;
            System.out.println(
                    "[HumanBeingClient] Sending DELETE request to: " + url
            );

            Response response = client.target(url).request().delete();
            System.out.println(
                    "[HumanBeingClient] Received DELETE response with status: " +
                            response.getStatus()
            );

            if (response.getStatus() >= 400) {
                throw new WebApplicationException(response);
            }
            client.close();
        } catch (ProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
