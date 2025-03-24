package com.example.monitoreoacua.views.farms.farm.modules;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.utils.NavigationHelper;
import com.example.monitoreoacua.views.farms.ListFarmsActivity;
import com.example.monitoreoacua.views.menu.CloseSectionActivity;
import com.example.monitoreoacua.views.menu.SupportActivity;

public class RegisterModulesActivity extends AppCompatActivity {

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

        // Get farm ID from intent
        int farmId = getIntent().getIntExtra("farmId", -1);
        if (farmId == -1) {
            Toast.makeText(this, "Error: ID de granja no proporcionado", Toast.LENGTH_LONG).show();
        }

        // Initialize navigation using NavigationHelper
        LinearLayout navigationContainer = findViewById(R.id.bottomNav);
        NavigationHelper.setupNavigation(navigationContainer, 
            itemId -> NavigationHelper.navigateToSection(this, itemId, RegisterModulesActivity.class));
        
        // No item is selected in registration screen
        // NavigationHelper.updateSelectedItem(navigationContainer, R.id.navModules);

    }

}