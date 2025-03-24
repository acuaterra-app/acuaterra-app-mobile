package com.example.monitoreoacua.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.monitoreoacua.R;

/**
 * A simple example fragment with a basic layout containing a TextView.
 */
public class ExampleFragment extends Fragment {

    private TextView textViewExample;
    
    public ExampleFragment() {
        // Required empty public constructor
    }
    
    /**
     * Factory method to create a new instance of this fragment.
     */
    public static ExampleFragment newInstance() {
        return new ExampleFragment();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_example, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        textViewExample = view.findViewById(R.id.textViewExample);
        
        // You can modify the TextView text if needed
        // textViewExample.setText("Updated text from fragment code");
    }
}

