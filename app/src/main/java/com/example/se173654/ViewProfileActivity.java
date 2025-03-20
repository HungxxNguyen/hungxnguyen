package com.example.se173654;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ViewProfileActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private TextView nameTextView, emailTextView;
    private ImageView profileImageView, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        // Khởi tạo FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Lấy các view
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        profileImageView = findViewById(R.id.profileImageView);
        backButton = findViewById(R.id.backButton);

        // Lấy người dùng hiện tại
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            // Hiển thị tên và email của người dùng
            nameTextView.setText(user.getDisplayName());
            emailTextView.setText(user.getEmail());

            // Hiển thị ảnh của người dùng nếu có
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(profileImageView);
        }

        // Xử lý sự kiện nhấn nút "Back"
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại màn hình trước đó
                onBackPressed();  // Phương thức này giúp quay lại màn hình trước
            }
        });
    }
}
