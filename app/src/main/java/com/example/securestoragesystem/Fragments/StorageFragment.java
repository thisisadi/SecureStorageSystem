package com.example.securestoragesystem.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.securestoragesystem.Adapters.FolderContentsAdapter;

import com.example.securestoragesystem.Models.FolderFile;
import com.example.securestoragesystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class StorageFragment extends Fragment {
    private final long Total = 1000000000;
    PieChart pieChart;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    long PDF = 0L, Image = 0L, Text = 0L, Others = 0L, Available;
    long ImagePercent, TextPercent, PDFPercent, OthersPercent, AvailablePercent;
    String PDFSize, ImageSize, TextSize, OthersSize, AvailableSize;
    TextView SpacePDF, SpaceText, SpaceImages, SpaceOthers, StorageAvailable, SpaceAvailable;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_storage, container, false);
        pieChart = view.findViewById(R.id.pieChart);
        SpacePDF = view.findViewById(R.id.spacePDF);
        SpaceText = view.findViewById(R.id.spaceTXT);
        SpaceImages = view.findViewById(R.id.spaceImages);
        SpaceOthers = view.findViewById(R.id.spaceOthers);
        StorageAvailable = view.findViewById(R.id.storageAvailable);
        SpaceAvailable = view.findViewById(R.id.spaceAvailable);

        database.getReference().child("Folders")
                .child(auth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot folder : snapshot.getChildren()){
                            for (DataSnapshot file : folder.child("Files").getChildren()) {
                                FolderFile f = file.getValue(FolderFile.class);
                                String[] type = f.getFileType().split("/");
                                switch (type[0]) {
                                    case "image":
                                        Image += f.getFileSize();
                                        break;
                                    case "application":
                                        PDF += f.getFileSize();
                                        break;
                                    case "text":
                                        Text += f.getFileSize();
                                        break;
                                    default:
                                        Others += f.getFileSize();
                                        break;
                                }
                            }
                        }
                        Available = Total-(Image+Text+PDF+Others);
                        ImageSize = FolderContentsAdapter.humanReadableByteCountSI(Image);
                        TextSize = FolderContentsAdapter.humanReadableByteCountSI(Text);
                        PDFSize = FolderContentsAdapter.humanReadableByteCountSI(PDF);
                        OthersSize = FolderContentsAdapter.humanReadableByteCountSI(Others);
                        AvailableSize = FolderContentsAdapter.humanReadableByteCountSI(Available);

                        ImagePercent = (long)Math.round(((double)Image/Total)*100);
                        PDFPercent = (long)Math.round(((double)PDF/Total)*100);
                        TextPercent = (long)Math.round(((double)Text/Total)*100);
                        OthersPercent = (long)Math.round(((double)Others/Total)*100);
                        AvailablePercent = (long)Math.round(100-(ImagePercent+TextPercent+PDFPercent+OthersPercent));
                        setData(PDFPercent,ImagePercent,TextPercent,OthersPercent,AvailablePercent);

                        SpaceImages.setText(ImageSize);
                        SpacePDF.setText(PDFSize);
                        SpaceText.setText(TextSize);
                        SpaceOthers.setText(OthersSize);
                        StorageAvailable.setText(AvailableSize);
                        SpaceAvailable.setText(AvailableSize);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return view;
    }
    private void setData(long PDF, long Image, long Text, long Others, long Available)
    {
        pieChart.addPieSlice(
                new PieModel(
                        "PDF",
                        PDF,
                        Color.parseColor("#22215B")));
        pieChart.addPieSlice(
                new PieModel(
                        "Images",
                        Image,
                        Color.parseColor("#FFC700")));
        pieChart.addPieSlice(
                new PieModel(
                        "Text",
                        Text,
                        Color.parseColor("#567DF4")));
        pieChart.addPieSlice(
                new PieModel(
                        "Others",
                        Others,
                        Color.parseColor("#00796B")));
        pieChart.addPieSlice(
                new PieModel(
                        "Available",
                        Available,
                        Color.parseColor("#4CE364")));

        pieChart.startAnimation();
    }
}