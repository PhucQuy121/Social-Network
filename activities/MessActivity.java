package com.example.project_fakebook.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.project_fakebook.R;
import com.example.project_fakebook.adapter.RecentConversationsAdapter;
import com.example.project_fakebook.adapter.ViewPagerAdapter;
import com.example.project_fakebook.databinding.ActivityMessBinding;
import com.example.project_fakebook.interfaces.ApiService;
import com.example.project_fakebook.listeners.ConversionListener;
import com.example.project_fakebook.listeners.onClickListener;
import com.example.project_fakebook.model.ApiResponse;
import com.example.project_fakebook.model.ApiResponseGetConversation;
import com.example.project_fakebook.model.Conversation;
import com.example.project_fakebook.model.UserList;
import com.example.project_fakebook.model.UserProfile;
import com.example.project_fakebook.utilities.Constants;
import com.example.project_fakebook.utilities.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MessActivity extends AppCompatActivity implements onClickListener {
    private ActivityMessBinding binding;
    private PreferenceManager preferenceManager;
    private ArrayList<UserList> conversions;
    private RecentConversationsAdapter conversationsAdapter;
    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initValue();
        valueAPI();
        setListener();
    }

    private void valueAPI() {
        binding.progressBar.setVisibility(View.VISIBLE);
        String Token = "Bearer " + preferenceManager.getString("data");
        String Accept = "application/json";
        Call<ApiResponseGetConversation> Conversation = apiService.getConversation(Token, Accept);
        Conversation.enqueue(new Callback<ApiResponseGetConversation>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ApiResponseGetConversation> call, Response<ApiResponseGetConversation> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseGetConversation apiResponse = response.body();
                    conversions = apiResponse.getData().getUserList();
                    conversationsAdapter.setUserLists(conversions);
                    binding.conversionsRecyclerView.setAdapter(conversationsAdapter);
                    binding.conversionsRecyclerView.smoothScrollToPosition(0);
                    binding.conversionsRecyclerView.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);

                } else {
                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                    Log.e("API_POST", "Fail with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseGetConversation> call, Throwable t) {

            }
        });
    }

    private void setListener() {
        binding.imageSignOut.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.fabNewChat.setOnClickListener(v -> {
            startActivity(new Intent(MessActivity.this, UserActivity.class));
        });
    }

    private void initValue() {
        preferenceManager = new PreferenceManager(getApplicationContext());
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

        conversationsAdapter = new RecentConversationsAdapter(getApplicationContext(), this);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER_ID, conversions.get(position).getUser_id());
        startActivity(intent);
    }
}