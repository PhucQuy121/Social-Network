package com.example.project_fakebook.framents;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import com.example.project_fakebook.activities.ChangeInfoActivity;
import com.example.project_fakebook.activities.PostInfoActivity;
import com.example.project_fakebook.adapter.PostAdapter;
import com.example.project_fakebook.databinding.FragmentProfileBinding;
import com.example.project_fakebook.interfaces.ApiService;
import com.example.project_fakebook.listeners.onClickListener;
import com.example.project_fakebook.model.ApiResponseGetUserProfile;
import com.example.project_fakebook.model.Post;
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

public class ProfileFragment extends Fragment  implements onClickListener {
    private FragmentProfileBinding binding;
    private PostAdapter postAdapter;
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    private ArrayList<Post> posts;
    private UserInfo userInfo;
    private Context context;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        initValue();
        valueAPI();
        setListener();

        return binding.getRoot();
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

        postAdapter = new PostAdapter(getContext(), this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void valueAPI() {
        binding.loadingProgressBar.setVisibility(View.VISIBLE);
        String Token = "Bearer " + preferenceManager.getString("data");
        String Accept = "application/json";
        Call<ApiResponseGetUserProfile> UserProfile = apiService.getUserProfile(Token, Accept, Integer.parseInt(preferenceManager.getString("userID")));
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
                    binding.postsRecView.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.loadingProgressBar.setVisibility(View.GONE);
//
                    if (userInfo.getAvatar() == null || userInfo.getAvatar().isEmpty()) {
                        Glide.with(context)
                                .asBitmap()
                                .load(Constants.KEY_IMAGE_DEFAULT)
                                .into(binding.profileImageUser);
                    }
                    else {
                        Glide.with(context)
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

                } else {
                    Toast.makeText(requireContext(), "Fail", Toast.LENGTH_SHORT).show();
                    Log.e("API_POST", "Fail with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseGetUserProfile> call, Throwable t) {
                Log.e("API_POST", String.valueOf(t));
            }
        });
    }


    private void setListener() {
        binding.btnChangeUserInfo.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ChangeInfoActivity.class));
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
