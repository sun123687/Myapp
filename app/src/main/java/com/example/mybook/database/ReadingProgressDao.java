package com.example.mybook.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ReadingProgressDao {
    @Insert
    long insert(ReadingProgress readingProgress);

    @Update
    void update(ReadingProgress readingProgress);

    @Delete
    void delete(ReadingProgress readingProgress);

    @Query("SELECT * FROM reading_progress WHERE bookId = :bookId ORDER BY date DESC")
    List<ReadingProgress> getProgressForBook(int bookId);

    @Query("SELECT SUM(pagesRead) FROM reading_progress WHERE bookId = :bookId")
    int getTotalPagesReadForBook(int bookId);
} 