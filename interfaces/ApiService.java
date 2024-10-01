package com.example.project_fakebook.interfaces;

import com.example.project_fakebook.model.ApiResponse;
import com.example.project_fakebook.model.ApiResponseCommentPost;
import com.example.project_fakebook.model.ApiResponseGetConversation;
import com.example.project_fakebook.model.ApiResponseGetFriend;
import com.example.project_fakebook.model.ApiResponseGetFriendResquest;
import com.example.project_fakebook.model.ApiResponseGetPost;
import com.example.project_fakebook.model.ApiResponseGetSearchUser;
import com.example.project_fakebook.model.ApiResponseGetSuggestFriend;
import com.example.project_fakebook.model.ApiResponseGetUser;
import com.example.project_fakebook.model.ApiResponseGetUserProfile;
import com.example.project_fakebook.model.ApiResponsePostMessage;
import com.example.project_fakebook.model.ApiResponseReaction;
import com.example.project_fakebook.model.Post;
import com.example.project_fakebook.model.Reaction;
import com.example.project_fakebook.model.Result;
import com.example.project_fakebook.model.UserInfo;
import com.example.project_fakebook.model.apiResponseForgetPassword;
import com.example.project_fakebook.model.user;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @FormUrlEncoded
    @POST("register")
    Call<user> sign_up(
            @Header("Accept") String accept,
            @Field("email") String email,
            @Field("password") String password,
            @Field("last_name") String last_name,
            @Field("first_name") String first_name,
            @Field("phone") String phone
    );
    @FormUrlEncoded
    @POST("login")
    Call<user> sign_in(
            @Header("Accept") String accept,
            @Field("email") String email,
            @Field("password") String password
    );

    @POST("logout")
    Call<Result> log_out(
            @Header("Authorization") String Token,
            @Header("Accept") String accept
    );

    @Multipart
    @POST("posts/add")
    Call<Result> uploadImage(
            @Header("Authorization") String Token,
            @Header("Accept") String accept,
            @Part("content") RequestBody content,
            @Part("status") RequestBody status,
            @Part MultipartBody.Part image
    );

    @GET("posts")
    Call<ApiResponse> getPost(
            @Header("Authorization") String Token,
            @Header("Accept") String accept
    );

    @GET("get-auth")
    Call<ApiResponseGetUser> getUser(
            @Header("Authorization") String Token
    );

    @FormUrlEncoded
    @POST("reaction")
    Call<ApiResponseReaction> reaction(
            @Header("Authorization") String Token,
            @Header("Accept") String accept,
            @Field("type") String type,
            @Field("post_id") String post_id
    );

    @GET("comments/{id}")
    Call<ApiResponseCommentPost> getComment(
            @Header("Authorization") String Token,
            @Header("Accept") String accept,
            @Path("id") int id
    );
    @FormUrlEncoded
    @POST("comments/{id}/add")
    Call<ApiResponseCommentPost> postComment(
            @Header("Authorization") String Token,
            @Header("Accept") String accept,
            @Path("id") int id,
            @Field("comment") String comment
    );

    @GET("profile/{id}")
    Call<ApiResponseGetUserProfile> getUserProfile(
            @Header("Authorization") String Token,
            @Header("Accept") String accept,
            @Path("id") int id
    );

    @GET("posts/{id}/show")
    Call<ApiResponseGetPost> getPost(
            @Header("Authorization") String Token,
            @Header("Accept") String accept,
            @Path("id") int id
    );

    @Multipart
    @POST("profile/user/update")
    Call<ApiResponseGetUser> updateUser(
            @Header("Authorization") String Token,
            @Header("Accept") String accept,
            @Part("last_name") RequestBody last_name,
            @Part("first_name") RequestBody first_name,
            @Part("phone") RequestBody phone,
            @Part("cover_image") RequestBody cover_image,
            @Part("gender") RequestBody gender,
            @Part("birth_date") RequestBody birth_date,
            @Part("address") RequestBody address
    );


    @Multipart
    @POST("profile/user/update")
    Call<ApiResponseGetUser> updateUserHasImg(
            @Header("Authorization") String Token,
            @Header("Accept") String accept,
            @Part("last_name") RequestBody last_name,
            @Part("first_name") RequestBody first_name,
            @Part("phone") RequestBody phone,
            @Part("cover_image") RequestBody cover_image,
            @Part("gender") RequestBody gender,
            @Part("birth_date") RequestBody birth_date,
            @Part("address") RequestBody address,
            @Part MultipartBody.Part avatar
    );

    @GET("friend-request")
    Call<ApiResponseGetFriendResquest> friendRequest(
            @Header("Authorization") String Token,
            @Header("Accept") String accept
    );

    @POST("send-request-to/{id}")
    Call<Result> addFriend(
            @Header("Authorization") String Token,
            @Header("Accept") String accept,
            @Path("id") int id
    );

    @GET("friends")
    Call<ApiResponseGetFriend> listFriend(
            @Header("Authorization") String Token,
            @Header("Accept") String accept
    );

    @GET("suggest-friends")
    Call<ApiResponseGetSuggestFriend> listSuggestFriend(
            @Header("Authorization") String Token,
            @Header("Accept") String accept
    );

    @POST("accept/{id}")
    Call<Result> acceptFriend(
            @Header("Authorization") String Token,
            @Header("Accept") String accept,
            @Path("id") int id
    );

    @POST("reject/{id}")
    Call<Result> rejectFriend(
            @Header("Authorization") String Token,
            @Header("Accept") String accept,
            @Path("id") int id
    );

    @POST("remove/{id}")
    Call<Result> removeFriend(
            @Header("Authorization") String Token,
            @Header("Accept") String accept,
            @Path("id") int id
    );


    @POST("messages")
    Call<ApiResponseGetConversation> getConversation(
            @Header("Authorization") String Token,
            @Header("Accept") String accept
    );

    @POST("messages/{id}")
    Call<ApiResponseGetConversation> getMessage(
            @Header("Authorization") String Token,
            @Header("Accept") String accept,
            @Path("id") int id
    );

    @FormUrlEncoded
    @POST("messages/store/{id}")
    Call<ApiResponsePostMessage> postMessage(
            @Header("Authorization") String Token,
            @Header("Accept") String accept,
            @Path("id") int id,
            @Field("message") String message
    );

    @FormUrlEncoded
    @POST("forget-password")
    Call<apiResponseForgetPassword> forgetPassword(
            @Header("Accept") String accept,
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("reset-password")
    Call<Result> resetPassword(
            @Header("Accept") String accept,
            @Field("token") String token,
            @Field("password") String password
    );

    @GET("/api/search")
    Call<ApiResponseGetSuggestFriend> search(
            @Header("Accept") String accept,
            @Header("Authorization") String authorization,
            @Query("search") String query
    );
}
