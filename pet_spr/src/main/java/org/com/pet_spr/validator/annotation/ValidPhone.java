package org.com.pet_spr.validator.annotation;

import jakarta.persistence.Table;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.com.pet_spr.validator.PhoneValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhone {
    String message() default "Invalid phone number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
