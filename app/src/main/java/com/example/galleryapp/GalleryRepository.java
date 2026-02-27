package com.example.galleryapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class GalleryRepository {

    private final Context context;
    private final SharedPreferences prefs;

    private static final String PREFS_NAME = "gallery_prefs";
    private static final String FAVORITES_KEY = "favorites";
    private static final String SAVED_KEY = "saved";

    public GalleryRepository(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public List<Photo> loadPhotos() {
        Set<String> favorites = getUriSet(FAVORITES_KEY);
        Set<String> saved = getUriSet(SAVED_KEY);

        List<Photo> photos = new ArrayList<>();
        Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATE_ADDED
        };

        String sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC";

        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(collection, projection, null, null, sortOrder);
        if (cursor != null) {
            try {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int bucketColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                int dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
                int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);

                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idColumn);
                    String bucket = cursor.getString(bucketColumn);
                    long dateTaken = cursor.getLong(dateTakenColumn);
                    long dateAdded = cursor.getLong(dateAddedColumn);

                    Uri uri = ContentUris.withAppendedId(collection, id);
                    String uriString = uri.toString();

                    long finalDate = (dateTaken != 0L) ? dateTaken : dateAdded * 1000L;

                    Photo photo = new Photo(
                            uri,
                            bucket,
                            finalDate,
                            favorites.contains(uriString),
                            saved.contains(uriString)
                    );
                    photos.add(photo);
                }
            } finally {
                cursor.close();
            }
        }

        return photos;
    }

    public List<Folder> buildFolders(List<Photo> allPhotos) {
        Map<String, List<Photo>> byFolder = new HashMap<>();
        for (Photo p : allPhotos) {
            String key = p.folderName != null ? p.folderName : "Unknown";
            List<Photo> list = byFolder.get(key);
            if (list == null) {
                list = new ArrayList<>();
                byFolder.put(key, list);
            }
            list.add(p);
        }

        List<Folder> folders = new ArrayList<>();
        for (Map.Entry<String, List<Photo>> entry : byFolder.entrySet()) {
            String name = entry.getKey();
            List<Photo> list = entry.getValue();
            Uri cover = list.isEmpty() ? null : list.get(0).uri;
            folders.add(new Folder(name, list.size(), cover));
        }

        folders.sort((a, b) -> a.name.toLowerCase(Locale.ROOT).compareTo(b.name.toLowerCase(Locale.ROOT)));
        return folders;
    }

    public void setFavorite(Photo photo, boolean isFavorite) {
        Set<String> current = new HashSet<>(getUriSet(FAVORITES_KEY));
        String uri = photo.uri.toString();
        if (isFavorite) {
            current.add(uri);
        } else {
            current.remove(uri);
        }
        saveUriSet(FAVORITES_KEY, current);
    }

    public void setSaved(Photo photo, boolean isSaved) {
        Set<String> current = new HashSet<>(getUriSet(SAVED_KEY));
        String uri = photo.uri.toString();
        if (isSaved) {
            current.add(uri);
        } else {
            current.remove(uri);
        }
        saveUriSet(SAVED_KEY, current);
    }

    private Set<String> getUriSet(String key) {
        Set<String> stored = prefs.getStringSet(key, null);
        return stored != null ? stored : new HashSet<String>();
    }

    private void saveUriSet(String key, Set<String> set) {
        prefs.edit().putStringSet(key, set).apply();
    }
}

