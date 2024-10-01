package com.example.project_fakebook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.project_fakebook.R;
import com.example.project_fakebook.databinding.ActivityAccountBinding;
import com.example.project_fakebook.interfaces.ApiService;
import com.example.project_fakebook.model.ApiResponse;
import com.example.project_fakebook.model.ApiResponseGetUser;
import com.example.project_fakebook.model.UserInfo;
import com.example.project_fakebook.model.user;
import com.example.project_fakebook.utilities.Constants;
import com.example.project_fakebook.utilities.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccountActivity extends AppCompatActivity {

    private Button btnFormSignUp;
    private ActivityAccountBinding binding;
    private PreferenceManager preferenceManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initValue();
        if(preferenceManager.getBoolean("isSignIn")){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        setListener();
    }

    private void initValue() {
        preferenceManager = new PreferenceManager(getApplicationContext());

        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy MM dd HH:mm:ss")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.KEY_API + "/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    private void setListener() {
        binding.btnFormSignUp.setOnClickListener(v -> startActivity(new Intent(AccountActivity.this, SignUpActivity.class)));
        binding.txtForgetPass.setOnClickListener(v -> startActivity(new Intent(AccountActivity.this, ForgetPassActivity.class)));
        binding.btnSignIn.setOnClickListener(v -> {
            if (isValidSignUpDetails()) {
                signUp();
            }
        });
    }

    private void signUp() {
        String Accept = "application/json";
        String Gmail = binding.inputEdtGmail.getText().toString().trim();
        String Passord = binding.inputEdtPass.getText().toString().trim();
        Call<user> call = apiService.sign_in(Accept, Gmail, Passord);
        call.enqueue(new Callback<user>() {

            @Override
            public void onResponse(@NonNull Call<user> call, @NonNull retrofit2.Response<user> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    user result = response.body();
                    Toast.makeText(getApplicationContext(), "Loading", Toast.LENGTH_SHORT).show();
                    preferenceManager.putBoolean("isSignIn", true);
                    preferenceManager.putString("data", result.getData());
                    getUser();
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyString = response.errorBody().string();
                            JSONObject errorJson = new JSONObject(errorBodyString);

                            String message = errorJson.optString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<user> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Email đã được sử dụng bởi một tài khoản khác", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUser() {
        String Token = "Bearer " + preferenceManager.getString("data");
        Call<ApiResponseGetUser> Post = apiService.getUser(Token);
        Post.enqueue(new Callback<ApiResponseGetUser>() {
            @Override
            public void onResponse(Call<ApiResponseGetUser> call, Response<ApiResponseGetUser> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseGetUser apiResponseGetUser = response.body();
                    preferenceManager.putString(Constants.KEY_USER_ID, String.valueOf(apiResponseGetUser.getData().getId()));
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                    Log.e("API_POST", "Fail with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseGetUser> call, Throwable t) {
                Log.e("API_POST", String.valueOf(t));
            }
        });
    }

    private Boolean isValidSignUpDetails() {
        if (!Patterns.EMAIL_ADDRESS.matcher(Objects.requireNonNull(binding.inputEdtGmail.getText()).toString()).matches()) {
            showToast("Enter Gmail không hợp lệ");
            return false;
        } else if (binding.inputEdtGmail.getText().toString().trim().isEmpty()) {
            showToast("Enter Gmail");
            return false;
        }  else if (Objects.requireNonNull(binding.inputEdtPass.getText()).toString().isEmpty()) {
            showToast("Enter Password");
            return false;
        } else {
            return true;
        }
    }

    private void showToast(String mess) {
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show();
    }

}