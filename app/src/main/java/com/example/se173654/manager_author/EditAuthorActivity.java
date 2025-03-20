package com.example.se173654.manager_author;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.se173654.services.ApiService;
import com.example.se173654.R;
import com.example.se173654.services.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAuthorActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, addressEditText, phoneEditText;
    private Button saveButton;
    private String authorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_author);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        addressEditText = findViewById(R.id.addressEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        saveButton = findViewById(R.id.saveButton);

        Intent intent = getIntent();
        authorId = intent.getStringExtra("TACGIA_ID");
        nameEditText.setText(intent.getStringExtra("TACGIA_NAME"));
        emailEditText.setText(intent.getStringExtra("TACGIA_EMAIL"));
        addressEditText.setText(intent.getStringExtra("TACGIA_ADDRESS"));
        phoneEditText.setText(String.valueOf(intent.getIntExtra("TACGIA_PHONE", 0)));

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String address = addressEditText.getText().toString();
            String phone = phoneEditText.getText().toString();

            if (!name.isEmpty() && !email.isEmpty() && !address.isEmpty() && !phone.isEmpty()) {
                checkDuplicateName(authorId, name, email, address, phone); // Sửa id thành authorId
            } else {
                Toast.makeText(EditAuthorActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void checkDuplicateName(String id, String name, String email, String address, String phone) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Author>> call = apiService.getTacGiaList();

        call.enqueue(new Callback<List<Author>>() {
            @Override
            public void onResponse(Call<List<Author>> call, Response<List<Author>> response) {
                if (response.isSuccessful()) {
                    List<Author> authorList = response.body();
                    if (authorList != null) {
                        for (Author author : authorList) {
                            if (author.getTentacgia().equals(name) && !author.getId().equals(id)) {
                                Toast.makeText(EditAuthorActivity.this, "Tên tác giả đã tồn tại!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        updateTacGia(id, name, email, address, phone);
                    }
                } else {
                    Toast.makeText(EditAuthorActivity.this, "Không thể kiểm tra tên tác giả!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Author>> call, Throwable t) {
                Toast.makeText(EditAuthorActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTacGia(String id, String name, String email, String address, String phone) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Author updatedAuthor = new Author();
        updatedAuthor.setId(id);
        updatedAuthor.setTentacgia(name);
        updatedAuthor.setEmail(email);
        updatedAuthor.setDiachi(address);
        updatedAuthor.setDienthoai(Integer.parseInt(phone));

        Call<Author> call = apiService.updateTacGia(id, updatedAuthor);

        call.enqueue(new Callback<Author>() {
            @Override
            public void onResponse(Call<Author> call, Response<Author> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditAuthorActivity.this, "Cập nhật tác giả thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditAuthorActivity.this, "Không thể cập nhật tác giả!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Author> call, Throwable t) {
                Toast.makeText(EditAuthorActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}