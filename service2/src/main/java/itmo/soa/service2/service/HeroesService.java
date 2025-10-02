package itmo.soa.service2.service;

import itmo.soa.service1.model.HumanBeing;
import itmo.soa.service2.external.HumanBeingClient;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;

public class HeroesService {
    @Inject
    private HumanBeingClient client;

    public Response getAllHeroes() {
        List<HumanBeing> heroes = client.getAllHeroes();
        return Response.ok(heroes).build();
    }

    public Response removeWithoutToothpick(long teamId) {
        client.removeWithoutToothpick(teamId);
        return Response.noContent().build();
    }

    public Response addCarToTeam(long teamId) {
        List<HumanBeing> heroes = client.addCarToTeam(teamId);
        return Response.ok(heroes).build();
    }
}
