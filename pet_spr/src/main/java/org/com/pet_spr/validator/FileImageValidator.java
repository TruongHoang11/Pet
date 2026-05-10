package org.com.pet_spr.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.com.pet_spr.constant.CommonConstant;
import org.com.pet_spr.validator.annotation.ValidFileImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

public class FileImageValidator implements ConstraintValidator<ValidFileImage, MultipartFile> {

  @Override
  public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
    if (file != null) {
      String contentType = file.getContentType();
      return isSupportedContentType(Objects.requireNonNull(contentType));
    }
    return false;
  }

  private boolean isSupportedContentType(String contentType) {
    return CommonConstant.CONTENT_TYPE_IMAGE.contains(contentType.substring("image/".length()));
  }

}
