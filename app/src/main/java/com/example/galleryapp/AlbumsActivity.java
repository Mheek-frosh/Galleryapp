package com.example.galleryapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Albums tab: list of device folders (Recent, Camera roll, Screenshots, Downloads).
 * Each folder uses photo1-4 as cover thumbnails.
 * Tapping a folder -> FolderDetailActivity with name and key passed as extras.
 */
public class AlbumsActivity extends AppCompatActivity {

    private RecyclerView recyclerFolders;
    private FolderAdapter folderAdapter;
    private GalleryRepository repository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    loadFolders();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        repository = new GalleryRepository(this);

        recyclerFolders = findViewById(R.id.recyclerFolders);
        recyclerFolders.setLayoutManager(new LinearLayoutManager(this));
        recyclerFolders.setHasFixedSize(true);
        // Tap folder -> open FolderDetailActivity with name and key for filtering
        folderAdapter = new FolderAdapter(folder -> {
            Intent intent = new Intent(this, FolderDetailActivity.class);
            intent.putExtra(FolderDetailActivity.EXTRA_FOLDER_NAME, folder.name);
            intent.putExtra(FolderDetailActivity.EXTRA_FOLDER_KEY, folder.key);
            startActivity(intent);
        });
        recyclerFolders.setAdapter(folderAdapter);

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
            loadFolders();
        } else {
            permissionLauncher.launch(permission);
        }
    }

    // Build folder list: Recent, Camera roll, Screenshots, Downloads. Cover images = photo1-4.
    private void loadFolders() {
        executor.execute(() -> {
            List<Photo> loaded = repository.loadPhotos();
            int totalCount = (loaded != null && !loaded.isEmpty()) ? loaded.size() : 6;
            int recentCount = totalCount > 0 ? Math.min(totalCount, 24) : 6;
            int cameraCount = totalCount > 0 ? Math.min(totalCount, 48) : 6;
            int screenshotsCount = totalCount > 0 ? Math.min(totalCount, 12) : 6;
            int downloadsCount = totalCount > 0 ? Math.min(totalCount, 8) : 6;

            // Each folder: name, key (for filterByFolder), count, cover drawable
            List<Folder> folders = new ArrayList<>();
            folders.add(new Folder(getString(R.string.section_recent), "recent", recentCount, resourceUri(R.drawable.photo1)));
            folders.add(new Folder(getString(R.string.section_camera_roll), "camera_roll", cameraCount, resourceUri(R.drawable.photo2)));
            folders.add(new Folder(getString(R.string.section_screenshots), "screenshots", screenshotsCount, resourceUri(R.drawable.photo3)));
            folders.add(new Folder(getString(R.string.section_downloads), "downloads", downloadsCount, resourceUri(R.drawable.photo4)));

            runOnUiThread(() -> folderAdapter.submitList(folders));
        });
    }

    private Uri resourceUri(int drawableId) {
        return Uri.parse("android.resource://" + getPackageName() + "/" + drawableId);
    }

    private boolean onBottomItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_photos) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        } else if (id == R.id.nav_albums) {
            return true;
        } else if (id == R.id.nav_memories) {
            startActivity(new Intent(this, MemoriesActivity.class));
            finish();
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
