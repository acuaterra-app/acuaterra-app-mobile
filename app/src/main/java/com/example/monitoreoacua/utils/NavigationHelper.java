package com.example.monitoreoacua.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.monitoreoacua.R;
import com.example.monitoreoacua.views.farms.ListFarmsActivity;
import com.example.monitoreoacua.views.menu.SupportActivity;
import com.example.monitoreoacua.views.menu.CloseSectionActivity;
import java.util.HashMap;
import java.util.Map;

public class NavigationHelper {
    private static final String TAG = "NavigationHelper";

    // Navigation map with destination IDs and classes
    private static final Map<Integer, Class<?>> NAVIGATION_MAP = new HashMap<>();

    static {
        NAVIGATION_MAP.put(R.id.navFarms, ListFarmsActivity.class);
        NAVIGATION_MAP.put(R.id.navUsers, ListFarmsActivity.class); // Adjust if there is a specific activity for Users
        NAVIGATION_MAP.put(R.id.navSupport, SupportActivity.class);
        NAVIGATION_MAP.put(R.id.navClose, CloseSectionActivity.class);
    }

    public interface NavigationItemClickListener {
        void onItemClick(int itemId);
    }

    // Set up navigation listeners
    public static void setupNavigation(LinearLayout navContainer, NavigationItemClickListener listener) {
        if (navContainer == null) {
            Log.e(TAG, "Navigation container is null");
            return;
        }

        for (int itemId : NAVIGATION_MAP.keySet()) {
            setupNavigationItem(navContainer, itemId, listener);
        }
    }

    private static void setupNavigationItem(LinearLayout container, int itemId, NavigationItemClickListener listener) {
        LinearLayout item = container.findViewById(itemId);
        if (item == null) {
            Log.e(TAG, "Navigation item not found: " + itemId);
            return;
        }

        item.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(itemId);
            }
            updateSelectedItem(container, itemId);
        });
    }

    // Handle navigation to a new section
    public static void navigateToSection(Context context, int itemId, Class<?> currentActivityClass) {
        if (context == null) {
            Log.e(TAG, "Context is null");
            return;
        }

        Class<?> targetActivityClass = NAVIGATION_MAP.get(itemId);

        if (targetActivityClass != null && !targetActivityClass.equals(currentActivityClass)) {
            Intent intent = new Intent(context, targetActivityClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }

    // Visually update the selected element
    public static void updateSelectedItem(LinearLayout container, int selectedItemId) {
        if (container == null) {
            return; // Prevent errors if the container is null
        }

        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);

            if (child instanceof LinearLayout) {
                LinearLayout navItem = (LinearLayout) child;
                TextView textView = (TextView) navItem.getChildAt(1); // The second child is the TextView

                if (textView != null) {
                    if (navItem.getId() == selectedItemId) {
                        textView.setTextColor(0xFF0000FF); // Blue for the selected one
                    } else {
                        textView.setTextColor(0xFF000000); // Black for the unselected ones
                    }
                }
            }
        }
    }
}

