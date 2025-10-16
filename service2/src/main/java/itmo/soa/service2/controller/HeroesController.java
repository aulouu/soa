package itmo.soa.service2.controller;

import itmo.soa.service2.service.HeroesService;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/heroes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll // Эта аннотация критически важна
public class HeroesController {
    @Inject
    private HeroesService heroesService;

    @GET
    public Response getAllHeroes() {
        return heroesService.getAllHeroes();
    }

    @DELETE
    @Path("/team/{teamId}/remove-without-toothpick")
    public Response removeWithoutToothpick(@PathParam("teamId") long teamId) {
        return heroesService.removeWithoutToothpick(teamId);
    }

    @POST
    @Path("/team/{teamId}/car/add")
    public Response addCarToTeam(@PathParam("teamId") long teamId) {
        return heroesService.addCarToTeam(teamId);
    }
}
