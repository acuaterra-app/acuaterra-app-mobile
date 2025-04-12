package com.example.monitoreoacua.views.measurements;

import android.os.Bundle;
import android.widget.Toast;

import com.example.monitoreoacua.business.models.Measurement;
import com.example.monitoreoacua.fragments.ListMeasurementsFragment;
import com.example.monitoreoacua.views.BaseActivity;

/**
 * Activity for displaying measurements of a module
 */
public class ListMeasurementsActivity extends BaseActivity implements ListMeasurementsFragment.OnMeasurementInteractionListener {
    private static final String TAG = "ListMeasurementsActivity";
    private int moduleId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        moduleId = getIntent().getIntExtra("moduleId", -1);
        if (moduleId == -1) {
            Toast.makeText(this, "Error: ID de módulo no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getActivityTitle() {
        return "Mediciones";
    }

    @Override
    protected void loadInitialFragment() {
        ListMeasurementsFragment fragment = ListMeasurementsFragment.newInstance(String.valueOf(moduleId));
        loadFragment(fragment, false);
    }

    @Override
    public void onMeasurementSelected(Measurement measurement) {
        // TODO: Implementar navegación a detalles de medición si es necesario
        Toast.makeText(this, "Medición seleccionada: " + measurement.getValue(), Toast.LENGTH_SHORT).show();
    }
}

