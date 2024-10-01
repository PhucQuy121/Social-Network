package com.example.project_fakebook.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.project_fakebook.R;
import com.example.project_fakebook.adapter.FriendAdapter;
import com.example.project_fakebook.adapter.PostAdapter;
import com.example.project_fakebook.adapter.SuggestFriendAdapter;
import com.example.project_fakebook.databinding.ActivitySearchBinding;
import com.example.project_fakebook.interfaces.ApiService;
import com.example.project_fakebook.listeners.onClickListener;
import com.example.project_fakebook.model.ApiResponseGetFriend;
import com.example.project_fakebook.model.ApiResponseGetSearchUser;
import com.example.project_fakebook.model.ApiResponseGetSuggestFriend;
import com.example.project_fakebook.model.Friend;
import com.example.project_fakebook.model.UserInfo;
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

public class SearchActivity extends AppCompatActivity implements onClickListener {
    private ActivitySearchBinding binding;
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    private SuggestFriendAdapter suggestFriendAdapter;
    private ArrayList<UserInfo> friends;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initValue();
        setListener();
    }

    private void setListener() {
        binding.imageBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        binding.imageSearch.setOnClickListener(v -> {
            if(binding.edtValueSearch.getText().toString().trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Vui lòng nhập giá trị tìm kiếm", Toast.LENGTH_SHORT).show();
            } else {
                valueAPI();
            }
        });
    }

    private void valueAPI() {
        String Token = "Bearer " + preferenceManager.getString("data");
        String Accept = "application/json";
        Call<ApiResponseGetSuggestFriend> Result = apiService.search(Accept, Token, binding.edtValueSearch.getText().toString().trim());
        Result.enqueue(new Callback<ApiResponseGetSuggestFriend>() {
            @Override
            public void onResponse(Call<ApiResponseGetSuggestFriend> call, Response<ApiResponseGetSuggestFriend> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseGetSuggestFriend apiResponse = response.body();
                    friends = apiResponse.getData();
                    suggestFriendAdapter.setFriends(friends);
                    binding.listFriendRecycelerView.setAdapter(suggestFriendAdapter);
                    binding.edtValueSearch.setText(null);
                    binding.edtValueSearch.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.edtValueSearch.getWindowToken(), 0);

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
//        Toast.makeText(this, friends.get(position).getId()+"", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        intent.putExtra(Constants.KEY_USER_ID, friends.get(position).getId());
        startActivity(intent);
    }
}