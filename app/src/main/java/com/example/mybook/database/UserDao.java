package com.example.mybook.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    User login(String username, String password);

    @Query("SELECT COUNT(*) FROM users")
    int getUserCount();

    @Query("SELECT * FROM users LIMIT 1")
    User getFirstUser();
} 