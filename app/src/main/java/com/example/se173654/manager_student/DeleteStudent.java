package com.example.se173654.manager_student;

import android.content.Context;
import android.widget.Toast;

import com.example.se173654.services.ApiService;
import com.example.se173654.services.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteStudent {

    private List<Student> studentList;
    private StudentAdapter adapter;
    private Context context;

    public DeleteStudent(List<Student> studentList, StudentAdapter adapter, Context context) {
        this.studentList = studentList;
        this.adapter = adapter;
        this.context = context;
    }

    public void deleteStudent(String id, int position) {
        if (id == null || id.isEmpty()) {
            Toast.makeText(context, "ID sinh viên không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getRetrofitInstance2().create(ApiService.class);
        Call<Void> call = apiService.deleteStudent(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    studentList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, studentList.size());
                    Toast.makeText(context, "Xóa sinh viên thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Không thể xóa sinh viên! Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}