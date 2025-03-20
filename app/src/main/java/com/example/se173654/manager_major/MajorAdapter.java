package com.example.se173654.manager_major;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se173654.R;

import java.util.List;

public class MajorAdapter extends RecyclerView.Adapter<MajorAdapter.MajorViewHolder> {

    private List<Major> majorList;
    private Context context;
    private DeleteMajor deleteMajor;

    public MajorAdapter(List<Major> majorList, Context context) {
        this.majorList = majorList;
        this.context = context;
        this.deleteMajor = new DeleteMajor(majorList, this, context);
    }

    @Override
    public MajorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_major, parent, false);
        return new MajorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MajorViewHolder holder, int position) {
        Major major = majorList.get(position);
        holder.nameMajorTextView.setText(major.getNameMajor());

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditMajorActivity.class);
            intent.putExtra("MAJOR_ID", major.getId());
            intent.putExtra("MAJOR_NAME", major.getNameMajor());
            ((AppCompatActivity) context).startActivityForResult(intent, 2);
        });

        holder.deleteButton.setOnClickListener(v -> {
            deleteMajor.deleteMajor(major.getId(), position);
        });
    }

    @Override
    public int getItemCount() {
        return majorList.size();
    }

    public static class MajorViewHolder extends RecyclerView.ViewHolder {
        TextView nameMajorTextView;
        ImageView editButton, deleteButton;

        public MajorViewHolder(View itemView) {
            super(itemView);
            nameMajorTextView = itemView.findViewById(R.id.nameMajorTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}