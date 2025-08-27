package pitmotion.env.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pitmotion.env.entities.User;
import pitmotion.env.http.requests.users.CreateUserRequest;
import pitmotion.env.http.requests.users.UpdateUserRequest;
import pitmotion.env.mappers.UserMapper;
import pitmotion.env.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper mapper;

  @Transactional
  public User create(CreateUserRequest req) {
    if (userRepository.existsByEmail(req.email())) {
      throw new DataIntegrityViolationException("Email already used");
    }

    String hashed = passwordEncoder.encode(req.password());

    User user = mapper.toEntity(req, hashed);
    return userRepository.save(user);
  }

  @Transactional
  public User update(Long id, UpdateUserRequest req) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

    if (!user.getEmail().equals(req.email()) && userRepository.existsByEmail(req.email())) {
      throw new DataIntegrityViolationException("Email already used");
    }

    String maybeHashedPassword = null;
    if (req.password() != null && !req.password().isBlank()) {
      if (req.password().length() < 8) {
        throw new IllegalArgumentException("Password must be at least 8 characters");
      }
      maybeHashedPassword = passwordEncoder.encode(req.password());
    }

    mapper.update(user, req, maybeHashedPassword);
    return userRepository.save(user);
  }

  @Transactional
  public void delete(Long id) {
    if (!userRepository.existsById(id)) {
      throw new EntityNotFoundException("User not found: " + id);
    }
    userRepository.deleteById(id);
  }
}
