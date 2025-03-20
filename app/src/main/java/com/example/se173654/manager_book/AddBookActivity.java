package com.example.se173654.manager_book;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.se173654.services.ApiService;
import com.example.se173654.R;
import com.example.se173654.services.RetrofitClient;
import com.example.se173654.manager_author.Author;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBookActivity extends AppCompatActivity {

    private EditText tenSachEditText, ngayXbEditText, theLoaiEditText;
    private Spinner tacGiaSpinner;
    private Button saveButton;
    private List<Author> authorList;
    private ArrayAdapter<String> tacGiaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sach);

        // Khởi tạo các thành phần giao diện
        tenSachEditText = findViewById(R.id.tenSachEditText);
        ngayXbEditText = findViewById(R.id.ngayXbEditText);
        theLoaiEditText = findViewById(R.id.theLoaiEditText);
        tacGiaSpinner = findViewById(R.id.tacGiaSpinner);
        saveButton = findViewById(R.id.saveButton);

        // Khởi tạo danh sách tác giả
        authorList = new ArrayList<>();
        List<String> tacGiaNames = new ArrayList<>();
        tacGiaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tacGiaNames);
        tacGiaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tacGiaSpinner.setAdapter(tacGiaAdapter);

        // Gọi API để lấy danh sách tác giả
        getTacGiaList();

        // Xử lý nút Save
        saveButton.setOnClickListener(v -> {
            String tenSach = tenSachEditText.getText().toString();
            String ngayXb = ngayXbEditText.getText().toString();
            String theLoai = theLoaiEditText.getText().toString();
            int selectedPosition = tacGiaSpinner.getSelectedItemPosition();

            if (!tenSach.isEmpty() && !ngayXb.isEmpty() && !theLoai.isEmpty() && selectedPosition >= 0) {
                String idTacGia = authorList.get(selectedPosition).getId();
                checkDuplicateSach(tenSach, idTacGia, ngayXb, theLoai);
            } else {
                Toast.makeText(AddBookActivity.this, "Vui lòng điền đầy đủ thông tin và chọn tác giả!", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút Back
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());
    }

    // Ghi đè onTouchEvent để đóng bàn phím khi bấm ra ngoài
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

    private void getTacGiaList() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Author>> call = apiService.getTacGiaList();

        call.enqueue(new Callback<List<Author>>() {
            @Override
            public void onResponse(Call<List<Author>> call, Response<List<Author>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    authorList.clear();
                    authorList.addAll(response.body());
                    List<String> tacGiaNames = new ArrayList<>();
                    for (Author author : authorList) {
                        tacGiaNames.add(author.getTentacgia());
                    }
                    tacGiaAdapter.clear();
                    tacGiaAdapter.addAll(tacGiaNames);
                    tacGiaAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AddBookActivity.this, "Không thể lấy danh sách tác giả!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Author>> call, Throwable t) {
                Toast.makeText(AddBookActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkDuplicateSach(String tenSach, String idTacGia, String ngayXb, String theLoai) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Book>> call = apiService.getSachList();

        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Book> bookList = response.body();
                    for (Book book : bookList) {
                        if (book.getTenSach().equals(tenSach) && book.getIdTacgia().equals(idTacGia)) {
                            Toast.makeText(AddBookActivity.this, "Tên sách đã tồn tại cho tác giả này!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    // Nếu không trùng, tiến hành thêm sách
                    addSach(tenSach, ngayXb, theLoai, idTacGia);
                } else {
                    Toast.makeText(AddBookActivity.this, "Không thể kiểm tra danh sách sách!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Toast.makeText(AddBookActivity.this, "Lỗi kết nối khi kiểm tra sách: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addSach(String tenSach, String ngayXb, String theLoai, String idTacGia) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Book newBook = new Book();
        newBook.setTenSach(tenSach);
        newBook.setNgayXb(ngayXb);
        newBook.setTheloai(theLoai);
        newBook.setIdTacgia(idTacGia);

        Call<Book> call = apiService.addSach(newBook);

        call.enqueue(new Callback<Book>() {
            @Override
            public void onResponse(Call<Book> call, Response<Book> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddBookActivity.this, "Thêm sách thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddBookActivity.this, "Không thể thêm sách! Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Book> call, Throwable t) {
                Toast.makeText(AddBookActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}