package com.example.monitoreoacua.views.farms.farm;

import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Farm;

public class FarmDetailsActivity extends AppCompatActivity {

    private TextView textViewFarmName, textViewFarmAddress;
    private Button buttonMonitors, buttonModules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_details);

        // Inicializar vistas
        textViewFarmName = findViewById(R.id.textViewFarmName);
        textViewFarmAddress = findViewById(R.id.textViewFarmAddress);
        buttonMonitors = findViewById(R.id.buttonMonitors);
        buttonModules = findViewById(R.id.buttonModules);

        // Recibir la granja del Intent
        Farm farm = getIntent().getParcelableExtra("farm");

        if (farm != null) {
            textViewFarmName.setText(farm.getName());
            textViewFarmAddress.setText(farm.getAddress());
        } else {
            Toast.makeText(this, "Error: No se recibió información de la granja", Toast.LENGTH_SHORT).show();
            finish(); // Cierra la actividad si no hay datos
        }

        // Botón Monitors (agrega la lógica necesaria)
        buttonMonitors.setOnClickListener(v -> {
            Toast.makeText(this, "Abrir Monitores", Toast.LENGTH_SHORT).show();
            // Aquí puedes iniciar otra actividad si es necesario
        });

        // Botón Modules (agrega la lógica necesaria)
        buttonModules.setOnClickListener(v -> {
            Toast.makeText(this, "Abrir Módulos", Toast.LENGTH_SHORT).show();
            // Aquí puedes iniciar otra actividad si es necesario
        });
    }
}
