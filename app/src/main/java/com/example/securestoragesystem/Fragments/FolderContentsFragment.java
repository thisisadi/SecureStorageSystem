package com.example.securestoragesystem.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.securestoragesystem.Adapters.FolderContentsAdapter;
import com.example.securestoragesystem.Models.FolderFile;
import com.example.securestoragesystem.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;

public class FolderContentsFragment extends Fragment {
    private ImageView backBtn, upload, displayNoFiles, contentsFolderTop, contentsFolderBottom;
    private RecyclerView recyclerView;
    private FolderContentsAdapter adapter;
    private ArrayList<FolderFile> arr = new ArrayList<>();
    private TextView contentsFolderName, contentsFolderCreationDate, uploadProgress, uploadProgressShowFiles ;
    private StorageReference reference;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private int identifier, numFiles;
    private ProgressDialog progressDialog, progressDialogShowFiles;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_folder_contents, container, false);
        recyclerView = view.findViewById(R.id.contentsFolderRecyclerView);
        backBtn = view.findViewById(R.id.contentsNavigateBack);
        contentsFolderTop = view.findViewById(R.id.contentsFolderTop);
        contentsFolderBottom = view.findViewById(R.id.contentsFolderBottom);
        contentsFolderName = view.findViewById(R.id.contentsFolderName);
        contentsFolderCreationDate = view.findViewById(R.id.contentsFolderCreationDate);
        upload = view.findViewById(R.id.encryptAndUpload);
        displayNoFiles = view.findViewById(R.id.displayNoFiles);

        adapter = new FolderContentsAdapter(arr, getContext());
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

        int colorTop = getArguments().getInt("colorTop");
        int colorBottom = getArguments().getInt("colorBottom");
        String name = getArguments().getString("Name");
        String creationDate = getArguments().getString("creationDate");
        identifier = getArguments().getInt("identifier");
        numFiles = getArguments().getInt("numFiles");

        contentsFolderTop.setColorFilter(colorTop);
        contentsFolderBottom.setColorFilter(colorBottom);
        contentsFolderName.setText(name);
        contentsFolderCreationDate.setText(creationDate);
        contentsFolderCreationDate.setTextColor(colorTop);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction =((AppCompatActivity)getContext()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new ProfileFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFile(1);
            }
        });

        progressDialogShowFiles = ProgressDialog.show(getContext(), null, null, true);
        progressDialogShowFiles.setContentView(R.layout.progress_dialog);
        uploadProgressShowFiles = progressDialogShowFiles.findViewById(R.id.progress);
        uploadProgressShowFiles.setVisibility(View.INVISIBLE);
        progressDialogShowFiles.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialogShowFiles.show();

        database.getReference().child("Folders")
                .child(auth.getCurrentUser().getUid())
                .child("Folder "+identifier)
                .child("Files")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                            arr.clear();
                            for (DataSnapshot val : snapshot.getChildren()){
                                FolderFile file = val.getValue(FolderFile.class);
                                arr.add(file);
                            }
                            if (!arr.isEmpty()) {
                                displayNoFiles.setVisibility(View.INVISIBLE);
                            }else {
                                displayNoFiles.setVisibility(View.VISIBLE);
                            }
                            adapter.notifyDataSetChanged();

                            if (progressDialogShowFiles != null && progressDialogShowFiles.isShowing()) {
                                progressDialogShowFiles.dismiss();
                            }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return view;
    }
    public void selectFile(int requestCode)
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent,requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data !=null) {

            Uri sFile = data.getData();
            progressDialog = ProgressDialog.show(getContext(), null, null, true);
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            uploadProgress = progressDialog.findViewById(R.id.progress);
            progressDialog.show();

            reference = FirebaseStorage.getInstance().getReference().child(auth.getCurrentUser().getUid())
                    .child("Folder " + identifier).child("File " + (numFiles + 1));

            reference.putFile(sFile).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double pro = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    int progress = (int) pro;
                    uploadProgress.setText(progress + "%");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            progressDialog.dismiss();
                            database.getReference().child("Folders").child(auth.getCurrentUser().getUid()).child("Folder " + identifier).child("numFiles")
                                    .setValue((numFiles + 1));
                            numFiles++;
                            reference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                @Override
                                public void onSuccess(StorageMetadata storageMetadata) {
                                    long fileSize = storageMetadata.getSizeBytes();
                                    String fileName = storageMetadata.getName();
                                    String fileType = storageMetadata.getContentType();
                                    String userId = auth.getCurrentUser().getUid();
                                    String folderId = "Folder " + identifier;
                                    String fileId = "File " + numFiles;
                                    FolderFile obj = new FolderFile(fileName, fileSize, userId, folderId, fileId, fileType, sFile.toString());
                                    database.getReference().child("Folders").child(auth.getCurrentUser().getUid()).child("Folder " + identifier)
                                            .child("Files").child("File " + numFiles).setValue(obj);
                                    adapter.notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                }
                            });
                        }
                    });
                }
            });
        }
    }

}