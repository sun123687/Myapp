package com.example.mybook;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mybook.database.AppDatabase;
import com.example.mybook.fragments.BookListFragment;
import com.example.mybook.fragments.LoginFragment;
import com.example.mybook.fragments.ProfileFragment;
import com.example.mybook.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener {

    private AppDatabase database;
    private BottomNavigationView bottomNavigationView;
    private static final String PREF_NAME = "app_settings";
    private static final String DARK_MODE_KEY = "dark_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 在设置内容视图之前应用主题
        applyTheme();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化数据库 - 使用破坏性迁移（会清除所有数据）
        database = AppDatabase.getDevInstance(this);

        // 检查是否有用户，如果没有则显示登录界面，否则显示书籍列表
        if (database.userDao().getUserCount() == 0) {
            loadFragment(new LoginFragment());
        } else {
            loadFragment(new BookListFragment());
            setupBottomNavigation();
        }
    }

    private void applyTheme() {
        // 读取保存的主题设置
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false);
        
        // 应用主题
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_books) {
                fragment = new BookListFragment();
            } else if (itemId == R.id.nav_profile) {
                fragment = new ProfileFragment(); // 使用ProfileFragment
            } else if (itemId == R.id.nav_settings) {
                fragment = new SettingsFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public void onLoginSuccess() {
        loadFragment(new BookListFragment());
        setupBottomNavigation();
    }
} 