package com.example.galleryapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ImageButton buttonCamera;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonCamera = findViewById(R.id.buttonCamera);
        bottomNav = findViewById(R.id.bottomNav);

        setupCameraButton();
        setupBottomNav();
    }

    private void setupCameraButton() {
        buttonCamera.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                startActivity(intent);
            } catch (Exception ignored) {
            }
        });
    }

    private void setupBottomNav() {
        bottomNav.setOnItemSelectedListener(this::onBottomItemSelected);
        bottomNav.setSelectedItemId(R.id.nav_photos);
    }

    private boolean onBottomItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_photos) {
            return true;
        } else if (id == R.id.nav_albums) {
            startActivity(new Intent(this, AlbumsActivity.class));
            return true;
        } else if (id == R.id.nav_memories) {
            startActivity(new Intent(this, MemoriesActivity.class));
            return true;
        }
        return false;
    }
}

