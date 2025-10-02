package itmo.soa.service1.repo;

import itmo.soa.service1.model.HumanBeing;
import org.springframework.data.jpa.domain.Specification;

public class HumanBeingSpecification {

  public static Specification<HumanBeing> nameContains(String name) {
    return (root, query, criteriaBuilder) ->
      criteriaBuilder.like(root.get("name"), "%" + name + "%");
  }

  public static Specification<HumanBeing> hasToothpick(boolean hasToothpick) {
    return (root, query, criteriaBuilder) ->
      criteriaBuilder.equal(root.get("hasToothpick"), hasToothpick);
  }

  public static Specification<HumanBeing> realHero(boolean realHero) {
    return (root, query, criteriaBuilder) ->
      criteriaBuilder.equal(root.get("realHero"), realHero);
  }

  public static Specification<HumanBeing> teamIdIs(Integer teamId) {
    return (root, query, criteriaBuilder) ->
      criteriaBuilder.equal(root.get("teamId"), teamId);
  }
}
