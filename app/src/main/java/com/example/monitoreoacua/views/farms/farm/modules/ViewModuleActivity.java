package com.example.monitoreoacua.views.farms.farm.modules;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.monitoreoacua.business.models.Sensor;
import com.example.monitoreoacua.fragments.ViewModuleFragment;
import com.example.monitoreoacua.views.BaseActivity;

/**
 * Activity for displaying module details.
 * This activity only receives the module ID and passes it to the fragment,
 * all the loading logic is now in the fragment.
 */
public class ViewModuleActivity extends BaseActivity implements ViewModuleFragment.OnModuleSensorListener {
    private static final String TAG = "ViewModuleActivityTag";
    private static final String ARG_MODULE_ID = "module_id";
    private int moduleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get the module ID from the intent
        moduleId = getIntent().getIntExtra(ARG_MODULE_ID, -1);
        Log.d(TAG, "ViewModuleActivity received module ID: " + moduleId);
        
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getActivityTitle() {
        return "Modulo";
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
        Toast.makeText(this, "Sensor: " + sensor.getName(), Toast.LENGTH_SHORT).show();
        // TODO: Navigate to sensor details screen
    }
}
