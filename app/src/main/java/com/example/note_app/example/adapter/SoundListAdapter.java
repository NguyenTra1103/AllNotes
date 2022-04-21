package com.example.note_app.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note_app.R;
import com.example.note_app.example.TimeAgo;

import java.io.File;
import java.nio.file.attribute.FileTime;

public class SoundListAdapter extends RecyclerView.Adapter<SoundListAdapter.SoundViewHolder> {

    private File[] allFiles;
    private TimeAgo timeAgo;
    private onItemListClick onItemListClick;

    public SoundListAdapter(File[] allFiles, onItemListClick onItemListClick){
        this.allFiles = allFiles;
        this.onItemListClick = onItemListClick;
    }
    @NonNull
    @Override
    public SoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_detail, parent, false);
        timeAgo = new TimeAgo();
        return new SoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoundViewHolder holder, int position) {

        holder.list_tittle.setText(allFiles[position].getName());
        holder.list_description.setText(timeAgo.getTimeAgo(allFiles[position].lastModified()));
    }

    @Override
    public int getItemCount() {
        return allFiles.length;
    }

    public class SoundViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView list_image;
        private TextView list_tittle;
        private TextView list_description;
        public SoundViewHolder(@NonNull View itemView) {
            super(itemView);
            list_image = itemView.findViewById(R.id.item_img);
            list_tittle = itemView.findViewById(R.id.item_tittle);
            list_description = itemView.findViewById(R.id.description);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            onItemListClick.onClickListener(allFiles[getAdapterPosition()],getAdapterPosition());
        }
    }
    public interface onItemListClick{
        void onClickListener(File file, int position);
    }
}
