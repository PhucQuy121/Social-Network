package com.example.project_fakebook.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.project_fakebook.R;
import com.example.project_fakebook.databinding.ActivityChangeInfoBinding;
import com.example.project_fakebook.interfaces.ApiService;
import com.example.project_fakebook.model.ApiResponseGetUser;
import com.example.project_fakebook.model.Result;
import com.example.project_fakebook.model.UserInfo;
import com.example.project_fakebook.utilities.Constants;
import com.example.project_fakebook.utilities.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangeInfoActivity extends AppCompatActivity {
    private ActivityChangeInfoBinding binding;
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    private MultipartBody.Part body;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initValue();
        valueAPI();
        setListener();
    }

    private void setListener() {
        binding.imageBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.btnChangeUserInfo.setOnClickListener(v -> {
            if (isValidSignUpDetails()){
                updateUser();
            }
        });

        binding.profileImageUser.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });

        binding.btnPickDate.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        // Lấy ngày tháng năm hiện tại
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Xử lý khi người dùng chọn ngày tháng
                        String selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        binding.edtIntroduceBirtDay.setText(selectedDate);
                    }
                },
                year, month, day);

        // Hiển thị DatePickerDialog
        datePickerDialog.show();
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
                            binding.profileImageUser.setImageBitmap(bitmap);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] imageBytes = baos.toByteArray();

                            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageBytes);

                            body = MultipartBody.Part.createFormData("avatar", "image.jpg", requestFile);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
    );

    private void updateUser() {
        RequestBody firstName = RequestBody.create(MediaType.parse("multipart/form-data"), binding.edtIntroduceFirstName.getText().toString().trim());
        RequestBody lastName = RequestBody.create(MediaType.parse("multipart/form-data"), binding.edtIntroduceLastName.getText().toString().trim());
        RequestBody address = RequestBody.create(MediaType.parse("multipart/form-data"), binding.edtIntroduceAddress.getText().toString().trim());
        RequestBody gender = RequestBody.create(MediaType.parse("multipart/form-data"), "1");
        if(!binding.rdoMale.isChecked())
            gender = RequestBody.create(MediaType.parse("multipart/form-data"), "0");
        RequestBody phone = RequestBody.create(MediaType.parse("multipart/form-data"), binding.edtIntroduceNumPhone.getText().toString().trim());
        RequestBody birtDay = RequestBody.create(MediaType.parse("multipart/form-data"), binding.edtIntroduceBirtDay.getText().toString().trim());

        String Token = "Bearer " + preferenceManager.getString("data");
        String Accept = "application/json";
        if (body == null) {
            Call<ApiResponseGetUser> Result = apiService.updateUser(Token, Accept , lastName, firstName, phone, null, gender, birtDay, address);
            Result.enqueue(new Callback<ApiResponseGetUser>() {
                @Override
                public void onResponse(Call<ApiResponseGetUser> call, Response<ApiResponseGetUser> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponseGetUser apiResponseGetUser = response.body();
                        UserInfo userInfo = apiResponseGetUser.getData();
                        Toast.makeText(ChangeInfoActivity.this, "Thành công", Toast.LENGTH_SHORT).show();
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
                public void onFailure(Call<ApiResponseGetUser> call, Throwable t) {
                    Toast.makeText(ChangeInfoActivity.this, "Thất bại", Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            Call<ApiResponseGetUser> Result = apiService.updateUserHasImg(Token, Accept , lastName, firstName, phone, null, gender, birtDay, address, body);
            Result.enqueue(new Callback<ApiResponseGetUser>() {
                @Override
                public void onResponse(Call<ApiResponseGetUser> call, Response<ApiResponseGetUser> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponseGetUser apiResponseGetUser = response.body();
                        UserInfo userInfo = apiResponseGetUser.getData();
                        Toast.makeText(ChangeInfoActivity.this, "Thành công", Toast.LENGTH_SHORT).show();
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
                public void onFailure(Call<ApiResponseGetUser> call, Throwable t) {
                    Toast.makeText(ChangeInfoActivity.this, "Thát bại", Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    private void valueAPI() {
        String Token = "Bearer " + preferenceManager.getString("data");
        Call<ApiResponseGetUser> Post = apiService.getUser(Token);
        Post.enqueue(new Callback<ApiResponseGetUser>() {
            @Override
            public void onResponse(Call<ApiResponseGetUser> call, Response<ApiResponseGetUser> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseGetUser apiResponseGetUser = response.body();
                    UserInfo userInfo = apiResponseGetUser.getData();
                    if (userInfo.getAvatar() == null) {
                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRK-cqV-aGe4jcDrt9MXAkp_uGOBFsBbmlLunQuD2xCt7pNXCdhVsL4ZTIqCRvakX81QTg&usqp=CAU")
                                .into(binding.profileImageUser);
                    } else {
                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(Constants.KEY_API + "/storage/" + userInfo.getAvatar())
                                .into(binding.profileImageUser);
                    }

                    binding.edtIntroduceFirstName.setText(userInfo.getFirstName());
                    binding.edtIntroduceLastName.setText(userInfo.getLastName());
                    binding.edtIntroduceBirtDay.setText(userInfo.getBirth_date());
                    binding.edtIntroduceAddress.setText(userInfo.getAddress());
                    binding.edtIntroduceNumPhone.setText(userInfo.getPhone());
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

    private boolean isValidSignUpDetails() {
        if(binding.edtIntroduceFirstName.getText().toString().trim().isEmpty()) {
            showToast("Vui lòng nhập Tên!!!");
            return  false;
        } else if (binding.edtIntroduceLastName.getText().toString().trim().isEmpty()) {
            showToast("Vui lòng nhập Họ!!!");
            return  false;

        }else if (binding.edtIntroduceNumPhone.getText().toString().trim().isEmpty()) {
            showToast("Vui lòng nhập Số điện thoại!!!");
            return  false;

        } else
            return true;
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
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
    }
}