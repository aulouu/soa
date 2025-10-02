package itmo.soa.service2.service;

import itmo.soa.service2.dto.HumanBeingRequest;
import itmo.soa.service2.dto.HumanBeingResponse;
import itmo.soa.service2.external.HumanBeingClient;
import itmo.soa.service2.model.Car;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class HeroesService {

  @Inject
  private HumanBeingClient client;

  public Response getAllHeroes() {
    List<HumanBeingResponse> heroes = client.getAllHumanBeings();
    return Response.ok(heroes).build();
  }

  public Response removeWithoutToothpick(long teamId) {
    // teamId is not represented in service1 domain; apply globally
    List<HumanBeingResponse> heroes = client.getAllHumanBeings();
    for (HumanBeingResponse hb : heroes) {
      Boolean hasToothpick = hb.getHasToothpick();
      if (hasToothpick == null || Boolean.FALSE.equals(hasToothpick)) {
        if (hb.getId() != null) {
          client.deleteHumanBeingById(hb.getId());
        }
      }
    }
    return Response.noContent().build();
  }

  public Response addCarToTeam(long teamId) {
    // teamId is not represented in service1 domain; apply globally
    List<HumanBeingResponse> heroes = client.getAllHumanBeings();
    List<HumanBeingResponse> updated = new ArrayList<>();
    for (HumanBeingResponse hb : heroes) {
      if (hb.getCar() == null && hb.getId() != null) {
        HumanBeingRequest body = new HumanBeingRequest();
        body.setName(hb.getName());
        body.setCoordinates(hb.getCoordinates());
        body.setRealHero(hb.getRealHero());
        body.setHasToothpick(hb.getHasToothpick());
        body.setImpactSpeed(hb.getImpactSpeed());
        body.setWeaponType(hb.getWeaponType());
        body.setMood(hb.getMood());
        body.setCar(new Car(true));
        HumanBeingResponse res = client.updateHumanBeing(hb.getId(), body);
        if (Objects.nonNull(res)) {
          updated.add(res);
        }
      }
    }
    return Response.ok(updated).build();
  }
}
