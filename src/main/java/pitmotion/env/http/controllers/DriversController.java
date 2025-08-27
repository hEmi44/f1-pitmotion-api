package pitmotion.env.http.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pitmotion.env.enums.ProfileName;
import pitmotion.env.http.resources.drivers.DriverListResource;
import pitmotion.env.http.resources.drivers.DriverResource;
import pitmotion.env.services.DriverService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/drivers")
@Profile(ProfileName.HTTP)
public class DriversController {
  private final DriverService driverService;

  @GetMapping
  public ResponseEntity<?> get(@RequestParam(value = "code", required = false) String code) {
    if (code == null || code.isBlank()) {
      DriverListResource list = driverService.listDrivers();
      return ResponseEntity.ok(list);
    }
    DriverResource driver = driverService.getDriver(code);
    return ResponseEntity.ok(driver);
  }
}
