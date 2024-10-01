package com.example.project_fakebook.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project_fakebook.R;
import com.example.project_fakebook.interfaces.ApiService;
import com.example.project_fakebook.model.FriendRequests;
import com.example.project_fakebook.model.Result;
import com.example.project_fakebook.model.comment;
import com.example.project_fakebook.utilities.Constants;
import com.example.project_fakebook.utilities.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class requestFriendAdapter  extends RecyclerView.Adapter<requestFriendAdapter.ViewHolder> {
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private ArrayList<FriendRequests> friendRequests = new ArrayList<>();
    private Context mContext;

    public requestFriendAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_request, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        init();
        if (friendRequests.get(position).getAvatar() == null || friendRequests.get(position).getAvatar().isEmpty()) {
            Glide.with(mContext)
                    .asBitmap()
                    .load(Constants.KEY_IMAGE_DEFAULT)
                    .into(holder.profileUserImageComment);
        } else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(Constants.KEY_API + "/storage/" + friendRequests.get(position).getAvatar())
                    .into(holder.profileUserImageComment);
        }

        holder.txtNameUser.setText(friendRequests.get(position).getFirstName() + " " + friendRequests.get(position).getLastName());

        holder.txtAcceptFriend.setOnClickListener(v -> {
            String Token = "Bearer " + preferenceManager.getString("data");
            String Accept = "application/json";
            Call<Result> resultCall = apiService.acceptFriend(Token, Accept, friendRequests.get(position).getPivot().getFriend_id());
            resultCall.enqueue(new Callback<Result>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(mContext, "Thành công", Toast.LENGTH_SHORT).show();
                        friendRequests.remove(position);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                        Log.e("API_POST", "Fail with code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    Log.e("API_POST", String.valueOf(t));
                }
            });
        });

        holder.txtNotAcceptFriend.setOnClickListener(v -> {
            String Token = "Bearer " + preferenceManager.getString("data");
            String Accept = "application/json";
            Call<Result> resultCall = apiService.rejectFriend(Token, Accept, friendRequests.get(position).getPivot().getFriend_id());
            resultCall.enqueue(new Callback<Result>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(mContext, "Thành công", Toast.LENGTH_SHORT).show();
                        friendRequests.remove(position);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
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

    public void init() {
        preferenceManager = new PreferenceManager(mContext);
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
    }


    public void setFriendRequests(ArrayList<FriendRequests> friendRequests) {
        this.friendRequests = friendRequests;
    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView profileUserImageComment;
        private TextView txtNameUser, txtAcceptFriend, txtNotAcceptFriend;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileUserImageComment = itemView.findViewById(R.id.profileUserImageComment);
            txtNameUser = itemView.findViewById(R.id.txtNameUser);
            txtNotAcceptFriend = itemView.findViewById(R.id.txtNotAcceptFriend);
            txtAcceptFriend = itemView.findViewById(R.id.txtAcceptFriend);

        }
    }
}
