package com.example.se173654.manager_book;

import android.content.Context;
import android.content.Intent;
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

public class EditBookActivity extends AppCompatActivity {

    private EditText tenSachEditText, ngayXbEditText, theLoaiEditText;
    private Spinner tacGiaSpinner;
    private Button saveButton;
    private String sachId;
    private List<Author> authorList;
    private ArrayAdapter<String> tacGiaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sach);

        tenSachEditText = findViewById(R.id.tenSachEditText);
        ngayXbEditText = findViewById(R.id.ngayXbEditText);
        theLoaiEditText = findViewById(R.id.theLoaiEditText);
        tacGiaSpinner = findViewById(R.id.tacGiaSpinner);
        saveButton = findViewById(R.id.saveButton);

        authorList = new ArrayList<>();
        List<String> tacGiaNames = new ArrayList<>();
        tacGiaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tacGiaNames);
        tacGiaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tacGiaSpinner.setAdapter(tacGiaAdapter);

        Intent intent = getIntent();
        sachId = intent.getStringExtra("SACH_ID");
        tenSachEditText.setText(intent.getStringExtra("SACH_TEN"));
        ngayXbEditText.setText(intent.getStringExtra("SACH_NGAYXB"));
        theLoaiEditText.setText(intent.getStringExtra("SACH_THELOAI"));
        String idTacGiaCurrent = intent.getStringExtra("SACH_ID_TACGIA");

        if (sachId == null || sachId.isEmpty()) {
            Toast.makeText(this, "ID sách không hợp lệ!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getTacGiaList(idTacGiaCurrent);

        saveButton.setOnClickListener(v -> {
            String tenSach = tenSachEditText.getText().toString();
            String ngayXb = ngayXbEditText.getText().toString();
            String theLoai = theLoaiEditText.getText().toString();
            int selectedPosition = tacGiaSpinner.getSelectedItemPosition();

            if (!tenSach.isEmpty() && !ngayXb.isEmpty() && !theLoai.isEmpty() && selectedPosition >= 0) {
                String idTacGia = authorList.get(selectedPosition).getId();
                checkDuplicateSach(sachId, tenSach, idTacGia, ngayXb, theLoai);
            } else {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin và chọn tác giả!", Toast.LENGTH_SHORT).show();
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

    private void getTacGiaList(String idTacGiaCurrent) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Author>> call = apiService.getTacGiaList();
        call.enqueue(new Callback<List<Author>>() {
            @Override
            public void onResponse(Call<List<Author>> call, Response<List<Author>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    authorList.clear();
                    authorList.addAll(response.body());
                    List<String> tacGiaNames = new ArrayList<>();
                    int selectedIndex = -1;
                    for (int i = 0; i < authorList.size(); i++) {
                        Author author = authorList.get(i);
                        tacGiaNames.add(author.getTentacgia());
                        if (author.getId().equals(idTacGiaCurrent)) {
                            selectedIndex = i;
                        }
                    }
                    tacGiaAdapter.clear();
                    tacGiaAdapter.addAll(tacGiaNames);
                    tacGiaAdapter.notifyDataSetChanged();
                    if (selectedIndex >= 0) {
                        tacGiaSpinner.setSelection(selectedIndex);
                    }
                } else {
                    Toast.makeText(EditBookActivity.this, "Không thể lấy danh sách tác giả!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Author>> call, Throwable t) {
                Toast.makeText(EditBookActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkDuplicateSach(String id, String tenSach, String idTacGia, String ngayXb, String theLoai) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Book>> call = apiService.getSachList();
        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Book book : response.body()) {
                        if (book.getTenSach().equals(tenSach) && book.getIdTacgia().equals(idTacGia) && !book.getId().equals(id)) {
                            Toast.makeText(EditBookActivity.this, "Tên sách đã tồn tại cho tác giả này!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    updateSach(id, tenSach, ngayXb, theLoai, idTacGia);
                } else {
                    Toast.makeText(EditBookActivity.this, "Không thể kiểm tra danh sách sách!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Toast.makeText(EditBookActivity.this, "Lỗi kết nối khi kiểm tra sách: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSach(String id, String tenSach, String ngayXb, String theLoai, String idTacGia) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Book updatedBook = new Book();
        updatedBook.setId(id);
        updatedBook.setTenSach(tenSach);
        updatedBook.setNgayXb(ngayXb);
        updatedBook.setTheloai(theLoai);
        updatedBook.setIdTacgia(idTacGia);

        Call<Book> call = apiService.updateSach(id, updatedBook);
        call.enqueue(new Callback<Book>() {
            @Override
            public void onResponse(Call<Book> call, Response<Book> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditBookActivity.this, "Cập nhật sách thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditBookActivity.this, "Không thể cập nhật sách! Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Book> call, Throwable t) {
                Toast.makeText(EditBookActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}