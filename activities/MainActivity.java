package com.example.project_fakebook.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.project_fakebook.R;
import com.example.project_fakebook.adapter.ViewPagerAdapter;
import com.example.project_fakebook.databinding.ActivityMainBinding;
import com.example.project_fakebook.framents.HomeFragment;
import com.example.project_fakebook.framents.ProfileFragment;
import com.example.project_fakebook.model.ChatMessage;
import com.example.project_fakebook.model.Post;
import com.example.project_fakebook.model.UserInfo;
import com.example.project_fakebook.model.UserProfile;
import com.example.project_fakebook.utilities.Constants;
import com.example.project_fakebook.utilities.PreferenceManager;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity{
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;

    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initValue();
        setUpViewPager();

        setListener();

    }


    private void initValue() {
        preferenceManager = new PreferenceManager(getApplicationContext());

    }

    private void setListener() {
        binding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                eventBotNavBar(item);
                return true;
            }
        });

        Log.e("KEY_DATA", preferenceManager.getString("data"));

        binding.imageMess.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MessActivity.class);
            intent.putExtra("token", "token");
            startActivity(intent);
        });

    }

    private void setUpViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(this);

        binding.vpMain.setAdapter(viewPagerAdapter);

        binding.vpMain.setUserInputEnabled(false);

    }

    private void eventBotNavBar(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_home) {
            binding.vpMain.setCurrentItem(0);
            Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
        }
        else if(item.getItemId() == R.id.action_account) {
            binding.vpMain.setCurrentItem(1);
            Toast.makeText(MainActivity.this, "Account", Toast.LENGTH_SHORT).show();
        }
        else if(item.getItemId() == R.id.action_bell) {
            binding.vpMain.setCurrentItem(2);
            Toast.makeText(MainActivity.this, "Notify", Toast.LENGTH_SHORT).show();
        }
        else {
            binding.vpMain.setCurrentItem(3);
            Toast.makeText(MainActivity.this, "Setting", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                boolean resultData = data.getBooleanExtra("finish", false);
                int postId = data.getIntExtra(Constants.KEY_POST_ID, 0);
                int postIndex = data.getIntExtra(Constants.KEY_POST_INDEX, -1);
                Fragment currentFragment;
                if (resultData) {
                    currentFragment = getSupportFragmentManager().findFragmentById(binding.vpMain.getCurrentItem());
                    if (currentFragment instanceof HomeFragment) {
                        Log.e("API_POST", "1");
                        HomeFragment homeFragment = (HomeFragment) currentFragment;
                        homeFragment.updateData(postIndex, postId);
                    }
                }
            }
        }
    }
}