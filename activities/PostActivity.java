package com.example.project_fakebook.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.project_fakebook.databinding.ActivityPostBinding;
import com.example.project_fakebook.func.RealPathUtil;
import com.example.project_fakebook.interfaces.ApiService;
import com.example.project_fakebook.model.Result;
import com.example.project_fakebook.utilities.Constants;
import com.example.project_fakebook.utilities.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostActivity extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 10;
    private ActivityPostBinding binding;
    private ApiService apiService;
    private MultipartBody.Part body;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initValue();
        setListener();
    }

    private void initValue() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS); // Set connection timeout to 15 seconds
        httpClientBuilder.readTimeout(20, java.util.concurrent.TimeUnit.SECONDS);    // Set read timeout to 20 seconds

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

    private void setListener() {
        binding.imageBack.setOnClickListener(v-> getOnBackPressedDispatcher().onBackPressed());

        binding.imageView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);

        });

        binding.imageDelete.setOnClickListener(v -> {
            body = null;
            binding.imageViewPost.setImageBitmap(null);
            binding.imageView.setVisibility(View.VISIBLE);
            binding.layoutImagePost.setVisibility(View.GONE);

        });

        binding.textViewUpload.setOnClickListener(v-> {

            if(isValidSignUpDetails()) {
                binding.loadingProgressBar.setVisibility(View.VISIBLE);
                RequestBody contentPart = RequestBody.create(MediaType.parse("multipart/form-data"), binding.edtContent.getText().toString().trim());
                RequestBody statusPart = RequestBody.create(MediaType.parse("multipart/form-data"), "1");

                String Token = "Bearer " + preferenceManager.getString("data");
                String Accept = "application/json";
                Call<Result> Result = apiService.uploadImage(Token, Accept , contentPart, statusPart, body);
                Result.enqueue(new Callback<Result>() {

                    @Override
                    public void onResponse(@NonNull Call<Result> call, @NonNull retrofit2.Response<Result> response) {
                        Result result = response.body();
                        if (result != null) {
                            binding.loadingProgressBar.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                            Log.e("API_POST", "Fail with code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Log.e("API_POST", String.valueOf(t));
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                pickImage.launch(Intent.createChooser(intent, "Select Picture"));
            }else {
                Toast.makeText(this, "Quyền bị từ chối. Không thể truy cập tệp.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data == null) {
                            Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Uri uri = data.getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.layoutImagePost.setVisibility(View.VISIBLE);
                            binding.imageViewPost.setImageBitmap(bitmap);
                            binding.imageView.setVisibility(View.GONE);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] imageBytes = baos.toByteArray();

                            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageBytes);

                            body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
    );


    private Boolean isValidSignUpDetails() {
        if (Objects.requireNonNull(binding.edtContent.getText()).toString().trim().isEmpty()) {
            showToast("Vui lòng nhập nội dung bài viết...");
            return false;
        }  else {
            return true;
        }
    }


    private void showToast(String mess) {
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show();
    }

}