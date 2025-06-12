package com.example.mybook.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mybook.R;
import com.example.mybook.database.AppDatabase;
import com.example.mybook.database.User;

public class LoginFragment extends Fragment {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private AppDatabase database;
    private LoginListener loginListener;

    public interface LoginListener {
        void onLoginSuccess();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LoginListener) {
            loginListener = (LoginListener) context;
        } else {
            throw new RuntimeException(context + " must implement LoginListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        database = AppDatabase.getInstance(requireContext());
        
        usernameEditText = view.findViewById(R.id.username_edit_text);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        loginButton = view.findViewById(R.id.login_button);
        registerButton = view.findViewById(R.id.register_button);

        loginButton.setOnClickListener(v -> login());
        registerButton.setOnClickListener(v -> register());

        return view;
    }

    private void login() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = database.userDao().login(username, password);
        if (user != null) {
            Toast.makeText(requireContext(), "登录成功", Toast.LENGTH_SHORT).show();
            loginListener.onLoginSuccess();
        } else {
            Toast.makeText(requireContext(), "用户名或密码错误", Toast.LENGTH_SHORT).show();
        }
    }

    private void register() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        User newUser = new User(username, password);
        long result = database.userDao().insert(newUser);

        if (result > 0) {
            Toast.makeText(requireContext(), "注册成功，请登录", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "注册失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }
} 