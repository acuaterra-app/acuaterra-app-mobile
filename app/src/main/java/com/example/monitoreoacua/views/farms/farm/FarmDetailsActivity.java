package com.example.monitoreoacua.views.farms.farm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatImageButton;//de la nav
import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiFarmsService;
import com.example.monitoreoacua.service.request.BaseRequest;
import com.example.monitoreoacua.service.response.FarmResponse;
import com.example.monitoreoacua.views.menu.CloseSessionActivity;
import com.example.monitoreoacua.views.menu.SupportActivity;
import com.example.monitoreoacua.views.farms.ListFarmsActivity;
import com.example.monitoreoacua.views.farms.farm.modules.ListModulesActivity;
import android.util.Log;
import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FarmDetailsActivity extends AppCompatActivity {

    private TextView textViewFarmName, textViewFarmAddress;
    // private Button buttonMonitors, buttonModules;
    private Button buttonModules;
    // Declaration of navigation bar elements
    private AppCompatImageButton navHome, navSettings, navProfile, navCloseSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_details);

        // Initialize views
        textViewFarmName = findViewById(R.id.textViewFarmName);
        textViewFarmAddress = findViewById(R.id.textViewFarmAddress);
        buttonModules = findViewById(R.id.buttonModules);

        // Initialize navigation bar elements
        navHome = findViewById(R.id.navHome);
        // navSettings = findViewById(R.id.navSettings);
        navProfile = findViewById(R.id.navProfile);
        navCloseSesion = findViewById(R.id.navCloseSesion);

        // Receive the farm from the Intent
        Farm farm = getIntent().getParcelableExtra("farm");

        if (farm != null) {
            textViewFarmName.setText(farm.getName());
            textViewFarmAddress.setText(farm.getAddress());
        } else {
            Toast.makeText(this, "Error: No se recibió información de la granja", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if there is no data
        }

        // Configure button modules
        buttonModules.setOnClickListener(v -> {
                Intent intent = new Intent(FarmDetailsActivity.this, ListModulesActivity.class);
                intent.putExtra("farmId", farm.getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
        });

        // Events for the navigation bar
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(FarmDetailsActivity.this, ListFarmsActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(FarmDetailsActivity.this, SupportActivity.class);
            startActivity(intent);
        });
        navCloseSesion.setOnClickListener(v -> {
            Intent intent = new Intent(FarmDetailsActivity.this, CloseSessionActivity.class);
            startActivity(intent);
        });
    }
}
