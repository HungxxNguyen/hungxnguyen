package com.example.se173654.manager_student;

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
import com.example.se173654.manager_major.Major;
import com.example.se173654.services.ApiService;
import com.example.se173654.services.RetrofitClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> studentList;
    private Context context;
    private DeleteStudent deleteStudent;
    private Map<String, String> majorMap;

    public StudentAdapter(List<Student> studentList, Context context) {
        this.studentList = studentList;
        this.context = context;
        this.deleteStudent = new DeleteStudent(studentList, this, context);
        this.majorMap = new HashMap<>();
        loadMajorList();
    }

    private void loadMajorList() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Major>> call = apiService.getMajorList();
        call.enqueue(new Callback<List<Major>>() {
            @Override
            public void onResponse(Call<List<Major>> call, Response<List<Major>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Major major : response.body()) {
                        majorMap.put(major.getId(), major.getNameMajor());
                    }
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Major>> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối khi lấy chuyên ngành: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.nameTextView.setText(student.getName());
        holder.dateTextView.setText(student.getDate());
        holder.genderTextView.setText(student.getGender());
        holder.emailTextView.setText(student.getEmail());
        holder.addressTextView.setText(student.getAddress());
        String majorName = majorMap.getOrDefault(student.getIdMajor(), "Không xác định");
        holder.idMajorTextView.setText(majorName);

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditStudentActivity.class);
            intent.putExtra("STUDENT_ID", student.getId());
            intent.putExtra("STUDENT_NAME", student.getName());
            intent.putExtra("STUDENT_DATE", student.getDate());
            intent.putExtra("STUDENT_GENDER", student.getGender());
            intent.putExtra("STUDENT_EMAIL", student.getEmail());
            intent.putExtra("STUDENT_ADDRESS", student.getAddress());
            intent.putExtra("STUDENT_ID_MAJOR", student.getIdMajor());
            ((AppCompatActivity) context).startActivityForResult(intent, 2);
        });

        holder.deleteButton.setOnClickListener(v -> {
            deleteStudent.deleteStudent(student.getId(), position);
        });

        holder.addressTextView.setOnClickListener(v -> {
            String address = student.getAddress();
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
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, dateTextView, genderTextView, emailTextView, addressTextView, idMajorTextView;
        ImageView editButton, deleteButton;

        public StudentViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            genderTextView = itemView.findViewById(R.id.genderTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            idMajorTextView = itemView.findViewById(R.id.idMajorTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}