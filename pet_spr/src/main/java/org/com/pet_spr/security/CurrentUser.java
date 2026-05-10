package org.com.pet_spr.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
//dùng để lấy đối tượng người dùng hiện tại (User Principal) từ SecurityContext
public @interface CurrentUser {
}
