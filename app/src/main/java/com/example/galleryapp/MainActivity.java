package com.example.galleryapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ImageButton buttonCamera;
    private BottomNavigationView bottomNav;
    private RecyclerView recyclerPhotos;

    private GalleryRepository repository;
    private final List<Photo> allPhotos = new ArrayList<>();
    private PhotoAdapter photoAdapter;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    loadPhotos();
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new GalleryRepository(this);

        buttonCamera = findViewById(R.id.buttonCamera);
        bottomNav = findViewById(R.id.bottomNav);
        recyclerPhotos = findViewById(R.id.recyclerPhotos);

        setupRecycler();
        setupCameraButton();
        setupBottomNav();
        checkPermissionAndLoad();
    }

    private void setupRecycler() {
        recyclerPhotos.setLayoutManager(new GridLayoutManager(this, 3));
        photoAdapter = new PhotoAdapter(photo -> {
            Intent intent = new Intent(this, PhotoViewerActivity.class);
            intent.putExtra(PhotoViewerActivity.EXTRA_PHOTO_URI, photo.uri.toString());
            startActivity(intent);
        });
        recyclerPhotos.setAdapter(photoAdapter);
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

    private void checkPermissionAndLoad() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            loadPhotos();
        } else {
            permissionLauncher.launch(permission);
        }
    }

    private void loadPhotos() {
        executor.execute(() -> {
            List<Photo> loaded = repository.loadPhotos();
            runOnUiThread(() -> {
                allPhotos.clear();
                if (loaded == null || loaded.isEmpty()) {
                    // Seed with 6 bundled images when device has no photos
                    long now = System.currentTimeMillis();
                    allPhotos.add(new Photo(
                            resourceUri(R.drawable.photo1),
                            "Featured",
                            now,
                            false,
                            false
                    ));
                    allPhotos.add(new Photo(
                            resourceUri(R.drawable.photo2),
                            "Featured",
                            now - 1,
                            false,
                            false
                    ));
                    allPhotos.add(new Photo(
                            resourceUri(R.drawable.photo3),
                            "Featured",
                            now - 2,
                            false,
                            false
                    ));
                    allPhotos.add(new Photo(
                            resourceUri(R.drawable.photo4),
                            "Featured",
                            now - 3,
                            false,
                            false
                    ));
                    allPhotos.add(new Photo(
                            resourceUri(R.drawable.photo5),
                            "Featured",
                            now - 4,
                            false,
                            false
                    ));
                    allPhotos.add(new Photo(
                            resourceUri(R.drawable.photo6),
                            "Featured",
                            now - 5,
                            false,
                            false
                    ));
                } else {
                    allPhotos.addAll(loaded);
                }
                photoAdapter.submitList(new ArrayList<>(allPhotos));
            });
        });
    }

    private Uri resourceUri(int drawableId) {
        return Uri.parse("android.resource://" + getPackageName() + "/" + drawableId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}

