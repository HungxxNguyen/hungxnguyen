package com.example.se173654.manager_book;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se173654.services.ApiService;
import com.example.se173654.R;
import com.example.se173654.services.RetrofitClient;
import com.example.se173654.manager_author.Author;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.SachViewHolder> {

    private final List<Book> bookList;
    private final Context context;
    private final DeleteBook deleteBook;
    private final Map<String, String> tacGiaMap;

    public BookAdapter(List<Book> bookList, Context context) {
        this.bookList = bookList;
        this.context = context;
        this.deleteBook = new DeleteBook(bookList, this, context);
        this.tacGiaMap = new HashMap<>();
        loadTacGiaList();
    }

    private void loadTacGiaList() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Author>> call = apiService.getTacGiaList();
        call.enqueue(new Callback<List<Author>>() {
            @Override
            public void onResponse(Call<List<Author>> call, Response<List<Author>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Author author : response.body()) {
                        tacGiaMap.put(author.getId(), author.getTentacgia());
                    }
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Không thể lấy danh sách tác giả!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Author>> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối khi lấy tác giả: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public SachViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sach, parent, false);
        return new SachViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SachViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.tenSachTextView.setText(book.getTenSach());
        holder.ngayXbTextView.setText(book.getNgayXb());
        holder.theLoaiTextView.setText(book.getTheloai());
        String tenTacGia = tacGiaMap.getOrDefault(book.getIdTacgia(), "Không xác định");
        holder.idTacGiaTextView.setText(tenTacGia);

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditBookActivity.class);
            intent.putExtra("SACH_ID", book.getId());
            intent.putExtra("SACH_TEN", book.getTenSach());
            intent.putExtra("SACH_NGAYXB", book.getNgayXb());
            intent.putExtra("SACH_THELOAI", book.getTheloai());
            intent.putExtra("SACH_ID_TACGIA", book.getIdTacgia());
            ((AppCompatActivity) context).startActivityForResult(intent, 2);
        });

        holder.deleteButton.setOnClickListener(v -> deleteBook.deleteSach(book.getId(), position));
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class SachViewHolder extends RecyclerView.ViewHolder {
        TextView tenSachTextView, ngayXbTextView, theLoaiTextView, idTacGiaTextView;
        ImageView editButton, deleteButton;

        SachViewHolder(View itemView) {
            super(itemView);
            tenSachTextView = itemView.findViewById(R.id.tenSachTextView);
            ngayXbTextView = itemView.findViewById(R.id.ngayXbTextView);
            theLoaiTextView = itemView.findViewById(R.id.theLoaiTextView);
            idTacGiaTextView = itemView.findViewById(R.id.idTacGiaTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}