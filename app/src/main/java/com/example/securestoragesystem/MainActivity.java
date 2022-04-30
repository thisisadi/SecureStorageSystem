package com.example.securestoragesystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.securestoragesystem.Fragments.DecryptorFragment;
import com.example.securestoragesystem.Fragments.HelpFragment;
import com.example.securestoragesystem.Fragments.HomeFragment;
import com.example.securestoragesystem.Fragments.ProfileFragment;
import com.example.securestoragesystem.Fragments.SettingsFragment;
import com.example.securestoragesystem.Fragments.StorageFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private int selectedItem = 0;
    private TextView logout, name, location;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        logout = findViewById(R.id.logout);
        name = findViewById(R.id.name);
        location = findViewById(R.id.location);

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState ==null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
            // Adds back image to the default menu item.
            navigationView.getMenu().getItem(selectedItem).setActionView(R.layout.menu_image);
            navigationView.setCheckedItem(R.id.nav_home);
        }

        TextView logout = navigationView.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(MainActivity.this, SplashScreen.class);
                startActivity(i);
                finishAffinity();
            }
        });
        ImageView closeMenu = navigationView.getHeaderView(0).findViewById(R.id.closeMenu);
        closeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Removes the back image from the previously chosen menu item.
        navigationView.getMenu().getItem(selectedItem).setActionView(null);
        // Updates the index of the current selected menu item.

        switch (item.getItemId()) {
            case R.id.nav_home:
                selectedItem = 0;
                toolbar.setTitle("Home");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;
            case R.id.nav_profile:
                selectedItem = 1;
                toolbar.setTitle("My Profile");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;
            case R.id.nav_storage:
                selectedItem = 2;
                toolbar.setTitle("Storage Details");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StorageFragment()).commit();
                break;
            case R.id.nav_decrypt:
                selectedItem = 3;
                toolbar.setTitle("Decryptor");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DecryptorFragment()).commit();
                break;
            case R.id.nav_settings:
                selectedItem = 4;
                toolbar.setTitle("Settings");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;
            case R.id.nav_help:
                selectedItem = 5;
                toolbar.setTitle("Help");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HelpFragment()).commit();
                break;

        }
        // Adds back image to the current selected menu item.
        navigationView.getMenu().getItem(selectedItem).setActionView(R.layout.menu_image).setChecked(false);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
}