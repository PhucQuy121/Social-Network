package com.example.project_fakebook.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.project_fakebook.R;
import com.example.project_fakebook.databinding.ActivityInputCodeBinding;
import com.example.project_fakebook.interfaces.ApiService;
import com.example.project_fakebook.model.Result;
import com.example.project_fakebook.model.apiResponseForgetPassword;
import com.example.project_fakebook.utilities.Constants;
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

public class InputCodeActivity extends AppCompatActivity {
    private ActivityInputCodeBinding binding;
    private ApiService apiService;
    private int token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInputCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initValue();

        setListener();
    }

    private void setListener() {
        binding.btnInpCode.setOnClickListener(v -> {
            if (isValidSuccess()) {
                recoverPassword();
            }
        });
    }

    private void recoverPassword() {
        String Accept = "application/json";
        String Token = binding.edtRecoverCode.getText().toString().trim();
        String Password = binding.edtNewPassword.getText().toString().trim();
        Call<Result> call = apiService.resetPassword(Accept, Token, Password);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if(isValidSuccess()) {
                    Result apiResponse = response.body();
                    assert apiResponse != null;
                    Toast.makeText(getApplicationContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Sai mã khôi phục", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Sai mã khôi phục", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initValue() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy MM dd HH:mm:ss")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.KEY_API + "/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        apiService = retrofit.create(ApiService.class);
        token = getIntent().getIntExtra(Constants.KEY_TOKEN, -1);
    }
    private boolean isValidSuccess() {
        if(Objects.requireNonNull(binding.edtRecoverCode.getText()).toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mã khôi phục của bạn", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!binding.edtRecoverCode.getText().toString().trim().equals(String.valueOf(token))) {
            Toast.makeText(this, "Mã khôi phục của bạn không đúng, vui lòng kiểm tra lại", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(Objects.requireNonNull(binding.edtRecoverCode.getText()).toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu mới của bạn", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.edtNewPassword.getText().toString().length() <6) {
            Toast.makeText(this, "Mật khẩu mới của bạn phải lớn hơn hoặc bằng 6 ký tự", Toast.LENGTH_SHORT).show();
            return false;

        } else {
            return true;
        }
    }
}