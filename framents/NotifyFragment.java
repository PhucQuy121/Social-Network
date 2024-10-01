package com.example.project_fakebook.framents;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.project_fakebook.R;
import com.example.project_fakebook.activities.FriendActivity;
import com.example.project_fakebook.activities.SearchActivity;
import com.example.project_fakebook.activities.SuggestFriendActivity;
import com.example.project_fakebook.adapter.PostAdapter;
import com.example.project_fakebook.adapter.requestFriendAdapter;
import com.example.project_fakebook.databinding.FragmentNotifyBinding;
import com.example.project_fakebook.databinding.FragmentProfileBinding;
import com.example.project_fakebook.interfaces.ApiService;
import com.example.project_fakebook.model.ApiResponseGetFriendResquest;
import com.example.project_fakebook.model.ApiResponseGetUserProfile;
import com.example.project_fakebook.model.FriendRequests;
import com.example.project_fakebook.model.Post;
import com.example.project_fakebook.model.RequestFriends;
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

public class NotifyFragment extends Fragment {
    private FragmentNotifyBinding binding;
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    private requestFriendAdapter requestFriendAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotifyBinding.inflate(inflater, container, false);

        initValue();
        valueAPI();
        setListener();

        return binding.getRoot();
    }

    private void setListener() {
        binding.txtFriend.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), FriendActivity.class));
        });
        binding.txtSuggestFriend.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SuggestFriendActivity.class));
        });

        binding.imageSearch.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SearchActivity.class));
        });
    }

    private void valueAPI() {
        String Token = "Bearer " + preferenceManager.getString("data");
        String Accept = "application/json";
        Call<ApiResponseGetFriendResquest> UserProfile = apiService.friendRequest(Token, Accept);
        UserProfile.enqueue(new Callback<ApiResponseGetFriendResquest>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponseGetFriendResquest> call, Response<ApiResponseGetFriendResquest> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseGetFriendResquest apiResponse = response.body();
                    ArrayList<FriendRequests> friendRequests = apiResponse.getData();
                    requestFriendAdapter.setFriendRequests(friendRequests);
                    binding.friendResRecycelerView.setAdapter(requestFriendAdapter);

                } else {
                    Toast.makeText(requireContext(), "Fail", Toast.LENGTH_SHORT).show();
                    Log.e("API_POST", "Fail with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseGetFriendResquest> call, Throwable t) {
                Log.e("API_POST", String.valueOf(t));
            }
        });
    }

    private void initValue() {
        preferenceManager = new PreferenceManager(requireContext());
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS); // Set connection timeout to 15 seconds
        httpClientBuilder.readTimeout(20, java.util.concurrent.TimeUnit.SECONDS);    // Set read timeout to 20 seconds

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

        requestFriendAdapter = new requestFriendAdapter(requireContext());

    }

}
