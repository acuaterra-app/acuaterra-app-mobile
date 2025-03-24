package com.example.monitoreoacua.views.sample;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.monitoreoacua.fragments.ExampleFragment;
import com.example.monitoreoacua.views.BaseActivity;

/**
 * Sample activity that demonstrates how to use the BaseActivity class.
 * This activity loads a simple ExampleFragment into the container.
 */
public class SampleActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // The parent class (BaseActivity) handles all the common setup
    }
    
    @Override
    protected String getActivityTitle() {
        return "Sample Activity";
    }
    
    @Override
    protected void loadInitialFragment() {
        // Load the example fragment into the container
        ExampleFragment exampleFragment = ExampleFragment.newInstance();
        loadFragment(exampleFragment, false);
    }
}

