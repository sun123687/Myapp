package com.example.mybook.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "reading_progress",
        foreignKeys = @ForeignKey(entity = Book.class,
                parentColumns = "id",
                childColumns = "bookId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("bookId")})
public class ReadingProgress {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int bookId;
    private int pagesRead;
    private long date;

    public ReadingProgress(int bookId, int pagesRead, long date) {
        this.bookId = bookId;
        this.pagesRead = pagesRead;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getPagesRead() {
        return pagesRead;
    }

    public void setPagesRead(int pagesRead) {
        this.pagesRead = pagesRead;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
} 