package com.example.se173654.manager_author;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.appcompat.app.AppCompatActivity;

import com.example.se173654.services.ApiService;
import com.example.se173654.R;
import com.example.se173654.services.RetrofitClient;

public class AddAuthorActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, addressEditText, phoneEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_author);

        // Lấy các thành phần giao diện
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        addressEditText = findViewById(R.id.addressEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        saveButton = findViewById(R.id.saveButton);

        // Xử lý sự kiện khi nhấn "Lưu"
        saveButton.setOnClickListener(v -> {
            // Lấy dữ liệu từ các trường nhập liệu
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String address = addressEditText.getText().toString();
            String phone = phoneEditText.getText().toString();

            if (!name.isEmpty() && !email.isEmpty() && !address.isEmpty() && !phone.isEmpty()) {
                // Gọi API để thêm tác giả mới
                addTacGia(name, email, address, phone);
            } else {
                Toast.makeText(AddAuthorActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện nhấn nút Back
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Quay lại ManagerTacGiaActivity khi nhấn nút back
            onBackPressed();
        });
    }

    // Phương thức để gọi API thêm tác giả mới
    private void addTacGia(String name, String email, String address, String phone) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Tạo đối tượng tác giả mới
        Author newAuthor = new Author();
        newAuthor.setTentacgia(name);
        newAuthor.setEmail(email);
        newAuthor.setDiachi(address);
        newAuthor.setDienthoai(Integer.parseInt(phone));

        // Gọi API để thêm tác giả mới
        Call<Author> call = apiService.addTacGia(newAuthor);

        call.enqueue(new Callback<Author>() {
            @Override
            public void onResponse(Call<Author> call, Response<Author> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddAuthorActivity.this, "Thêm tác giả thành công!", Toast.LENGTH_SHORT).show();
                    // Trả kết quả thành công về ManagerTacGiaActivity
                    setResult(RESULT_OK);
                    finish();  // Đóng activity khi thêm thành công
                } else {
                    Toast.makeText(AddAuthorActivity.this, "Không thể thêm tác giả!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Author> call, Throwable t) {
                Toast.makeText(AddAuthorActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
