package com.example.se173654.manager_author;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.se173654.services.ApiService;
import com.example.se173654.services.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteAuthor {

    private List<Author> authorList;
    private AuthorAdapter adapter;
    private Context context;

    public DeleteAuthor(List<Author> authorList, AuthorAdapter adapter, Context context) {
        this.authorList = authorList;
        this.adapter = adapter;
        this.context = context;
    }

    public void deleteTacGia(String id, int position) {
        if (id == null || id.isEmpty()) {
            Toast.makeText(context, "ID tác giả không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<Void> call = apiService.deleteTacGia(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    authorList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, authorList.size());
                    Toast.makeText(context, "Xóa tác giả thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    // Thêm thông tin lỗi từ response
                    String errorMsg = "Không thể xóa tác giả! Mã lỗi: " + response.code();
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e("DeleteAuthor", "Lỗi: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("DeleteAuthor", "Lỗi kết nối: ", t);
            }
        });
    }
}