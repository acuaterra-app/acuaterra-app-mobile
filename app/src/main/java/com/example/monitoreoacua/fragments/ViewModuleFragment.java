package com.example.monitoreoacua.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Module;
import com.example.monitoreoacua.business.models.Sensor;
import com.example.monitoreoacua.business.models.User;
import com.example.monitoreoacua.interfaces.OnApiRequestCallback;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiUserService;
import com.example.monitoreoacua.service.request.AssignMonitorsRequest;
import com.example.monitoreoacua.service.request.GetModuleRequest;
import com.example.monitoreoacua.service.request.ListUsersRequest;
import com.example.monitoreoacua.service.response.ApiResponse;
import com.example.monitoreoacua.service.response.ListUserResponse;
import com.example.monitoreoacua.service.response.UserMonitorResponse;
import com.example.monitoreoacua.views.farms.farm.modules.SensorAdapter;
import com.example.monitoreoacua.views.users.MonitorUserAdapter;
import com.example.monitoreoacua.views.users.RegisterUserFragment;
import com.example.monitoreoacua.views.users.UserCheckboxAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment for displaying module details and its sensors.
 */
public class ViewModuleFragment extends Fragment implements SensorAdapter.OnSensorInteractionListener {

    private static final String TAG = "ViewModuleFragment";
    private static final String ARG_MODULE = "module";
    private static final String ARG_MODULE_ID = "module_id";

    private int moduleId;
    private Module module;
    private OnModuleSensorListener listener;
    private boolean isLoading = false;
    private UserCheckboxAdapter userCheckboxAdapter;

    private RecyclerView recyclerViewMonitors;
    private TextView tvNoUsers;

    // UI elements
    private TextView textModuleName;
    private TextView textModuleLocation;
    private TextView textModuleCoordinates;
    private TextView textModuleSpecies;
    private TextView textModuleQuantity;
    private TextView textModuleAge;
    private TextView textModuleDimensions;
    private RecyclerView recyclerViewSensors;
    private ProgressBar progressBar;
    private TextView tvErrorMessage;
    private Button btnRetry;
    private TextView tvNoSensors;

    /**
     * Interface for handling sensor interactions.
     */
    public interface OnModuleSensorListener {
        void onSensorClick(Sensor sensor);
    }

