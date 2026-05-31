package org.com.pet_spr.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

public final class SecurityUtil {

    //  Private constructor để chặn tạo đối tượng bằng lệnh new
    private SecurityUtil() {
        throw new IllegalStateException("Utility class");
    }

    // trả về email
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    // trich xuat dinh danh thuong la email hoac username
    private static String extractPrincipal(Authentication authentication) {
        //Nếu không có thông tin xác thực nào (người dùng chưa gửi bất kỳ thông tin đăng nhập nào),
        // hàm lập tức trả về null.
        if (authentication == null) {
            return null;
        }
        //Trường hợp 1 (Đăng nhập kiểu truyền thống - Session/Cookie):Nếu đối tượng định danh (getPrincipal())
        // là một instance của UserDetails (giao diện chuẩn của Spring Security khi dùng Form Login
        // hoặc cơ chế lưu database thông thường),nó sẽ ép kiểu sang biến springSecurityUser và lấy ra username.
        else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        }
        //Trường hợp 2 (Đăng nhập kiểu Stateless - Token JWT): Nếu bạn đang xây dựng API sử dụng OAuth2 hoặc JWT,
        // đối tượng định danh lúc này sẽ là một chuỗi mã hóa Jwt.
        // Hàm sẽ bóc tách thuộc tính Subject (thường chứa Email hoặc ID/Username được mã hóa bên trong token) để trả về.
        else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        //Trường hợp 3 (Người dùng ẩn danh hoặc Chuỗi thô): Nếu hệ thống chưa bắt đăng nhập
        // nhưng cấu hình cho phép truy cập ẩn danh, Spring Security thường gán getPrincipal()
        // là một chuỗi String mang giá trị "anonymousUser". Hàm sẽ trả về chính chuỗi String này.
        else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }
}
