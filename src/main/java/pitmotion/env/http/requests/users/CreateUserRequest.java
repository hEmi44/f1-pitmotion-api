package pitmotion.env.http.requests.users;

import jakarta.validation.constraints.*;
import pitmotion.env.enums.UserRole;

public record CreateUserRequest(
  @NotBlank @Size(max = 255) String username,
  @NotBlank @Email @Size(max = 255) String email,
  @NotBlank @Size(min = 8, max = 255) String password,
  @NotNull UserRole role
) {}
