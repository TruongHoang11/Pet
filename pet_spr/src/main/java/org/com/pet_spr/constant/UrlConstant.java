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
    public static final String OAUTH2_AUTHORIZE = PRE_FIX + "/oauth2/authorize";
    public static final String OAUTH2_CALLBACK = PRE_FIX + "/oauth2/callback";

    private Auth() {
    }
  }

  public static class User {
    private static final String PRE_FIX = "/user";

    public static final String GET_USERS = PRE_FIX;
    public static final String GET_USER = PRE_FIX + "/{id}";
    public static final String GET_CURRENT_USER = PRE_FIX + "/current";
    public static final String CREATE_USER = PRE_FIX;
    public static final String UPDATE_USER = PRE_FIX;
    public static final String DELETE_USER = PRE_FIX + "/{id}";


    private User() {
    }
  }
  public static class Product {
    private static final String PRE_FIX = "/product";

    public static final String GET_PRODUCTS = PRE_FIX;
    public static final String GET_PRODUCT = PRE_FIX + "/{id}";
    public static final String CREATE_PRODUCT = PRE_FIX;
    public static final String UPDATE_PRODUCT = PRE_FIX;
    public static final String DELETE_PRODUCT = PRE_FIX + "/{id}";


    private Product() {
    }
  }

  public static class Job {
    private static final String PRE_FIX = "/job";

    public static final String GET_JOBS = PRE_FIX;
    public static final String GET_JOB = PRE_FIX + "/{id}";
    public static final String CREATE_JOB = PRE_FIX;
    public static final String UPDATE_JOB = PRE_FIX;
    public static final String DELETE_JOB = PRE_FIX + "/{id}";


    private Job() {
    }
  }


  public static class ProductImages {
    private static final String PRE_FIX = "/product-images";

    public static final String ADD_IMAGES = PRE_FIX;
    public static final String DELETE_IMAGE = PRE_FIX + "/{id}";
    public static final String SET_MAIN_IMAGE = PRE_FIX + "/set-main-image";


    private ProductImages() {
    }
  }

  public static class Inventory {
    private static final String PRE_FIX = "/inventory";

    public static final String IMPORT_PRODUCT = PRE_FIX + "/import";
    public static final String EXPORT_PRODUCT = PRE_FIX + "/export";
    public static final String ADJUST_PRODUCT = PRE_FIX + "/adjust";
    public static final String GET_INVENTORY_BY_PRODUCT_ID = PRE_FIX + "/{id}";
    public static final String GET_INVENTORY_TRANSACTION_HISTORY = PRE_FIX + "/transaction-history";



    private Inventory() {
    }
  }

  public static class Cart {
    private static final String PRE_FIX = "/cart";

    public static final String GET_CART = PRE_FIX;
   // public static final String GET_CART = PRE_FIX + "/{id}";
//    public static final String CREATE_CART = PRE_FIX;
//    public static final String UPDATE_CARD = PRE_FIX;
    public static final String DELETE_CART = PRE_FIX;


    private Cart() {
    }
  }

  public static class CartItem {
    private static final String PRE_FIX = "/cartItem";

    public static final String ADD_CART_ITEM = PRE_FIX;
    public static final String GET_CART_ITEM = PRE_FIX + "/{id}";
    public static final String CREATE_CART_ITEM = PRE_FIX;
    public static final String UPDATE_CART_ITEM = PRE_FIX;
    public static final String DELETE_CART_ITEM = PRE_FIX + "/{id}";


    private CartItem() {
    }
  }


  public static class ShippingAddress {
    private static final String PRE_FIX = "/shippingAddress";

    public static final String CREATE_SHIPPING_ADDRESS = PRE_FIX;
    public static final String GET_SHIPPING_ADDRESSES = PRE_FIX + "/list";
    public static final String SET_DEFAULT_ADDRESS = PRE_FIX + "/{id}";
    public static final String UPDATE_SHIPPING_ADDRESS = PRE_FIX;
    public static final String DELETE_SHIPPING_ADDRESS = PRE_FIX + "/{id}";


    private ShippingAddress() {
    }
  }

  public static class Order {
    private static final String PRE_FIX = "/order";

    public static final String CREATE_ORDER_FROM_CART = PRE_FIX + "/from-cart";
    public static final String GET_MY_ORDERS = PRE_FIX;
    public static final String CREATE_ORDER_FROM_BUY_NOW = PRE_FIX + "/from-buy-now";
    public static final String UPDATE_ORDER_STATUS = PRE_FIX + "/status";
    public static final String CANCEL_ORDER = PRE_FIX + "/{id}";
    public static final String GET_ALL_ORDERS = PRE_FIX + "/list";
    public static final String GET_ORDER_DETAIL_ADMIN = PRE_FIX + "/{id}";






    private Order() {
    }
  }

}
