package com.example.monitoreoacua.views.users;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.User;
import com.example.monitoreoacua.interfaces.OnApiRequestCallback;
import com.example.monitoreoacua.service.request.RegisterUserRequest;
import com.example.monitoreoacua.service.request.UserRequest;
import com.example.monitoreoacua.service.response.UserRegisterResponse;
import com.example.monitoreoacua.service.response.UserUpdateResponse;
import com.google.android.material.textfield.TextInputEditText;

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
    private User user;


    public RegisterUserFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RegisterUserFragment newInstance(User user) {
        RegisterUserFragment fragment = new RegisterUserFragment();
        Bundle args = new Bundle();

        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_user, container, false);

        nameEditText = view.findViewById(R.id.nameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        dniEditText = view.findViewById(R.id.dniEditText);
        roleEditText = view.findViewById(R.id.roleEditText);
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
            String name = Objects.requireNonNull(nameEditText.getText()).toString();
            String email = Objects.requireNonNull(emailEditText.getText()).toString();
            String dni = Objects.requireNonNull(dniEditText.getText()).toString();
            String role = Objects.requireNonNull(roleEditText.getText()).toString();
            String address = Objects.requireNonNull(addressEditText.getText()).toString();
            String contact = Objects.requireNonNull(contactEditText.getText()).toString();

            UserRequest userRequest = new UserRequest(name, email, dni, role, address, contact);

            if (user == null) {
                registerUser(userRequest);
            } else {
                updateUser(user.getId(), userRequest);
            }
        });


        return view;
    }

    private void loadUserData(User user) {
        nameEditText.setText(user.getName());
        emailEditText.setText(user.getEmail());
        dniEditText.setText(user.getDni());
        roleEditText.setText(user.getRole().getName());
        addressEditText.setText(user.getAddress());
        contactEditText.setText(user.getContact());
    }

    private void updateUser(int userId, UserRequest userRequest) {
        RegisterUserRequest request = new RegisterUserRequest();
        userRequest.setId_rol("3");
        request.updateUser(userId, userRequest, new OnApiRequestCallback<UserUpdateResponse, Throwable>() {
            @Override
            public void onSuccess(UserUpdateResponse response) {
                Toast.makeText(getContext(), "Usuario actualizado: " + response.getData().get(0).getName(), Toast.LENGTH_SHORT).show();
                closeFragment();
            }

            @Override
            public void onFail(Throwable error) {
                Toast.makeText(getContext(), "Error al actualizar usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUser(UserRequest userRequest) {
        RegisterUserRequest request = new RegisterUserRequest();
        userRequest.setId_rol("3");
        request.registerUser(userRequest, new OnApiRequestCallback<UserRegisterResponse, Throwable>() {
            @Override
            public void onSuccess(UserRegisterResponse response) {
                Toast.makeText(getContext(), "Usuario registrado: " + response.getData().getName(), Toast.LENGTH_SHORT).show();
                closeFragment();
            }

            @Override
            public void onFail(Throwable error) {
                Toast.makeText(getContext(), "Error al registrar usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

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
}