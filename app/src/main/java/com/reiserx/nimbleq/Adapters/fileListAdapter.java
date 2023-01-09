package com.reiserx.nimbleq.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reiserx.nimbleq.Models.fileTypeModel;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.databinding.FileViewLayoutBinding;

import java.io.File;
import java.util.ArrayList;

public class fileListAdapter extends RecyclerView.Adapter<fileListAdapter.UsersViewHolder> {

    Context context;
    ArrayList<fileTypeModel> users;
    View done;
    fileListAdapter adapter;


    public fileListAdapter(Context context, ArrayList<fileTypeModel> users, fileListAdapter adapter) {
        this.context = context;
        this.users = users;
        this.adapter = adapter;
    }

    @NonNull
    @Override
    public fileListAdapter.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.file_view_layout, parent, false);
        return new fileListAdapter.UsersViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull fileListAdapter.UsersViewHolder holder, int position) {
        fileTypeModel model = users.get(position);
        holder.binding.textView33.setText(model.getFilename());

        checkFile(model.getFilePath(), holder);

        if (model.isUploaded()) {
            holder.binding.imageView16.setVisibility(View.VISIBLE);
            holder.binding.progressBar4.setVisibility(View.GONE);
        } else {
            holder.binding.imageView16.setVisibility(View.GONE);
            holder.binding.progressBar4.setVisibility(View.VISIBLE);
            holder.binding.progressBar4.setProgress(model.getProg());
        }
        done = holder.binding.imageView16;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        FileViewLayoutBinding binding;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FileViewLayoutBinding.bind(itemView);
        }
    }

    public int getTargetPosition(String filename) {
        fileTypeModel msg;
        for (int i = 0; i < users.size(); i++) {
            msg = users.get(i);
            if (msg.getFilename().equals(filename)) {
                return i;
            }
        }
        return 0;
    }

    public void uploadDone(int pos) {
        fileTypeModel model = users.get(pos);
        model.setUploaded(true);
    }

    public void updateProg(int pos, int prog) {
        fileTypeModel model = users.get(pos);
        model.setProg(prog);
    }

    private void checkFile(String filePath, UsersViewHolder holder) {
        File file = new File(filePath);

        if (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".png"))
            holder.binding.imageView15.setImageResource(R.drawable.ic_baseline_image_24);
        if (file.getName().endsWith(".mp3") || file.getName().endsWith(".mp4a") || file.getName().endsWith(".wma") || file.getName().endsWith(".m4a") || file.getName().endsWith(".aac"))
            holder.binding.imageView15.setImageResource(R.drawable.ic_baseline_audio_file_24);
        else if (file.getName().endsWith(".pdf") || file.getName().endsWith(".docx"))
            holder.binding.imageView15.setImageResource(R.drawable.ic_baseline_picture_as_pdf_24);
        else if (file.getName().endsWith(".txt"))
            holder.binding.imageView15.setImageResource(R.drawable.ic_baseline_insert_drive_file_24);
    }
}