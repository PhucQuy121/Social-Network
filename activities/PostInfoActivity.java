package com.example.project_fakebook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.project_fakebook.R;
import com.example.project_fakebook.adapter.CommentAdapter;
import com.example.project_fakebook.adapter.ViewPagerAdapter;
import com.example.project_fakebook.databinding.ActivityChatBinding;
import com.example.project_fakebook.databinding.ActivityPostInfoBinding;
import com.example.project_fakebook.interfaces.ApiService;
import com.example.project_fakebook.model.ApiResponse;
import com.example.project_fakebook.model.ApiResponseCommentPost;
import com.example.project_fakebook.model.ApiResponseGetPost;
import com.example.project_fakebook.model.ApiResponseReaction;
import com.example.project_fakebook.model.Post;
import com.example.project_fakebook.model.Reaction;
import com.example.project_fakebook.model.UserProfile;
import com.example.project_fakebook.model.comment;
import com.example.project_fakebook.utilities.Constants;
import com.example.project_fakebook.utilities.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostInfoActivity extends AppCompatActivity {
    private ActivityPostInfoBinding binding;
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    private Post post;
    private ArrayList<comment> comments;
    private CommentAdapter commentAdapter;
    private int postId, postIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initValue();
        setValuePost();
        setListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListener() {
        binding.btnBackPost.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("finish", true);
            intent.putExtra(Constants.KEY_POST_ID, postId);
            intent.putExtra(Constants.KEY_POST_INDEX, postIndex);
            setResult(RESULT_OK, intent);
            finish();
        });

        binding.layoutLike.setOnClickListener(v -> {
            String Token = "Bearer " + preferenceManager.getString("data");
            String Accept = "application/json";
            Call<ApiResponseReaction> call = apiService.reaction(Token, Accept, "1", String.valueOf(post.getId()));
            call.enqueue(new Callback<ApiResponseReaction>() {

                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<ApiResponseReaction> call, @NonNull Response<ApiResponseReaction> response) {
                    if (response.isSuccessful() && response.body() != null) {

                        Drawable drawableLike = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_like);
                        binding.icLike.setImageDrawable(drawableLike);
                        binding.txtLike.setText(R.string.liked_text);
                        post.setLikes_count(post.getLikes_count() + 1);
                        binding.btnCountLike.setText(post.getLikes_count() + " người thích");
                        Drawable drawableDisLike = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_no_dis_like);
                        binding.icDisLike.setImageDrawable(drawableDisLike);
                    }
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onFailure(@NonNull Call<ApiResponseReaction> call, @NonNull Throwable t) {
                    Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_no_like);
                    binding.icLike.setImageDrawable(drawable);
                    post.setLikes_count(post.getLikes_count() - 1);
                    binding.btnCountLike.setText(post.getLikes_count() + " người thích");
                    binding.txtLike.setText(R.string.like_text);

                }
            });
        });

        binding.layoutDisLike.setOnClickListener(v -> {
            String Token = "Bearer " + preferenceManager.getString("data");
            String Accept = "application/json";
            Call<ApiResponseReaction> call = apiService.reaction(Token, Accept, "0", String.valueOf(post.getId()));
            call.enqueue(new Callback<ApiResponseReaction>() {

                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<ApiResponseReaction> call, @NonNull Response<ApiResponseReaction> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (binding.txtLike.getText().equals("Đã thích")) {
                            post.setLikes_count(post.getLikes_count() - 1);
                            binding.btnCountLike.setText(post.getLikes_count() + " người thích");
                        }
                        Drawable drawableLike = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_no_like);
                        binding.icLike.setImageDrawable(drawableLike);
                        binding.txtLike.setText(R.string.like_text);

                        Drawable drawableDisLike = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_dis_like);
                        binding.icDisLike.setImageDrawable(drawableDisLike);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponseReaction> call, @NonNull Throwable t) {
                    Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_no_dis_like);
                    binding.icDisLike.setImageDrawable(drawable);

                }
            });
        });

        binding.layoutSend.setOnClickListener(v -> {
            if(isValidSignUpDetails()) {
                postComment();
            }
        });

        binding.mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(binding.inputMessage.hasFocus()){
                    binding.inputMessage.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.inputMessage.getWindowToken(), 0);
                }
                return false;
            }
        });

    }

    private void postComment() {

        String Token = "Bearer " + preferenceManager.getString("data");
        String Accept = "application/json";
        Call<ApiResponseCommentPost> commentPost = apiService.postComment(Token, Accept, postId, binding.inputMessage.getText().toString().trim());
        commentPost.enqueue(new Callback<ApiResponseCommentPost>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ApiResponseCommentPost> call, Response<ApiResponseCommentPost> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseCommentPost apiResponse = response.body();
                    ArrayList<comment> commentPost = apiResponse.getData();
                    comment comment = commentPost.get(0);
                    comments.add(comment);
                    comments.sort((obj1, obj2) -> obj2.getCreated_at().compareTo(obj1.getCreated_at()));
                    commentAdapter.notifyDataSetChanged();
                    binding.cmtRecycelerView.smoothScrollToPosition(0);
                    binding.inputMessage.setText(null);
                    binding.inputMessage.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.inputMessage.getWindowToken(), 0);

                    post.setComments_count(post.getComments_count() + 1);
                    binding.btnCountCmt.setText(post.getComments_count() + " bình luận");

                } else {
                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                    Log.e("API_POST", "Fail with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseCommentPost> call, Throwable t) {
                Log.e("API_POST", String.valueOf(t));
            }
        });
    }

    private boolean isValidSignUpDetails() {
        if(binding.inputMessage.getText().toString().trim().isEmpty()) {
            showToast("Vui lòng nhập comment!!!");
            return  false;
        }
        else
            return true;
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    private void setValuePost() {
        binding.loadingProgressBar.setVisibility(View.VISIBLE);
        String Token = "Bearer " + preferenceManager.getString("data");
        String Accept = "application/json";
        Call<ApiResponseGetPost> PostGet = apiService.getPost(Token, Accept, postId);
        PostGet.enqueue(new Callback<ApiResponseGetPost>() {
            @Override
            public void onResponse(Call<ApiResponseGetPost> call, Response<ApiResponseGetPost> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseGetPost apiResponse = response.body();
                    post = apiResponse.getData().get(0);

                    if(post.getUser().getAvatar() == null) {
                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRK-cqV-aGe4jcDrt9MXAkp_uGOBFsBbmlLunQuD2xCt7pNXCdhVsL4ZTIqCRvakX81QTg&usqp=CAU")
                                .into(binding.profileImage);
                    } else {
                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(Constants.KEY_API + "/storage/" + post.getUser().getAvatar())
                                .into(binding.profileImage);
                    }

                    binding.btnProfile.setText(post.getUser().getFirstName() + " " + post.getUser().getLastName());
                    binding.txtContentPost.setText(post.getContent());

                    Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(Constants.KEY_API + "/storage/" + post.getImage())
                            .into(binding.imgPost);

                    binding.btnCountLike.setText(post.getLikes_count() + " người thích");
                    binding.btnCountCmt.setText(post.getComments_count() + " bình luận");

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
                    inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                    try {
                        Date date = inputFormat.parse(post.getCreated_at());

                        assert date != null;
                        String outputDate = outputFormat.format(date);
                        binding.txtDatePost.setText(outputDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    for (Reaction obj : post.getReactions()) {
                        if (obj.getUser_id() == Integer.parseInt(preferenceManager.getString("userID"))  && obj.getPost_id().equals(String.valueOf(post.getId()))) {
                            if (obj.getType().equals("0")){
                                Drawable drawableDisLike = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_dis_like);
                                binding.icDisLike.setImageDrawable(drawableDisLike);
                            } else {
                                Drawable drawableLike = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_like);
                                binding.icLike.setImageDrawable(drawableLike);
                                binding.txtLike.setText("Đã thích");
                            }
                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                    Log.e("API_POST", "Fail with code: " + response.code());
                }
                binding.loadingProgressBar.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(Call<ApiResponseGetPost> call, Throwable t) {
                Log.e("API_POST", String.valueOf(t));
            }
        });

        Call<ApiResponseCommentPost> Comment = apiService.getComment(Token, Accept, postId);
        Comment.enqueue(new Callback<ApiResponseCommentPost>() {
            @Override
            public void onResponse(Call<ApiResponseCommentPost> call, Response<ApiResponseCommentPost> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseCommentPost apiResponse = response.body();
                    comments = apiResponse.getData();
                    commentAdapter.setComments(comments);
                    binding.cmtRecycelerView.setAdapter(commentAdapter);

                } else {
                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                    Log.e("API_POST", "Fail with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseCommentPost> call, Throwable t) {
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

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.KEY_POST_ID) && intent.hasExtra(Constants.KEY_POST_INDEX)) {
            postId = intent.getIntExtra(Constants.KEY_POST_ID, 0);
            postIndex = intent.getIntExtra(Constants.KEY_POST_INDEX, -1);

        }

        commentAdapter = new CommentAdapter(getApplicationContext());
    }
}