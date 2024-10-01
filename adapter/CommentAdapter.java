package com.example.project_fakebook.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project_fakebook.R;
import com.example.project_fakebook.activities.PostInfoActivity;
import com.example.project_fakebook.interfaces.ApiService;
import com.example.project_fakebook.model.ApiResponseReaction;
import com.example.project_fakebook.model.Post;
import com.example.project_fakebook.model.Reaction;
import com.example.project_fakebook.model.UserProfile;
import com.example.project_fakebook.model.comment;
import com.example.project_fakebook.utilities.Constants;
import com.example.project_fakebook.utilities.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private Context mContext;
    private ArrayList<comment> comments = new ArrayList<>();
    public CommentAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        init();
        if (comments.get(position).getUser().getAvatar() == null || comments.get(position).getUser().getAvatar().isEmpty()) {
            Glide.with(mContext)
                    .asBitmap()
                    .load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRK-cqV-aGe4jcDrt9MXAkp_uGOBFsBbmlLunQuD2xCt7pNXCdhVsL4ZTIqCRvakX81QTg&usqp=CAU")
                    .into(holder.profileUserImageComment);
        } else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(Constants.KEY_API + "/storage/" + comments.get(position).getUser().getAvatar())
                    .into(holder.profileUserImageComment);
        }

        holder.txtNameUser.setText(comments.get(position).getUser().getFirstName() + " " + comments.get(position).getUser().getLastName());
        holder.txtComment.setText(comments.get(position).getComment());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        try {
            Date date = inputFormat.parse(comments.get(position).getCreated_at());

            assert date != null;
            String outputDate = outputFormat.format(date);
            holder.txtDateComment.setText(outputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        setListenerAdapter(holder, position);
    }


    private void setListenerAdapter(ViewHolder holder, int position) {

    }

    public ArrayList<comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<comment> comments) {
        this.comments = comments;
    }

    public void init() {
        preferenceManager = new PreferenceManager(mContext);
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

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView profileUserImageComment;
        private TextView txtNameUser, txtDateComment, txtComment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileUserImageComment = itemView.findViewById(R.id.profileUserImageComment);
            txtNameUser = itemView.findViewById(R.id.txtNameUser);
            txtComment = itemView.findViewById(R.id.txtComment);
            txtDateComment = itemView.findViewById(R.id.txtDateComment);

        }
    }
}
