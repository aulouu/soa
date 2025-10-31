package itmo.soa.service1.rest;

import itmo.soa.service1.ejb.HumanBeingServiceLocal;
import itmo.soa.service1.model.HumanBeing;
import itmo.soa.service1.model.Mood;
import itmo.soa.service1.model.WeaponType;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * REST контроллер, который делегирует вызовы к EJB
 * Сохраняет совместимость с предыдущим API
 */
@Path("/human-beings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HumanBeingResource {

  private static final Logger logger = Logger.getLogger(
    HumanBeingResource.class.getName()
  );

  private HumanBeingServiceLocal humanBeingService;

  @PostConstruct
  public void init() {
    try {
      Context context = new InitialContext();

      // Пробуем разные JNDI имена для поиска EJB
      String[] jndiNames = {
        "java:global/service1-web/service1-ejb/HumanBeingServiceBean!itmo.soa.service1.ejb.HumanBeingServiceLocal",
        "java:global/service1-ejb-1.0.0/HumanBeingServiceBean!itmo.soa.service1.ejb.HumanBeingServiceLocal",
        "java:app/service1-ejb/HumanBeingServiceBean!itmo.soa.service1.ejb.HumanBeingServiceLocal",
        "java:module/HumanBeingServiceBean!itmo.soa.service1.ejb.HumanBeingServiceLocal",
      };

      for (String jndiName : jndiNames) {
        try {
          humanBeingService = (HumanBeingServiceLocal) context.lookup(jndiName);
          logger.info("EJB successfully found via JNDI: " + jndiName);
          break;
        } catch (NamingException e) {
          logger.warning(
            "Failed JNDI lookup: " + jndiName + " - " + e.getMessage()
          );
        }
      }

      if (humanBeingService == null) {
        logger.severe("All JNDI lookups failed, using stub implementation");
        humanBeingService = createStub();
      }
    } catch (NamingException e) {
      logger.log(Level.SEVERE, "JNDI InitialContext failed", e);
      humanBeingService = createStub();
    }
  }

  private HumanBeingServiceLocal createStub() {
    return new HumanBeingServiceLocal() {
      @Override
      public Map<String, Object> getAllHumanBeings(
        Long id,
        String name,
        Boolean hasToothpick,
        Boolean realHero,
        Integer impactSpeed,
        WeaponType weaponType,
        Mood mood,
        Integer teamId,
        LocalDateTime createdAfter,
        int page,
        int size
      ) {
        return Map.of(
          "content",
          List.of(),
          "totalElements",
          0,
          "totalPages",
          0,
          "size",
          size,
          "number",
          page,
          "first",
          true,
          "last",
          true,
          "message",
          "EJB stub - real EJB not available"
        );
      }

      @Override
      public HumanBeing getHumanBeingById(Long id) {
        throw new RuntimeException(
          "EJB stub - getHumanBeingById not implemented"
        );
      }

      @Override
      public HumanBeing createHumanBeing(HumanBeing humanBeing) {
        throw new RuntimeException(
          "EJB stub - createHumanBeing not implemented"
        );
      }

      @Override
      public HumanBeing updateHumanBeing(Long id, HumanBeing humanBeing) {
        throw new RuntimeException(
          "EJB stub - updateHumanBeing not implemented"
        );
      }

      @Override
      public void deleteHumanBeing(Long id) {
        throw new RuntimeException(
          "EJB stub - deleteHumanBeing not implemented"
        );
      }

      @Override
      public long countByMood(Mood mood) {
        return 0;
      }

      @Override
      public List<Integer> getUniqueImpactSpeeds() {
        return List.of();
      }

      @Override
      public long countByNameStartsWith(String prefix) {
        return 0;
      }
    };
  }

  public HumanBeingResource() {
    logger.info("HumanBeingResource INSTANCE CREATED.");
  }

  @GET
  @Path("/health")
  public Response health() {
    return Response.ok(
      "{\"status\":\"ok\",\"service\":\"human-beings\"}"
    ).build();
  }

  @GET
  @Path("/db-test")
  public Response dbTest() {
    try {
      // Direct JDBC test without Hibernate
      String url = "jdbc:postgresql://localhost:5432/soa_lab3?connectTimeout=5";
      java.sql.Connection conn = java.sql.DriverManager.getConnection(
        url,
        "postgres",
        "postgres"
      );
      java.sql.Statement stmt = conn.createStatement();
      java.sql.ResultSet rs = stmt.executeQuery(
        "SELECT COUNT(*) as cnt FROM information_schema.tables WHERE table_schema='public'"
      );
      int count = 0;
      if (rs.next()) {
        count = rs.getInt("cnt");
      }
      rs.close();
      stmt.close();
      conn.close();
      return Response.ok(
        "{\"status\":\"ok\",\"db\":\"connected\",\"tables\":" + count + "}"
      ).build();
    } catch (Exception e) {
      return Response.status(500)
        .entity("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}")
        .build();
    }
  }

  @GET
  public Response getAllHumanBeings(
    @QueryParam("id") Long id,
    @QueryParam("name") String name,
    @QueryParam("hasToothpick") Boolean hasToothpick,
    @QueryParam("realHero") Boolean realHero,
    @QueryParam("impactSpeed") Integer impactSpeed,
    @QueryParam("weaponType") String weaponTypeStr,
    @QueryParam("mood") String moodStr,
    @QueryParam("teamId") Integer teamId,
    @QueryParam("createdAfter") String createdAfterStr,
    @QueryParam("page") @DefaultValue("0") int page,
    @QueryParam("size") @DefaultValue("10") int size
  ) {
    try {
      logger.info("REST endpoint /human-beings (GET all) triggered.");

      WeaponType weaponType = weaponTypeStr != null
        ? WeaponType.valueOf(weaponTypeStr.toUpperCase())
        : null;
      Mood mood = moodStr != null ? Mood.valueOf(moodStr.toUpperCase()) : null;
      LocalDateTime createdAfter = null;
      if (createdAfterStr != null && !createdAfterStr.isEmpty()) {
        createdAfter = LocalDateTime.parse(
          createdAfterStr,
          DateTimeFormatter.ISO_DATE_TIME
        );
      }

      Map<String, Object> result = humanBeingService.getAllHumanBeings(
        id,
        name,
        hasToothpick,
        realHero,
        impactSpeed,
        weaponType,
        mood,
        teamId,
        createdAfter,
        page,
        size
      );

      return Response.ok(result).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST)
        .entity(Map.of("error", e.getMessage()))
        .build();
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error in getAllHumanBeings REST endpoint", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(Map.of("error", e.getMessage()))
        .build();
    }
  }

  @GET
  @Path("/{id}")
  public Response getHumanBeingById(@PathParam("id") Long id) {
    try {
      HumanBeing humanBeing = humanBeingService.getHumanBeingById(id);
      return Response.ok(humanBeing).build();
    } catch (RuntimeException e) {
      return Response.status(Response.Status.NOT_FOUND)
        .entity(Map.of("error", e.getMessage()))
        .build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(Map.of("error", e.getMessage()))
        .build();
    }
  }

  @POST
  public Response createHumanBeing(HumanBeing humanBeing) {
    try {
      HumanBeing created = humanBeingService.createHumanBeing(humanBeing);
      return Response.status(Response.Status.CREATED).entity(created).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST)
        .entity(Map.of("error", e.getMessage()))
        .build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(Map.of("error", e.getMessage()))
        .build();
    }
  }

  @PUT
  @Path("/{id}")
  public Response updateHumanBeing(
    @PathParam("id") Long id,
    HumanBeing humanBeing
  ) {
    try {
      HumanBeing updated = humanBeingService.updateHumanBeing(id, humanBeing);
      return Response.ok(updated).build();
    } catch (RuntimeException e) {
      if (e.getMessage().contains("not found")) {
        return Response.status(Response.Status.NOT_FOUND)
          .entity(Map.of("error", e.getMessage()))
          .build();
      }
      return Response.status(Response.Status.BAD_REQUEST)
        .entity(Map.of("error", e.getMessage()))
        .build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(Map.of("error", e.getMessage()))
        .build();
    }
  }

  @DELETE
  @Path("/{id}")
  public Response deleteHumanBeing(@PathParam("id") Long id) {
    try {
      humanBeingService.deleteHumanBeing(id);
      return Response.noContent().build();
    } catch (RuntimeException e) {
      return Response.status(Response.Status.NOT_FOUND)
        .entity(Map.of("error", e.getMessage()))
        .build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(Map.of("error", e.getMessage()))
        .build();
    }
  }

  // Statistics endpoints

  @GET
  @Path("/statistics/mood-count/{moodValue}")
  public Response countByMood(@PathParam("moodValue") int moodValue) {
    try {
      // Convert moodValue to Mood enum
      // 0=SADNESS, 1=SORROW, 2=APATHY, 3=FRENZY
      Mood mood = convertMoodValue(moodValue);
      long count = humanBeingService.countByMood(mood);
      return Response.ok(Map.of("count", count, "mood", mood.toString())).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST)
        .entity(Map.of("error", e.getMessage()))
        .build();
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error in countByMood endpoint", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(Map.of("error", e.getMessage()))
        .build();
    }
  }

  @GET
  @Path("/statistics/name/starts-with/{prefix}")
  public Response countByNameStartsWith(@PathParam("prefix") String prefix) {
    try {
      long count = humanBeingService.countByNameStartsWith(prefix);
      return Response.ok(Map.of("count", count, "prefix", prefix)).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST)
        .entity(Map.of("error", e.getMessage()))
        .build();
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error in countByNameStartsWith endpoint", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(Map.of("error", e.getMessage()))
        .build();
    }
  }

  @GET
  @Path("/statistics/impact-speeds")
  public Response getUniqueImpactSpeeds() {
    try {
      List<Integer> speeds = humanBeingService.getUniqueImpactSpeeds();
      return Response.ok(speeds).build();
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error in getUniqueImpactSpeeds endpoint", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(Map.of("error", e.getMessage()))
        .build();
    }
  }

  private Mood convertMoodValue(int value) {
    switch (value) {
      case 0:
        return Mood.SADNESS;
      case 1:
        return Mood.SORROW;
      case 2:
        return Mood.APATHY;
      case 3:
        return Mood.FRENZY;
      default:
        throw new IllegalArgumentException(
          "Invalid mood value: " + value + ". Must be 0-3"
        );
    }
  }
}
