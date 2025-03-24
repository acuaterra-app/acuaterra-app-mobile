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

    // Mapa de navegación con IDs y clases de destino
    private static final Map<Integer, Class<?>> NAVIGATION_MAP = new HashMap<>();

    static {
        NAVIGATION_MAP.put(R.id.navFarms, ListFarmsActivity.class);
        NAVIGATION_MAP.put(R.id.navUsers, ListFarmsActivity.class); // Ajusta si hay una actividad específica para Usuarios
        NAVIGATION_MAP.put(R.id.navSupport, SupportActivity.class);
        NAVIGATION_MAP.put(R.id.navClose, CloseSectionActivity.class);
    }

    public interface NavigationItemClickListener {
        void onItemClick(int itemId);
    }

    // Configura los listeners de navegación
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

    // Maneja la navegación a una nueva sección
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

    // Actualiza visualmente el elemento seleccionado
    public static void updateSelectedItem(LinearLayout container, int selectedItemId) {
        if (container == null) {
            return; // Evita errores si el contenedor es nulo
        }

        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);

            if (child instanceof LinearLayout) {
                LinearLayout navItem = (LinearLayout) child;
                TextView textView = (TextView) navItem.getChildAt(1); // El segundo hijo es el TextView

                if (textView != null) {
                    if (navItem.getId() == selectedItemId) {
                        textView.setTextColor(0xFF0000FF); // Azul para el seleccionado
                    } else {
                        textView.setTextColor(0xFF000000); // Negro para los no seleccionados
                    }
                }
            }
        }
    }
}

