package com.example.project_fakebook.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private ArrayList<Friend> friends = new ArrayList<>();
    private Context mContext;
    private onClickListener onClickListener;
    public UserAdapter(Context mContext, com.example.project_fakebook.listeners.onClickListener onClickListener) {
        this.mContext = mContext;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_user, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (friends.get(position).getAvatar() == null || friends.get(position).getAvatar().isEmpty()) {
            Glide.with(mContext)
                    .asBitmap()
                    .load(Constants.KEY_IMAGE_DEFAULT)
                    .into(holder.imageProfile);
        } else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(Constants.KEY_API + "/storage/" + friends.get(position).getAvatar())
                    .into(holder.imageProfile);
        }

        holder.textName.setText(friends.get(position).getFirstName() + " " + friends.get(position).getLastName());

//
        holder.layoutFriend.setOnClickListener(v -> {
            onClickListener.onItemClick(position);
        });
    }


    public void setFriends(ArrayList<Friend> friends) {
        this.friends = friends;
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView imageProfile;
        private TextView textName;
        private ConstraintLayout layoutFriend;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.imageProfile);
            textName = itemView.findViewById(R.id.textName);
            layoutFriend = itemView.findViewById(R.id.layoutFriend);

        }
    }
}
