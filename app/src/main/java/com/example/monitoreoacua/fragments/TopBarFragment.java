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
 * TopBarFragment handles the top bar functionality.
 * It provides a customizable title and consistent styling across the app.
 */
public class TopBarFragment extends Fragment {

    private TextView textViewTitle;
    private String title;

    /**
     * Factory method to create a new instance of TopBarFragment.
     * @return A new instance of TopBarFragment
     */
    public static TopBarFragment newInstance() {
        return new TopBarFragment();
    }

    /**
     * Factory method to create a new instance of TopBarFragment with a specific title.
     * @param title The title to display in the top bar
     * @return A new instance of TopBarFragment with the specified title
     */
    public static TopBarFragment newInstance(String title) {
        TopBarFragment fragment = new TopBarFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title", "");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_bar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize the title TextView
        textViewTitle = view.findViewById(R.id.textViewActivityTitle);
        
        // Set the title if it was provided
        if (title != null && !title.isEmpty()) {
            setTitle(title);
        }
    }

    /**
     * Sets the title text in the top bar.
     * @param title The title text to display
     */
    public void setTitle(String title) {
        this.title = title;
        if (textViewTitle != null) {
            textViewTitle.setText(title);
        }
    }

    /**
     * Gets the current title text.
     * @return The current title text
     */
    public String getTitle() {
        return title;
    }
}

