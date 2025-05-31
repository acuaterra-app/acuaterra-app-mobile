package com.example.monitoreoacua.views.users;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.User;
import com.example.monitoreoacua.interfaces.OnApiRequestCallback;
import com.example.monitoreoacua.service.request.RegisterUserRequest;
import com.example.monitoreoacua.service.request.UserRequest;
import com.example.monitoreoacua.service.response.UserRegisterResponse;
import com.example.monitoreoacua.service.response.UserUpdateResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import java.util.Objects;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterUserFragment extends Fragment {
    private TextInputEditText nameEditText, emailEditText, dniEditText, roleEditText, addressEditText, contactEditText;
    private int userId = -1; // -1 indicates a new user
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "RegisterUserFragment";
    
    // Constantes para mensajes de error
    private static final String ERROR_NAME_LENGTH = "El nombre debe tener al menos 3 caracteres";
    private static final String ERROR_EMAIL_FORMAT = "Email inválido (ejemplo: usuario@dominio.com)";
    private static final String ERROR_DNI_FORMAT = "DNI debe tener 10 dígitos numéricos";
    private static final String ERROR_CONTACT_LENGTH = "Contacto debe tener al menos 7 dígitos";
    private static final String ERROR_CONTACT_FORMAT = "Formato de contacto inválido (ejemplo: +593 98-765-4321)";
    private static final String ERROR_DNI_EXISTS = "El DNI ingresado no es válido o ya está registrado";
    private static final String ERROR_EMAIL_EXISTS = "El email ingresado no es válido o ya está en uso";
    
    // Patrones de validación
    private static final String CONTACT_PATTERN = "[0-9+\\- ]+";
    private static final String DNI_PATTERN = "\\d{10}";
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MIN_CONTACT_LENGTH = 7;
    
    private User user;
    private int moduleId;


    public RegisterUserFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RegisterUserFragment newInstance(User user) {
        RegisterUserFragment fragment = new RegisterUserFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
            moduleId = getArguments().getInt("module_id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_user, container, false);

        nameEditText = view.findViewById(R.id.nameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        dniEditText = view.findViewById(R.id.dniEditText);
        addressEditText = view.findViewById(R.id.addressEditText);
        contactEditText = view.findViewById(R.id.contactEditText);
        Button registerButton = view.findViewById(R.id.registerButton);
        Button closeButton = view.findViewById(R.id.closeButton);

        closeButton.setOnClickListener(v -> closeFragment());

        if (user != null) {
            loadUserData(user);
            registerButton.setText("Actualizar");
        }

        registerButton.setOnClickListener(v -> {
            if (validateInputs()) {
                String name = Objects.requireNonNull(nameEditText.getText()).toString().trim();
                String email = Objects.requireNonNull(emailEditText.getText()).toString().trim();
                String dni = Objects.requireNonNull(dniEditText.getText()).toString().trim();
                String address = Objects.requireNonNull(addressEditText.getText()).toString().trim();
                String contact = Objects.requireNonNull(contactEditText.getText()).toString().trim();

                UserRequest userRequest = new UserRequest(name, email, dni, address, contact);

                if (user == null) {
                    registerUser(userRequest);
                } else {
                    updateUser(user.getId(), userRequest);
                }
            }
        });


        return view;
    }

    private void loadUserData(User user) {
        nameEditText.setText(user.getName());
        emailEditText.setText(user.getEmail());
        dniEditText.setText(user.getDni());
        addressEditText.setText(user.getAddress());
        contactEditText.setText(user.getContact());
    }

    private void updateUser(int userId, UserRequest userRequest) {
        RegisterUserRequest request = new RegisterUserRequest();
        request.updateUser(userId, userRequest, new OnApiRequestCallback<UserUpdateResponse, Throwable>() {
            @Override
            public void onSuccess(UserUpdateResponse response) {
                Toast.makeText(getContext(), "Usuario actualizado: " + response.getData().get(0).getName(), Toast.LENGTH_SHORT).show();
                closeFragment();
            }

            @Override
            public void onFail(Throwable error) {
                String errorMessage = handleApiError(error, true);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registerUser(UserRequest userRequest) {
        RegisterUserRequest request = new RegisterUserRequest();
        request.registerUser(userRequest, new OnApiRequestCallback<UserRegisterResponse, Throwable>() {
            @Override
            public void onSuccess(UserRegisterResponse response) {
                Toast.makeText(getContext(), "Usuario registrado: " + response.getData().getName(), Toast.LENGTH_SHORT).show();
                closeFragment();
            }

            @Override
            public void onFail(Throwable error) {
                String errorMessage = handleApiError(error, false);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Validates all input fields before submission.
     * Checks if all fields are filled correctly and meet the requirements.
     *
     * @return true if all inputs are valid, false otherwise
     */
    private boolean validateInputs() {
        Log.d(TAG, "Starting input validation");
        boolean isValid = true;
        List<String> errorMessages = new ArrayList<>();

        String name = Objects.requireNonNull(nameEditText.getText()).toString().trim();
        String email = Objects.requireNonNull(emailEditText.getText()).toString().trim();
        String dni = Objects.requireNonNull(dniEditText.getText()).toString().trim();
        String address = Objects.requireNonNull(addressEditText.getText()).toString().trim();
        String contact = Objects.requireNonNull(contactEditText.getText()).toString().trim();

        // Validate name
        if (name.length() < MIN_NAME_LENGTH) {
            nameEditText.setError(ERROR_NAME_LENGTH);
            errorMessages.add(ERROR_NAME_LENGTH);
            Log.e(TAG, String.format("Name validation failed: length=%d, minimum required=%d", name.length(), MIN_NAME_LENGTH));
            isValid = false;
        }

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(ERROR_EMAIL_FORMAT);
            errorMessages.add(ERROR_EMAIL_FORMAT);
            Log.e(TAG, String.format("Email validation failed: invalid format [%s]", email));
            isValid = false;
        }

        // Validate DNI (exactly 10 digits)
        if (!dni.matches(DNI_PATTERN)) {
            dniEditText.setError(ERROR_DNI_FORMAT);
            errorMessages.add(ERROR_DNI_FORMAT);
            Log.e(TAG, String.format("DNI validation failed: length=%d, format invalid [%s]", dni.length(), dni));
            isValid = false;
        }

        // Validate contact
        if (contact.length() < MIN_CONTACT_LENGTH) {
            contactEditText.setError(ERROR_CONTACT_LENGTH);
            errorMessages.add(ERROR_CONTACT_LENGTH);
            Log.e(TAG, String.format("Contact validation failed: length=%d, minimum required=%d", contact.length(), MIN_CONTACT_LENGTH));
            isValid = false;
        } else if (!contact.matches(CONTACT_PATTERN)) {
            contactEditText.setError(ERROR_CONTACT_FORMAT);
            errorMessages.add(ERROR_CONTACT_FORMAT);
            Log.e(TAG, String.format("Contact validation failed: invalid format [%s]", contact));
            isValid = false;
        }

        if (!errorMessages.isEmpty()) {
            String summary = "Por favor corrija los siguientes errores:\n" + 
                            String.join("\n", errorMessages);
            Snackbar.make(requireView(), summary, Snackbar.LENGTH_LONG).show();
            Log.e(TAG, String.format("Validation failed with %d errors:\n%s", 
                  errorMessages.size(), String.join("\n", errorMessages)));
        } else {
            Log.d(TAG, "All validation checks passed successfully");
        }

        return isValid;
    }

    /**
     * Closes the current fragment and returns to the user list.
     */
    private void closeFragment() {
        UserFragment userFragment = new UserFragment();
        assert getFragmentManager() != null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.fragmentContainer, userFragment)
                .addToBackStack(null)
                .commit();
    }
    
    /**
     * Common error handling for API failures.
     * 
     * @param error The error thrown by the API
     * @param isUpdate Flag indicating if this is an update (true) or registration (false) operation
     * @return User-friendly error message
     */
    private String handleApiError(Throwable error, boolean isUpdate) {
        String errorDetails = error.getMessage() != null ? error.getMessage() : "Unknown error";
        Log.e(TAG, "API Error Details: " + errorDetails);

        String errorMessage;
        if (error.getMessage() != null) {
            if (error.getMessage().contains("dni")) {
                errorMessage = ERROR_DNI_EXISTS;
                Log.e(TAG, "DNI validation error: " + errorDetails);
            } else if (error.getMessage().contains("email")) {
                errorMessage = ERROR_EMAIL_EXISTS;
                Log.e(TAG, "Email validation error: " + errorDetails);
            } else if (error.getMessage().contains("contact")) {
                errorMessage = ERROR_CONTACT_FORMAT;
                Log.e(TAG, "Contact validation error: " + errorDetails);
            } else if (error.getMessage().contains("name")) {
                errorMessage = "El nombre ingresado no es válido";
                Log.e(TAG, "Name validation error: " + errorDetails);
            } else if (error.getMessage().contains("address")) {
                errorMessage = "La dirección ingresada no es válida";
                Log.e(TAG, "Address validation error: " + errorDetails);
            } else {
                errorMessage = "Error al " + (isUpdate ? "actualizar" : "registrar") + " usuario: " + error.getMessage();
                Log.e(TAG, "Unspecified error: " + errorDetails);
            }
        } else {
            errorMessage = "Error al " + (isUpdate ? "actualizar" : "registrar") + " usuario. Verifique los datos e intente nuevamente.";
            Log.e(TAG, "Unknown error occurred");
        }
        
        Log.e(TAG, "Final error message shown to user: " + errorMessage);
        return errorMessage;
    }
}
