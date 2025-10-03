package itmo.soa.service1.repo;

import itmo.soa.service1.model.HumanBeing;
import itmo.soa.service1.model.Mood;
import itmo.soa.service1.model.WeaponType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class HumanBeingSpecification {

    public static Specification<HumanBeing> hasId(Long id) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("id"), id);
    }

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

    public static Specification<HumanBeing> impactSpeed(Integer speed) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("impactSpeed"), speed);
    }

    public static Specification<HumanBeing> weaponType(WeaponType type) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("weaponType"), type);
    }

    public static Specification<HumanBeing> mood(Mood mood) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("mood"), mood);
    }

    public static Specification<HumanBeing> teamIdIs(Integer teamId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("teamId"), teamId);
    }

    // Фильтр по дате (например, >=)
    public static Specification<HumanBeing> createdAfter(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("creationDate"), date);
    }
}
