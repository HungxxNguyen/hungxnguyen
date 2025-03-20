package com.example.se173654;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.se173654.manager_author.ManagerAuthorActivity;
import com.example.se173654.manager_book.ManagerBookActivity;
import com.example.se173654.manager_major.ManagerMajorActivity;
import com.example.se173654.manager_student.ManagerStudentActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ScreenActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);

        Button managerSachButton = findViewById(R.id.managerSachButton);
        Button managerTacGiaButton = findViewById(R.id.managerTacGiaButton);
        Button viewProfileButton = findViewById(R.id.viewProfileButton);
        Button managerMajorButton = findViewById(R.id.managerMajorButton);
        Button managerStudentButton = findViewById(R.id.managerStudentButton);
        Button signOutButton = findViewById(R.id.signOutButton);
        Button searchLocationButton = findViewById(R.id.myLocationButton); // Thêm nút Search Location

        auth = FirebaseAuth.getInstance();

        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome");

        managerSachButton.setOnClickListener(v -> {
            Intent intent = new Intent(ScreenActivity.this, ManagerBookActivity.class);
            startActivity(intent);
        });

        managerTacGiaButton.setOnClickListener(v -> {
            Intent intent = new Intent(ScreenActivity.this, ManagerAuthorActivity.class);
            startActivity(intent);
        });

        managerStudentButton.setOnClickListener(v -> {
            Intent intent = new Intent(ScreenActivity.this, ManagerStudentActivity.class);
            startActivity(intent);
        });

        managerMajorButton.setOnClickListener(v -> {
            Intent intent = new Intent(ScreenActivity.this, ManagerMajorActivity.class);
            startActivity(intent);
        });

        viewProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(ScreenActivity.this, ViewProfileActivity.class);
            startActivity(intent);
        });

        signOutButton.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(ScreenActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ScreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Thêm sự kiện click cho nút Search Location
        searchLocationButton.setOnClickListener(v -> {
            Intent intent = new Intent(ScreenActivity.this, SearchViewActivity.class);
            startActivity(intent);
        });
    }
}