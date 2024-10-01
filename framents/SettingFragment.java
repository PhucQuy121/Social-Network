package com.example.project_fakebook.framents;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.project_fakebook.R;
import com.example.project_fakebook.activities.AccountActivity;
import com.example.project_fakebook.databinding.FragmentHomeBinding;
import com.example.project_fakebook.databinding.FragmentSettingBinding;
import com.example.project_fakebook.interfaces.ApiService;
import com.example.project_fakebook.model.Result;
import com.example.project_fakebook.utilities.Constants;
import com.example.project_fakebook.utilities.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingFragment extends Fragment {
    private FragmentSettingBinding binding;
    private ApiService apiService;
    private PreferenceManager preferenceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);

        initValue();
        setListener();
        return binding.getRoot();
    }

    private void initValue() {
        preferenceManager = new PreferenceManager(requireContext());
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
        binding.btnLogOut.setOnClickListener(v -> {
            logOut();
        });
    }

    private void logOut() {
        String Token = "Bearer " + preferenceManager.getString("data");
        String Accept = "application/json";

        Call<Result> Result = apiService.log_out(Token, Accept);
        Result.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@NonNull Call<Result> call, @NonNull retrofit2.Response<Result> response) {
                Result result = response.body();
                if (result != null) {
                    Toast.makeText(requireContext(), "Loading log out", Toast.LENGTH_SHORT).show();
                    preferenceManager.clear();
                    startActivity(new Intent(requireContext(), AccountActivity.class));
                } else {
                    Toast.makeText(requireContext(), "Fail", Toast.LENGTH_SHORT).show();
                    Log.e("API_POST", "Fail with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.e("API_POST", String.valueOf(t));
            }
        });
    }

}
