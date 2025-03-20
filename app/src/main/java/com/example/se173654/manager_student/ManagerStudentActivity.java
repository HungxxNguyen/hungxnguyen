package com.example.se173654.manager_student;

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

public class ManagerStudentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StudentAdapter studentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_student);

        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Chào mừng đến với trang quản lý sinh viên!");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        getStudentList();

        findViewById(R.id.addStudentButton).setOnClickListener(v -> {
            Intent intent = new Intent(ManagerStudentActivity.this, AddStudentActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    private void getStudentList() {
        ApiService apiService = RetrofitClient.getRetrofitInstance2().create(ApiService.class);
        Call<List<Student>> call = apiService.getStudentList();

        call.enqueue(new Callback<List<Student>>() {
            @Override
            public void onResponse(Call<List<Student>> call, Response<List<Student>> response) {
                if (response.isSuccessful()) {
                    List<Student> studentList = response.body();
                    if (studentList != null) {
                        studentAdapter = new StudentAdapter(studentList, ManagerStudentActivity.this);
                        recyclerView.setAdapter(studentAdapter);
                    }
                } else {
                    Toast.makeText(ManagerStudentActivity.this, "Không thể lấy dữ liệu sinh viên!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Student>> call, Throwable t) {
                Toast.makeText(ManagerStudentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 1 || requestCode == 2) && resultCode == RESULT_OK) {
            getStudentList();
        }
    }
}