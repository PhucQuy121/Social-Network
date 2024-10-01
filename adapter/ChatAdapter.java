package com.example.project_fakebook.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project_fakebook.databinding.ItemContainerReceivedMessageBinding;
import com.example.project_fakebook.databinding.ItemContainerSentMessageBinding;
import com.example.project_fakebook.model.ChatMessage;
import com.example.project_fakebook.utilities.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ChatMessage> chatMessages;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private String receiverProfileImage;
    private final int senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(ArrayList<ChatMessage> chatMessages, Context mContext, String receiverProfileImage, int senderId) {
        this.chatMessages = chatMessages;
        ChatAdapter.mContext = mContext;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(ItemContainerSentMessageBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false)
            );
        } else {
            return new ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false)
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder)  holder).setData(chatMessages.get(position));
        }else {
            ((ReceivedMessageViewHolder) holder).setData(chatMessages.get(position), receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).getReceiver_id() == senderId) {
            return VIEW_TYPE_SENT;
        }
        else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    public void setChatMessages(ArrayList<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding){
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;

        }

        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.getMessage());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            try {
                Date date = inputFormat.parse(chatMessage.getCreated_at());

                assert date != null;
                String outputDate = outputFormat.format(date);
                binding.textDateTime.setText(outputDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding){
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;

        }

        void setData(ChatMessage chatMessage, String receiverProfileImage) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            try {
                Date date = inputFormat.parse(chatMessage.getCreated_at());

                assert date != null;
                String outputDate = outputFormat.format(date);
                binding.textDateTime.setText(outputDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            binding.textMessage.setText(chatMessage.getMessage());
            if (receiverProfileImage != null && !receiverProfileImage.isEmpty()) {
                Glide.with(mContext)
                        .asBitmap()
                        .load(Constants.KEY_API + "/storage/" + receiverProfileImage)
                        .into(binding.imageProfile);
            } else {
                Glide.with(mContext)
                        .asBitmap()
                        .load(Constants.KEY_IMAGE_DEFAULT)
                        .into(binding.imageProfile);
            }
        }

    }
}
