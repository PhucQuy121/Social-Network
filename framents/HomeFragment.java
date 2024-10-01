package com.example.project_fakebook.framents;

import android.app.Activity;
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
import com.example.project_fakebook.activities.MainActivity;
import com.example.project_fakebook.activities.PostActivity;
import com.example.project_fakebook.activities.PostInfoActivity;
import com.example.project_fakebook.adapter.PostAdapter;
import com.example.project_fakebook.databinding.FragmentHomeBinding;
import com.example.project_fakebook.interfaces.ApiService;
import com.example.project_fakebook.listeners.onClickListener;
import com.example.project_fakebook.model.ApiResponse;
import com.example.project_fakebook.model.Post;
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

public class HomeFragment extends Fragment  implements onClickListener{
    private static final String ARG_POSITION = "position";
    private static final String ARG_OBJECT = "post";
    private static final String ARG_OBJECT_USER_PROFILE = "userProfile";
    private FragmentHomeBinding binding;
    private PostAdapter postAdapter;
    private MainActivity mMainActivity;

    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private ArrayList<Post> posts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initValue();
        valueAPI();
        setListener();
        Glide .with(requireContext())
                .load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSaNGlEMBmyMD6uXamHaxzdyoPcwTPybEkobg&usqp=CAU")
                .into(binding.profileImageUser);


        return binding.getRoot();
    }

    private void valueAPI() {
        binding.loadingProgressBar.setVisibility(View.VISIBLE);
        String Token = "Bearer " + preferenceManager.getString("data");
        String Accept = "application/json";
        Call<ApiResponse> Post = apiService.getPost(Token, Accept);
        Post.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    posts = apiResponse.getData();
                    postAdapter.setPosts(posts);
                    binding.postsRecView.setAdapter(postAdapter);
                    binding.postsRecView.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.loadingProgressBar.setVisibility(View.GONE);

                } else {
                    Toast.makeText(requireContext(), "Fail", Toast.LENGTH_SHORT).show();
                    Log.e("API_POST", "Fail with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
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

        mMainActivity = (MainActivity) getActivity();

        postAdapter = new PostAdapter(getContext(), this);
    }

    private void setListener() {
        binding.textAddPost.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PostActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            mMainActivity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
        });

    }

    public void updateData(int postIndex, int postId) {
        postAdapter.updateData(postIndex, postId);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), PostInfoActivity.class);
        intent.putExtra(Constants.KEY_POST_ID, posts.get(position).getId());
        intent.putExtra(Constants.KEY_POST_INDEX, position);
        ((Activity) requireContext()).startActivityForResult(intent, 1);
    }
}
