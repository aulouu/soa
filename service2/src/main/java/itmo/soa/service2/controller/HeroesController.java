package itmo.soa.service2.controller;

import itmo.soa.service2.service.HeroesService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/heroes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
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

    // Явная обработка OPTIONS запросов для CORS
    @OPTIONS
    @Path("{path:.*}")
    public Response handleOptions() {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, X-Requested-With")
                .header("Access-Control-Max-Age", "3600")
                .build();
    }
}
