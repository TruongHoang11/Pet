package org.com.pet_spr.configuration;


import lombok.RequiredArgsConstructor;
import org.com.pet_spr.constant.UrlConstant;
import org.com.pet_spr.security.jwt.JwtCustomAuthenticationEntryPoint;
import org.com.pet_spr.security.jwt.JwtPreFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {
    private final JwtPreFilter jwtPreFilter;
    private final JwtCustomAuthenticationEntryPoint jwtCustomAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {

                    // ===== PUBLIC =====
                    auth.requestMatchers(
                            HttpMethod.POST, "/api/v1" + UrlConstant.Auth.REGISTER,
                            "/api/v1" + UrlConstant.Auth.LOGIN
                    ).permitAll();

                    auth.requestMatchers(
                            "/api/v1" + UrlConstant.ForgetPassword.VERIFY_EMAIL,
                            "/api/v1" + UrlConstant.ForgetPassword.VERIFY_OTP,
                            "/api/v1" + UrlConstant.ForgetPassword.CHANGE_PASSWORD
                    ).permitAll();

                    // Xem sản phẩm - public
                    auth.requestMatchers(
                            HttpMethod.GET,
                            "/api/v1" + UrlConstant.Product.GET_PRODUCTS,
                            "/api/v1" + UrlConstant.Product.GET_PRODUCT
                    ).permitAll();

                    // ===== ADMIN ONLY =====
                    // User management
                    auth.requestMatchers(
                            HttpMethod.GET,
                            "/api/v1" + UrlConstant.User.GET_USERS,
                            "/api/v1" + UrlConstant.User.GET_USER
                    ).hasRole("ADMIN");

                    auth.requestMatchers(
                            HttpMethod.POST,
                            "/api/v1" + UrlConstant.User.CREATE_USER
                    ).hasRole("ADMIN");

                    auth.requestMatchers(
                            HttpMethod.PUT,
                            "/api/v1" + UrlConstant.User.UPDATE_USER
                    ).hasAuthority("ROLE_ADMIN");

                    auth.requestMatchers(
                            HttpMethod.DELETE,
                            "/api/v1" + UrlConstant.User.DELETE_USER
                    ).hasAuthority("ROLE_ADMIN");

                    // Product management
                    auth.requestMatchers(
                            HttpMethod.POST,
                            "/api/v1" + UrlConstant.Product.CREATE_PRODUCT
                    ).hasAuthority("ROLE_ADMIN");

                    auth.requestMatchers(
                            HttpMethod.PUT,
                            "/api/v1" + UrlConstant.Product.UPDATE_PRODUCT
                    ).hasAuthority("ROLE_ADMIN");

                    auth.requestMatchers(
                            HttpMethod.DELETE,
                            "/api/v1" + UrlConstant.Product.DELETE_PRODUCT
                    ).hasAuthority("ROLE_ADMIN");


                    // ProductImages
                    auth.requestMatchers(
                            "/api/v1" + UrlConstant.ProductImages.ADD_IMAGES,
                            "/api/v1" + UrlConstant.ProductImages.DELETE_IMAGE,
                            "/api/v1" + UrlConstant.ProductImages.SET_MAIN_IMAGE
                    ).hasAuthority("ROLE_ADMIN");


                    // Inventory
                    auth.requestMatchers(
                            "/api/v1" + UrlConstant.Inventory.IMPORT_PRODUCT,
                            "/api/v1" + UrlConstant.Inventory.EXPORT_PRODUCT,
                            "/api/v1" + UrlConstant.Inventory.ADJUST_PRODUCT,
                            "/api/v1" + UrlConstant.Inventory.GET_INVENTORY_BY_PRODUCT_ID,
                            "/api/v1" + UrlConstant.Inventory.GET_INVENTORY_TRANSACTION_HISTORY
                    ).hasAuthority("ROLE_ADMIN");

                    // Order - Admin
                    auth.requestMatchers(
                            "/api/v1" + UrlConstant.Order.GET_ALL_ORDERS,
                            "/api/v1" + UrlConstant.Order.UPDATE_ORDER_STATUS
                    ).hasAuthority("ROLE_ADMIN");

                    // Pet - Admin
                    auth.requestMatchers(
                            "/api/v1" + UrlConstant.Pet.GET_ALL_PETS,
                            "/api/v1" + UrlConstant.Pet.PATCH_DEACTIVATE_PET,
                            "/api/v1" + UrlConstant.Pet.PATCH_ACTIVATE_PET
                    ).hasAuthority("ROLE_ADMIN");

                    // ===== USER =====
                    // User profile
                    auth.requestMatchers(
                            HttpMethod.GET,
                            "/api/v1" + UrlConstant.User.GET_CURRENT_USER
                    ).hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");

                    // Cart
                    auth.requestMatchers(
                            "/api/v1" + UrlConstant.Cart.GET_CART,
                            "/api/v1" + UrlConstant.Cart.DELETE_CART
                    ).hasAuthority("ROLE_USER");



                    // CartItem
                    auth.requestMatchers(
                            "/api/v1" + UrlConstant.CartItem.ADD_CART_ITEM,
                            "/api/v1" + UrlConstant.CartItem.UPDATE_CART_ITEM,
                            "/api/v1" + UrlConstant.CartItem.DELETE_CART_ITEM
                    ).hasAuthority("ROLE_USER");



                    // ShippingAddress
                    auth.requestMatchers(
                            "/api/v1" + UrlConstant.ShippingAddress.CREATE_SHIPPING_ADDRESS,
                            "/api/v1" + UrlConstant.ShippingAddress.GET_SHIPPING_ADDRESSES,
                            "/api/v1" + UrlConstant.ShippingAddress.UPDATE_SHIPPING_ADDRESS,
                            "/api/v1" + UrlConstant.ShippingAddress.DELETE_SHIPPING_ADDRESS,
                            "/api/v1" + UrlConstant.ShippingAddress.SET_DEFAULT_ADDRESS
                    ).hasAuthority("ROLE_USER");



                    // Order - User
                    auth.requestMatchers(
                            "/api/v1" + UrlConstant.Order.CREATE_ORDER_FROM_CART,
                            "/api/v1" + UrlConstant.Order.CREATE_ORDER_FROM_BUY_NOW,
                            "/api/v1" + UrlConstant.Order.GET_MY_ORDERS,
                            "/api/v1" + UrlConstant.Order.CANCEL_ORDER,
                            "/api/v1" + UrlConstant.Order.GET_ORDER_STATUS_HISTORY
                    ).hasAuthority("ROLE_USER");



                    // Order detail - cả User và Admin
                    auth.requestMatchers(
                            HttpMethod.GET,
                            "/api/v1" + UrlConstant.Order.GET_ORDER_DETAIL
                    ).hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");

                    // Pet - User
                    auth.requestMatchers(
                            "/api/v1" + UrlConstant.Pet.GET_MY_PETS,
                            "/api/v1" + UrlConstant.Pet.GET_PET_DETAIL,
                            "/api/v1" + UrlConstant.Pet.CREATE_PET,
                            "/api/v1" + UrlConstant.Pet.UPDATE_PET,
                            "/api/v1" + UrlConstant.Pet.DELETE_PET
                    ).hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");



                    // Tất cả request còn lại cần đăng nhập
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtCustomAuthenticationEntryPoint))
                .addFilterBefore(jwtPreFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
