package com.bestspa.spa.client.API;

import com.bestspa.spa.client.Model.Booking;
import com.bestspa.spa.client.Model.ServiceUserModel;
import com.bestspa.spa.client.Model.ServiceModel;
import com.bestspa.spa.client.Model.SubServiceModel;
import com.bestspa.spa.client.Model.User;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIInterface {

    @POST("GetAllUser")
    Call<ArrayList<User>> GetAllUser();

    @POST("UserLogin")
    Call<User> UserLogin(@Query("email") String email , @Query("password") String password);

    @POST("UserRegistration")
    Call<User> UserRegistration(@Body User user);

    @POST("GoogleSignIn")
    Call<User> GoogleSignIn(@Query("email") String email,@Query("password") String password,@Query("signupwith") String signupwith);

    @GET("GetServices")
    Call<ArrayList<ServiceModel>> GetServices();

    @GET("GetServiceById")
    Call<ServiceModel> GetServiceById(@Query("serviceId") String serviceId);


    @POST("GetServiceAndUserById")
    Call<ServiceUserModel> GetServiceAndUserById(@Query("serviceId") String serviceId,@Query("userId") String userId);

    @GET("GetServicesPrivate")
    Call<ArrayList<ServiceModel>> GetServicesPrivate(@Query("userId") String userId);

    @POST("CreateService")
    Call<SubServiceModel> CreateService(@Body SubServiceModel serviceModel, @Query("seviceCategoryId") String seviceCategoryId);

    @POST("UpdateService")
    Call<SubServiceModel> UpdateService(@Body SubServiceModel serviceModel, @Query("seviceCategoryId") String seviceCategoryId, @Query("serviceId") String serviceId);


    @POST("UpdateStaffUser")
    Call<User> UpdateStaffUser(@Body User user, @Query("userId") String userId);

    @Multipart
    @POST("UploadImages")
    Call<Object> postImage(@Part List<MultipartBody.Part> file, @Part("upload") RequestBody name,@Query("userid") String userid);

    @POST("DeleteImage")
    Call<Object> deleteImage(@Query("imageName") String imageName, @Query("userId") String userId);

    @POST("GetUser")
    Call<User> GetUser(@Query("userId") String userId);

    @POST("DeleteService")
    Call<Object> DeleteService(@Query("serviceCatId") String serviceCatId , @Query("serviceId") String serviceId);

    @POST("Booking")
    Call<Booking> Booking(@Body Booking booking);

    @POST("GetBooking")
    Call<ArrayList<Booking>> GetBooking(@Query("userId") String userId);

    @POST("GetMerchantBooking")
    Call<ArrayList<Booking>> GetMerchantBooking(@Query("merchantId") String merchantId);

    @POST("CancelBooking")
    Call<Object> CancelBooking(@Query("bookingId") String bookingId);

    @POST("UpdateBooking")
    Call<Booking> UpdateBooking(@Body Booking booking);

    @POST("UpdateToken")
    Call<User> UpdateToken(@Query("token") String token, @Query("userId") String userID);
}
