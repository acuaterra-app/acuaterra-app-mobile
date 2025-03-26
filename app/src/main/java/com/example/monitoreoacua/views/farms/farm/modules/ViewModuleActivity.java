package com.example.monitoreoacua.views.farms.farm.modules;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Module;
import com.example.monitoreoacua.business.models.Sensor;
import com.example.monitoreoacua.views.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class ViewModuleActivity extends BaseActivity implements SensorAdapter.OnSensorInteractionListener {

    private static final String TAG = "ViewModuleActivity";
    private static final String ARG_MODULE = "module";
    
    private Module module;
    private TextView textModuleName;
    private TextView textModuleLocation;
    private TextView textModuleCoordinates;
    private TextView textModuleSpecies;
    private TextView textModuleQuantity;
    private TextView textModuleAge;
    private TextView textModuleDimensions;
    private RecyclerView recyclerViewSensors;
    private SensorAdapter sensorAdapter;

    /**
     * Creates an intent to start the ViewModuleActivity with the specified module.
     *
     * @param context The context to create the intent from
     * @param module The module to display
     * @return An intent to start the ViewModuleActivity
     */
    public static Intent newIntent(Context context, Module module) {
        Intent intent = new Intent(context, ViewModuleActivity.class);
        intent.putExtra(ARG_MODULE, module);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_module);
        
        // Initialize views
        textModuleName = findViewById(R.id.module_name);
        textModuleLocation = findViewById(R.id.module_location);
        textModuleCoordinates = findViewById(R.id.module_coordinates);
        textModuleSpecies = findViewById(R.id.module_species);
        textModuleQuantity = findViewById(R.id.module_fish_quantity);
        textModuleAge = findViewById(R.id.module_fish_age);
        textModuleDimensions = findViewById(R.id.module_dimensions);
        recyclerViewSensors = findViewById(R.id.recycler_view_sensors);
        
        // Get module from intent
        if (getIntent().hasExtra(ARG_MODULE)) {
            module = (Module) getIntent().getSerializableExtra(ARG_MODULE);
            loadModuleData();
            setupRecyclerView();
        } else {
            Log.e(TAG, "No module provided in intent");
            Toast.makeText(this, "Error: No module data available", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected String getActivityTitle() {
        return module != null ? module.getName() : "Module Details";
    }

    /**
     * Loads the module data into the views
     */
    private void loadModuleData() {
        if (module != null) {
            textModuleName.setText(module.getName());
            textModuleLocation.setText(module.getLocation());
            
            String coordinates = String.format("Lat: %s, Long: %s", 
                    module.getLatitude(), module.getLongitude());
            textModuleCoordinates.setText(coordinates);
            
            textModuleSpecies.setText(module.getSpeciesFish());
            textModuleQuantity.setText(String.valueOf(module.getFishQuantity()));
            textModuleAge.setText(String.valueOf(module.getFishAge()));
            textModuleDimensions.setText(module.getDimensions());
        }
    }

    /**
     * Sets up the RecyclerView with the module's sensors
     */
    private void setupRecyclerView() {
        recyclerViewSensors.setLayoutManager(new LinearLayoutManager(this));
        
        List<Sensor> sensors = module.getSensors();
        if (sensors == null) {
            sensors = new ArrayList<>();
        }
        
        sensorAdapter = new SensorAdapter(this, sensors, this);
        recyclerViewSensors.setAdapter(sensorAdapter);
        
        if (sensors.isEmpty()) {
            Log.i(TAG, "No sensors found for module: " + module.getId());
            Toast.makeText(this, "No sensors available for this module", Toast.LENGTH_SHORT).show();
        } else {
            Log.i(TAG, "Loaded " + sensors.size() + " sensors for module: " + module.getId());
        }
    }

    /**
     * Handles the sensor item click event
     *
     * @param sensor The sensor that was clicked
     */
    @Override
    public void onSensorSelected(Sensor sensor) {
        if (sensor != null) {
            // In the future, this could navigate to a detailed sensor view
            Toast.makeText(this, "Sensor selected: " + sensor.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the view details button click event
     *
     * @param sensor The sensor whose details button was clicked
     */
    @Override
    public void onViewSensorDetails(Sensor sensor) {
        if (sensor != null) {
            // In the future, this could navigate to a detailed sensor view
            Toast.makeText(this, "View details for sensor: " + sensor.getName(), Toast.LENGTH_SHORT).show();
        }
    }
}

