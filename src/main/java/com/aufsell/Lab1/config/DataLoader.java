package com.aufsell.Lab1.config;
import com.aufsell.Lab1.model.*;
import com.aufsell.Lab1.repository.*;
import com.aufsell.Lab1.service.AuthenticationService;
import com.aufsell.Lab1.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final VehicleRepository vehicleRepository;
    private final CoordinatesRepository coordinatesRepository;

    public DataLoader(
            UserService userService,
            AuthenticationService authenticationService,
            UserRepository userRepository,
            FuelTypeRepository fuelTypeRepository,
            VehicleTypeRepository vehicleTypeRepository,
            VehicleRepository vehicleRepository,
            CoordinatesRepository coordinatesRepository
    ) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
        this.fuelTypeRepository = fuelTypeRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.vehicleRepository = vehicleRepository;
        this.coordinatesRepository = coordinatesRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        //load base admin in db
        if (userRepository.findByUsername("admin").isEmpty()) {
            var user = User.builder()
                    .username("admin")
                    .password(authenticationService.encodePassword("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .build();

            userService.save(user);
        }


        if (fuelTypeRepository.count() == 0) {
            String[] fuelTypes = {"KEROSIN", "DIESEL", "MANPOWER", "PLASMA", "ANTIMATTER"};
            for (String fuel : fuelTypes) {
                fuelTypeRepository.save(new FuelType(fuel));
            }
        }

        if (vehicleTypeRepository.count() == 0) {
            String[] vehicleTypes = {"PLANE", "HELICOPTER", "SHIP", "MOTORCYCLE", "HOVERBOARD"};
            for (String vehicle : vehicleTypes) {
                vehicleTypeRepository.save(new VehicleType(vehicle));
            }
        }
        if (coordinatesRepository.count() == 0) {
            coordinatesRepository.save(new Coordinates(555,Double.parseDouble("555")));
        }
        if (vehicleRepository.count() == 0) {
            Vehicle vehicle = new Vehicle();
            vehicle.setName("BMW");
            vehicle.setCoordinates(coordinatesRepository.findAll().get(0));
            vehicle.setVehicleType(vehicleTypeRepository.findByName("MOTORCYCLE"));
            vehicle.setEnginePower(Long.parseLong("555"));
            vehicle.setNumberOfWheels(Long.parseLong("555"));
            vehicle.setCapacity(555);
            vehicle.setDistanceTravelled(Float.parseFloat("555"));
            vehicle.setFuelConsumption(Float.parseFloat("555"));
            vehicle.setFuelType(fuelTypeRepository.findByName("PLASMA"));
            vehicle.setUser(userRepository.findByUsername("admin").orElseThrow());
            System.out.println(vehicle);
            vehicleRepository.save(vehicle);

        }
    }
}