    public static ViewModuleFragment newInstance(int moduleId) {
        ViewModuleFragment fragment = new ViewModuleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MODULE_ID, moduleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            moduleId = getArguments().getInt(ARG_MODULE_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Determinar si el usuario es un monitor
        boolean isMonitor = false;
        try {
            if (getContext() != null) {
                com.example.monitoreoacua.business.models.auth.AuthUser currentUser = 
                    com.example.monitoreoacua.utils.SessionManager.getInstance(requireContext()).getUser();
                isMonitor = com.example.monitoreoacua.business.utils.RolePermissionHelper.isMonitor(currentUser);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al determinar el rol del usuario", e);
        }
        
        // Inflar la vista según el rol del usuario
        View view = inflater.inflate(
            isMonitor ? R.layout.activity_view_module_monitor : R.layout.activity_view_module, 
            container, 
            false
        );

        // Initialize views
        textModuleName = view.findViewById(R.id.module_name);
        textModuleLocation = view.findViewById(R.id.module_location);
        textModuleCoordinates = view.findViewById(R.id.module_coordinates);
        textModuleSpecies = view.findViewById(R.id.module_species);
        textModuleQuantity = view.findViewById(R.id.module_fish_quantity);
        textModuleAge = view.findViewById(R.id.module_fish_age);
        textModuleDimensions = view.findViewById(R.id.module_dimensions);
        recyclerViewSensors = view.findViewById(R.id.recycler_view_sensors);
        progressBar = view.findViewById(R.id.progress_bar);
        tvNoSensors = view.findViewById(R.id.tv_no_sensors);

        // Make sure progress bar is visible by default
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        // Initialize error message and retry button
        tvErrorMessage = view.findViewById(R.id.tv_error_message);
        btnRetry = view.findViewById(R.id.btn_retry);

        if (btnRetry != null) {
            btnRetry.setOnClickListener(v -> loadModuleData());
        }


        // Estas vistas solo existen en la vista de propietario, no en la de monitor
        recyclerViewMonitors = view.findViewById(R.id.recycler_view_monitors);
        tvNoUsers = view.findViewById(R.id.tv_no_users);

        // Solo configurar si las vistas existen (vista de propietario)
        if (recyclerViewMonitors != null) {
            recyclerViewMonitors.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerViewMonitors.setAdapter(new MonitorUserAdapter(new ArrayList<>()));
            
            RecyclerView recyclerViewUsers = view.findViewById(R.id.recycler_view_users);
            if (recyclerViewUsers != null) {
                userCheckboxAdapter = new UserCheckboxAdapter();
                recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerViewUsers.setAdapter(userCheckboxAdapter);
                
                // Solo cargar usuarios si no es un monitor
                //fetchUsers();
            }
            
            Button btnAssignMonitors = view.findViewById(R.id.btn_assign_users);
            if (btnAssignMonitors != null) {
                btnAssignMonitors.setOnClickListener(v -> assignMonitors());
            }
        }



        return view;
    }

    private void loadModuleDataAndFetchUsers() {
        showLoading(true);
        new GetModuleRequest().getModuleById(new OnApiRequestCallback<Module, Throwable>() {
            @Override
            public void onSuccess(Module result) {
                if (isAdded() && getContext() != null) {
                    module = result;
                    showLoading(false);
                    updateUI();
                    fetchUsers(); // Ahora sí, refresca la lista de usuarios asignables
                }
            }

            @Override
            public void onFail(Throwable error) {
                if (isAdded() && getContext() != null) {
                    showLoading(false);
                    Log.e(TAG, "Error cargando modulo", error);
                }
            }
        }, moduleId);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: moduleId=" + moduleId);

        // Setup RecyclerView initially with empty list
        recyclerViewSensors.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewSensors.setAdapter(new SensorAdapter(getContext(), new ArrayList<>(), this));

        loadModuleData();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnModuleSensorListener) {
            listener = (OnModuleSensorListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnModuleSensorListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    /**
     * Updates the UI with module details.
     */
    private void updateUI() {
        if (module != null) {
            textModuleName.setText(module.getName());
            textModuleLocation.setText(module.getLocation());
            textModuleCoordinates.setText(String.format("Lat: %s, Lng: %s", module.getLatitude(), module.getLongitude()));
            textModuleSpecies.setText(module.getSpeciesFish());
            textModuleQuantity.setText(String.valueOf(module.getFishQuantity()));
            textModuleAge.setText(String.valueOf(module.getFishAge()));
            textModuleDimensions.setText(module.getDimensions());

            // Setup RecyclerView with sensors
            setupRecyclerView();

            // Actualizar la sección de usuarios monitores solo si existe (vista de propietario)
            if (recyclerViewMonitors != null && tvNoUsers != null) {
                List<User> users = module.getUsers();
                if (users != null && !users.isEmpty()) {
                    recyclerViewMonitors.setAdapter(new MonitorUserAdapter(users));
                    recyclerViewMonitors.setVisibility(View.VISIBLE);
                    tvNoUsers.setVisibility(View.GONE);
                } else {
                    recyclerViewMonitors.setVisibility(View.GONE);
                    tvNoUsers.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * Sets up the RecyclerView with sensors.
     */
    private void setupRecyclerView() {
        if (module != null && module.getSensors() != null) {
            List<Sensor> sensors = module.getSensors();
            if (!sensors.isEmpty()) {
                // We have sensors, show the RecyclerView and hide the empty message
                SensorAdapter adapter = new SensorAdapter(getContext(), sensors, this);
                recyclerViewSensors.setAdapter(adapter);
                recyclerViewSensors.setVisibility(View.VISIBLE);
                if (tvNoSensors != null) {
                    tvNoSensors.setVisibility(View.GONE);
                }
                Log.d(TAG, "Displaying " + sensors.size() + " sensors");
            } else {
                // Empty sensors list, show message and hide RecyclerView
                if (tvNoSensors != null) {
                    tvNoSensors.setVisibility(View.VISIBLE);
                }
                recyclerViewSensors.setVisibility(View.GONE);
                Log.d(TAG, "No sensors to display");
            }
        } else {
            // Null sensors list, show message and hide RecyclerView
            if (tvNoSensors != null) {
                tvNoSensors.setVisibility(View.VISIBLE);
            }
            recyclerViewSensors.setVisibility(View.GONE);
            Log.d(TAG, "Sensors list is null");
        }
    }
    public void showLoading(boolean show) {
        isLoading = show;
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);

            // Hide error views when loading
            if (tvErrorMessage != null) {
                tvErrorMessage.setVisibility(View.GONE);
            }
            if (btnRetry != null) {
                btnRetry.setVisibility(View.GONE);
            }

            // Also show/hide other UI elements based on loading state
            int contentVisibility = show ? View.INVISIBLE : View.VISIBLE;
            if (textModuleName != null) textModuleName.setVisibility(contentVisibility);
            if (textModuleLocation != null) textModuleLocation.setVisibility(contentVisibility);
            if (textModuleCoordinates != null)
                textModuleCoordinates.setVisibility(contentVisibility);
            if (textModuleSpecies != null) textModuleSpecies.setVisibility(contentVisibility);
            if (textModuleQuantity != null) textModuleQuantity.setVisibility(contentVisibility);
            if (textModuleAge != null) textModuleAge.setVisibility(contentVisibility);
            if (textModuleDimensions != null) textModuleDimensions.setVisibility(contentVisibility);
            if (recyclerViewSensors != null) recyclerViewSensors.setVisibility(contentVisibility);
        }
    }


    @Override
    public void onSensorClick(Sensor sensor) {
        if (listener != null) {
            listener.onSensorClick(sensor);
        }
    }

    private void loadModuleData() {
        showLoading(true);

        new GetModuleRequest().getModuleById(new OnApiRequestCallback<Module, Throwable>() {
            @Override
            public void onSuccess(Module result) {
                if (isAdded() && getContext() != null) {
                    module = result;
                    showLoading(false);
                    updateUI();
                    fetchUsers();
                    Log.d(TAG, "Modulo cargado exitosamente: " + result.getName());
                }
            }

            @Override
            public void onFail(Throwable error) {
                if (isAdded() && getContext() != null) {
                    showLoading(false);
                    Log.e(TAG, "Error cargando modulo", error);
                }
            }
        }, moduleId);
    }
    private void fetchUsersMonitors() {
        progressBar.setVisibility(View.VISIBLE);
        new ListUsersRequest().fetchUsersMonitors(new OnApiRequestCallback<List<UserMonitorResponse>, Throwable>() {
            @Override
            public void onSuccess(List<UserMonitorResponse> users) {
                progressBar.setVisibility(View.GONE);
                //userCheckboxAdapter.setUsers(users);
            }

            @Override
            public void onFail(Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error de conexión: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchUsers() {
        progressBar.setVisibility(View.VISIBLE);
        new ListUsersRequest().fetchUsers(new OnApiRequestCallback<List<User>, Throwable>() {
            @Override
            public void onSuccess(List<User> users) {
                progressBar.setVisibility(View.GONE);
                List<User> assignableUsers = getAssignableUsers(users, module != null ? module.getUsers() : null);
                userCheckboxAdapter.setUsers(assignableUsers);
            }

            @Override
            public void onFail(Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error de conexión: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    // Java
    private List<User> getAssignableUsers(List<User> allUsers, List<User> assignedUsers) {
        List<Integer> assignedIds = new ArrayList<>();
        if (assignedUsers != null) {
            for (User u : assignedUsers) {
                assignedIds.add(u.getId());
            }
        }
        List<User> result = new ArrayList<>();
        for (User user : allUsers) {
            if (!assignedIds.contains(user.getId())) {
                result.add(user);
            }
        }
        return result;
    }

    private void assignMonitors() {
        List<Integer> selectedUserIds = userCheckboxAdapter.getSelectedUserIds();
        if (selectedUserIds.isEmpty()) {
            Toast.makeText(getContext(), "Seleccione al menos un monitor", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        new AssignMonitorsRequest().assignMonitors(moduleId, selectedUserIds, new OnApiRequestCallback<ApiResponse, Throwable>() {
            @Override
            public void onSuccess(ApiResponse response) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Monitores asignados exitosamente", Toast.LENGTH_SHORT).show();
                loadModuleDataAndFetchUsers(); // Recargar datos del módulo
                selectedUserIds.clear();
            }

            @Override
            public void onFail(Throwable error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al asignar monitores: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void loadAssignedUsers() {
        // Implementación para cargar usuarios asignados
        Toast.makeText(getContext(), "Cargar usuarios asignados aún no implementado", Toast.LENGTH_SHORT).show();
    }
}
