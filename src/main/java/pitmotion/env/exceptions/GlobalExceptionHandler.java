package pitmotion.env.exceptions;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import pitmotion.env.http.resources.exceptions.ApiExceptionResource;
import pitmotion.env.mappers.ApiExceptionMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {
  private ApiExceptionMapper apiExceptionMapper;

  @ExceptionHandler(exception = EntityNotFoundException.class)
  public ResponseEntity<ApiExceptionResource> handleEntityNotFoundException(
      EntityNotFoundException exception) {
    ApiException apiException = this.apiExceptionMapper.entityNotFound(exception);

    return this.apiExceptionMapper.responseEntity(apiException);
  }

  @ExceptionHandler(exception = MethodArgumentNotValidException.class)
  public ResponseEntity<ApiExceptionResource> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException exception) {
    ApiException apiException = this.apiExceptionMapper.methodArgumentNotValid(exception);

    return this.apiExceptionMapper.responseEntity(apiException);
  }
}
