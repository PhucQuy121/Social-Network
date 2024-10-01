package com.example.project_fakebook.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.project_fakebook.R;
import com.example.project_fakebook.adapter.FriendAdapter;
import com.example.project_fakebook.adapter.SuggestFriendAdapter;
import com.example.project_fakebook.databinding.ActivitySuggestFriendBinding;
import com.example.project_fakebook.interfaces.ApiService;
import com.example.project_fakebook.listeners.onClickListener;
import com.example.project_fakebook.model.ApiResponseGetFriend;
import com.example.project_fakebook.model.ApiResponseGetSuggestFriend;
import com.example.project_fakebook.model.Friend;
import com.example.project_fakebook.model.UserInfo;
import com.example.project_fakebook.model.UserProfile;
import com.example.project_fakebook.utilities.Constants;
import com.example.project_fakebook.utilities.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SuggestFriendActivity extends AppCompatActivity implements onClickListener {;

    private ActivitySuggestFriendBinding binding;
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    private SuggestFriendAdapter suggestFriendAdapter;
    private ArrayList<UserInfo> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuggestFriendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initValue();
        valueAPI();
        setListener();

    }

    private void setListener() {
        binding.imageBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
    }

    private void valueAPI() {
        String Token = "Bearer " + preferenceManager.getString("data");
        String Accept = "application/json";
        Call<ApiResponseGetSuggestFriend> UserProfile = apiService.listSuggestFriend(Token, Accept);
        UserProfile.enqueue(new Callback<ApiResponseGetSuggestFriend>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponseGetSuggestFriend> call, Response<ApiResponseGetSuggestFriend> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseGetSuggestFriend apiResponse = response.body();
                    friends = apiResponse.getData();
                    suggestFriendAdapter.setFriends(friends);
                    binding.listFriendRecycelerView.setAdapter(suggestFriendAdapter);

                } else {
                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                    Log.e("API_POST", "Fail with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseGetSuggestFriend> call, Throwable t) {
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

        suggestFriendAdapter = new SuggestFriendAdapter(getApplicationContext(), this);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        intent.putExtra(Constants.KEY_USER_ID, friends.get(position).getId());
        startActivity(intent);
    }
}