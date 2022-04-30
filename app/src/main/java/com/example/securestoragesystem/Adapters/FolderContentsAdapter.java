package com.example.securestoragesystem.Adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.securestoragesystem.Models.Encryption;
import com.example.securestoragesystem.Fragments.FingerprintFragment;
import com.example.securestoragesystem.Models.FolderFile;
import com.example.securestoragesystem.R;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import javax.crypto.NoSuchPaddingException;

public class FolderContentsAdapter extends RecyclerView.Adapter<FolderContentsAdapter.ViewHolder>{

    ArrayList<FolderFile> list;
    Context context;

    public FolderContentsAdapter(ArrayList<FolderFile> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_contents_folder,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FolderFile file = list.get(position);
        holder.fileName.setText(file.getFilename());
        holder.fileSize.setText(humanReadableByteCountSI(file.getFileSize()));
        holder.fileDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FingerprintFragment.decrypt) {
                    try {
                        downloadFile(file);
                    } catch (Exception e) {
                        Log.d("error",e.toString());
                    }
                } else {
                    try {
                        FingerprintFragment.decrypt = false;
                        Encryption.decrypt(file.getFolderId(),file.getFileId(), file.getFileType());
                    } catch (Exception e) {
                        Log.d("decrypt",e.toString());
                    }
                }
            }
        });

        String[] extension = file.getFileType().split("/");
        switch (extension[0]) {
            case "image":
                holder.fileIcon.setImageResource(R.drawable.imgicon);
                break;
            case "application":
                if (extension[1].charAt(0) == 'v') {
                    holder.fileIcon.setImageResource(R.drawable.wordicon);
                } else {
                    holder.fileIcon.setImageResource(R.drawable.pdficon);
                }
                break;
            case "video":
                holder.fileIcon.setImageResource(R.drawable.mp4icon);
                break;
            case "text":
                holder.fileIcon.setImageResource(R.drawable.txticon);
                break;
            default:
                holder.fileIcon.setImageResource(R.drawable.miscicon);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout fileContent;
        ImageView fileIcon, fileDownload;
        TextView fileName, fileSize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileIcon = itemView.findViewById(R.id.fileIcon);
            fileName = itemView.findViewById(R.id.uploadFileName);
            fileSize = itemView.findViewById(R.id.fileSize);
            fileContent = itemView.findViewById(R.id.folderFile);
            fileDownload = itemView.findViewById(R.id.fileDownload);
        }
    }

    public void downloadFile(FolderFile file) throws NoSuchPaddingException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        Encryption.encrypt(context,Uri.parse(file.getUri()),file.getFolderId(),file.getFileId(), file.getFileType());
    }


    public static String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

}


