package com.example.se173654.manager_major;

import android.content.Context;
import android.widget.Toast;

import com.example.se173654.services.ApiService;
import com.example.se173654.services.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteMajor {

    private List<Major> majorList;
    private MajorAdapter adapter;
    private Context context;

    public DeleteMajor(List<Major> majorList, MajorAdapter adapter, Context context) {
        this.majorList = majorList;
        this.adapter = adapter;
        this.context = context;
    }

    public void deleteMajor(String id, int position) {
        if (id == null || id.isEmpty()) {
            Toast.makeText(context, "ID chuyên ngành không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getRetrofitInstance2().create(ApiService.class);
        Call<Void> call = apiService.deleteMajor(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    majorList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, majorList.size());
                    Toast.makeText(context, "Xóa chuyên ngành thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Không thể xóa chuyên ngành! Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}