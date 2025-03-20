package com.example.se173654.manager_book;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.se173654.services.ApiService;
import com.example.se173654.services.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteBook {

    private List<Book> bookList;
    private BookAdapter adapter;
    private Context context;

    public DeleteBook(List<Book> bookList, BookAdapter adapter, Context context) {
        this.bookList = bookList;
        this.adapter = adapter;
        this.context = context;
    }

    public void deleteSach(String id, int position) {
        if (id == null || id.isEmpty()) {
            Toast.makeText(context, "ID sách không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("DeleteSach", "ID gửi đi: " + id);
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<Void> call = apiService.deleteSach(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    bookList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, bookList.size());
                    Toast.makeText(context, "Xóa sách thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMsg = "Không thể xóa sách! Mã lỗi: " + response.code();
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e("DeleteSach", "Lỗi: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("DeleteSach", "Lỗi kết nối: ", t);
            }
        });
    }
}