package org.com.pet_spr.exception;


import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.com.pet_spr.base.RestData;
import org.com.pet_spr.base.VsResponseUtil;
import org.com.pet_spr.constant.ErrorMessage;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

  private final MessageSource messageSource;

  //Error validate for param
  @ExceptionHandler({ConstraintViolationException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<RestData<?>> handleConstraintViolationException(ConstraintViolationException ex) {
    Map<String, String> result = new LinkedHashMap<>();
    ex.getConstraintViolations().forEach((error) -> {
      String fieldName = ((PathImpl) error.getPropertyPath()).getLeafNode().getName();
      String errorMessage = messageSource.getMessage(Objects.requireNonNull(error.getMessage()), null,
          LocaleContextHolder.getLocale());
      result.put(fieldName, errorMessage);
    });
    return VsResponseUtil.error(HttpStatus.BAD_REQUEST, result);
  }

//  @ExceptionHandler({MethodArgumentNotValidException.class})
//  @ResponseStatus(HttpStatus.BAD_REQUEST)
//  public ResponseEntity<RestData<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
//    Map<String, String> result = new LinkedHashMap<>();
//    ex.getBindingResult().getFieldErrors().forEach((error) -> {
//      String fieldName = error.getField();
//      String errorMessage = messageSource.getMessage(Objects.requireNonNull(error.getDefaultMessage()), null,
//          LocaleContextHolder.getLocale());
//      result.put(fieldName, errorMessage);
//    });
//    return VsResponseUtil.error(HttpStatus.BAD_REQUEST, result);
//
//    //.stream()
//    //            .map(error -> error.getField() + ": " + error.getDefaultMessage())
//    //            .collect(Collectors.joining(", "));
//   // return VsResponseUtil.error(HttpStatus.BAD_REQUEST, errorMessage);
//  }

  @ExceptionHandler({MethodArgumentNotValidException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<RestData<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    Map<String, String> result = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors().forEach((error) -> {
      String fieldName = error.getField();

      // Sử dụng hàm getMessage có defaultMessage để chấp nhận cả chuỗi text thô viết ở DTO
      String errorMessage = messageSource.getMessage(
              Objects.requireNonNull(error.getDefaultMessage()),
              null,
              error.getDefaultMessage(), // Nếu không tìm thấy Key, dùng luôn chuỗi này làm nội dung lỗi
              LocaleContextHolder.getLocale()
      );
      result.put(fieldName, errorMessage);
    });
    return VsResponseUtil.error(HttpStatus.BAD_REQUEST, result);
  }
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<RestData<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    String message = messageSource.getMessage(ex.getMessage(), null,
        LocaleContextHolder.getLocale());
    return VsResponseUtil.error(HttpStatus.BAD_REQUEST, message);
  }


  //Error validate for body
  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<RestData<?>> handleValidException(BindException ex) {
    Map<String, String> result = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = messageSource.getMessage(Objects.requireNonNull(error.getDefaultMessage()), null,
          LocaleContextHolder.getLocale());
      result.put(fieldName, errorMessage);
    });
    return VsResponseUtil.error(HttpStatus.BAD_REQUEST, result);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<RestData<?>> handlerInternalServerError(Exception ex) {
    log.error(ex.getMessage(), ex);
    String message = messageSource.getMessage(ErrorMessage.ERR_EXCEPTION_GENERAL, null,
        LocaleContextHolder.getLocale());
    return VsResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, message);
  }

  //Exception custom
  @ExceptionHandler(VsException.class)
  public ResponseEntity<RestData<?>> handleVsException(VsException ex) {
    log.error(ex.getMessage(), ex);
    return VsResponseUtil.error(ex.getStatus(), ex.getErrMessage());
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<RestData<?>> handlerNotFoundException(NotFoundException ex) {
    String message = messageSource.getMessage(ex.getMessage(), ex.getParams(), LocaleContextHolder.getLocale());
    log.error(message, ex);
    return VsResponseUtil.error(ex.getStatus(), message);
  }

  @ExceptionHandler(InvalidException.class)
  public ResponseEntity<RestData<?>> handlerInvalidException(InvalidException ex) {
    log.error(ex.getMessage(), ex);
    String message = messageSource.getMessage(ex.getMessage(), ex.getParams(), LocaleContextHolder.getLocale());
    return VsResponseUtil.error(ex.getStatus(), message);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<RestData<?>> handleBadRequest(BadRequestException ex) {
    log.error(ex.getMessage(), ex);
    String message = messageSource.getMessage(ex.getMessage(), ex.getParams(), LocaleContextHolder.getLocale());
    return VsResponseUtil.error(ex.getStatus(), message);

  }

  @ExceptionHandler(InternalServerException.class)
  public ResponseEntity<RestData<?>> handlerInternalServerException(InternalServerException ex) {
    String message = messageSource.getMessage(ex.getMessage(), ex.getParams(), LocaleContextHolder.getLocale());
    log.error(message, ex);
    return VsResponseUtil.error(ex.getStatus(), message);
  }

  @ExceptionHandler(UploadFileException.class)
  public ResponseEntity<RestData<?>> handleUploadImageException(UploadFileException ex) {
    String message = messageSource.getMessage(ex.getMessage(), ex.getParams(), LocaleContextHolder.getLocale());
    log.error(message, ex);
    return VsResponseUtil.error(ex.getStatus(), message);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<RestData<?>> handleUnauthorizedException(UnauthorizedException ex) {
    String message = messageSource.getMessage(ex.getMessage(), ex.getParams(), LocaleContextHolder.getLocale());
    log.error(message, ex);
    return VsResponseUtil.error(ex.getStatus(), message);
  }


  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<RestData<?>> handleAccessDeniedException(ForbiddenException ex) {
    String message = messageSource.getMessage(ex.getMessage(), ex.getParams(), LocaleContextHolder.getLocale());
    log.error(message, ex);
    return VsResponseUtil.error(ex.getStatus(), message);
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<RestData<?>> handleConflictException(ConflictException ex) {
    String message = messageSource.getMessage(ex.getMessage(), ex.getParams() ,LocaleContextHolder.getLocale());
    log.warn(message);
    return VsResponseUtil.error(ex.getStatus(), message);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<RestData<?>> handleNoResourceFoundException(NoResourceFoundException ex) {
    String message = messageSource.getMessage(ErrorMessage.INVALID_RESOURCE_NOT_FOUND, null, LocaleContextHolder.getLocale());
    log.warn(message);
    return VsResponseUtil.error(HttpStatus.NOT_FOUND, message);
  }

}