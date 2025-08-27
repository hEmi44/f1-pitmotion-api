package pitmotion.env.http.resources.users;

import pitmotion.env.enums.UserRole;

public record UserResource(
  Long id,
  String username,
  String email,
  UserRole role
) {}
