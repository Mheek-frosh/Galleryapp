package com.example.galleryapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * Memories tab: grid of photos (photo1-6) as "favorite moments".
 * Tap opens PhotoViewerActivity.
 */
public class MemoriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memories);

        RecyclerView recyclerMemories = findViewById(R.id.recyclerMemories);
        recyclerMemories.setLayoutManager(new GridLayoutManager(this, 3));
        PhotoAdapter photoAdapter = new PhotoAdapter(photo -> {
            Intent intent = new Intent(this, PhotoViewerActivity.class);
            intent.putExtra(PhotoViewerActivity.EXTRA_PHOTO_URI, photo.uri.toString());
            startActivity(intent);
        });
        recyclerMemories.setAdapter(photoAdapter);

        loadMemories(photoAdapter);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(this::onBottomItemSelected);
        bottomNav.setSelectedItemId(R.id.nav_memories);
    }

    // Populate with bundled photo1-6 so the grid always has content
    private void loadMemories(PhotoAdapter adapter) {
        long now = System.currentTimeMillis();
        int[] drawables = {R.drawable.photo1, R.drawable.photo2, R.drawable.photo3,
                R.drawable.photo4, R.drawable.photo5, R.drawable.photo6};
        List<Photo> photos = new ArrayList<>();
        for (int i = 0; i < drawables.length; i++) {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + drawables[i]);
            photos.add(new Photo(uri, "Memories", now - i, false, false));
        }
        adapter.submitList(photos);
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

