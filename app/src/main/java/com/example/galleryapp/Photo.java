package com.example.galleryapp;

import android.net.Uri;

/**
 * Model: single photo with uri, folder (bucket), date, favorite/saved flags.
 */
public class Photo {
    public final Uri uri;
    public final String folderName;
    public final long dateTaken;
    public boolean isFavorite;
    public boolean isSaved;

    public Photo(Uri uri, String folderName, long dateTaken, boolean isFavorite, boolean isSaved) {
        this.uri = uri;
        this.folderName = folderName;
        this.dateTaken = dateTaken;
        this.isFavorite = isFavorite;
        this.isSaved = isSaved;
    }
}

