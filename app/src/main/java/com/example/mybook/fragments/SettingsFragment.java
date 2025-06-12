package com.example.mybook.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.mybook.R;

public class SettingsFragment extends Fragment {

    private Switch darkModeSwitch;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "app_settings";
    private static final String DARK_MODE_KEY = "dark_mode";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // 设置标题
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("设置");

        // 初始化SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        
        // 初始化控件
        darkModeSwitch = view.findViewById(R.id.dark_mode_switch);
        TextView darkModeText = view.findViewById(R.id.dark_mode_text);
        Button aboutButton = view.findViewById(R.id.about_button);
        
        // 设置开关状态
        boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false);
        darkModeSwitch.setChecked(isDarkMode);
        
        // 设置开关监听器
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 保存设置
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(DARK_MODE_KEY, isChecked);
            editor.apply();
            
            // 应用夜间模式
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(requireContext(), "已切换到夜间模式", Toast.LENGTH_SHORT).show();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(requireContext(), "已切换到白天模式", Toast.LENGTH_SHORT).show();
            }
        });
        
        // 设置关于按钮点击事件
        aboutButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "我的读书 App v1.0", Toast.LENGTH_SHORT).show();
        });
        
        return view;
    }
} 