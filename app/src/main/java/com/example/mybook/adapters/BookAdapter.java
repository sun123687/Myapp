package com.example.mybook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybook.R;
import com.example.mybook.database.Book;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookList;
    private OnBookClickListener listener;
    private OnBookDeleteListener deleteListener;

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }
    
    public interface OnBookDeleteListener {
        void onBookDelete(Book book, int position);
    }

    public BookAdapter(List<Book> bookList, OnBookClickListener listener, OnBookDeleteListener deleteListener) {
        this.bookList = bookList;
        this.listener = listener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.titleTextView.setText(book.getTitle());
        holder.pagesTextView.setText("总页数: " + book.getTotalPages());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookClick(book);
            }
        });
        
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onBookDelete(book, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView pagesTextView;
        ImageButton deleteButton;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.book_title);
            pagesTextView = itemView.findViewById(R.id.book_pages);
            deleteButton = itemView.findViewById(R.id.delete_book_button);
        }
    }
} 