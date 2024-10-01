package com.example.project_fakebook.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project_fakebook.R;
import com.example.project_fakebook.activities.PostInfoActivity;
import com.example.project_fakebook.activities.UserProfileActivity;
import com.example.project_fakebook.interfaces.ApiService;
import com.example.project_fakebook.listeners.onClickListener;
import com.example.project_fakebook.model.ApiResponseGetPost;
import com.example.project_fakebook.model.ApiResponseReaction;
import com.example.project_fakebook.model.Post;
import com.example.project_fakebook.model.Reaction;
import com.example.project_fakebook.utilities.Constants;
import com.example.project_fakebook.utilities.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private Context mContext;
    private onClickListener listener;
    private ArrayList<Post> posts = new ArrayList<>();
    private HashMap<String, Integer> isLike = new HashMap<>();
    private ActivityResultLauncher<Intent> activityResultLauncher;
    public PostAdapter(Context mContext, onClickListener listener) {
        this.mContext = mContext;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        init();
        if (posts.get(position).getUser().getAvatar() == null || posts.get(position).getUser().getAvatar().isEmpty()) {
            Glide.with(mContext)
                    .asBitmap()
                    .load(Constants.KEY_IMAGE_DEFAULT)
                    .into(holder.profile_image);
        } else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(Constants.KEY_API + "/storage/" + posts.get(position).getUser().getAvatar())
                    .into(holder.profile_image);
        }

        holder.btn_profile.setText( posts.get(position).getUser().getLastName() + " " + posts.get(position).getUser().getFirstName());
        holder.txtContentPost.setText(posts.get(position).getContent());

        Glide.with(mContext)
                .asBitmap()
                .load(Constants.KEY_API + "/storage/" + posts.get(position).getImage())
                .into(holder.img_post);

        holder.btn_count_like.setText(posts.get(position).getLikes_count() + " người thích");
        holder.btn_count_cmt.setText(posts.get(position).getComments_count() + " bình luận");

        // Định dạng ngày tháng
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        try {
            Date date = inputFormat.parse(posts.get(position).getCreated_at());

            assert date != null;
            String outputDate = outputFormat.format(date);
            holder.txtDatePost.setText(outputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (Reaction obj : posts.get(position).getReactions()) {
            if (obj.getUser_id() == Integer.parseInt(preferenceManager.getString("userID"))  && obj.getPost_id().equals(String.valueOf(posts.get(position).getId()))) {
                if (obj.getType().equals("0")){
                    Drawable drawableLike = ContextCompat.getDrawable(mContext, R.drawable.ic_no_like);
                    holder.icLike.setImageDrawable(drawableLike);
                    holder.txtLike.setText(R.string.like_text);

                    Drawable drawableDisLike = ContextCompat.getDrawable(mContext, R.drawable.ic_dis_like);
                    holder.icDisLike.setImageDrawable(drawableDisLike);
                } else {
                    Drawable drawableLike = ContextCompat.getDrawable(mContext, R.drawable.ic_like);
                    holder.icLike.setImageDrawable(drawableLike);
                    holder.txtLike.setText(R.string.liked_text);
                    Drawable drawableDisLike = ContextCompat.getDrawable(mContext, R.drawable.ic_no_dis_like);
                    holder.icDisLike.setImageDrawable(drawableDisLike);
                }
            }
        }

        setListenerAdapter(holder, position);
    }


    private void setListenerAdapter(ViewHolder holder, int position) {
        holder.layoutLike.setOnClickListener(v -> {
            String Token = "Bearer " + preferenceManager.getString("data");
            String Accept = "application/json";
            Call<ApiResponseReaction> call = apiService.reaction(Token, Accept, "1", String.valueOf(posts.get(position).getId()));
            call.enqueue(new Callback<ApiResponseReaction>() {

                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<ApiResponseReaction> call, Response<ApiResponseReaction> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Call<ApiResponseGetPost> PostGet = apiService.getPost(Token, Accept, posts.get(position).getId());
                        PostGet.enqueue(new Callback<ApiResponseGetPost>() {
                            @Override
                            public void onResponse(Call<ApiResponseGetPost> call, Response<ApiResponseGetPost> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    ApiResponseGetPost apiResponse = response.body();
                                    posts.set(position, apiResponse.getData().get(0));
                                    notifyItemChanged(position);
                                } else {
                                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                                    Log.e("API_POST", "Fail with code: " + response.code());
                                }
                            }
                            @Override
                            public void onFailure(Call<ApiResponseGetPost> call, Throwable t) {
                                Log.e("API_POST", String.valueOf(t));
                            }
                        });

                    }
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onFailure(Call<ApiResponseReaction> call, Throwable t) {
                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_no_like);
                    holder.icLike.setImageDrawable(drawable);
                    holder.btn_count_like.setText(posts.get(position).getLikes_count() - 1  + " người thích");
                    holder.txtLike.setText(R.string.like_text);

                }
            });
        });

        holder.layoutDisLike.setOnClickListener(v -> {
            String Token = "Bearer " + preferenceManager.getString("data");
            String Accept = "application/json";
            Call<ApiResponseReaction> call = apiService.reaction(Token, Accept, "0", String.valueOf(posts.get(position).getId()));
            call.enqueue(new Callback<ApiResponseReaction>() {

                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<ApiResponseReaction> call, Response<ApiResponseReaction> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Call<ApiResponseGetPost> PostGet = apiService.getPost(Token, Accept, posts.get(position).getId());
                        PostGet.enqueue(new Callback<ApiResponseGetPost>() {
                            @Override
                            public void onResponse(Call<ApiResponseGetPost> call, Response<ApiResponseGetPost> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    ApiResponseGetPost apiResponse = response.body();
                                    posts.set(position, apiResponse.getData().get(0));
                                    notifyItemChanged(position);
                                } else {
                                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                                    Log.e("API_POST", "Fail with code: " + response.code());
                                }
                            }
                            @Override
                            public void onFailure(Call<ApiResponseGetPost> call, Throwable t) {
                                Log.e("API_POST", String.valueOf(t));
                            }
                        });

                    }
                }

                @Override
                public void onFailure(Call<ApiResponseReaction> call, Throwable t) {
                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_no_dis_like);
                    holder.icDisLike.setImageDrawable(drawable);

                }
            });
        });

        holder.txtContentPost.setOnClickListener(v -> {
            listener.onItemClick(position);
        });

        holder.btn_profile.setOnClickListener(v -> {
            if (mContext instanceof Activity) {
                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra(Constants.KEY_USER_ID, posts.get(position).getUser_id());
                ((Activity) mContext).startActivityForResult(intent, 1);
            } else {
                Toast.makeText(mContext, "Bạn đang ở trang hiện tại", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void init() {
        preferenceManager = new PreferenceManager(mContext);
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
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void updateData(final int postIndex, final int postId) {
        String Token = "Bearer " + preferenceManager.getString("data");
        String Accept = "application/json";
        Call<ApiResponseGetPost> PostGet = apiService.getPost(Token, Accept, postId);
        PostGet.enqueue(new Callback<ApiResponseGetPost>() {
            @Override
            public void onResponse(Call<ApiResponseGetPost> call, Response<ApiResponseGetPost> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseGetPost apiResponse = response.body();
                    posts.set(postIndex, apiResponse.getData().get(0));
                    notifyItemChanged(postIndex);
                } else {
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                    Log.e("API_POST", "Fail with code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<ApiResponseGetPost> call, Throwable t) {
                Log.e("API_POST", String.valueOf(t));
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView profile_image;
        private TextView btn_profile, txtDatePost, txtContentPost;
        private TextView btn_count_like, txtLike;
        private TextView btn_count_cmt;
        private LinearLayout layoutLike, layoutDisLike;
        private AppCompatImageView icLike, icDisLike;
        private ImageView img_post;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.profile_image);
            btn_profile = itemView.findViewById(R.id.btn_profile);
            txtDatePost = itemView.findViewById(R.id.txtDatePost);
            txtContentPost = itemView.findViewById(R.id.txtContentPost);
            img_post = itemView.findViewById(R.id.img_post);
            btn_count_like = itemView.findViewById(R.id.btn_count_like);
            btn_count_cmt = itemView.findViewById(R.id.btn_count_cmt);
            layoutLike = itemView.findViewById(R.id.layoutLike);
            layoutDisLike = itemView.findViewById(R.id.layoutDisLike);
            icLike = itemView.findViewById(R.id.icLike);
            icDisLike = itemView.findViewById(R.id.icDisLike);
            txtLike = itemView.findViewById(R.id.txtLike);

        }
    }
}
