package com.dudebag.palaver;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface JsonApi {

    //@GET("/api/message/get")
    @GET("posts")
    Call<List<Post>> getPosts();

    @POST ("/api/user/register")
    Call<Post> createPost(@Body Post post);


}
