package com.example.monitoreoacua.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.monitoreoacua.R;

/**
 * Helper class for managing navigation item selection, state management, and animations
 * for the app's bottom navigation bar.
 */
public class NavigationHelper {
    private static final String TAG = "NavigationHelper";
    private static final int ANIMATION_DURATION = 300; // milliseconds
    
    // Interface for navigation item click callbacks
    public interface NavigationItemClickListener {
        void onItemClick(int itemId);
    }
    
    /**
     * Sets up click listeners for the navigation items in the bottom navigation bar.
     *
     * @param navContainer LinearLayout containing the navigation items
     * @param listener Callback to be invoked when a navigation item is clicked
     */
    public static void setupNavigation(LinearLayout navContainer, NavigationItemClickListener listener) {
        if (navContainer == null) {
            Log.e(TAG, "Navigation container is null");
            return;
        }
        
        // Find and setup all navigation items
        setupNavigationItem(navContainer, R.id.navHome, listener);
        setupNavigationItem(navContainer, R.id.navFarms, listener);
        setupNavigationItem(navContainer, R.id.navModules, listener);
        setupNavigationItem(navContainer, R.id.navSettings, listener);
    }
    
    /**
     * Sets up a single navigation item with click listeners.
     *
     * @param container Parent container holding navigation items
     * @param itemId Resource ID of the navigation item
     * @param listener Callback to be invoked when the item is clicked
     */
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
    
    /**
     * Updates the selected state of navigation items.
     *
     * @param container Parent container holding navigation items
     * @param selectedItemId Resource ID of the selected navigation item
     */
    public static void updateSelectedItem(LinearLayout container, int selectedItemId) {
        if (container == null) {
            Log.e(TAG, "Navigation container is null");
            return;
        }
        
        // Find all navigation items
        LinearLayout homeItem = container.findViewById(R.id.navHome);
        LinearLayout farmsItem = container.findViewById(R.id.navFarms);
        LinearLayout modulesItem = container.findViewById(R.id.navModules);
        LinearLayout settingsItem = container.findViewById(R.id.navSettings);
        
        // Update state for each item
        updateItemState(homeItem, homeItem.getId() == selectedItemId);
        updateItemState(farmsItem, farmsItem.getId() == selectedItemId);
        updateItemState(modulesItem, modulesItem.getId() == selectedItemId);
        updateItemState(settingsItem, settingsItem.getId() == selectedItemId);
    }
    
    /**
     * Updates the visual state of a navigation item based on selection state.
     *
     * @param item Navigation item view
     * @param selected Whether the item is selected
     */
    public static void updateItemState(LinearLayout item, boolean selected) {
        if (item == null) {
            Log.e(TAG, "Navigation item is null");
            return;
        }
        
        View indicator = item.findViewById(R.id.indicator);
        TextView textView = item.findViewById(R.id.tvNavText);
        ImageView imageView = item.findViewById(R.id.ivNavIcon);
        
        if (indicator == null || textView == null || imageView == null) {
            Log.e(TAG, "Navigation item views not found");
            return;
        }
        
        // Animate indicator visibility
        animateIndicatorVisibility(indicator, selected);
        
        // Update text color
        Context context = item.getContext();
        int selectedColor = context.getResources().getColor(R.color.colorPrimary);
        int defaultColor = context.getResources().getColor(R.color.colorBackground);
        
        textView.setTextColor(selected ? selectedColor : defaultColor);
        
        // Optional: Update icon tint if needed
        // imageView.setColorFilter(selected ? selectedColor : defaultColor);
    }
    
    /**
     * Animate the visibility change of the navigation indicator.
     *
     * @param indicator The indicator view to animate
     * @param visible Whether the indicator should be visible
     */
    private static void animateIndicatorVisibility(View indicator, boolean visible) {
        if (indicator == null) return;
        
        float targetAlpha = visible ? 1.0f : 0.0f;
        
        // If already at target state, don't animate
        if (indicator.getAlpha() == targetAlpha) {
            return;
        }
        
        // Stop any running animations
        indicator.clearAnimation();
        
        // Create and start fade animation
        ValueAnimator animator = ValueAnimator.ofFloat(indicator.getAlpha(), targetAlpha);
        animator.setDuration(ANIMATION_DURATION);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float alpha = (float) animation.getAnimatedValue();
            indicator.setAlpha(alpha);
            
            // Also adjust visibility for performance
            if (alpha == 0f && indicator.getVisibility() != View.INVISIBLE) {
                indicator.setVisibility(View.INVISIBLE);
            } else if (alpha > 0f && indicator.getVisibility() != View.VISIBLE) {
                indicator.setVisibility(View.VISIBLE);
            }
        });
        
        animator.start();
    }
    
    /**
     * Navigate to the specified activity based on the selected navigation item.
     *
     * @param context Current context
     * @param itemId ID of the selected navigation item
     * @param currentActivityClass Class of the current activity to avoid redundant navigation
     */
    public static void navigateToSection(Context context, int itemId, Class<?> currentActivityClass) {
        if (context == null) {
            Log.e(TAG, "Context is null");
            return;
        }
        
        Class<?> targetActivityClass = null;
        
        // Map navigation item IDs to their respective activities
        if (itemId == R.id.navHome) {
            targetActivityClass = getActivityClassByName(context, "HomeActivity");
        } else if (itemId == R.id.navFarms) {
            targetActivityClass = getActivityClassByName(context, "FarmsActivity");
        } else if (itemId == R.id.navModules) {
            targetActivityClass = getActivityClassByName(context, "ModulesListActivity");
        } else if (itemId == R.id.navSettings) {
            targetActivityClass = getActivityClassByName(context, "SettingsActivity");
        }
        
        // Don't navigate if we're already on the target activity
        if (targetActivityClass != null && !targetActivityClass.equals(currentActivityClass)) {
            Intent intent = new Intent(context, targetActivityClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }
    
    /**
     * Get activity class by name using reflection.
     * 
     * @param context Current context
     * @param activityName Simple name of the activity class
     * @return Class object for the activity or null if not found
     */
    @SuppressWarnings("unchecked")
    private static Class<?> getActivityClassByName(Context context, String activityName) {
        try {
            return Class.forName(context.getPackageName() + ".views." + activityName);
        } catch (ClassNotFoundException e) {
            try {
                // Try in the root package
                return Class.forName(context.getPackageName() + "." + activityName);
            } catch (ClassNotFoundException ex) {
                Log.e(TAG, "Activity class not found: " + activityName, ex);
                return null;
            }
        }
    }
}

