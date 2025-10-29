package itmo.soa.heroes.service;

import itmo.soa.heroes.client.HumanBeingClient;
import itmo.soa.heroes.dto.HumanBeingRequest;
import itmo.soa.heroes.dto.HumanBeingResponse;
import itmo.soa.heroes.model.Car;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Сервис для работы с героями
 * Вызывает human-beings-service через Feign + Ribbon
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HeroesService {

    private final HumanBeingClient humanBeingClient;

    /**
     * Получить всех героев
     */
    public List<HumanBeingResponse> getAllHeroes() {
        log.info("Fetching all heroes");
        Map<String, Object> response = humanBeingClient.getAllHumanBeings(null, 0, 1000);
        return (List<HumanBeingResponse>) response.get("content");
    }

    /**
     * Удалить всех героев команды без зубочистки
     */
    public void removeWithoutToothpick(Long teamId) {
        log.info("Removing heroes without toothpick from team {}", teamId);
        
        Map<String, Object> response = humanBeingClient.getAllHumanBeings(teamId, 0, 1000);
        List<Map<String, Object>> heroes = (List<Map<String, Object>>) response.get("content");
        
        for (Map<String, Object> hero : heroes) {
            Boolean hasToothpick = (Boolean) hero.get("hasToothpick");
            if (hasToothpick == null || !hasToothpick) {
                Long id = ((Number) hero.get("id")).longValue();
                log.info("Deleting hero {} without toothpick", id);
                humanBeingClient.deleteHumanBeing(id);
            }
        }
    }

    /**
     * Добавить машину всем героям команды без машины
     */
    public List<HumanBeingResponse> addCarToTeam(Long teamId) {
        log.info("Adding car to team {}", teamId);
        
        Map<String, Object> response = humanBeingClient.getAllHumanBeings(teamId, 0, 1000);
        List<Map<String, Object>> heroes = (List<Map<String, Object>>) response.get("content");
        List<HumanBeingResponse> updated = new ArrayList<>();
        
        for (Map<String, Object> heroMap : heroes) {
            Object carObj = heroMap.get("car");
            
            // Если у героя нет машины, добавляем
            if (carObj == null) {
                Long id = ((Number) heroMap.get("id")).longValue();
                log.info("Adding car to hero {}", id);
                
                // Формируем запрос на обновление
                HumanBeingRequest request = new HumanBeingRequest();
                request.setName((String) heroMap.get("name"));
                request.setCoordinates(mapToCoordinates(heroMap.get("coordinates")));
                request.setRealHero((Boolean) heroMap.get("realHero"));
                request.setHasToothpick((Boolean) heroMap.get("hasToothpick"));
                request.setImpactSpeed(heroMap.get("impactSpeed") != null ? 
                        ((Number) heroMap.get("impactSpeed")).intValue() : null);
                request.setWeaponType(heroMap.get("weaponType") != null ?
                        itmo.soa.heroes.model.WeaponType.valueOf((String) heroMap.get("weaponType")) : null);
                request.setMood(heroMap.get("mood") != null ?
                        itmo.soa.heroes.model.Mood.valueOf((String) heroMap.get("mood")) : null);
                request.setCar(new Car(true));
                request.setTeamId(teamId.intValue());
                
                HumanBeingResponse updatedHero = humanBeingClient.updateHumanBeing(id, request);
                updated.add(updatedHero);
            }
        }
        
        log.info("Updated {} heroes with cars", updated.size());
        return updated;
    }
    
    private itmo.soa.heroes.model.Coordinates mapToCoordinates(Object coordsObj) {
        if (coordsObj instanceof Map) {
            Map<String, Object> coordsMap = (Map<String, Object>) coordsObj;
            Long x = coordsMap.get("x") != null ? ((Number) coordsMap.get("x")).longValue() : null;
            Integer y = coordsMap.get("y") != null ? ((Number) coordsMap.get("y")).intValue() : null;
            return new itmo.soa.heroes.model.Coordinates(x, y);
        }
        return null;
    }
}
