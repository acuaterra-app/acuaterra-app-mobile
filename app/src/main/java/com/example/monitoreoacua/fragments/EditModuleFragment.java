package com.example.monitoreoacua.fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Module;
import com.example.monitoreoacua.business.models.User;
import com.example.monitoreoacua.interfaces.OnApiRequestCallback;
import com.example.monitoreoacua.service.request.GetModuleRequest;
import com.example.monitoreoacua.service.request.UpdateModuleRequest;
import com.example.monitoreoacua.service.response.RegisterModuleResponse;
import com.example.monitoreoacua.utils.LocationHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Fragment que permite editar un módulo existente.
 * Reutiliza el mismo layout de registro pero pre-llena los campos con los datos del módulo actual.
 */
public class EditModuleFragment extends Fragment {

    private EditText etModuleName, etLocation, etLatitude, etLongitude, etFishType, etFishQuantity, etFishAge, etVolumeUnit;
    private MaterialButton btnUpdateModulo, btnCancelar, btnGetLocation;
    private ProgressBar progressBar;
    private TextView tvTitle;
    private static final String TAG = "EditModuleFragment";
    private Module currentModule;

    private static final String ARG_MODULE_ID = "module_id";
    private int moduleId;

    public EditModuleFragment() {
        // Required empty public constructor
    }

