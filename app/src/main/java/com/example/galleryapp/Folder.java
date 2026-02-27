package com.example.galleryapp;

import android.net.Uri;

public class Folder {
    public final String name;
    public final int count;
    public final Uri coverUri;

    public Folder(String name, int count, Uri coverUri) {
        this.name = name;
        this.count = count;
        this.coverUri = coverUri;
    }
}

