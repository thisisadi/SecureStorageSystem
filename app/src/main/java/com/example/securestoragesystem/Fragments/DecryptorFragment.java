package com.example.securestoragesystem.Fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.securestoragesystem.R;

public class DecryptorFragment extends Fragment {
    private ImageView decrypt;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_decryptor, container, false);
        decrypt = view.findViewById(R.id.decrypt);
        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction =((AppCompatActivity)getContext()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new FingerprintFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
    }
}