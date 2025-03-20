package com.example.se173654.manager_major;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.se173654.R;
import com.example.se173654.services.ApiService;
import com.example.se173654.services.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMajorActivity extends AppCompatActivity {

    private EditText nameMajorEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_major);

        nameMajorEditText = findViewById(R.id.nameMajorEditText);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> {
            String nameMajor = nameMajorEditText.getText().toString();
            if (!nameMajor.isEmpty()) {
                checkDuplicateMajor(nameMajor);
            } else {
                Toast.makeText(AddMajorActivity.this, "Vui lòng nhập tên chuyên ngành!", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                currentFocus.clearFocus();
            }
        }
        return super.onTouchEvent(event);
    }

    private void checkDuplicateMajor(String nameMajor) {
        ApiService apiService = RetrofitClient.getRetrofitInstance2().create(ApiService.class);
        Call<List<Major>> call = apiService.getMajorList();

        call.enqueue(new Callback<List<Major>>() {
            @Override
            public void onResponse(Call<List<Major>> call, Response<List<Major>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Major> majorList = response.body();
                    for (Major major : majorList) {
                        if (major.getNameMajor().equalsIgnoreCase(nameMajor)) {
                            Toast.makeText(AddMajorActivity.this, "Tên chuyên ngành đã tồn tại!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    // Nếu không trùng, tiến hành thêm chuyên ngành
                    addMajor(nameMajor);
                } else {
                    Toast.makeText(AddMajorActivity.this, "Không thể kiểm tra danh sách chuyên ngành!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Major>> call, Throwable t) {
                Toast.makeText(AddMajorActivity.this, "Lỗi kết nối khi kiểm tra: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMajor(String nameMajor) {
        ApiService apiService = RetrofitClient.getRetrofitInstance2().create(ApiService.class);
        Major newMajor = new Major();
        newMajor.setNameMajor(nameMajor);

        Call<Major> call = apiService.addMajor(newMajor);
        call.enqueue(new Callback<Major>() {
            @Override
            public void onResponse(Call<Major> call, Response<Major> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddMajorActivity.this, "Thêm chuyên ngành thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddMajorActivity.this, "Không thể thêm chuyên ngành! Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Major> call, Throwable t) {
                Toast.makeText(AddMajorActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}