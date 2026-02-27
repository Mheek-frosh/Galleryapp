package com.example.galleryapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlbumsActivity extends AppCompatActivity {

    private RecyclerView recyclerRecent;
    private PhotoAdapter photoAdapter;
    private GalleryRepository repository;
    private final List<Photo> recentPhotos = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    loadRecent();
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        repository = new GalleryRepository(this);

        recyclerRecent = findViewById(R.id.recyclerRecent);
        recyclerRecent.setLayoutManager(new GridLayoutManager(this, 3));
        photoAdapter = new PhotoAdapter(photo -> {
            Intent intent = new Intent(this, PhotoViewerActivity.class);
            intent.putExtra(PhotoViewerActivity.EXTRA_PHOTO_URI, photo.uri.toString());
            startActivity(intent);
        });
        recyclerRecent.setAdapter(photoAdapter);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(this::onBottomItemSelected);
        bottomNav.setSelectedItemId(R.id.nav_albums);

        checkPermissionAndLoad();
    }

    private void checkPermissionAndLoad() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            loadRecent();
        } else {
            permissionLauncher.launch(permission);
        }
    }

    private void loadRecent() {
        executor.execute(() -> {
            List<Photo> loaded = repository.loadPhotos();
            runOnUiThread(() -> {
                recentPhotos.clear();
                if (loaded == null || loaded.isEmpty()) {
                    long now = System.currentTimeMillis();
                    recentPhotos.add(new Photo(
                            resourceUri(R.drawable.photo1),
                            "Recently added",
                            now,
                            false,
                            false
                    ));
                    recentPhotos.add(new Photo(
                            resourceUri(R.drawable.photo2),
                            "Recently added",
                            now - 1,
                            false,
                            false
                    ));
                    recentPhotos.add(new Photo(
                            resourceUri(R.drawable.photo3),
                            "Recently added",
                            now - 2,
                            false,
                            false
                    ));
                    recentPhotos.add(new Photo(
                            resourceUri(R.drawable.photo4),
                            "Recently added",
                            now - 3,
                            false,
                            false
                    ));
                    recentPhotos.add(new Photo(
                            resourceUri(R.drawable.photo5),
                            "Recently added",
                            now - 4,
                            false,
                            false
                    ));
                    recentPhotos.add(new Photo(
                            resourceUri(R.drawable.photo6),
                            "Recently added",
                            now - 5,
                            false,
                            false
                    ));
                } else {
                    int max = Math.min(loaded.size(), 60);
                    recentPhotos.addAll(loaded.subList(0, max));
                }
                photoAdapter.submitList(new ArrayList<>(recentPhotos));
            });
        });
    }

    private Uri resourceUri(int drawableId) {
        return Uri.parse("android.resource://" + getPackageName() + "/" + drawableId);
    }

    private boolean onBottomItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_photos) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else if (id == R.id.nav_albums) {
            return true;
        } else if (id == R.id.nav_memories) {
            startActivity(new Intent(this, MemoriesActivity.class));
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}

