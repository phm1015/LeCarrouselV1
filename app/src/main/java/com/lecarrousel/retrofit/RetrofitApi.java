package com.lecarrousel.retrofit;


import com.lecarrousel.model.AddAddressModel;
import com.lecarrousel.model.AddCardModel;
import com.lecarrousel.model.AddToCartModel;
import com.lecarrousel.model.CancelOrderModel;
import com.lecarrousel.model.CartCountListModel;
import com.lecarrousel.model.CartListModel;
import com.lecarrousel.model.CategoryListModel;
import com.lecarrousel.model.ChangePasswordModel;
import com.lecarrousel.model.CouponModel;
import com.lecarrousel.model.DeleteCardModel;
import com.lecarrousel.model.ForgetPasswordModel;
import com.lecarrousel.model.GeneralModel;
import com.lecarrousel.model.LoginModel;
import com.lecarrousel.model.MasterDataModel;
import com.lecarrousel.model.NotificationModel;
import com.lecarrousel.model.OrderDetailModel;
import com.lecarrousel.model.OrderListModel;
import com.lecarrousel.model.PlaceOrderModel;
import com.lecarrousel.model.ProductListModel;
import com.lecarrousel.model.RegisterModel;
import com.lecarrousel.model.SecurityCodeModel;
import com.lecarrousel.model.UpdateCard;
import com.lecarrousel.model.UpdateProfileModel;
import com.lecarrousel.model.UserAccountModel;
import com.lecarrousel.model.WishListModel;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RetrofitApi {

    //Loginnnnn
    @FormUrlEncoded
    @POST("user/login")
    Call<LoginModel> login(@Header("lancode") String lancode,
                           @Field("email") String email,
                           @Field("password") String password);

    //Register
    @FormUrlEncoded
    @POST("user/registration")
    Call<RegisterModel> register(@Header("lancode") String lancode,
                                 @Field("email") String email,
                                 @Field("password") String password,
                                 @Field("name") String name,
                                 @Field("phone") String phone,
                                 @Field("country_id") String country_id); //Register

    @FormUrlEncoded
    @POST("user/verify_code")
    Call<LoginModel> verifyCode(@Header("lancode") String lancode,
                                @Field("user_id") String user_id,
                                @Field("verification_code") String verification_code); //Register

    //Update Prifile
    @Multipart
    @POST("user/update_profile")
    Call<UpdateProfileModel> updateProfileWithImage(@Header("lancode") String lancode,
                                                    @Query("user_id") String user_id,
                                                    @Query("name") String name,
                                                    @Query("mobile_number") String mobile_number,
                                                    @Query("country_id") String country_id,
                                                    @Part MultipartBody.Part file);

    //Update Prifile
    @FormUrlEncoded
    @POST("user/update_profile")
    Call<UpdateProfileModel> updateProfileWithoutImage(@Header("lancode") String lancode,
                                                       @Field("user_id") String user_id,
                                                       @Field("name") String name,
                                                       @Field("mobile_number") String mobile_number,
                                                       @Field("country_id") String country_id);

    //forgot password
    @FormUrlEncoded
    @POST("user/forgot_password")
    Call<ForgetPasswordModel> forgotPassword(@Header("lancode") String lancode,
                                             @Field("email") String email,
                                             @Field("phone") String phone);

    @FormUrlEncoded
    @POST("user/update_forgot_password")
    Call<UpdateCard> resetPassword(@Header("lancode") String lancode,
                                   @Field("email") String email,
                                   @Field("fp_code") String fp_code,
                                   @Field("new_password") String new_password);

    //Add Device Token
    @FormUrlEncoded
    @POST("notification/add_device_token")
    Call<GeneralModel> addDeviceToken(@Header("lancode") String lancode,
                                      @Field("user_id") String user_id,
                                      @Field("device_token") String device_token,
                                      @Field("device_type") String device_type);

    //remove Device Token
    @FormUrlEncoded
    @POST("notification/remove_device_token")
    Call<GeneralModel> removeDeviceToken(@Header("lancode") String lancode,
                                         @Field("user_id") String user_id,
                                         @Field("device_token") String device_token,
                                         @Field("device_type") String device_type);

    // CategoryList
    @FormUrlEncoded
    @POST("category/category_list")
    Call<CategoryListModel> categoryList(@Header("lancode") String lancode,
                                         @Field("user_id") String user_id,
                                         @Field("store_id") String store_id);

    // Get Otp For COD
    @FormUrlEncoded
    @POST("order/get_confirm_order")
    Call<GeneralModel> getConfirmOrder(@Header("lancode") String lancode,
                                       @Field("user_id") String user_id,
                                       @Field("mobile_number") String mobile_number);

    @FormUrlEncoded
    @POST("category/product_list")
    Call<ProductListModel> productList(@Header("lancode") String lancode,
                                       @Field("user_id") String user_id,
                                       @Field("page_no") String page_no,
                                       @Field("catId") String catId,
                                       @Field("type") String type,
                                       @Field("store_id") String store_id);

    @FormUrlEncoded
    @POST("category/highlight_product_list")
    Call<ProductListModel> highLightList(@Header("lancode") String lancode,
                                         @Field("user_id") String user_id,
                                         @Field("catId") String catId,
                                         @Field("type") String type,
                                         @Field("store_id") String store_id);

    @FormUrlEncoded
    @POST("category/product/favourite")
    Call<GeneralModel> requestFavourite(@Header("lancode") String lancode,
                                        @Field("user_id") String user_id,
                                        @Field("pId") String pId,
                                        @Field("isFavourite") String isFavourite,
                                        @Field("catId") String catId);

    @FormUrlEncoded
    @POST("user/add_shipping_address")
    Call<AddAddressModel> addAddress(@Header("lancode") String lancode,
                                     @Field("user_id") String user_id,
                                     @Field("address_id") String address_id,
                                     @Field("contact_name") String contact_name,
                                     @Field("building_no") String building_no,
                                     @Field("street_no") String street_no,
                                     @Field("zone") String zone,
                                     @Field("street_address") String street_address,
                                     @Field("city") String city,
                                     @Field("mobile") String mobile,
                                     @Field("country_id") String country_id,
                                     @Field("country_name") String country_name);

    @FormUrlEncoded
    @POST("user/add_shipping_address")
    Call<UpdateCard> updateAddress(@Header("lancode") String lancode,
                                   @Field("user_id") String user_id,
                                   @Field("address_id") String address_id,
                                   @Field("contact_name") String contact_name,
                                   @Field("building_no") String building_no,
                                   @Field("street_no") String street_no,
                                   @Field("zone") String zone,
                                   @Field("street_address") String street_address,
                                   @Field("city") String city,
                                   @Field("mobile") String mobile,
                                   @Field("country_id") String country_id,
                                   @Field("country_name") String country_name);

    @FormUrlEncoded
    @POST("user/add_debit_card")
    Call<AddCardModel> addCard(@Header("lancode") String lancode,
                               @Field("user_id") String user_id,
                               @Field("card_id") String card_id,
                               @Field("card_number") String card_number,
                               @Field("card_holder_name") String card_holder_name,
                               @Field("expiry_date") String expiry_date);

    @FormUrlEncoded
    @POST("user/add_debit_card")
    Call<UpdateCard> updateCard(@Header("lancode") String lancode,
                                @Field("user_id") String user_id,
                                @Field("card_id") String card_id,
                                @Field("card_number") String card_number,
                                @Field("card_holder_name") String card_holder_name,
                                @Field("expiry_date") String expiry_date);

    @FormUrlEncoded
    @POST("user/change_password")
    Call<ChangePasswordModel> changePassword(@Header("lancode") String lancode,
                                             @Field("user_id") String user_id,
                                             @Field("new_password") String new_password,
                                             @Field("old_password") String old_password,
                                             @Field("security_code") String security_code);

    @FormUrlEncoded
    @POST("user/generate_security_code")
    Call<SecurityCodeModel> securityCode(@Header("lancode") String lancode,
                                         @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("user/details")
    Call<UserAccountModel> userAccount(@Header("lancode") String lancode,
                                       @Field("user_id") String user_id);

    @POST("get_master_data")
    Call<MasterDataModel> masterData(@Header("lancode") String lancode);

    @FormUrlEncoded
    @POST("user/delete_debit_card")
    Call<DeleteCardModel> deleteCard(@Header("lancode") String lancode,
                                     @Field("user_id") String user_id,
                                     @Field("card_id") String card_id);

    @FormUrlEncoded
    @POST("user/delete_shipping_address")
    Call<DeleteCardModel> deleteAddress(@Header("lancode") String lancode,
                                        @Field("user_id") String user_id,
                                        @Field("address_id") String address_id);

    @FormUrlEncoded
    @POST("order/wish_list")
    Call<WishListModel> wishList(@Header("lancode") String lancode,
                                 @Field("user_id") String user_id,
                                 @Field("store_id") String store_id);

    @FormUrlEncoded
    @POST("order/get_cart_list")
    Call<CartListModel> cartList(@Header("lancode") String lancode,
                                 @Field("user_id") String user_id,
                                 @Field("store_id") String store_id);

    @FormUrlEncoded
    @POST("user/notification_list")
    Call<NotificationModel> notificationList(@Header("lancode") String lancode,
                                             @Field("user_id") String user_id,
                                             @Field("timeZone") String timeZone);

    @FormUrlEncoded
    @POST("user/remove_notification")
    Call<GeneralModel> removeNotification(@Header("lancode") String lancode,
                                          @Field("user_id") String user_id,
                                          @Field("notificationId") String notificationId);

    @FormUrlEncoded
    @POST("order/get_coupon_discount")
    Call<CouponModel> getDiscount(@Header("lancode") String lancode,
                                  @Field("user_id") String user_id,
                                  @Field("coupon_code") String coupon_code,
                                  @Field("timeZone") String timeZone);

    @FormUrlEncoded
    @POST("order/count_cart_list")
    Call<CartCountListModel> requestGetCartCount(@Header("lancode") String lancode,
                                                 @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("order/add_to_cart")
    Call<AddToCartModel> addToCart(@Header("lancode") String lancode,
                                   @Field("user_id") String user_id,
                                   @Field("pId") String pId,
                                   @Field("quantity") String quantity,
                                   @Field("type") String type,
                                   @Field("store_id") String store_id);

    @FormUrlEncoded
    @POST("order/remove_cart_item")
    Call<GeneralModel> removeCartItem(@Header("lancode") String lancode,
                                      @Field("user_id") String user_id,
                                      @Field("cart_id") String cart_id);

    @FormUrlEncoded
    @POST("order/order_list")
    Call<OrderListModel> orderList(@Header("lancode") String lancode,
                                   @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("order/order_details")
    Call<OrderDetailModel> orderDetail(@Header("lancode") String lancode,
                                       @Field("user_id") String user_id,
                                       @Field("orderId") String orderId);

    @FormUrlEncoded
    @POST("order/cancel_order")
    Call<CancelOrderModel> cancelOrder(@Header("lancode") String lancode,
                                       @Field("user_id") String user_id,
                                       @Field("orderId") String orderId);

    @FormUrlEncoded
    @POST("order/confirm_order")
    Call<PlaceOrderModel> placeOrder(@Header("lancode") String lancode,
                                     @Field("user_id") String user_id,
                                     @Field("couponCode") String couponCode,
                                     @Field("confirmOrderCode") String confirmOrderCode,
                                     @Field("contactName") String contactName,
                                     @Field("building_no") String building_no,
                                     @Field("street_no") String street_no,
                                     @Field("zone") String zone,
                                     @Field("streetAddress") String streetAddress,
                                     @Field("city") String city,
                                     @Field("mobile") String mobile,
                                     @Field("country_id") String country_id,
                                     @Field("country_name") String country_name,
                                     @Field("payment_method") String payment_method,
                                     @Field("expected_delivery_date") String expected_delivery_date,
                                     @Field("delivery_estimate_time") String delivery_estimate_time,
                                     @Field("cart_message") String cart_message,
                                     @Field("store_id") String store_id,
                                     @Field("transactionId") String transactionId);


    @FormUrlEncoded
    @POST("user/change_user_store")
    Call<LoginModel> changeUserStore(@Header("lancode") String lancode,
                                     @Field("user_id") String user_id,
                                     @Field("store_id") String store_id);

    @FormUrlEncoded
    @POST("user/read_notification")
    Call<GeneralModel> readNotification(@Header("lancode") String lancode,
                                        @Field("notificationId") String notificationId);

}
