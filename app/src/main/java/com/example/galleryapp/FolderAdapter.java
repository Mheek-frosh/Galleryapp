package com.example.galleryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.galleryapp.R;

import java.util.ArrayList;
import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    public interface OnFolderClickListener {
        void onFolderClick(Folder folder);
    }

    private final List<Folder> items = new ArrayList<>();
    private final OnFolderClickListener listener;

    public FolderAdapter(OnFolderClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Folder> folders) {
        items.clear();
        if (folders != null) {
            items.addAll(folders);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_folder, parent, false);
        return new FolderViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class FolderViewHolder extends RecyclerView.ViewHolder {

        private final ImageView thumb;
        private final TextView name;
        private final TextView count;
        private Folder current;

        FolderViewHolder(@NonNull View itemView, final OnFolderClickListener listener) {
            super(itemView);
            thumb = itemView.findViewById(R.id.folderThumb);
            name = itemView.findViewById(R.id.folderName);
            count = itemView.findViewById(R.id.folderCount);

            itemView.setOnClickListener(v -> {
                if (listener != null && current != null) {
                    listener.onFolderClick(current);
                }
            });
        }

        void bind(Folder folder) {
            this.current = folder;
            name.setText(folder.name);
            count.setText(String.valueOf(folder.count));

            if (folder.coverUri != null) {
                Glide.with(thumb.getContext())
                        .load(folder.coverUri)
                        .centerCrop()
                        .into(thumb);
            } else {
                thumb.setImageDrawable(null);
            }
        }
    }
}

