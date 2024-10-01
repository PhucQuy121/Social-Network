package com.example.project_fakebook.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.project_fakebook.R;
import com.example.project_fakebook.databinding.ActivityForgetPassBinding;
import com.example.project_fakebook.interfaces.ApiService;
import com.example.project_fakebook.model.apiResponseForgetPassword;
import com.example.project_fakebook.model.user;
import com.example.project_fakebook.utilities.Constants;
import com.example.project_fakebook.utilities.PreferenceManager;
import com.google.android.material.textfield.TextInputEditText;
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

public class ForgetPassActivity extends AppCompatActivity {
    private ActivityForgetPassBinding binding;
    private  String mGmail = "";
    private ApiService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initValue();
        setListener();
    }

    private void setListener() {
        binding.btnSearchGmail.setOnClickListener(v -> {
            if (isValidSuccess()) {
                searchGmail();
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
    }
    private boolean isValidSuccess() {
        if(Objects.requireNonNull(binding.inputEdtGmail.getText()).toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email của bạn", Toast.LENGTH_SHORT).show();
            return false;
        }

        else {
            return true;
        }
    }

    private void searchGmail() {
        binding.progressBar.setVisibility(View.VISIBLE);
        String Accept = "application/json";
        String Gmail = binding.inputEdtGmail.getText().toString().trim();
        Call<apiResponseForgetPassword> call = apiService.forgetPassword(Accept, Gmail);
        call.enqueue(new Callback<apiResponseForgetPassword>() {
            @Override
            public void onResponse(Call<apiResponseForgetPassword> call, Response<apiResponseForgetPassword> response) {
                if(isValidSuccess()) {
                    apiResponseForgetPassword apiResponse = response.body();
                    assert apiResponse != null;
                    Toast.makeText(ForgetPassActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgetPassActivity.this, InputCodeActivity.class);
                    intent.putExtra(Constants.KEY_TOKEN, apiResponse.getData());
                    startActivity(intent);
                    finish();
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
            public void onFailure(Call<apiResponseForgetPassword> call, Throwable t) {

            }
        });




    }

}