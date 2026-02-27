package com.example.galleryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MemoriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memories);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(this::onBottomItemSelected);
        bottomNav.setSelectedItemId(R.id.nav_memories);
    }

    private boolean onBottomItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_photos) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else if (id == R.id.nav_albums) {
            startActivity(new Intent(this, AlbumsActivity.class));
            return true;
        } else if (id == R.id.nav_memories) {
            return true;
        }
        return false;
    }
}

