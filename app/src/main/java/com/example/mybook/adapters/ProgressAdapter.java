package com.example.mybook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybook.R;
import com.example.mybook.database.ReadingProgress;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ProgressViewHolder> {

    private List<ReadingProgress> progressList;
    private SimpleDateFormat dateFormat;
    private OnProgressDeleteListener deleteListener;

    public interface OnProgressDeleteListener {
        void onProgressDelete(ReadingProgress progress, int position);
    }

    public ProgressAdapter(List<ReadingProgress> progressList, OnProgressDeleteListener deleteListener) {
        this.progressList = progressList;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false);
        return new ProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {
        ReadingProgress progress = progressList.get(position);
        holder.pagesTextView.setText("阅读页数: " + progress.getPagesRead());
        
        String formattedDate = dateFormat.format(new Date(progress.getDate()));
        holder.dateTextView.setText(formattedDate);
        
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onProgressDelete(progress, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return progressList.size();
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        TextView pagesTextView;
        TextView dateTextView;
        ImageButton deleteButton;

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            pagesTextView = itemView.findViewById(R.id.progress_pages);
            dateTextView = itemView.findViewById(R.id.progress_date);
            deleteButton = itemView.findViewById(R.id.delete_progress_button);
        }
    }
} 