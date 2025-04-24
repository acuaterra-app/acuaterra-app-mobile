package com.example.monitoreoacua.views.measurements;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.monitoreoacua.business.models.Measurement;
import com.example.monitoreoacua.fragments.ListMeasurementsFragment;
import com.example.monitoreoacua.views.BaseActivity;

/**
 * Activity for displaying measurements of a specific sensor
 */
public class SensorMeasurementsActivity extends BaseActivity implements ListMeasurementsFragment.OnMeasurementInteractionListener {
    private static final String TAG = "SensorMeasurementsActivity";
    
    private int sensorId;
    private String sensorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get data from intent
        if (getIntent() != null) {
            sensorId = getIntent().getIntExtra("SENSOR_ID", -1);
            sensorName = getIntent().getStringExtra("SENSOR_NAME");
            
            if (sensorId == -1) {
                // Invalid sensor ID
                Toast.makeText(this, "Error: ID de sensor inválido", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            
            if (sensorName == null || sensorName.isEmpty()) {
                sensorName = "Sensor";
            }
            
            Log.d(TAG, "Showing measurements for sensor ID: " + sensorId + ", name: " + sensorName);
        } else {
            // No intent data
            Toast.makeText(this, "Error: Datos de sensor no proporcionados", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getActivityTitle() {
        return "Mediciones: " + sensorName;
    }

    @Override
    protected void loadInitialFragment() {
        // Create fragment with sensor ID
        ListMeasurementsFragment measurementsFragment = ListMeasurementsFragment.newInstance(String.valueOf(sensorId));
        loadFragment(measurementsFragment, false);
    }

    @Override
    public void onMeasurementSelected(Measurement measurement) {
        // Handle measurement selection
        Toast.makeText(this, "Medición seleccionada: " + measurement.getValue(), Toast.LENGTH_SHORT).show();
        // TODO: Add detailed view for individual measurements if needed
    }
}

