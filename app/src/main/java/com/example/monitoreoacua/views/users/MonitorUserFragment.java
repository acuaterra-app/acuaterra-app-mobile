package com.example.monitoreoacua.views.users;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.auth.AuthUser;
import com.example.monitoreoacua.fragments.TopBarFragment;
import com.example.monitoreoacua.utils.SessionManager;

/**
 * A simplified fragment that displays only the current user's profile
 * for users with the monitor role.
 */
public class MonitorUserFragment extends Fragment {

    private static final String TAG = "MonitorUserFragment";
    private TextView tvUserName, tvUserEmail, tvUserRole, tvUserDni;
    private CardView userCard;

    public MonitorUserFragment() {
        // Required empty public constructor
    }

    public static MonitorUserFragment newInstance() {
        return new MonitorUserFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment - using a simple layout without FAB
        View view = inflater.inflate(R.layout.fragment_monitor_user, container, false);
        
        // Initialize views
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvUserRole = view.findViewById(R.id.tvUserRole);
        tvUserDni = view.findViewById(R.id.tvUserDni);
        userCard = view.findViewById(R.id.userCard);
        
        // Load current user data
        loadUserData();
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Set the title in the TopBar
        if (getActivity() != null) {
            TopBarFragment topBarFragment = (TopBarFragment) getActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.topBarContainer);
            if (topBarFragment != null) {
                topBarFragment.setTitle("Mi Perfil");
            }
        }
    }

    private void loadUserData() {
        // Get current user from SessionManager
        SessionManager sessionManager = SessionManager.getInstance(requireContext());
        AuthUser currentUser = sessionManager.getUser();
        
        if (currentUser != null) {
            Log.d(TAG, "Loading user data for: " + currentUser.getName());
            
            // Set user information to TextViews
            tvUserName.setText(currentUser.getName());
            tvUserEmail.setText(currentUser.getEmail());
            tvUserRole.setText("Rol: " + currentUser.getRole());
            tvUserDni.setText("DNI: " + currentUser.getDni());
        } else {
            Log.e(TAG, "Current user is null");
            userCard.setVisibility(View.GONE);
        }
    }
}

