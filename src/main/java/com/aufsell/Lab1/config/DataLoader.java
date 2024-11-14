package com.aufsell.Lab1.config;
import com.aufsell.Lab1.model.*;
import com.aufsell.Lab1.repository.*;
import com.aufsell.Lab1.service.AuthenticationService;
import com.aufsell.Lab1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
// preload into DB
public class DataLoader implements CommandLineRunner {

    @Autowired
    private final UserService userService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FuelTypeRepository fuelTypeRepository;
    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private CoordinatesRepository coordinatesRepository;

    public DataLoader(UserService userService) {
        this.userService = userService;
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