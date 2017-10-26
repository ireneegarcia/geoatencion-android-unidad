package com.example.irene.geoatencionunidad.Remote;

import com.example.irene.geoatencionunidad.Model.Alarma;
import com.example.irene.geoatencionunidad.Model.Alarmas;
import com.example.irene.geoatencionunidad.Model.CategoriaServicios;
import com.example.irene.geoatencionunidad.Model.FirebaseToken;
import com.example.irene.geoatencionunidad.Model.Logs;
import com.example.irene.geoatencionunidad.Model.Networks;
import com.example.irene.geoatencionunidad.Model.Solicitudes;
import com.example.irene.geoatencionunidad.Model.Users;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface APIService {

    String BASE_URL = "http://192.168.0.150:3000/";
    //String BASE_URL = "http://10.0.0.41:3000/";


    //@Headers("Content-Type: application/json")
    @POST("api/auth/signin")
    @FormUrlEncoded
    Call<Users> login(@Field("usernameOrEmail") String username,
                       @Field("password") String password);

    @POST("/api/firebasetokens")
    @FormUrlEncoded
    Call<FirebaseToken> registerToken(@Field("token") String token,
                                      @Field("userId") String user);

    @POST("/api/networks/{networkId}/new-position")
    @FormUrlEncoded
    Call<Networks> updateNetworkLocation(@Path("networkId") String networkId,
                                       @Field("lat") String lat,
                                       @Field("lng") String lng,
                                       @Field("address") String address);

    @POST("/api/logs")
    @FormUrlEncoded
    Call<Logs> createLog(@Field("description") String description,
                         @Field("alarm") String alarm,
                         @Field("network") String network,
                         @Field("client") String client,
                         @Field("organism") String organism);

    @GET("api/networks")
    Call<List<Networks>> listNetworks();

    @GET("/api/users/{userId}")
    Call<Users> getClient(@Path("userId") String userId);

    @GET("api/alarms")
    Call<List<Alarmas>> listAlarms();

    @PUT("api/networks/{networkId}")
    Call<Networks> updateNetwork(@Path("networkId") String networkId,
                                 @Body Networks network);










    @POST("api/alarms")
    @FormUrlEncoded
    Call<Alarma> createAlarm(@Field("categoryService") String categoryService,
                        @Field("status") String status,
                        @Field("latitude") String latitude,
                        @Field("longitude") String longitude,
                        @Field("address") String address,
                        @Field("organism") String organism,
                        @Field("user") String user);





    @GET("api/categoriaservicios")
    Call<List<CategoriaServicios>> listCategories();

    @GET("api/solicituds")
    Call<List<Solicitudes>> listSolicituds();



    @PUT("/api/alarms/{alarmId}")
    Call<Alarma> updateAlarm(@Path("alarmId") String alarmId,
                            @Body Alarma alarma);


    class Factory {
        private static APIService service;

        public static APIService getIntance() {
            if (service == null) {
                //inicializacion Retrofit
                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(120, TimeUnit.SECONDS)
                        .writeTimeout(120, TimeUnit.SECONDS)
                        .readTimeout(120, TimeUnit.SECONDS)
                        .build();

                Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(gson)).baseUrl(BASE_URL).client(client).build();
                service = retrofit.create(APIService.class);
                return service;
            } else {
                return service;
            }
        }

    }

}