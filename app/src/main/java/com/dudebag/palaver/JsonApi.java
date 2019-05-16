package com.dudebag.palaver;

//import java.util.List;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
//import retrofit2.http.GET;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface JsonApi {

    //@GET("/api/message/get")
    //@GET("posts")
    //Call<List<Post>> getPosts();

    @POST ("/api/user/register")
    Call<Post> createPost(@Body Post post);

    /*@FormUrlEncoded
    @POST ("/api/user/validate")
    Call<Post> createPost(
            @Field("username") String username,
            @Field("password") String password,
            @Field("newPassword") String newPassword
    );*/


}
