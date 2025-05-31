package com.example.monitoreoacua.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.views.notifications.NotificationsActivity;

/**
 * TopBarFragment handles the top bar functionality.
 * It provides a customizable title and consistent styling across the app.
 */
public class TopBarFragment extends Fragment {

    private TextView textViewTitle;
    private TextView notificationBadge;
    private ImageView notificationIcon;
    private ImageView profileIcon;
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
        notificationBadge = view.findViewById(R.id.notificationBadge);
        notificationIcon = view.findViewById(R.id.notificationIcon);
        profileIcon = view.findViewById(R.id.profileIcon);
        
        // Set click listener for notification icon
        if (notificationIcon != null) {
            notificationIcon.setOnClickListener(v -> {
                // Navigate to the NotificationsActivity
                Intent intent = new Intent(getContext(), com.example.monitoreoacua.views.notifications.NotificationsActivity.class);
                getContext().startActivity(intent);
            });
        }
        
        // Set click listener for profile icon
        if (profileIcon != null) {
            profileIcon.setOnClickListener(v -> {
                // Navigate to the ProfileActivity
                Intent intent = new Intent(getContext(), com.example.monitoreoacua.views.profile.ProfileActivity.class);
                getContext().startActivity(intent);
            });
        }
        
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

    /**
     * Updates the notification badge with the given count
     * @param count The number of unread notifications
     *              If count is 0, hides the badge
     *              If count is greater than 0, shows the badge with the count
     */
    public void updateNotificationBadge(int count) {
        if (notificationBadge == null) return;
        
        if (count <= 0) {
            notificationBadge.setVisibility(View.GONE);
        } else {
            notificationBadge.setText(String.valueOf(count));
            notificationBadge.setVisibility(View.VISIBLE);
        }
    }
}

