package com.example.se173654.manager_student;

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

import com.example.se173654.R;
import com.example.se173654.manager_major.Major;
import com.example.se173654.services.ApiService;
import com.example.se173654.services.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddStudentActivity extends AppCompatActivity {

    private EditText nameEditText, dateEditText, emailEditText, addressEditText;
    private Spinner genderSpinner, majorSpinner;
    private Button saveButton;
    private List<Major> majorList;
    private ArrayAdapter<String> majorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        nameEditText = findViewById(R.id.nameEditText);
        dateEditText = findViewById(R.id.dateEditText);
        emailEditText = findViewById(R.id.emailEditText);
        addressEditText = findViewById(R.id.addressEditText);
        genderSpinner = findViewById(R.id.genderSpinner);
        majorSpinner = findViewById(R.id.majorSpinner);
        saveButton = findViewById(R.id.saveButton);

        // Thiết lập Spinner cho giới tính
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Thiết lập Spinner cho chuyên ngành
        majorList = new ArrayList<>();
        List<String> majorNames = new ArrayList<>();
        majorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, majorNames);
        majorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        majorSpinner.setAdapter(majorAdapter);
        loadMajorList();

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String date = dateEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String address = addressEditText.getText().toString();
            String gender = genderSpinner.getSelectedItem().toString();
            int selectedMajorPosition = majorSpinner.getSelectedItemPosition();

            if (!name.isEmpty() && !date.isEmpty() && !email.isEmpty() && !address.isEmpty() && selectedMajorPosition >= 0) {
                String idMajor = majorList.get(selectedMajorPosition).getId();
                checkDuplicateStudent(name, date, gender, email, address, idMajor);
            } else {
                Toast.makeText(AddStudentActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
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

    private void loadMajorList() {
        ApiService apiService = RetrofitClient.getRetrofitInstance2().create(ApiService.class);
        Call<List<Major>> call = apiService.getMajorList();
        call.enqueue(new Callback<List<Major>>() {
            @Override
            public void onResponse(Call<List<Major>> call, Response<List<Major>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    majorList.clear();
                    majorList.addAll(response.body());
                    List<String> majorNames = new ArrayList<>();
                    for (Major major : majorList) {
                        majorNames.add(major.getNameMajor());
                    }
                    majorAdapter.clear();
                    majorAdapter.addAll(majorNames);
                    majorAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AddStudentActivity.this, "Không thể lấy danh sách chuyên ngành!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Major>> call, Throwable t) {
                Toast.makeText(AddStudentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkDuplicateStudent(String name, String date, String gender, String email, String address, String idMajor) {
        ApiService apiService = RetrofitClient.getRetrofitInstance2().create(ApiService.class);
        Call<List<Student>> call = apiService.getStudentList();

        call.enqueue(new Callback<List<Student>>() {
            @Override
            public void onResponse(Call<List<Student>> call, Response<List<Student>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Student> studentList = response.body();
                    for (Student student : studentList) {
                        if (student.getName().equalsIgnoreCase(name)) {
                            Toast.makeText(AddStudentActivity.this, "Tên sinh viên đã tồn tại!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    // Nếu không trùng, tiến hành thêm sinh viên
                    addStudent(name, date, gender, email, address, idMajor);
                } else {
                    Toast.makeText(AddStudentActivity.this, "Không thể kiểm tra danh sách sinh viên!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Student>> call, Throwable t) {
                Toast.makeText(AddStudentActivity.this, "Lỗi kết nối khi kiểm tra: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addStudent(String name, String date, String gender, String email, String address, String idMajor) {
        ApiService apiService = RetrofitClient.getRetrofitInstance2().create(ApiService.class);
        Student newStudent = new Student();
        newStudent.setName(name);
        newStudent.setDate(date);
        newStudent.setGender(gender);
        newStudent.setEmail(email);
        newStudent.setAddress(address);
        newStudent.setIdMajor(idMajor);

        Call<Student> call = apiService.addStudent(newStudent);
        call.enqueue(new Callback<Student>() {
            @Override
            public void onResponse(Call<Student> call, Response<Student> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddStudentActivity.this, "Thêm sinh viên thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddStudentActivity.this, "Không thể thêm sinh viên! Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Student> call, Throwable t) {
                Toast.makeText(AddStudentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}