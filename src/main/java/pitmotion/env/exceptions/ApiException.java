package pitmotion.env.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import lombok.Builder;
import lombok.Getter;
import pitmotion.env.enums.exceptions.ExceptionType;

import org.springframework.http.HttpStatus;

@Builder
@Getter
public class ApiException {
  private final String message;
  private final HttpStatus status;
  private final LocalDateTime timestamp;
  private final ExceptionType type;
  private final HashMap<String, ?> data;
  private final StackTraceElement[] stackTrace;
}
