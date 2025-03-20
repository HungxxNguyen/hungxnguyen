package com.example.se173654.manager_major;

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

public class ManagerMajorActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MajorAdapter majorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_major);

        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Chào mừng đến với trang quản lý chuyên ngành!");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        getMajorList();

        findViewById(R.id.addMajorButton).setOnClickListener(v -> {
            Intent intent = new Intent(ManagerMajorActivity.this, AddMajorActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    private void getMajorList() {
        ApiService apiService = RetrofitClient.getRetrofitInstance2().create(ApiService.class);
        Call<List<Major>> call = apiService.getMajorList();

        call.enqueue(new Callback<List<Major>>() {
            @Override
            public void onResponse(Call<List<Major>> call, Response<List<Major>> response) {
                if (response.isSuccessful()) {
                    List<Major> majorList = response.body();
                    if (majorList != null) {
                        majorAdapter = new MajorAdapter(majorList, ManagerMajorActivity.this);
                        recyclerView.setAdapter(majorAdapter);
                    }
                } else {
                    Toast.makeText(ManagerMajorActivity.this, "Không thể lấy dữ liệu chuyên ngành!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Major>> call, Throwable t) {
                Toast.makeText(ManagerMajorActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 1 || requestCode == 2) && resultCode == RESULT_OK) {
            getMajorList();
        }
    }
}