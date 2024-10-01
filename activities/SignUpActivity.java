package com.example.project_fakebook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_fakebook.databinding.ActivitySignUpBinding;
import com.example.project_fakebook.interfaces.ApiService;
import com.example.project_fakebook.model.ApiResponseGetUser;
import com.example.project_fakebook.model.user;
import com.example.project_fakebook.utilities.Constants;
import com.example.project_fakebook.utilities.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private ApiService apiService;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initValue();
        setListener();
    }

    private void setListener() {
        binding.btnFormLogIn.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.btnSignUp.setOnClickListener(v -> {
            if(isValidSignUpDetails()) {
                callApi();
            }
        });
    }



    private void initValue() {
        preferenceManager = new PreferenceManager(getApplicationContext());

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Tăng timeout kết nối
                .readTimeout(30, TimeUnit.SECONDS)    // Tăng timeout đọc dữ liệu
                .writeTimeout(30, TimeUnit.SECONDS)   // Tăng timeout ghi dữ liệu
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.KEY_API + "/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    private void callApi() {
        String Accept = "application/json";
        String Gmail = binding.inputEdtGmail.getText().toString().trim();
        String Passord = binding.inputEdtPass.getText().toString().trim();
        String First_Name = binding.inputEdtFirstName.getText().toString().trim();
        String Last_Name = binding.inputEdtLastName.getText().toString().trim();
        String Phone = binding.inputEdtPhone.getText().toString().trim();
        Call<user> call = apiService.sign_up(Accept, Gmail, Passord, Last_Name, First_Name, Phone);
        call.enqueue(new Callback<user>() {

            @Override
            public void onResponse(@NonNull Call<user> call, @NonNull retrofit2.Response<user> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    user result = response.body();
                    Toast.makeText(SignUpActivity.this, "Loading", Toast.LENGTH_SHORT).show();
                    preferenceManager.putBoolean("isSignIn", true);
                    preferenceManager.putString("data", result.getData());
                    getUser();
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyString = response.errorBody().string();
                            // Giải mã JSON từ errorBodyString
                            JSONObject errorJson = new JSONObject(errorBodyString);

                            // Trích xuất thông tin lỗi từ JSON
                            String message = errorJson.optString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                            // Xử lý message và errors theo nhu cầu của bạn
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<user> call, @NonNull Throwable t) {
                Toast.makeText(SignUpActivity.this, "Email đã được sử dụng bởi một tài khoản khác", Toast.LENGTH_SHORT).show();
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

    private void handleJsonError(ResponseBody errorJson) {
        try {
            JSONObject jsonObject = new JSONObject((Map) errorJson);

            if (jsonObject.has("message")) {
                String errorMessage = jsonObject.getString("message");
                Log.e("API Error", "Error Message: " + errorMessage);
            }

            if (jsonObject.has("errors")) {
                JSONObject errorsObject = jsonObject.getJSONObject("errors");
                Iterator<String> keys = errorsObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONArray errorArray = errorsObject.getJSONArray(key);
                    for (int i = 0; i < errorArray.length(); i++) {
                        String error = errorArray.getString(i);
                        Log.e("API Error", key + " Error: " + error);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Boolean isValidSignUpDetails() {
        if (Objects.requireNonNull(binding.inputEdtFirstName.getText()).toString().trim().isEmpty()) {
            showToast("Enter Tên");
            return false;
        } else if (Objects.requireNonNull(binding.inputEdtLastName.getText()).toString().trim().isEmpty()) {
            showToast("Enter Họ");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(Objects.requireNonNull(binding.inputEdtGmail.getText()).toString()).matches()) {
            showToast("Enter Gmail hợp lệ");
            return false;
        } else if (binding.inputEdtGmail.getText().toString().trim().isEmpty()) {
            showToast("Enter Gmail");
            return false;
        } else if (Objects.requireNonNull(binding.inputEdtPhone.getText()).toString().trim().isEmpty()) {
            showToast("Enter Phone");
            return false;
        } else if (Objects.requireNonNull(binding.inputEdtPass.getText()).toString().trim().isEmpty()) {
            showToast("Confirm Password");
            return false;
        } else if (!binding.inputEdtPass.getText().toString().equals(Objects.requireNonNull(binding.inputEdtRePass.getText()).toString())) {
            showToast("Password & confirm password phải giống nhau");
            return false;
        } else {
            return true;
        }
    }

    private void showToast(String mess) {
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show();
    }
}