package com.example.project_fakebook.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project_fakebook.R;
import com.example.project_fakebook.interfaces.ApiService;
import com.example.project_fakebook.listeners.onClickListener;
import com.example.project_fakebook.model.Friend;
import com.example.project_fakebook.utilities.Constants;
import com.example.project_fakebook.utilities.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private ArrayList<Friend> friends = new ArrayList<>();
    private Context mContext;
    private onClickListener onClickListener;
    public FriendAdapter(Context mContext, com.example.project_fakebook.listeners.onClickListener onClickListener) {
        this.mContext = mContext;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        init();
        if (friends.get(position).getAvatar() == null || friends.get(position).getAvatar().isEmpty()) {
            Glide.with(mContext)
                    .asBitmap()
                    .load(Constants.KEY_IMAGE_DEFAULT)
                    .into(holder.profileUserImageComment);
        } else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(Constants.KEY_API + "/storage/" + friends.get(position).getAvatar())
                    .into(holder.profileUserImageComment);
        }

        holder.txtNameUser.setText(friends.get(position).getFirstName() + " " + friends.get(position).getLastName());


        holder.layoutFriend.setOnClickListener(v -> {
            onClickListener.onItemClick(position);
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

    public void setFriends(ArrayList<Friend> friends) {
        this.friends = friends;
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView profileUserImageComment;
        private TextView txtNameUser;
        private LinearLayout layoutFriend;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileUserImageComment = itemView.findViewById(R.id.profileUserImageComment);
            txtNameUser = itemView.findViewById(R.id.txtNameUser);
            layoutFriend = itemView.findViewById(R.id.layoutFriend);

        }
    }
}
