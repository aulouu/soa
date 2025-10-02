package itmo.soa.service1.service;

import itmo.soa.service1.dto.HumanBeingRequest;
import itmo.soa.service1.dto.HumanBeingResponse;
import itmo.soa.service1.exception.NotValidInputException;
import itmo.soa.service1.exception.ResourceNotFoundException;
import itmo.soa.service1.model.*;
import itmo.soa.service1.repo.HumanBeingRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HumanBeingService {
    private final HumanBeingRepository humanBeingRepository;
    private final ModelMapper modelMapper;

    private static void validateHumanBeing(HumanBeingRequest humanBeingRequest) throws IllegalArgumentException {
        if (humanBeingRequest.getName() == null || humanBeingRequest.getName().isEmpty()) {
            throw new NotValidInputException("Name cannot be null or empty");
        }
        Coordinates coordinates = humanBeingRequest.getCoordinates();
        if (coordinates == null) {
            throw new NotValidInputException("Coordinates cannot be null");
        }
        if (coordinates.getX() == null) {
            throw new NotValidInputException("Coordinate X cannot be null");
        }
        if (coordinates.getY() == null || coordinates.getY() > 507) {
            throw new NotValidInputException("Coordinate Y cannot be null and must be less than or equal to 507");
        }
        if (humanBeingRequest.getImpactSpeed() != null && humanBeingRequest.getImpactSpeed() < -441) {
            throw new NotValidInputException("Impact speed must be greater than or equal to -441");
        }
        if (humanBeingRequest.getWeaponType() != null && !isValidWeaponType(humanBeingRequest.getWeaponType().name())) {
            throw new NotValidInputException("Invalid weapon type");
        }
        if (humanBeingRequest.getMood() != null && !isValidMood(humanBeingRequest.getMood().name())) {
            throw new NotValidInputException("Invalid mood");
        }
        Car car = humanBeingRequest.getCar();
        if (car != null && car.getCool() == null) {
            throw new NotValidInputException("Car 'cool' property cannot be null");
        }
    }

    private static boolean isValidWeaponType(String weaponType) {
        if (weaponType == null) {
            return false;
        }
        try {
            WeaponType.valueOf(weaponType.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static boolean isValidMood(String mood) {
        if (mood == null) {
            return false;
        }
        try {
            Mood.valueOf(mood.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public Page<HumanBeingResponse> getAllHumanBeings(Pageable pageable) {
        Page<HumanBeing> humanBeings = humanBeingRepository.findAll(pageable);
        return humanBeings.map(hb -> modelMapper.map(hb, HumanBeingResponse.class));
    }

    public HumanBeingResponse getHumanBeingById(Long id) {
        HumanBeing humanBeing = humanBeingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("HumanBeing with id %d not found", id)
                ));
        return modelMapper.map(humanBeing, HumanBeingResponse.class);
    }

    public HumanBeingResponse addHumanBeing(HumanBeingRequest humanBeingRequest) {
        validateHumanBeing(humanBeingRequest);
        HumanBeing humanBeing = modelMapper.map(humanBeingRequest, HumanBeing.class);
        HumanBeing savedHumanBeing = humanBeingRepository.save(humanBeing);
        return modelMapper.map(savedHumanBeing, HumanBeingResponse.class);
    }

    public HumanBeingResponse updateHumanBeingById(Long id, HumanBeingRequest humanBeingRequest) {
        HumanBeing humanBeing = humanBeingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("HumanBeing with id %d not found", id)
                ));
        validateHumanBeing(humanBeingRequest);
        modelMapper.map(humanBeingRequest, humanBeing);
        HumanBeing updatedHumanBeing = humanBeingRepository.save(humanBeing);
        return modelMapper.map(updatedHumanBeing, HumanBeingResponse.class);
    }

    public void deleteHumanBeingById(Long id) {
        HumanBeing humanBeing = humanBeingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("HumanBeing with id %d not found", id)
                ));
        humanBeingRepository.delete(humanBeing);
    }
}
