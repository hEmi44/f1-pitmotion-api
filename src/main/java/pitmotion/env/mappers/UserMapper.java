package pitmotion.env.mappers;

import org.springframework.stereotype.Component;
import pitmotion.env.entities.User;
import pitmotion.env.http.requests.users.CreateUserRequest;
import pitmotion.env.http.requests.users.UpdateUserRequest;
import pitmotion.env.http.resources.users.UserResource;

@Component
public class UserMapper {
  public User toEntity(CreateUserRequest req, String hashedPassword) {
    var u = new User();
    u.setUsername(req.username());
    u.setEmail(req.email());
    u.setPassword(hashedPassword);
    u.setRole(req.role());
    return u;
  }

  public void update(User u, UpdateUserRequest req, String maybeHashedPassword) {
    u.setUsername(req.username());
    u.setEmail(req.email());
    u.setRole(req.role());
    if (maybeHashedPassword != null) u.setPassword(maybeHashedPassword);
  }

  public UserResource toResource(User u) {
    return new UserResource(u.getId(), u.getUsername(), u.getEmail(), u.getRole());
  }
}
