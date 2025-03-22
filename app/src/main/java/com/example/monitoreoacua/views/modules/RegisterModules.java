package com.example.monitoreoacua.views.modules;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.views.farms.ListFarmsActivity;
import com.example.monitoreoacua.views.menu.ClosesectionActivity;
import com.example.monitoreoacua.views.menu.SupportActivity;

public class RegisterModules extends AppCompatActivity {

    // Declaration of navigation bar elements
    private AppCompatImageButton navHome, navSettings, navProfile, navCloseSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_modules);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize navigation bar elements
        navHome = findViewById(R.id.navHome);
        // navSettings = findViewById(R.id.navSettings);
        navProfile = findViewById(R.id.navProfile);
        navCloseSesion = findViewById(R.id.navCloseSesion);

        // Events for the navigation bar
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterModules.this, ListFarmsActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterModules.this, SupportActivity.class);
            startActivity(intent);
        });

        navCloseSesion.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterModules.this, ClosesectionActivity.class);
            startActivity(intent);
        });

    }

}