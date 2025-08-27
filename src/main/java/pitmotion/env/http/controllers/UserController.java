// pitmotion/env/http/controllers/UserController.java
package pitmotion.env.http.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pitmotion.env.enums.ProfileName;
import pitmotion.env.http.requests.users.CreateUserRequest;
import pitmotion.env.http.requests.users.UpdateUserRequest;
import pitmotion.env.http.resources.users.UserResource;
import pitmotion.env.mappers.UserMapper;
import pitmotion.env.services.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Profile(ProfileName.HTTP)
public class UserController {
  private final UserService userService;
  private final UserMapper mapper;

  @PostMapping
  public ResponseEntity<UserResource> create(@Valid @RequestBody CreateUserRequest request) {
    var user = userService.create(request);
    return ResponseEntity
      .created(java.net.URI.create("/users/" + user.getId()))
      .body(mapper.toResource(user));
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserResource> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
    var user = userService.update(id, request);
    return ResponseEntity.ok(mapper.toResource(user));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.delete(id);
    return ResponseEntity.noContent().build();
  }

  // Gestion d’erreurs simple
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<String> notFound(EntityNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<String> conflict(DataIntegrityViolationException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }
}
