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


    @POST ("api/user/register")
    Call<Post> processRegistration(@Body Post post);

    @POST ("api/user/validate")
    Call<Post> processLogin(@Body Post post);

    @POST ("api/message/get")
    Call<PostAnswer> getMessages(@Body PostAnswer post);

    @POST ("api/friends/get")
    Call<Post> getFriends(@Body Post post);

    @POST ("api/friends/add")
    Call<Post> addFriends(@Body Post post);

    @POST ("api/friends/remove")
    Call<Post> deleteFriends(@Body Post post);

    @POST ("api/message/send")
    Call<PostMessage> sendMessage(@Body PostMessage post);

    @POST ("api/user/pushtoken")
    Call<Pushtoken> refreshToken(@Body Pushtoken pushtoken);

    @POST ("api/user/password")
    Call<PostChange> changePassword(@Body PostChange postChange);

}
