package com.example.project_fakebook.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project_fakebook.databinding.ItemContainerRecentConversionBinding;
import com.example.project_fakebook.listeners.ConversionListener;
import com.example.project_fakebook.listeners.onClickListener;
import com.example.project_fakebook.model.UserList;
import com.example.project_fakebook.utilities.Constants;

import java.util.ArrayList;


public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder> {

    private ArrayList<UserList> userLists;
    private onClickListener onClickListener;
    private Context mContext;

    public RecentConversationsAdapter(Context mContext, onClickListener onClickListener) {
        this.mContext = mContext;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContainerRecentConversionBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
        if (userLists.get(position).getInfo().get(0).getAvatar() == null || userLists.get(position).getInfo().get(0).getAvatar().isEmpty()){
            Glide.with(mContext)
                    .asBitmap()
                    .load(Constants.KEY_IMAGE_DEFAULT)
                    .into(holder.binding.imageProfile);
        } else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(Constants.KEY_API + "/storage/" + userLists.get(position).getInfo().get(0).getAvatar())
                    .into(holder.binding.imageProfile);
        }

        holder.binding.textName.setText(userLists.get(position).getInfo().get(0).getFirstName() + " " + userLists.get(position).getInfo().get(0).getLastName());

        holder.binding.layoutFriend.setOnClickListener(v -> {
            onClickListener.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return userLists.size();
    }

    public void setUserLists(ArrayList<UserList> userLists) {
        this.userLists = userLists;
    }

    static class ConversionViewHolder extends RecyclerView.ViewHolder {
        ItemContainerRecentConversionBinding binding;

        ConversionViewHolder(ItemContainerRecentConversionBinding itemContainerRecentConversionBinding) {
            super(itemContainerRecentConversionBinding.getRoot());
            binding = itemContainerRecentConversionBinding;
        }
    }


}
