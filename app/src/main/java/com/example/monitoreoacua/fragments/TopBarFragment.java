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


    public static TopBarFragment newInstance() {
        return new TopBarFragment();
    }


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
        return inflater.inflate(R.layout.fragment_top_bar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        textViewTitle = view.findViewById(R.id.textViewActivityTitle);
        
        if (title != null && !title.isEmpty()) {
            setTitle(title);
        }
    }

    public void setTitle(String title) {
        this.title = title;
        if (textViewTitle != null) {
            textViewTitle.setText(title);
        }
    }


    public String getTitle() {
        return title;
    }
}

