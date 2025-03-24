package com.example.monitoreoacua.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.views.login.LoginActivity;

/**
 * Fragment for handling user logout functionality.
 * Provides a confirmation dialog with options to confirm or cancel logout.
 */
public class LogoutFragment extends Fragment {

    private static final String TAG = "LogoutFragment";
    private static final String PREFS_NAME = "user_prefs";
    private static final String TOKEN_KEY = "token";

    private OnLogoutInteractionListener mListener;

    /**
     * Interface for communication with host activity
     */
    public interface OnLogoutInteractionListener {
        void onLogoutCancelled();
    }

    public LogoutFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment
     */
    public static LogoutFragment newInstance() {
        return new LogoutFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (OnLogoutInteractionListener) context;
        } catch (ClassCastException e) {
            Log.e(TAG, "Activity must implement OnLogoutInteractionListener", e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        Button buttonConfirmLogout = view.findViewById(R.id.buttonConfirmLogout);
        Button buttonCancelLogout = view.findViewById(R.id.buttonCancelLogout);

        // Set click listeners
        buttonConfirmLogout.setOnClickListener(v -> logout());
        buttonCancelLogout.setOnClickListener(v -> cancelLogout());
    }

    /**
     * Handles the logout action, removes authentication token and redirects to login screen
     */
    private void logout() {
        Log.i(TAG, "Performing user logout");
        
        if (getContext() == null) {
            Log.e(TAG, "Context is null, cannot perform logout");
            return;
        }

        // Remove authentication token from SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(TOKEN_KEY);
        editor.apply();
        
        Log.d(TAG, "Token removed from SharedPreferences");
        
        // Navigate to login screen and clear activity stack
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        
        // Close current activity
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    /**
     * Handles the cancel action, returns to previous screen
     */
    private void cancelLogout() {
        Log.d(TAG, "Logout canceled by user");
        if (mListener != null) {
            mListener.onLogoutCancelled();
        }
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}

