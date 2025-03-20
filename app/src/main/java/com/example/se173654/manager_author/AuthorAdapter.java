package com.example.se173654.manager_author;

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

import com.example.se173654.MapActivity;
import com.example.se173654.R;

import java.util.List;

public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.TacGiaViewHolder> {

    private List<Author> authorList;
    private Context context;
    private DeleteAuthor deleteAuthor;

    public AuthorAdapter(List<Author> authorList, Context context) {
        this.authorList = authorList;
        this.context = context;
        this.deleteAuthor = new DeleteAuthor(authorList, this, context);
    }

    @Override
    public TacGiaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tacgia, parent, false);
        return new TacGiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TacGiaViewHolder holder, int position) {
        Author author = authorList.get(position);
        holder.nameTextView.setText(author.getTentacgia());
        holder.emailTextView.setText(author.getEmail());
        holder.addressTextView.setText(author.getDiachi());
        holder.phoneTextView.setText(String.valueOf(author.getDienthoai()));

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditAuthorActivity.class);
            intent.putExtra("TACGIA_ID", author.getId());
            intent.putExtra("TACGIA_NAME", author.getTentacgia());
            intent.putExtra("TACGIA_EMAIL", author.getEmail());
            intent.putExtra("TACGIA_ADDRESS", author.getDiachi());
            intent.putExtra("TACGIA_PHONE", author.getDienthoai());
            ((AppCompatActivity) context).startActivityForResult(intent, 2);
        });

        holder.deleteButton.setOnClickListener(v -> {
            deleteAuthor.deleteTacGia(author.getId(), position);
        });

        holder.addressTextView.setOnClickListener(v -> {
            String address = author.getDiachi();
            if (address != null && !address.isEmpty()) {
                Intent mapIntent = new Intent(context, MapActivity.class);
                mapIntent.putExtra("ADDRESS", address);
                context.startActivity(mapIntent);
            } else {
                Toast.makeText(context, "Địa chỉ không hợp lệ!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return authorList.size();
    }

    public static class TacGiaViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView, addressTextView, phoneTextView;
        ImageView editButton, deleteButton;

        public TacGiaViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            phoneTextView = itemView.findViewById(R.id.phoneTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}