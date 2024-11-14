package com.aufsell.Lab1.service;

import com.aufsell.Lab1.dto.*;
import com.aufsell.Lab1.exception.ResourceNotFoundException;
import com.aufsell.Lab1.model.*;
import com.aufsell.Lab1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private CoordinatesRepository coordinatesRepository;
    @Autowired
    private FuelTypeRepository fuelTypeRepository;
    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;
    @Autowired
    private UserRepository userRepository;

    private final SimpMessagingTemplate template;
    @Autowired
    private AuditLogService auditLogService;

    public VehicleService(SimpMessagingTemplate template) {
        this.template = template;
    }

    public List<VehicleDTO> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CoordinatesDTO> getAllCoordinates() {
        return coordinatesRepository.findAll().stream()
                .map(this::convertCoordinatesToCoordinatesDTO)
                .collect(Collectors.toList());
    }

    public VehicleDTO createVehicle(VehicleDTO vehicleDTO) {
        Vehicle vehicle = convertToEntity(vehicleDTO);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        VehicleUpdateMessage message = new VehicleUpdateMessage("create",vehicle.getId(), convertToDTO(savedVehicle));
        template.convertAndSend("/topic/vehicles", message);
        auditLogService.logAction("Create", vehicle.getId(), vehicle.getName(), vehicle.getUser().getId(),vehicle.getUser().getUsername(),"Vehicle created");
        return convertToDTO(savedVehicle);
    }

    private boolean isAdmin() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    public void deleteVehicle(Long id, Long userId) {
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(id);

        if (vehicleOptional.isPresent()) {
            Vehicle vehicle = vehicleOptional.get();
            Long ownerId = vehicle.getUser().getId();

            if (Objects.equals(ownerId, userId) || isAdmin()) {
                vehicleRepository.delete(vehicle);
                VehicleUpdateMessage message = new VehicleUpdateMessage("delete",id, null);
                template.convertAndSend("/topic/vehicles", message);
                auditLogService.logAction("Delete", vehicle.getId(), vehicle.getName(), vehicle.getUser().getId(),vehicle.getUser().getUsername(),"Vehicle deleted");
            } else {
                throw new ResourceNotFoundException("You are not owner of this vehicle");
            }
        } else {
            throw new ResourceNotFoundException("Vehicle not found");
        }
    }

    public void updateVehicle(Long id, VehicleDTO vehicleDTO) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(id);
        if (optionalVehicle.isPresent()) {
            Vehicle vehicle = optionalVehicle.get();
            vehicle.setName(vehicleDTO.getName());
            vehicle.setCoordinates(convertCoordinatesDTOToCoordinates(vehicleDTO.getCoordinates()));
            vehicle.setVehicleType(convertVehicleTypeDTOToVehicleType(vehicleDTO.getVehicleType()));
            vehicle.setEnginePower(vehicleDTO.getEnginePower());
            vehicle.setNumberOfWheels(vehicleDTO.getNumberOfWheels());
            vehicle.setCapacity(vehicleDTO.getCapacity());
            vehicle.setDistanceTravelled(vehicleDTO.getDistanceTravelled());
            vehicle.setFuelConsumption(vehicleDTO.getFuelConsumption());
            vehicle.setFuelType(convertFuelTypeDTOToFuelType(vehicleDTO.getFuelType()));
            Vehicle updatedVehicle = vehicleRepository.save(vehicle);
            convertToDTO(updatedVehicle);
            VehicleUpdateMessage message = new VehicleUpdateMessage("update",updatedVehicle.getId(), convertToDTO(updatedVehicle));
            template.convertAndSend("/topic/vehicles", message);
            auditLogService.logAction("Update", vehicle.getId(), vehicle.getName(), vehicle.getUser().getId(),vehicle.getUser().getUsername(),"Vehicle updated");
        } else{
            throw new ResourceNotFoundException("Vehicle not found");
        }
    }

    public VehicleDTO getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle ID not found")); // Возвращаем null, если объект не найден
    }

    // Вспомогательные методы для конвертации
    public VehicleDTO convertToDTO(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setVehicleId(vehicle.getId());
        vehicleDTO.setName(vehicle.getName());

        // Проверяем наличие координат перед конвертацией
        if (vehicle.getCoordinates() != null) {
            vehicleDTO.setCoordinates(convertCoordinatesToCoordinatesDTO(vehicle.getCoordinates()));
        }
        vehicleDTO.setCreationDate(vehicle.getCreationDate());
        vehicleDTO.setEnginePower(vehicle.getEnginePower());
        vehicleDTO.setNumberOfWheels(vehicle.getNumberOfWheels());
        vehicleDTO.setCapacity(vehicle.getCapacity());
        vehicleDTO.setDistanceTravelled(vehicle.getDistanceTravelled());
        vehicleDTO.setFuelConsumption(vehicle.getFuelConsumption());
        vehicleDTO.setVehicleType(convertVehicleTypeToVehicleTypeDTO(vehicle.getVehicleType()));
        vehicleDTO.setFuelType(convertFuelTypeToFuelTypeDTO(vehicle.getFuelType()));
        vehicleDTO.setUserID(vehicle.getUser().getId());
        return vehicleDTO;
    }

    private Vehicle convertToEntity(VehicleDTO vehicleDTO) {
        if (vehicleDTO == null) {
            return null;
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setName(vehicleDTO.getName());
        vehicle.setCoordinates(convertCoordinatesDTOToCoordinates(vehicleDTO.getCoordinates()));
        vehicle.setCreationDate(vehicleDTO.getCreationDate());
        vehicle.setEnginePower(vehicleDTO.getEnginePower());
        vehicle.setNumberOfWheels(vehicleDTO.getNumberOfWheels());
        vehicle.setCapacity(vehicleDTO.getCapacity());
        vehicle.setDistanceTravelled(vehicleDTO.getDistanceTravelled());
        vehicle.setFuelConsumption(vehicleDTO.getFuelConsumption());
        vehicle.setVehicleType(convertVehicleTypeDTOToVehicleType(vehicleDTO.getVehicleType()));
        vehicle.setFuelType(convertFuelTypeDTOToFuelType(vehicleDTO.getFuelType()));
        vehicle.setUser(userRepository.findById(vehicleDTO.getUserID()).orElseThrow(() -> new ResourceNotFoundException("User with given ID not found")));
        return vehicle;
    }

    // Вспомогательные методы для конвертации Coordinates
    private CoordinatesDTO convertCoordinatesToCoordinatesDTO(Coordinates coordinates) {
        if (coordinates == null) {
            return null;
        }
        CoordinatesDTO coordinatesDTO = new CoordinatesDTO();
        coordinatesDTO.setId(coordinates.getId());
        coordinatesDTO.setX(coordinates.getX());
        coordinatesDTO.setY(coordinates.getY());
        return coordinatesDTO;
    }

    private Coordinates convertCoordinatesDTOToCoordinates(CoordinatesDTO coordinatesDTO) {
        if (coordinatesDTO == null) {
            return null;
        }
        if (coordinatesDTO.getId() != null) {
            return coordinatesRepository.findById(coordinatesDTO.getId())
                    .orElseGet(() -> {
                        Coordinates newCoordinates = new Coordinates();
                        newCoordinates.setX(coordinatesDTO.getX());
                        newCoordinates.setY(coordinatesDTO.getY());
                        return coordinatesRepository.save(newCoordinates);
                    });
        }

        // Если ID не указан, создаем новые координаты и сохраняем их
        Coordinates coordinates = new Coordinates();
        coordinates.setX(coordinatesDTO.getX());
        coordinates.setY(coordinatesDTO.getY());
        coordinatesRepository.save(coordinates);
        return coordinates;
    }

    private FuelTypeDTO convertFuelTypeToFuelTypeDTO(FuelType fuelType) {
        if (fuelType == null) {
            return null;
        }
        FuelTypeDTO fuelTypeDTO = new FuelTypeDTO();
        fuelTypeDTO.setId(fuelType.getId());
        fuelTypeDTO.setName(fuelType.getName());
        return fuelTypeDTO;
    }

    private FuelType convertFuelTypeDTOToFuelType(FuelTypeDTO fuelTypeDTO) {
        if (fuelTypeDTO == null || fuelTypeDTO.getId() == null) {
            throw new ResourceNotFoundException("FuelType ID must be provided");
        }
        return fuelTypeRepository.findById(fuelTypeDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("FuelType with given ID not found"));
    }


    private VehicleTypeDTO convertVehicleTypeToVehicleTypeDTO(VehicleType vehicleType) {
        if (vehicleType == null) {
            return null;
        }
        VehicleTypeDTO vehicleTypeDTO = new VehicleTypeDTO();
        vehicleTypeDTO.setId(vehicleType.getId());
        vehicleTypeDTO.setName(vehicleType.getName());
        return vehicleTypeDTO;
    }

    private VehicleType convertVehicleTypeDTOToVehicleType(VehicleTypeDTO vehicleTypeDTO) {
        if (vehicleTypeDTO == null || vehicleTypeDTO.getId() == null) {
            throw new ResourceNotFoundException("VehicleType ID must be provided");
        }
        return vehicleTypeRepository.findById(vehicleTypeDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("VehicleType with given ID not found"));
    }

    public Map<Long, List<VehicleDTO>> groupVehiclesByEnginePower() {
        return vehicleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.groupingBy(VehicleDTO::getEnginePower));
    }
    public Long countVehiclesByFuelConsumptionLessThan(Long value){
        return vehicleRepository.countByFuelConsumptionLessThan(value);
    }

    public Page<VehicleDTO> searchVehiclesByNameSubstring(String nameSubstring, int page, int size) {
        Pageable pageable = PageRequest.of(page, size); // Создаем объект пагинации
        Page<Vehicle> vehiclePage = vehicleRepository.findByNameContaining(nameSubstring, pageable);
        return vehiclePage.map(this::convertToDTO);
    }
    public List<VehicleDTO> getVehiclesByType(Long vehicleTypeId) {
        if (!vehicleTypeRepository.existsById(vehicleTypeId)) {
            throw new ResourceNotFoundException("Vehicle type with given ID not found");
        }

        return vehicleRepository.findByVehicleTypeId(vehicleTypeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public VehicleDTO addWheels(Long vehicleId, int count) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with given ID not found"));

        if (count < 0) {
            throw new ResourceNotFoundException("Count cannot be less than zero");
        }

        // Добавляем колёса
        long newNumberOfWheels = vehicle.getNumberOfWheels() + count;
        vehicle.setNumberOfWheels(newNumberOfWheels);

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return convertToDTO(updatedVehicle);
    }
}
