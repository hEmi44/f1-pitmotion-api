package pitmotion.env.http.requests.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pitmotion.env.enums.UserRole;

public record UpdateUserRequest(
    @NotBlank @Size(max = 255) String username,
    @NotBlank @Email @Size(max = 255) String email,
    @Size(min = 8, max = 255, message = "Password must be at least 8 characters")
    String password,
    @NotNull UserRole role
) {}
