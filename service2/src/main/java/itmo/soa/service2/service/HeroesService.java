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
    List<HumanBeingResponse> heroes = client.getAllHumanBeings(null);
    return Response.ok(heroes).build();
  }

  public Response removeWithoutToothpick(long teamId) {
    List<HumanBeingResponse> heroes = client.getAllHumanBeings(teamId);
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
    System.out.println(
      "[HeroesService] Starting addCarToTeam for teamId: " + teamId
    );
    List<HumanBeingResponse> heroes = client.getAllHumanBeings(teamId);
    System.out.println(
      "[HeroesService] Found " + heroes.size() + " heroes in team " + teamId
    );
    List<HumanBeingResponse> updated = new ArrayList<>();

    for (HumanBeingResponse hb : heroes) {
      System.out.println(
        "[HeroesService] Processing hero id: " +
        hb.getId() +
        ". Car object is: " +
        (hb.getCar() == null ? "null" : "present")
      );

      // ПРАВИЛЬНОЕ УСЛОВИЕ: Обновляем только тех, у кого поля car вообще нет (оно null)
      if (hb.getCar() == null && hb.getId() != null) {
        System.out.println(
          "[HeroesService] Hero id: " +
          hb.getId() +
          " has no car. Preparing update."
        );

        HumanBeingRequest body = new HumanBeingRequest();
        body.setName(hb.getName());
        body.setCoordinates(hb.getCoordinates());
        body.setRealHero(hb.getRealHero());
        body.setHasToothpick(hb.getHasToothpick());
        body.setImpactSpeed(hb.getImpactSpeed());
        body.setWeaponType(hb.getWeaponType());
        body.setMood(hb.getMood());
        body.setTeamId((int) teamId);

        // Устанавливаем новую машину
        body.setCar(new Car(true));
        System.out.println(
          "[HeroesService] Sending PUT request for hero id: " + hb.getId()
        );

        HumanBeingResponse res = client.updateHumanBeing(hb.getId(), body);
        if (Objects.nonNull(res)) {
          updated.add(res);
        }
      } else {
        System.out.println(
          "[HeroesService] Hero id: " +
          hb.getId() +
          " already has a car. Skipping."
        );
      }
    }
    System.out.println(
      "[HeroesService] Finished addCarToTeam. Updated " +
      updated.size() +
      " heroes."
    );
    return Response.ok(updated).build();
  }
}
