package com.example.monitoreoacua.views.farms.farm.modules;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.monitoreoacua.business.models.Sensor;
import com.example.monitoreoacua.fragments.ViewModuleFragment;
import com.example.monitoreoacua.views.BaseActivity;
import com.example.monitoreoacua.views.measurements.SensorMeasurementsActivity;

/**
 * Activity for displaying module details.
 * This activity only receives the module ID and passes it to the fragment,
 * all the loading logic is now in the fragment.
 */
public class ViewModuleActivity extends BaseActivity implements ViewModuleFragment.OnModuleSensorListener {
    private static final String TAG = "ViewModuleActivityTag";
    public static final String ARG_MODULE_ID = "module_id";
    private int moduleId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get the module ID from the intent
        moduleId = getIntent().getIntExtra(ARG_MODULE_ID, -1);
        
        // Validate the module ID
        if (moduleId == -1) {
            Log.e(TAG, "Invalid module ID received");
            Toast.makeText(this, "Error: ID de módulo inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        Log.d(TAG, "ViewModuleActivity received valid module ID: " + moduleId);
        
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getActivityTitle() {
        return "Detalles del Módulo";

    }

    @Override
    protected void loadInitialFragment() {
        // Create the fragment and pass the module ID through arguments
        Log.d("My_Tag", "About to create fragment with module ID: " + moduleId);
        ViewModuleFragment moduleFragment = ViewModuleFragment.newInstance(moduleId);
        Log.d("My_Tag", "Created fragment with module ID: " + moduleId);
        loadFragment(moduleFragment, false);
    }

    @Override
    public void onSensorClick(Sensor sensor) {
        // Handle sensor click event
        Toast.makeText(this, "Abriendo mediciones de: " + sensor.getName(), Toast.LENGTH_SHORT).show();
        
        // Navigate to SensorMeasurementsActivity
        Intent intent = new Intent(this, SensorMeasurementsActivity.class);
        intent.putExtra("SENSOR_ID", sensor.getId());
        intent.putExtra("SENSOR_NAME", sensor.getName());
        startActivity(intent);
    }
}
