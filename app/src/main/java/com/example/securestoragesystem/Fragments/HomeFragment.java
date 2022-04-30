package com.example.securestoragesystem.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.securestoragesystem.Adapters.MyFoldersAdapter;
import com.example.securestoragesystem.Models.Folder;
import com.example.securestoragesystem.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class HomeFragment extends Fragment {
    FirebaseAuth auth;
    FirebaseDatabase database;
    private RecyclerView recyclerView;
    private TextView progress;
    MyFoldersAdapter adapter;
    ProgressDialog progressDialog;
    ImageView displayNoFolders;
    EditText searchBar;
    final ArrayList<Folder> folderArray = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.homeRecyclerView);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        displayNoFolders = view.findViewById(R.id.displayNoFoldersHome);
        searchBar = view.findViewById(R.id.searchBar);

        progressDialog = ProgressDialog.show(getContext(), null, null, true);
        progressDialog.setContentView(R.layout.progress_dialog);
        progress = progressDialog.findViewById(R.id.progress);
        progress.setVisibility(View.INVISIBLE);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.show();

        // Search Bar Functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterFolders(editable.toString());
            }
        });

        database.getReference().child("Folders")
                .child(auth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        folderArray.clear();
                        for (DataSnapshot val : snapshot.getChildren()){
                            Folder folder = val.getValue(Folder.class);
                            folderArray.add(folder);
                        }
                        if (folderArray.isEmpty()) {
                            displayNoFolders.setVisibility(View.VISIBLE);
                            searchBar.setVisibility(View.INVISIBLE);
                        }else {
                            displayNoFolders.setVisibility(View.INVISIBLE);
                            searchBar.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();

                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        adapter = new MyFoldersAdapter(folderArray, getContext());
        GridLayoutManager grid = new GridLayoutManager(getContext(),2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(grid);
        recyclerView.setAdapter(adapter);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog();
            }
        });
        return view;
    }
    private void showCustomDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.foldercustomdialog);
        final EditText name = dialog.findViewById(R.id.folderName);
        final Button button = dialog.findViewById(R.id.btDone);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!name.getText().toString().isEmpty())
                {
                    DatabaseReference FolderCount = database.getReference().child("Folders").child(auth.getCurrentUser().getUid());
                    ValueEventListener eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long count = dataSnapshot.getChildrenCount() + 1;
                            Folder obj = new Folder(name.getText().toString(), getDateCurrentTimeZone(new Date().getTime()));
                            obj.setIdentifier((int)count);
                            database.getReference().child("Folders").child(auth.getCurrentUser().getUid()).child("Folder "+ count).setValue(obj);
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
                        }
                    };
                    FolderCount.addListenerForSingleValueEvent(eventListener);
                }
            }
        });
        dialog.show();
    }
    public String getDateCurrentTimeZone(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        Date resultdate = new Date(timestamp);
        return (sdf.format(resultdate));
    }

    private void filterFolders(String text) {
        ArrayList<Folder> filteredList = new ArrayList<>();
        for (Folder folder : folderArray) {
            if (folder.getFolderName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(folder);
            }
        }
        adapter.filterList(filteredList);
    }
}