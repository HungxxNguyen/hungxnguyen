package com.example.se173654.manager_book;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se173654.R;
import com.example.se173654.services.ApiService;
import com.example.se173654.services.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerBookActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_sach);

        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Chào mừng đến với trang quản lý sách!");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        getSachList();

        findViewById(R.id.addSachButton).setOnClickListener(v -> {
            Intent intent = new Intent(ManagerBookActivity.this, AddBookActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    private void getSachList() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Book>> call = apiService.getSachList();

        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                if (response.isSuccessful()) {
                    List<Book> bookList = response.body();
                    if (bookList != null) {
                        bookAdapter = new BookAdapter(bookList, ManagerBookActivity.this);
                        recyclerView.setAdapter(bookAdapter);
                    }
                } else {
                    Toast.makeText(ManagerBookActivity.this, "Không thể lấy dữ liệu sách!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Toast.makeText(ManagerBookActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 1 || requestCode == 2) && resultCode == RESULT_OK) {
            getSachList();
        }
    }
}