package com.example.se173654.manager_author;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se173654.services.ApiService;
import com.example.se173654.R;
import com.example.se173654.services.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerAuthorActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AuthorAdapter authorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_tacgia);

        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Chào mừng đến với trang quản lý tác giả!");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        getTacGiaList();

        findViewById(R.id.addAuthorButton).setOnClickListener(v -> {
            Intent intent = new Intent(ManagerAuthorActivity.this, AddAuthorActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    private void getTacGiaList() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Author>> call = apiService.getTacGiaList();

        call.enqueue(new Callback<List<Author>>() {
            @Override
            public void onResponse(Call<List<Author>> call, Response<List<Author>> response) {
                if (response.isSuccessful()) {
                    List<Author> authorList = response.body();
                    if (authorList != null) {
                        authorAdapter = new AuthorAdapter(authorList, ManagerAuthorActivity.this);
                        recyclerView.setAdapter(authorAdapter);
                    }
                } else {
                    Toast.makeText(ManagerAuthorActivity.this, "Không thể lấy dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Author>> call, Throwable t) {
                Toast.makeText(ManagerAuthorActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 1 || requestCode == 2) && resultCode == RESULT_OK) {
            getTacGiaList();
        }
    }
}