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

/**
 * Main gallery screen (Photos tab).
 * Shows grid of photos from device or bundled photo1-6 when empty.
 * Camera button launches device camera.
 * Bottom nav: Photos (here), Albums, Memories.
 */
public class MainActivity extends AppCompatActivity {

    private ImageButton buttonCamera;
    private BottomNavigationView bottomNav;
    private RecyclerView recyclerPhotos;

    private GalleryRepository repository;
    private final List<Photo> allPhotos = new ArrayList<>();
    private PhotoAdapter photoAdapter;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Handles result when user grants/denies storage permission
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

    // Configure 3-column grid; tap opens PhotoViewerActivity
    private void setupRecycler() {
        recyclerPhotos.setLayoutManager(new GridLayoutManager(this, 3));
        photoAdapter = new PhotoAdapter(photo -> {
            Intent intent = new Intent(this, PhotoViewerActivity.class);
            intent.putExtra(PhotoViewerActivity.EXTRA_PHOTO_URI, photo.uri.toString());
            startActivity(intent);
        });
        recyclerPhotos.setAdapter(photoAdapter);
    }

    // Launch device camera app
    private void setupCameraButton() {
        buttonCamera.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                startActivity(intent);
            } catch (Exception ignored) {
            }
        });
    }

    // Bottom nav: Photos stays here; Albums/Memories start their activities
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

    // Use READ_MEDIA_IMAGES on Android 13+; otherwise READ_EXTERNAL_STORAGE
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

    // Load from MediaStore on background thread; if empty, use photo1-6 from drawable
    private void loadPhotos() {
        executor.execute(() -> {
            List<Photo> loaded = repository.loadPhotos();
            runOnUiThread(() -> {
                allPhotos.clear();
                if (loaded == null || loaded.isEmpty()) {
                    // Fallback: populate with bundled photo1-6 so the grid always shows content
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

    // Convert R.drawable.photo1 etc. to content URI for Glide/ImageView
    private Uri resourceUri(int drawableId) {
        return Uri.parse("android.resource://" + getPackageName() + "/" + drawableId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}