    /**
     * Crea una nueva instancia del fragmento con el ID del módulo a editar.
     *
     * @param moduleId ID del módulo a editar
     * @return Nueva instancia del fragmento
     */
    public static EditModuleFragment newInstance(int moduleId) {
        EditModuleFragment fragment = new EditModuleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MODULE_ID, moduleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            moduleId = getArguments().getInt(ARG_MODULE_ID);
            Log.d(TAG, "Module ID recibido: " + moduleId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Usar el mismo layout del registro de módulo
        View view = inflater.inflate(R.layout.fragment_register_module, container, false);

        // Inicializar las vistas
        initializeViews(view);
        
        // Configurar los listeners
        setupListeners();
        
        // Cargar los datos del módulo
        loadModuleData();

        return view;
    }

    private void initializeViews(View view) {
        etModuleName = view.findViewById(R.id.etModuleName);
        etLocation = view.findViewById(R.id.etLocation);
        etLatitude = view.findViewById(R.id.etLatitude);
        etLongitude = view.findViewById(R.id.etLongitude);
        etFishType = view.findViewById(R.id.etFishType);
        etFishQuantity = view.findViewById(R.id.etFishQuantity);
        etFishAge = view.findViewById(R.id.etFishAge);
        etVolumeUnit = view.findViewById(R.id.etVolumeUnit);
        
        progressBar = view.findViewById(R.id.progressBar);
        btnUpdateModulo = view.findViewById(R.id.btnRegisterModulo);
        btnCancelar = view.findViewById(R.id.btnCancelar);
        btnGetLocation = view.findViewById(R.id.btnGetLocation);
        tvTitle = view.findViewById(R.id.tvTitle);

        // Cambiar el título y texto del botón para indicar que es edición
        if (tvTitle != null) {
            tvTitle.setText("Editar Módulo");
        }
        if (btnUpdateModulo != null) {
            btnUpdateModulo.setText("Actualizar Módulo");
        }
    }

    private void setupListeners() {
        btnUpdateModulo.setOnClickListener(v -> {
            if (validateInputs()) {
                updateModule();
            }
        });

        btnCancelar.setOnClickListener(v -> {
            closeFragment();
        });

        // Set up location button click listener
        btnGetLocation.setOnClickListener(v -> getCurrentLocation());
    }

    /**
     * Carga los datos del módulo desde la API y pre-llena el formulario
     */
    private void loadModuleData() {
        showLoading(true);
        
        new GetModuleRequest().getModuleById(new OnApiRequestCallback<Module, Throwable>() {
            @Override
            public void onSuccess(Module module) {
                if (isAdded() && getContext() != null) {
                    currentModule = module;
                    showLoading(false);
                    populateForm();
                    Log.d(TAG, "Módulo cargado exitosamente: " + module.getName());
                }
            }

            @Override
            public void onFail(Throwable error) {
                if (isAdded() && getContext() != null) {
                    showLoading(false);
                    Log.e(TAG, "Error cargando módulo", error);
                    Toast.makeText(getContext(), "Error al cargar los datos del módulo", Toast.LENGTH_LONG).show();
                    closeFragment();
                }
            }
        }, moduleId);
    }

    /**
     * Pre-llena el formulario con los datos del módulo actual
     */
    private void populateForm() {
        if (currentModule != null) {
            etModuleName.setText(currentModule.getName());
            etLocation.setText(currentModule.getLocation());
            etLatitude.setText(LocationHelper.formatCoordinate(Double.parseDouble(currentModule.getLatitude())));
            etLongitude.setText(LocationHelper.formatCoordinate(Double.parseDouble(currentModule.getLongitude())));
            etFishType.setText(currentModule.getSpeciesFish());
            etFishQuantity.setText(currentModule.getFishQuantity());
            etFishAge.setText(currentModule.getFishAge());
            etVolumeUnit.setText(currentModule.getDimensions());
        }
    }

    /**
     * Valida todos los campos de entrada antes del envío.
     * Usa la misma validación que el registro de módulo.
     */
    private boolean validateInputs() {
        boolean isValid = true;

        // Validar Nombre del Módulo
        if (TextUtils.isEmpty(etModuleName.getText())) {
            etModuleName.setError("El nombre del módulo es requerido");
            Log.e(TAG, "Validation error: Nombre del módulo vacío");
            isValid = false;
        }

        // Validar Ubicación
        if (TextUtils.isEmpty(etLocation.getText())) {
            etLocation.setError("La ubicación es requerida");
            Log.e(TAG, "Validation error: Ubicación vacía");
            isValid = false;
        }

        // Validar Latitud
        String latitude = etLatitude.getText().toString().trim();
        if (TextUtils.isEmpty(latitude)) {
            etLatitude.setError("La latitud es requerida (ejemplo: 4.610)");
            Log.e(TAG, "Validation error: Latitud vacía");
            isValid = false;
        } else {
            try {
                double lat = Double.parseDouble(latitude);
                if (lat < -90 || lat > 90) {
                    etLatitude.setError("La latitud debe estar entre -90 y 90 grados");
                    Log.e(TAG, "Validation error: Latitud fuera de rango: " + lat);
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                etLatitude.setError("La latitud debe ser un número decimal válido (ejemplo: 4.610)");
                Log.e(TAG, "Validation error: Formato de latitud inválido: " + latitude);
                isValid = false;
            }
        }

        // Validar Longitud
        String longitude = etLongitude.getText().toString().trim();
        if (TextUtils.isEmpty(longitude)) {
            etLongitude.setError("La longitud es requerida (ejemplo: -74.082)");
            Log.e(TAG, "Validation error: Longitud vacía");
            isValid = false;
        } else {
            try {
                double lon = Double.parseDouble(longitude);
                if (lon < -180 || lon > 180) {
                    etLongitude.setError("La longitud debe estar entre -180 y 180 grados");
                    Log.e(TAG, "Validation error: Longitud fuera de rango: " + lon);
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                etLongitude.setError("La longitud debe ser un número decimal válido (ejemplo: -74.082)");
                Log.e(TAG, "Validation error: Formato de longitud inválido: " + longitude);
                isValid = false;
            }
        }

        // Validar Especie de Pez
        if (TextUtils.isEmpty(etFishType.getText())) {
            etFishType.setError("La especie de pez es requerida");
            Log.e(TAG, "Validation error: Especie de pez vacía");
            isValid = false;
        }

        // Validar Cantidad de Peces
        String quantity = etFishQuantity.getText().toString().trim();
        if (TextUtils.isEmpty(quantity)) {
            etFishQuantity.setError("La cantidad de peces es requerida");
            Log.e(TAG, "Validation error: Cantidad de peces vacía");
            isValid = false;
        } else {
            try {
                int qty = Integer.parseInt(quantity);
                if (qty <= 0) {
                    etFishQuantity.setError("La cantidad debe ser mayor a cero");
                    Log.e(TAG, "Validation error: Cantidad inválida: " + qty);
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                etFishQuantity.setError("La cantidad debe ser un número entero");
                Log.e(TAG, "Validation error: Formato de cantidad inválido: " + quantity);
                isValid = false;
            }
        }

        // Validar Edad de Peces
        String fishAge = etFishAge.getText().toString().trim();
        if (TextUtils.isEmpty(fishAge)) {
            String errorMsg = "La edad es requerida (ingrese solo el número de meses)";
            etFishAge.setError(errorMsg);
            Log.e(TAG, "Validation error: Edad vacía");
            isValid = false;
        } else {
            try {
                int age = Integer.parseInt(fishAge);
                if (age <= 0) {
                    String errorMsg = "La edad debe ser un número positivo que representa los meses (ejemplo: 6)";
                    etFishAge.setError(errorMsg);
                    Log.e(TAG, "Validation error: Edad inválida: " + age);
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                String errorMsg = "Ingrese solo el número de meses sin texto adicional (ejemplo: 6)";
                etFishAge.setError(errorMsg);
                Log.e(TAG, "Validation error: Formato de edad inválido: " + fishAge);
                isValid = false;
            }
        }

        // Validar Dimensiones
        String volumeUnit = etVolumeUnit.getText().toString().trim();
        if (TextUtils.isEmpty(volumeUnit)) {
            etVolumeUnit.setError("Las dimensiones son requeridas (formato: anchoxlargoxalto, ejemplo: 2x3x1.5)");
            Log.e(TAG, "Validation error: Dimensiones vacías");
            isValid = false;
        } else if (!volumeUnit.matches("\\d+(\\.\\d+)?x\\d+(\\.\\d+)?x\\d+(\\.\\d+)?")) {
            etVolumeUnit.setError("Formato inválido. Use: anchoxlargoxalto (ejemplo: 2x3x1.5)");
            Log.e(TAG, "Validation error: Formato de dimensiones inválido: " + volumeUnit);
            isValid = false;
        }

        return isValid;
    }

    /**
     * Actualiza el módulo con los nuevos datos ingresados.
     */
    private void updateModule() {
        if (currentModule == null) {
            Toast.makeText(getContext(), "Error: No se encontraron los datos del módulo original", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar indicador de carga
        showLoading(true);

        String moduleName = Objects.requireNonNull(etModuleName.getText()).toString().trim();
        String location = Objects.requireNonNull(etLocation.getText()).toString().trim();
        String latitude = Objects.requireNonNull(etLatitude.getText()).toString().trim();
        String longitude = Objects.requireNonNull(etLongitude.getText()).toString().trim();
        String fishSpecies = Objects.requireNonNull(etFishType.getText()).toString().trim();
        String fishQuantity = Objects.requireNonNull(etFishQuantity.getText()).toString().trim();
        String fishAge = Objects.requireNonNull(etFishAge.getText()).toString().trim();
        String moduleDimensions = Objects.requireNonNull(etVolumeUnit.getText()).toString().trim();

        // Usar los IDs de usuarios existentes del módulo
        List<Integer> users = currentModule.getUserIds();
        if (users == null) {
            users = new ArrayList<>();
        }

        // Crear el módulo actualizado
        Module updatedModule = new Module(
                moduleName,
                location,
                latitude,
                longitude,
                fishSpecies,
                fishQuantity,
                fishAge,
                moduleDimensions,
                currentModule.getIdFarm(),
                users
        );

        Log.d(TAG, "Intentando actualizar módulo: " + updatedModule.toString());

        UpdateModuleRequest updateModuleRequest = new UpdateModuleRequest();

        updateModuleRequest.updateModule(moduleId, updatedModule, new OnApiRequestCallback<RegisterModuleResponse, Throwable>() {
            @Override
            public void onSuccess(RegisterModuleResponse response) {
                // Ocultar indicador de carga
                showLoading(false);

                // Manejar respuesta exitosa
                List<RegisterModuleResponse.Data> dataListModule = response.getResponseModuleData();
                if (dataListModule != null && !dataListModule.isEmpty()) {
                    Module updatedModule = dataListModule.get(0).moduleData;

                    // Log de actualización exitosa
                    Log.d(TAG, "Módulo actualizado exitosamente: " + updatedModule.getName());

                    // Mostrar mensaje de éxito
                    Snackbar.make(
                            requireView(),
                            "Módulo actualizado exitosamente: " + updatedModule.getName(),
                            Snackbar.LENGTH_LONG
                    ).show();

                    // Cerrar el fragmento después de la actualización
                    closeFragment();
                } else {
                    // Manejar caso de respuesta vacía
                    Log.w(TAG, "Respuesta vacía o nula del servidor");
                    Snackbar.make(
                            requireView(),
                            "Error: Respuesta vacía del servidor",
                            Snackbar.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFail(Throwable error) {
                // Ocultar indicador de carga
                showLoading(false);

                // Log de error
                Log.e(TAG, "Error actualizando módulo: " + error.getMessage(), error);

                // Determinar el tipo de error y mostrar mensaje específico
                String errorMessage;
                if (error.getMessage() != null) {
                    if (error.getMessage().contains("Fish age must be a positive integer")) {
                        errorMessage = "La edad de los peces debe ser un número entero positivo (ejemplo: 6 para 6 meses)";
                    } else if (error.getMessage().contains("volume_unit")) {
                        errorMessage = "El formato de las dimensiones debe ser anchoxlargoxalto (ejemplo: 2x3x1.5)";
                    } else if (error.getMessage().contains("quantity")) {
                        errorMessage = "La cantidad de peces debe ser un número entero positivo";
                    } else if (error.getMessage().contains("location")) {
                        errorMessage = "La ubicación proporcionada no es válida";
                    } else if (error.getMessage().contains("longitude") || error.getMessage().contains("latitude")) {
                        errorMessage = "Las coordenadas deben estar en formato decimal válido (ejemplo: 4.610, -74.082)";
                    } else {
                        errorMessage = "Error al actualizar módulo: " + error.getMessage();
                    }
                } else {
                    errorMessage = "Error al actualizar módulo. Verifique los datos e intente nuevamente.";
                }

                // Mostrar mensaje de error
                Snackbar.make(
                        requireView(),
                        errorMessage,
                        Snackbar.LENGTH_LONG
                ).show();
            }
        });
    }

    /**
     * Muestra u oculta el indicador de carga
     */
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (btnUpdateModulo != null) {
            btnUpdateModulo.setEnabled(!show);
        }
    }

    /**
     * Obtiene la ubicación actual del dispositivo y actualiza los campos de latitud y longitud.
     */
    private void getCurrentLocation() {
        if (!LocationHelper.hasLocationPermissions(requireContext())) {
            LocationHelper.requestLocationPermissions(requireActivity());
            return;
        }

        LocationHelper.getCurrentLocation(requireContext(), new LocationHelper.LocationCallback() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                etLatitude.setText(LocationHelper.formatCoordinate(latitude));
                etLongitude.setText(LocationHelper.formatCoordinate(longitude));
            }

            @Override
            public void onLocationError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Método para manejar la respuesta de permisos del usuario.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LocationHelper.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(getContext(), "Permisos de ubicación denegados", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Cierra el fragmento actual y regresa a la vista anterior.
     */
    private void closeFragment() {
        if (getFragmentManager() != null) {
            getFragmentManager().popBackStack();
        }
    }
}

