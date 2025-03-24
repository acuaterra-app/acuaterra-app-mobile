package com.example.monitoreoacua.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.views.LoginActivity;
import com.example.monitoreoacua.views.farms.ListFarmsActivity;
import com.example.monitoreoacua.views.SupportActivity;
import com.example.monitoreoacua.views.users.ListUsersActivity;

public class NavigationHelper {
    private static final String TAG = "NavigationHelper";

    public interface NavigationItemClickListener {
        void onNavigationItemClicked(int itemId);
    }

    public static void setupNavigation(View navigationView, final NavigationItemClickListener listener) {
        if (navigationView == null) return;

        // Setup click listeners for navigation items
        setupNavigationItem(navigationView, R.id.navFarms, listener);
        setupNavigationItem(navigationView, R.id.navUsers, listener);
        setupNavigationItem(navigationView, R.id.navSupport, listener);
        setupNavigationItem(navigationView, R.id.navClose, listener);
    }

    private static void setupNavigationItem(View container, int itemId, final NavigationItemClickListener listener) {
        View navigationItem = container.findViewById(itemId);
        if (navigationItem != null) {
            navigationItem.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNavigationItemClicked(itemId);
                }
            });
        }
    }

    public static void navigateToSection(Context context, int itemId) {
        if (context == null) return;

        Intent intent = null;
        boolean shouldFinish = true;

        switch (itemId) {
            case R.id.navFarms:
                intent = new Intent(context, ListFarmsActivity.class);
                break;
            case R.id.navUsers:
                intent = new Intent(context, ListUsersActivity.class);
                break;
            case R.id.navSupport:
                intent = new Intent(context, SupportActivity.class);
                break;
            case R.id.navClose:
                intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                shouldFinish = false;
                break;
        }

        if (intent != null) {
            if (shouldFinish) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
            context.startActivity(intent);
            if (shouldFinish && context instanceof Activity) {
                ((Activity) context).finish();
            }
        }
    }

    public static void updateSelectedItem(View container, int selectedItemId) {
        if (container == null) return;

        int[] navigationIds = {
            R.id.navFarms,
            R.id.navUsers,
            R.id.navSupport,
            R.id.navClose
        };

        for (int id : navigationIds) {
            View item = container.findViewById(id);
            if (item != null) {
                View indicator = item.findViewById(R.id.indicator);
                TextView text = item.findViewById(R.id.tvNavText);
                boolean isSelected = id == selectedItemId;
                
                if (indicator != null) {
                    indicator.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
                }
                if (text != null) {
                    text.setTextColor(container.getContext().getResources().getColor(
                        isSelected ? R.color.colorPrimary : R.color.colorDivider
                    ));
                }
            }
        }
    }
}

