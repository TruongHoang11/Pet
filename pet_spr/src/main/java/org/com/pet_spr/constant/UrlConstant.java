package org.com.pet_spr.constant;

public class UrlConstant {

  public static class ForgetPassword {
    public static final String PREFIX= "/forget-password";
    public static final String VERIFY_EMAIL =PREFIX+ "/email-verification/{email}";
    public static final String VERIFY_OTP = PREFIX+"/otp-verification";
    public static final String CHANGE_PASSWORD =PREFIX+ "/password-update/{email}";
  }

  public static class Auth {
    private static final String PRE_FIX = "/auth";
    public static final String REGISTER = PRE_FIX + "/register";
    public static final String LOGIN = PRE_FIX + "/login";
    public static final String LOGOUT = PRE_FIX + "/logout";
    private Auth() {}
  }

  public static class User {
    private static final String PRE_FIX = "/users";
    public static final String GET_USERS = PRE_FIX;
    public static final String GET_USER = PRE_FIX + "/{id}";
    public static final String GET_CURRENT_USER = PRE_FIX + "/current";
    public static final String CREATE_USER = PRE_FIX;
    public static final String UPDATE_USER = PRE_FIX;
    public static final String DELETE_USER = PRE_FIX + "/{id}";
    private User() {}
  }

  public static class Product {
    private static final String PRE_FIX = "/products";
    public static final String GET_PRODUCTS = PRE_FIX;
    public static final String GET_PRODUCT = PRE_FIX + "/{id}";
    public static final String CREATE_PRODUCT = PRE_FIX;
    public static final String UPDATE_PRODUCT = PRE_FIX;
    public static final String DELETE_PRODUCT = PRE_FIX + "/{id}";
    private Product() {}
  }

  public static class ProductImages {
    private static final String PRE_FIX = "/product-images";
    public static final String ADD_IMAGES = PRE_FIX;
    public static final String DELETE_IMAGE = PRE_FIX + "/{id}";
    public static final String SET_MAIN_IMAGE = PRE_FIX + "/set-main-image";
    private ProductImages() {}
  }

  public static class Inventory {
    private static final String PRE_FIX = "/inventories";
    public static final String IMPORT_PRODUCT = PRE_FIX + "/import";
    public static final String EXPORT_PRODUCT = PRE_FIX + "/export";
    public static final String ADJUST_PRODUCT = PRE_FIX + "/adjust";
    public static final String GET_INVENTORY_BY_PRODUCT_ID = PRE_FIX + "/{id}";
    public static final String GET_INVENTORY_TRANSACTION_HISTORY = PRE_FIX + "/transaction-history";
    private Inventory() {}
  }

  public static class Cart {
    private static final String PRE_FIX = "/cart";
    public static final String GET_CART = PRE_FIX;
    public static final String DELETE_CART = PRE_FIX;
    private Cart() {}
  }

  public static class CartItem {
    private static final String PRE_FIX = "/cart-items";
    public static final String ADD_CART_ITEM = PRE_FIX;
    public static final String UPDATE_CART_ITEM = PRE_FIX;
    public static final String DELETE_CART_ITEM = PRE_FIX + "/{id}";
    private CartItem() {}
  }

  public static class ShippingAddress {
    private static final String PRE_FIX = "/shipping-addresses";
    public static final String CREATE_SHIPPING_ADDRESS = PRE_FIX;
    public static final String GET_SHIPPING_ADDRESSES = PRE_FIX;
    public static final String SET_DEFAULT_ADDRESS = PRE_FIX + "/{id}/default";
    public static final String UPDATE_SHIPPING_ADDRESS = PRE_FIX;
    public static final String DELETE_SHIPPING_ADDRESS = PRE_FIX + "/{id}";
    private ShippingAddress() {}
  }

  public static class Order {
    private static final String PRE_FIX = "/orders";
    private static final String ADMIN_PRE_FIX = "/admin/orders";

    // User
    public static final String CREATE_ORDER_FROM_CART = PRE_FIX + "/from-cart";
    public static final String CREATE_ORDER_FROM_BUY_NOW = PRE_FIX + "/buy-now";
    public static final String GET_MY_ORDERS = PRE_FIX + "/my-orders";
    public static final String GET_ORDER_DETAIL = PRE_FIX + "/{id}"; // both admin and user
    public static final String CANCEL_ORDER = PRE_FIX + "/{id}/cancel";
    public static final String GET_ORDER_STATUS_HISTORY = PRE_FIX + "/{id}/status-history";

    // Admin
    public static final String GET_ALL_ORDERS = ADMIN_PRE_FIX;

    public static final String UPDATE_ORDER_STATUS = ADMIN_PRE_FIX + "/status";

    private Order() {}
  }

  public static class Pet {
    private static final String PRE_FIX = "/pets";
    private static final String ADMIN_PRE_FIX = "/admin/pets";

    // User
    public static final String GET_MY_PETS = PRE_FIX + "/my-pets";
    public static final String GET_PET_DETAIL = PRE_FIX + "/{id}";
    public static final String CREATE_PET = PRE_FIX;
    public static final String UPDATE_PET = PRE_FIX;
    public static final String DELETE_PET = PRE_FIX + "/{id}";

    // Admin
    public static final String GET_ALL_PETS = ADMIN_PRE_FIX;
    public static final String PATCH_DEACTIVATE_PET = ADMIN_PRE_FIX + "/{id}/deactivate";
    public static final String PATCH_ACTIVATE_PET = ADMIN_PRE_FIX + "/{id}/activate";

    private Pet() {}
  }

}
