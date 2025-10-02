package itmo.soa.service2.external;

import itmo.soa.service1.model.HumanBeing;

import javax.ejb.Stateless;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Stateless
public class HumanBeingClient {
    private static final String BASE_URL = "http://localhost:8080/api/human-beings";

    public List<HumanBeing> getAllHeroes() {
        try {
            Client client = ClientBuilder.newClient();
            Response response = client.target(BASE_URL).request(MediaType.APPLICATION_JSON).get();

            if (response.getStatus() != 200) {
                throw new WebApplicationException(response);
            }

            List<HumanBeing> heroes = response.readEntity(new GenericType<List<HumanBeing>>() {});
            client.close();
            return heroes;
        } catch (ProcessingException e) {
            return null;
        }
    }

    public void removeWithoutToothpick(long teamId) {
        try {
            Client client = ClientBuilder.newClient();
            Response response = client.target(BASE_URL + "/team/" + teamId + "/remove-without-toothpick")
                    .request()
                    .delete();

            if (response.getStatus() != 204) {
                throw new WebApplicationException(response);
            }
            client.close();
        } catch (ProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<HumanBeing> addCarToTeam(long teamId) {
        try {
            Client client = ClientBuilder.newClient();
            Response response = client.target(BASE_URL + "/team/" + teamId + "/car/add")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(""));

            if (response.getStatus() != 200) {
                throw new WebApplicationException(response);
            }

            List<HumanBeing> heroes = response.readEntity(new GenericType<List<HumanBeing>>() {});
            client.close();
            return heroes;
        } catch (ProcessingException e) {
            return null;
        }
    }
}
