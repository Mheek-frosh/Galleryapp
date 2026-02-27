package com.example.galleryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.galleryapp.R;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    public interface OnPhotoClickListener {
        void onPhotoClick(Photo photo);
    }

    private final List<Photo> items = new ArrayList<>();
    private final OnPhotoClickListener listener;

    public PhotoAdapter(OnPhotoClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Photo> photos) {
        items.clear();
        if (photos != null) {
            items.addAll(photos);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {

        private final ImageView thumb;
        private final ImageView iconFavorite;
        private final ImageView iconSaved;
        private Photo current;

        PhotoViewHolder(@NonNull View itemView, final OnPhotoClickListener listener) {
            super(itemView);
            thumb = itemView.findViewById(R.id.imageThumb);
            iconFavorite = itemView.findViewById(R.id.iconFavorite);
            iconSaved = itemView.findViewById(R.id.iconSaved);

            itemView.setOnClickListener(v -> {
                if (listener != null && current != null) {
                    listener.onPhotoClick(current);
                }
            });
        }

        void bind(Photo photo) {
            this.current = photo;
            Glide.with(thumb.getContext())
                    .load(photo.uri)
                    .centerCrop()
                    .into(thumb);

            iconFavorite.setVisibility(photo.isFavorite ? View.VISIBLE : View.GONE);
            iconSaved.setVisibility(photo.isSaved ? View.VISIBLE : View.GONE);
        }
    }
}

