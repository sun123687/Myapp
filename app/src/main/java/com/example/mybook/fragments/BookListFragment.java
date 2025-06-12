package com.example.mybook.fragments;

import android.app.AlertDialog;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybook.R;
import com.example.mybook.adapters.BookAdapter;
import com.example.mybook.database.AppDatabase;
import com.example.mybook.database.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class BookListFragment extends Fragment implements BookAdapter.OnBookClickListener, BookAdapter.OnBookDeleteListener {

    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<Book> bookList;
    private AppDatabase database;
    private FloatingActionButton addBookFab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        database = AppDatabase.getInstance(requireContext());
        
        recyclerView = view.findViewById(R.id.books_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        bookList = new ArrayList<>();
        bookAdapter = new BookAdapter(bookList, this, this);
        recyclerView.setAdapter(bookAdapter);
        
        addBookFab = view.findViewById(R.id.add_book_fab);
        addBookFab.setOnClickListener(v -> showAddBookDialog());
        
        loadBooks();
        
        return view;
    }

    private void loadBooks() {
        bookList.clear();
        bookList.addAll(database.bookDao().getAllBooks());
        bookAdapter.notifyDataSetChanged();
    }

    private void showAddBookDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_book, null);
        builder.setView(dialogView);

        EditText titleEditText = dialogView.findViewById(R.id.book_title_edit_text);
        EditText pagesEditText = dialogView.findViewById(R.id.book_pages_edit_text);
        Button addButton = dialogView.findViewById(R.id.add_book_button);

        AlertDialog dialog = builder.create();

        addButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String pagesStr = pagesEditText.getText().toString().trim();

            if (title.isEmpty() || pagesStr.isEmpty()) {
                Toast.makeText(requireContext(), "请填写所有字段", Toast.LENGTH_SHORT).show();
                return;
            }

            int totalPages;
            try {
                totalPages = Integer.parseInt(pagesStr);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "页数必须是数字", Toast.LENGTH_SHORT).show();
                return;
            }

            Book newBook = new Book(title, totalPages);
            long result = database.bookDao().insert(newBook);

            if (result > 0) {
                Toast.makeText(requireContext(), "添加成功", Toast.LENGTH_SHORT).show();
                loadBooks();
                dialog.dismiss();
            } else {
                Toast.makeText(requireContext(), "添加失败", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    public void onBookClick(Book book) {
        Bundle bundle = new Bundle();
        bundle.putInt("bookId", book.getId());
        
        BookDetailFragment detailFragment = new BookDetailFragment();
        detailFragment.setArguments(bundle);
        
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }
    
    @Override
    public void onBookDelete(Book book, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("删除确认")
                .setMessage("确定要删除《" + book.getTitle() + "》及其所有阅读记录吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    database.bookDao().delete(book);
                    bookList.remove(position);
                    bookAdapter.notifyItemRemoved(position);
                    Toast.makeText(requireContext(), "删除成功", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }
} 