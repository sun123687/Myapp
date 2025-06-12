package com.example.mybook.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mybook.R;
import com.example.mybook.database.AppDatabase;
import com.example.mybook.database.User;

public class ProfileFragment extends Fragment {
    
    private AppDatabase database;
    private TextView usernameTextView;
    private TextView userEmailTextView;
    private TextView userIdTextView;
    private TextView booksCountTextView;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        database = AppDatabase.getInstance(requireContext());
        
        usernameTextView = view.findViewById(R.id.username_text);
        userEmailTextView = view.findViewById(R.id.email_text);
        userIdTextView = view.findViewById(R.id.user_id_text);
        booksCountTextView = view.findViewById(R.id.books_count_text);
        
        loadUserInfo();
        
        return view;
    }
    
    private void loadUserInfo() {
        // 获取第一个用户（简化版，实际应用中应该获取当前登录用户）
        User user = database.userDao().getFirstUser();
        if (user != null) {
            usernameTextView.setText("用户名: " + user.getUsername());
            userEmailTextView.setText("邮箱: " + user.getEmail());
            userIdTextView.setText("用户ID: " + user.getId());
            
            // 获取该用户的书籍数量
            int booksCount = database.bookDao().getBookCount();
            booksCountTextView.setText("书籍数量: " + booksCount);
        }
    }
} 