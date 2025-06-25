package com.example.monitoreoacua.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import java.util.Locale;

/**
 * Clase utilitaria para manejar la ubicación GPS del dispositivo.
 * Proporciona métodos para obtener la ubicación actual y gestionar permisos.
 */
public class LocationHelper {

    private static final String TAG = "LocationHelper";
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    /**
     * Interface para callback de ubicación
     */
    public interface LocationCallback {
        void onLocationReceived(double latitude, double longitude);
        void onLocationError(String error);
    }

    /**
     * Verifica si los permisos de ubicación están concedidos
     */
    public static boolean hasLocationPermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Solicita permisos de ubicación
     */
    public static void requestLocationPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    /**
     * Verifica si el GPS está habilitado
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Obtiene la ubicación actual del dispositivo
     */
    public static void getCurrentLocation(Context context, LocationCallback callback) {
        // Verificar permisos
        if (!hasLocationPermissions(context)) {
            callback.onLocationError("Permisos de ubicación no concedidos");
            return;
        }

        // Verificar si GPS está habilitado
        if (!isGpsEnabled(context)) {
            callback.onLocationError("GPS no está habilitado. Por favor, actívelo en la configuración.");
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            
                            Log.d(TAG, "Ubicación obtenida - Lat: " + latitude + ", Lon: " + longitude);
                            callback.onLocationReceived(latitude, longitude);
                        } else {
                            Log.w(TAG, "No se pudo obtener la ubicación actual");
                            callback.onLocationError("No se pudo obtener la ubicación actual. Asegúrese de que el GPS esté activo y tenga señal.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error al obtener ubicación", e);
                        callback.onLocationError("Error al obtener ubicación: " + e.getMessage());
                    });
        } catch (SecurityException e) {
            Log.e(TAG, "Error de permisos de seguridad", e);
            callback.onLocationError("Error de permisos de ubicación");
        }
    }

    /**
     * Formatear coordenadas para mostrar con precisión de 3 decimales
     * Utiliza Locale.US para garantizar que siempre use punto como separador decimal
     */
    public static String formatCoordinate(double coordinate) {
        return String.format(Locale.US, "%.3f", coordinate);
    }

    /**
     * Validar si las coordenadas están en rangos válidos
     */
    public static boolean isValidLatitude(double latitude) {
        return latitude >= -90.0 && latitude <= 90.0;
    }

    public static boolean isValidLongitude(double longitude) {
        return longitude >= -180.0 && longitude <= 180.0;
    }
}
