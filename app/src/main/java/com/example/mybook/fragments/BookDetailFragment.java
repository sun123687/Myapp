package com.example.mybook.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybook.R;
import com.example.mybook.adapters.ProgressAdapter;
import com.example.mybook.database.AppDatabase;
import com.example.mybook.database.Book;
import com.example.mybook.database.ReadingProgress;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class BookDetailFragment extends Fragment implements ProgressAdapter.OnProgressDeleteListener {

    private TextView titleTextView;
    private TextView totalPagesTextView;
    private TextView pagesReadTextView;
    private ProgressBar readingProgressBar;
    private RecyclerView progressRecyclerView;
    private ProgressAdapter progressAdapter;
    private List<ReadingProgress> progressList;
    private FloatingActionButton addProgressFab;
    
    private AppDatabase database;
    private int bookId;
    private Book currentBook;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_detail, container, false);

        database = AppDatabase.getInstance(requireContext());
        
        if (getArguments() != null) {
            bookId = getArguments().getInt("bookId", -1);
            if (bookId == -1) {
                Toast.makeText(requireContext(), "书籍ID无效", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        }
        
        titleTextView = view.findViewById(R.id.book_title_text_view);
        totalPagesTextView = view.findViewById(R.id.total_pages_text_view);
        pagesReadTextView = view.findViewById(R.id.pages_read_text_view);
        readingProgressBar = view.findViewById(R.id.reading_progress_bar);
        
        progressRecyclerView = view.findViewById(R.id.progress_recycler_view);
        progressRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        progressList = new ArrayList<>();
        progressAdapter = new ProgressAdapter(progressList, this);
        progressRecyclerView.setAdapter(progressAdapter);
        
        addProgressFab = view.findViewById(R.id.add_progress_fab);
        addProgressFab.setOnClickListener(v -> showAddProgressDialog());
        
        loadBookDetails();
        loadReadingProgress();
        
        return view;
    }

    private void loadBookDetails() {
        currentBook = database.bookDao().getBookById(bookId);
        if (currentBook != null) {
            titleTextView.setText(currentBook.getTitle());
            totalPagesTextView.setText("总页数: " + currentBook.getTotalPages());
            
            int pagesRead = database.readingProgressDao().getTotalPagesReadForBook(bookId);
            pagesReadTextView.setText("已读页数: " + pagesRead);
            
            int progress = (int) (((float) pagesRead / currentBook.getTotalPages()) * 100);
            readingProgressBar.setProgress(Math.min(progress, 100));
        }
    }

    private void loadReadingProgress() {
        progressList.clear();
        progressList.addAll(database.readingProgressDao().getProgressForBook(bookId));
        progressAdapter.notifyDataSetChanged();
    }

    private void showAddProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_progress, null);
        builder.setView(dialogView);

        EditText pagesEditText = dialogView.findViewById(R.id.progress_pages_edit_text);
        Button addButton = dialogView.findViewById(R.id.add_progress_button);

        // 获取当前已读总页数
        int currentTotalRead = database.readingProgressDao().getTotalPagesReadForBook(bookId);
        int remainingPages = currentBook != null ? currentBook.getTotalPages() - currentTotalRead : 0;

        // 显示当前书籍的总页数和剩余页数信息
        TextView totalPagesInfoText = dialogView.findViewById(R.id.total_pages_info);
        if (totalPagesInfoText != null && currentBook != null) {
            totalPagesInfoText.setText(String.format("书籍总页数: %d，已读页数: %d，剩余页数: %d", 
                currentBook.getTotalPages(), currentTotalRead, remainingPages));
        }

        AlertDialog dialog = builder.create();

        addButton.setOnClickListener(v -> {
            String pagesStr = pagesEditText.getText().toString().trim();

            if (pagesStr.isEmpty()) {
                Toast.makeText(requireContext(), "请输入页数", Toast.LENGTH_SHORT).show();
                return;
            }

            int pages;
            try {
                pages = Integer.parseInt(pagesStr);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "页数必须是数字", Toast.LENGTH_SHORT).show();
                return;
            }

            // 验证已读页数必须为正数
            if (pages <= 0) {
                Toast.makeText(requireContext(), "已读页数必须大于0", Toast.LENGTH_SHORT).show();
                return;
            }

            // 验证添加后的总页数不超过书籍总页数
            if (currentBook != null && (currentTotalRead + pages) > currentBook.getTotalPages()) {
                Toast.makeText(requireContext(), 
                    String.format("添加后总阅读页数(%d)将超过书籍总页数(%d)，最多还能添加%d页", 
                        currentTotalRead + pages, currentBook.getTotalPages(), remainingPages), 
                    Toast.LENGTH_LONG).show();
                return;
            }

            ReadingProgress newProgress = new ReadingProgress(bookId, pages, System.currentTimeMillis());
            long result = database.readingProgressDao().insert(newProgress);

            if (result > 0) {
                Toast.makeText(requireContext(), "添加成功", Toast.LENGTH_SHORT).show();
                loadBookDetails();
                loadReadingProgress();
                dialog.dismiss();
            } else {
                Toast.makeText(requireContext(), "添加失败", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
    
    @Override
    public void onProgressDelete(ReadingProgress progress, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("删除确认")
                .setMessage("确定要删除这条阅读记录吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    database.readingProgressDao().delete(progress);
                    progressList.remove(position);
                    progressAdapter.notifyItemRemoved(position);
                    loadBookDetails(); // 更新阅读进度
                    Toast.makeText(requireContext(), "删除成功", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}