package com.example.galleryapp;

import android.net.Uri;

/**
 * Model: album/folder with name, key (for filtering), photo count, cover image URI.
 */
public class Folder {
    public final String name;
    public final String key;
    public final int count;
    public final Uri coverUri;

    public Folder(String name, String key, int count, Uri coverUri) {
        this.name = name;
        this.key = key;
        this.count = count;
        this.coverUri = coverUri;
    }
}

