package com.example.project_fakebook.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.project_fakebook.adapter.PostAdapter;
import com.example.project_fakebook.databinding.ActivityUserProfileBinding;
import com.example.project_fakebook.interfaces.ApiService;
import com.example.project_fakebook.listeners.onClickListener;
import com.example.project_fakebook.model.ApiResponseGetUserProfile;
import com.example.project_fakebook.model.Friend;
import com.example.project_fakebook.model.FriendRequests;
import com.example.project_fakebook.model.Post;
import com.example.project_fakebook.model.RequestFriends;
import com.example.project_fakebook.model.Result;
import com.example.project_fakebook.model.UserInfo;
import com.example.project_fakebook.utilities.Constants;
import com.example.project_fakebook.utilities.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserProfileActivity extends AppCompatActivity  implements onClickListener {
    private ActivityUserProfileBinding binding;
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    private PostAdapter postAdapter;
    private ArrayList<Post> posts;
    private UserInfo userInfo;
    private int userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initValue();
        valueAPI();
        setListener();

    }

    private void setListener() {
        binding.imageBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        binding.btnAddFriend.setOnClickListener(v -> {

            String Token = "Bearer " + preferenceManager.getString("data");
            String Accept = "application/json";
            Call<Result> resultCall = apiService.addFriend(Token, Accept, userId);
            resultCall.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(getApplicationContext(), "Gửi lời mời kết bạn thành công", Toast.LENGTH_SHORT).show();
                        binding.btnAddFriend.setVisibility(View.GONE);
                        binding.btnRejectFriend.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                        Log.e("API_POST", "Fail with code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    Log.e("API_POST", String.valueOf(t));
                }
            });
        });

        binding.btnMess.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra(Constants.KEY_USER_ID, userId);
            startActivity(intent);
        });

        binding.btnRemoveFriend.setOnClickListener(v -> {
            String Token = "Bearer " + preferenceManager.getString("data");
            String Accept = "application/json";
            Call<Result> resultCall = apiService.rejectFriend(Token, Accept, userId);
            resultCall.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Result result = response.body();
                        Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.btnAddFriend.setVisibility(View.VISIBLE);
                        binding.btnRemoveFriend.setVisibility(View.GONE);
                        binding.btnMess.setVisibility(View.GONE);
                    }else {
                        Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                        Log.e("API_POST", "Fail with code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    Log.e("API_POST", String.valueOf(t));
                }
            });
        });

        binding.btnRejectFriend.setOnClickListener(v -> {
            String Token = "Bearer " + preferenceManager.getString("data");
            String Accept = "application/json";
            Call<Result> resultCall = apiService.removeFriend(Token, Accept, userId);
            resultCall.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Result result = response.body();
                        Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.btnAddFriend.setVisibility(View.VISIBLE);
                        binding.btnRejectFriend.setVisibility(View.GONE);
                    }else {
                        Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                        Log.e("API_POST", "Fail with code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    Log.e("API_POST", String.valueOf(t));
                }
            });
        });

        binding.btnAcceptFriend.setOnClickListener(v -> {
            String Token = "Bearer " + preferenceManager.getString("data");
            String Accept = "application/json";
            Call<Result> resultCall = apiService.acceptFriend(Token, Accept, userId);
            resultCall.enqueue(new Callback<Result>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(getApplicationContext(), "Thành công", Toast.LENGTH_SHORT).show();
                        binding.btnAddFriend.setVisibility(View.GONE);
                        binding.btnRemoveFriend.setVisibility(View.VISIBLE);
                        binding.btnMess.setVisibility(View.VISIBLE);
                        binding.btnAcceptFriend.setVisibility(View.GONE);
                        binding.btnNotAcceptFriend.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                        Log.e("API_POST", "Fail with code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    Log.e("API_POST", String.valueOf(t));
                }
            });
        });

        binding.btnNotAcceptFriend.setOnClickListener(v -> {
            String Token = "Bearer " + preferenceManager.getString("data");
            String Accept = "application/json";
            Call<Result> resultCall = apiService.rejectFriend(Token, Accept, userId);
            resultCall.enqueue(new Callback<Result>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(getApplicationContext(), "Thành công", Toast.LENGTH_SHORT).show();
                        binding.btnAddFriend.setVisibility(View.VISIBLE);
                        binding.btnAcceptFriend.setVisibility(View.GONE);
                        binding.btnNotAcceptFriend.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                        Log.e("API_POST", "Fail with code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    Log.e("API_POST", String.valueOf(t));
                }
            });
        });
    }

    private void valueAPI() {
        binding.loadingProgressBar.setVisibility(View.VISIBLE);
        String Token = "Bearer " + preferenceManager.getString("data");
        String Accept = "application/json";
        Call<ApiResponseGetUserProfile> UserProfile = apiService.getUserProfile(Token, Accept, userId);
        UserProfile.enqueue(new Callback<ApiResponseGetUserProfile>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponseGetUserProfile> call, Response<ApiResponseGetUserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseGetUserProfile apiResponse = response.body();
                    posts = apiResponse.getData().getPosts();
                    userInfo = apiResponse.getData().getInfo();
                    postAdapter.setPosts(posts);
                    binding.postsRecView.setAdapter(postAdapter);

                    if (userInfo.getAvatar() == null || userInfo.getAvatar().isEmpty()) {
                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(Constants.KEY_IMAGE_DEFAULT)
                                .into(binding.profileImageUser);
                    } else {
                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(Constants.KEY_API + "/storage/" + userInfo.getAvatar())
                                .into(binding.profileImageUser);
                    }

                    binding.txtUserName.setText(userInfo.getFirstName() + " " + userInfo.getLastName());
                    binding.txtIntroduceName.setText(userInfo.getFirstName() + " " + userInfo.getLastName());
                    binding.txtIntroduceBirt.setText(userInfo.getBirth_date());
                    binding.txtIntroduceAddress.setText(userInfo.getAddress());
                    binding.txtIntroduceNumPhone.setText(userInfo.getPhone());
                    if (Objects.equals(userInfo.getGender(), "1")) {
                        binding.txtIntroduceGender.setText("Nam");
                    } else if (Objects.equals(userInfo.getGender(), "0")) {
                        binding.txtIntroduceGender.setText("Nữ");
                    }
                    else {
                        binding.txtIntroduceGender.setText(null);
                    }

                    if (String.valueOf(userInfo.getId()).equals(preferenceManager.getString("userID"))) {
                        binding.btnAddFriend.setVisibility(View.GONE);
                    }
                    ArrayList<RequestFriends> requestFriends = apiResponse.getData().getRequestFriends();
                    requestFriends.forEach(requestFriend -> {
                        if(String.valueOf(requestFriend.getPivot().getUser_id()).equals(preferenceManager.getString("userID")) && requestFriend.getPivot().getFriend_id() == userId) {
                            binding.btnAddFriend.setVisibility(View.GONE);
                            binding.btnAcceptFriend.setVisibility(View.VISIBLE);
                            binding.btnNotAcceptFriend.setVisibility(View.VISIBLE);
                        }
                    });

                    ArrayList<FriendRequests> friendRequests = apiResponse.getData().getFriendRequests();
                    friendRequests.forEach(friendRequest -> {
                        if(String.valueOf(friendRequest.getPivot().getFriend_id()).equals(preferenceManager.getString("userID")) && friendRequest.getPivot().getUser_id() == userId) {
                            binding.btnAddFriend.setVisibility(View.GONE);
                            binding.btnRejectFriend.setVisibility(View.VISIBLE);
                        }
                    });

                    ArrayList<Friend> friends = apiResponse.getData().getFriends();
                    friends.forEach(friend -> {
                        if(String.valueOf(friend.getPivot().getFriend_id()).equals(preferenceManager.getString("userID"))) {
                            binding.btnAddFriend.setVisibility(View.GONE);
                        } else if (friend.getPivot().getFriend_id() == userId) {
                            binding.btnAddFriend.setVisibility(View.GONE);
                            binding.btnRemoveFriend.setVisibility(View.VISIBLE);
                            binding.btnMess.setVisibility(View.VISIBLE);
                        }
                    });

                    binding.loadingProgressBar.setVisibility(View.GONE);

                } else {
                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                    Log.e("API_POST", "Fail with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseGetUserProfile> call, Throwable t) {
                Log.e("API_POST", String.valueOf(t));
            }
        });
    }

    private void initValue() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(20, java.util.concurrent.TimeUnit.SECONDS);

        OkHttpClient httpClient = httpClientBuilder.build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy MM dd HH:mm:ss")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.KEY_API + "/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();
        apiService = retrofit.create(ApiService.class);

        postAdapter = new PostAdapter(getApplicationContext(), this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.KEY_USER_ID)) {
            userId = intent.getIntExtra(Constants.KEY_USER_ID, 0);

        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getApplicationContext(), PostInfoActivity.class);
        intent.putExtra(Constants.KEY_POST_ID, posts.get(position).getId());
        intent.putExtra(Constants.KEY_POST_INDEX, position);
        startActivity(intent);
    }
}