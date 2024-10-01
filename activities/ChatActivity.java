package com.example.project_fakebook.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.project_fakebook.adapter.ChatAdapter;
import com.example.project_fakebook.adapter.UserAdapter;
import com.example.project_fakebook.databinding.ActivityChatBinding;
import com.example.project_fakebook.interfaces.ApiService;
import com.example.project_fakebook.model.ApiResponseGetConversation;
import com.example.project_fakebook.model.ApiResponsePostMessage;
import com.example.project_fakebook.model.ChatMessage;
import com.example.project_fakebook.model.Friend;
import com.example.project_fakebook.utilities.Constants;
import com.example.project_fakebook.utilities.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    private ArrayList<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private int receiverID;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Constants.KEY_SOCKET_SERVER);
        } catch (URISyntaxException e) {
            Log.e("mess fail", e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initValue();
        listenSocket();
        loadReceiverDetails();
        setListener();
    }

    private void listenSocket() {
        mSocket.emit("user_connected", preferenceManager.getString("userID"));
        mSocket.on("private-channel:App\\Events\\SendMessage", onNewMessage);
        mSocket.connect();
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject jsonObject = (JSONObject) args[0];
            Gson gson = new Gson();
            ChatMessage chatMessage = gson.fromJson(jsonObject.toString(), ChatMessage.class);

            // Xử lý dữ liệu chatMessage ở đây
            if (chatMessage != null) {
                chatMessages.add(chatMessage);
                runOnUiThread(new Runnable() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Thành công", Toast.LENGTH_SHORT).show();
                        chatMessages.sort(Comparator.comparing(ChatMessage::getCreated_at));
                        if (chatMessages.size() == 0) {
                            chatAdapter.notifyDataSetChanged();
                        }else {
                            chatAdapter.notifyItemRangeChanged(chatMessages.size(), chatMessages.size());
                            binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
                        }
                        binding.inputMessage.setText(null);
                        binding.chatRecyclerView.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    };

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

        receiverID = getIntent().getIntExtra(Constants.KEY_USER_ID, -1);

    }

    private void loadReceiverDetails() {
        receiverID = getIntent().getIntExtra(Constants.KEY_USER_ID, -1);
        binding.progressBar.setVisibility(View.VISIBLE);
        String Token = "Bearer " + preferenceManager.getString("data");
        String Accept = "application/json";
        Call<ApiResponseGetConversation> Conversation = apiService.getMessage(Token, Accept, receiverID);
        Conversation.enqueue(new Callback<ApiResponseGetConversation>() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onResponse(Call<ApiResponseGetConversation> call, Response<ApiResponseGetConversation> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseGetConversation apiResponse = response.body();
                    chatMessages = apiResponse.getData().getMessages();
                    chatAdapter = new ChatAdapter(
                            chatMessages,
                            getApplicationContext(),
                            apiResponse.getData().getReceiver().getAvatar(),
                            receiverID
                    );
                    binding.chatRecyclerView.setAdapter(chatAdapter);
                    binding.textName.setText(apiResponse.getData().getReceiver().getFirstName() + " " + apiResponse.getData().getReceiver().getLastName());
                    binding.chatRecyclerView.smoothScrollToPosition(0);
                    binding.chatRecyclerView.setVisibility(View.VISIBLE);
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
        binding.imageBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.layoutSend.setOnClickListener(v -> {
            if (isValidSignUpDetails()){
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String Token = "Bearer " + preferenceManager.getString("data");
        String Accept = "application/json";
        Call<ApiResponsePostMessage> Result = apiService.postMessage(Token, Accept, receiverID, binding.inputMessage.getText().toString().trim());
        Result.enqueue(new Callback<ApiResponsePostMessage>() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onResponse(Call<ApiResponsePostMessage> call, Response<ApiResponsePostMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponsePostMessage apiResponse = response.body();
                    chatMessages.add(apiResponse.getData());
                    chatMessages.sort(Comparator.comparing(ChatMessage::getCreated_at));
                    if (chatMessages.size() == 0) {
                        chatAdapter.notifyDataSetChanged();
                    }else {
                        chatAdapter.notifyItemRangeChanged(chatMessages.size(), chatMessages.size());
                        binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
                    }
                    binding.inputMessage.setText(null);
                    binding.chatRecyclerView.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);

                } else {
                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                    Log.e("API_POST", "Fail with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponsePostMessage> call, Throwable t) {

            }
        });
    }

    private Boolean isValidSignUpDetails() {
        if (Objects.requireNonNull(binding.inputMessage.getText()).toString().trim().isEmpty()) {
            showToast("Vui lòng nhập văn bản...");
            return false;
        }  else {
            return true;
        }
    }


    private void showToast(String mess) {
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show();
    }
}