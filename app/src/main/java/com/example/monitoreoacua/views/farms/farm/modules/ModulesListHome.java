package com.example.monitoreoacua.views.farms.farm.modules;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

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

public class ModulesListHome extends AppCompatActivity {

    // Declaration of navigation bar elements
    private AppCompatImageButton navHome, navSettings, navProfile, navCloseSesion;
    private Button buttonAddModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modules_list_home);
        
        Log.d("ModulesListHome", "Activity started");
        Toast.makeText(this, "Bienvenido a Módulos", Toast.LENGTH_SHORT).show();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        buttonAddModule = findViewById(R.id.buttonAddModule);
        buttonAddModule.setOnClickListener(v -> {
            Intent intent = new Intent(ModulesListHome.this, RegisterModules.class);
            startActivity(intent);
        });

        // Initialize navigation bar elements
        navHome = findViewById(R.id.navHome);
        // navSettings = findViewById(R.id.navSettings);
        navProfile = findViewById(R.id.navProfile);
        navCloseSesion = findViewById(R.id.navCloseSesion);

        // Events for the navigation bar
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(ModulesListHome.this, ListFarmsActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ModulesListHome.this, SupportActivity.class);
            startActivity(intent);
        });

        navCloseSesion.setOnClickListener(v -> {
            Intent intent = new Intent(ModulesListHome.this, ClosesectionActivity.class);
            startActivity(intent);
        });
    }
}