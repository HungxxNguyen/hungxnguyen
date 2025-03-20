package com.example.se173654.manager_major;
import android.content.Context;
import android.content.Intent;
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

public class EditMajorActivity extends AppCompatActivity {

    private EditText nameMajorEditText;
    private Button saveButton;
    private String majorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_major);

        nameMajorEditText = findViewById(R.id.nameMajorEditText);
        saveButton = findViewById(R.id.saveButton);

        Intent intent = getIntent();
        majorId = intent.getStringExtra("MAJOR_ID");
        nameMajorEditText.setText(intent.getStringExtra("MAJOR_NAME"));

        if (majorId == null || majorId.isEmpty()) {
            Toast.makeText(this, "ID chuyên ngành không hợp lệ!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        saveButton.setOnClickListener(v -> {
            String nameMajor = nameMajorEditText.getText().toString();
            if (!nameMajor.isEmpty()) {
                checkDuplicateMajor(majorId, nameMajor);
            } else {
                Toast.makeText(EditMajorActivity.this, "Vui lòng nhập tên chuyên ngành!", Toast.LENGTH_SHORT).show();
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

    private void checkDuplicateMajor(String id, String nameMajor) {
        ApiService apiService = RetrofitClient.getRetrofitInstance2().create(ApiService.class);
        Call<List<Major>> call = apiService.getMajorList();

        call.enqueue(new Callback<List<Major>>() {
            @Override
            public void onResponse(Call<List<Major>> call, Response<List<Major>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Major> majorList = response.body();
                    for (Major major : majorList) {
                        // Kiểm tra trùng tên, nhưng bỏ qua chính chuyên ngành đang chỉnh sửa
                        if (major.getNameMajor().equalsIgnoreCase(nameMajor) && !major.getId().equals(id)) {
                            Toast.makeText(EditMajorActivity.this, "Tên chuyên ngành đã tồn tại!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    // Nếu không trùng, tiến hành cập nhật
                    updateMajor(id, nameMajor);
                } else {
                    Toast.makeText(EditMajorActivity.this, "Không thể kiểm tra danh sách chuyên ngành!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Major>> call, Throwable t) {
                Toast.makeText(EditMajorActivity.this, "Lỗi kết nối khi kiểm tra: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMajor(String id, String nameMajor) {
        ApiService apiService = RetrofitClient.getRetrofitInstance2().create(ApiService.class);
        Major updatedMajor = new Major();
        updatedMajor.setId(id);
        updatedMajor.setNameMajor(nameMajor);

        Call<Major> call = apiService.updateMajor(id, updatedMajor);
        call.enqueue(new Callback<Major>() {
            @Override
            public void onResponse(Call<Major> call, Response<Major> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditMajorActivity.this, "Cập nhật chuyên ngành thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditMajorActivity.this, "Không thể cập nhật chuyên ngành! Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Major> call, Throwable t) {
                Toast.makeText(EditMajorActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}