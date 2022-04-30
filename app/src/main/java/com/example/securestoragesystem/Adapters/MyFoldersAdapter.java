package com.example.securestoragesystem.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.securestoragesystem.Fragments.FingerprintFragment;
import com.example.securestoragesystem.Fragments.FolderContentsFragment;
import com.example.securestoragesystem.Models.Folder;
import com.example.securestoragesystem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MyFoldersAdapter extends RecyclerView.Adapter<MyFoldersAdapter.ViewHolder>{

    ArrayList<Folder> list;
    Context context;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private StorageReference reference;

    public MyFoldersAdapter(ArrayList<Folder> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_folder,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Folder folder = list.get(position);
        holder.folderName.setText(folder.getFolderName());
        holder.creationDate.setText(folder.getCreationDate());
        holder.folderTop.setColorFilter(folder.getColorTop());
        holder.folderBottom.setColorFilter(folder.getColorBottom());
        holder.folderSettings.setColorFilter(folder.getColorBottom());
        holder.cardView.setCardBackgroundColor(folder.getColorBackground());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction =((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                FolderContentsFragment fragment = new FolderContentsFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("colorTop", folder.getColorTop());
                bundle.putInt("colorBottom", folder.getColorBottom());
                bundle.putString("creationDate",folder.getCreationDate());
                bundle.putString("Name", folder.getFolderName());
                bundle.putInt("identifier", folder.getIdentifier());
                database.getReference().child("Folders").child(auth.getCurrentUser().getUid()).child("Folder " + folder.getIdentifier())
                        .child("numFiles").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        bundle.putInt("numFiles", task.getResult().getValue(Integer.class));
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.fragment_container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });
            }
        });
        holder.folderSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, holder.folderSettings);
                popup.getMenuInflater()
                        .inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("encryptoDrive");
                        builder.setMessage("Do you want to delete this folder ?");
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                database.getReference().child("Folders").child(auth.getCurrentUser().getUid()).child("Folder "+folder.getIdentifier()).removeValue();
                                reference = FirebaseStorage.getInstance().getReference().child(auth.getCurrentUser().getUid())
                                        .child("Folder " + folder.getIdentifier());
                                reference.delete();
                                notifyDataSetChanged();
                            }
                        });
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList (ArrayList<Folder> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView folderTop, folderBottom, folderSettings;
        TextView folderName, creationDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.folderDescription);
            creationDate = itemView.findViewById(R.id.folderCreationTimestamp);
            folderTop = itemView.findViewById(R.id.folderTop);
            folderBottom = itemView.findViewById(R.id.folderBottom);
            folderSettings = itemView.findViewById(R.id.folderSettings);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

}


