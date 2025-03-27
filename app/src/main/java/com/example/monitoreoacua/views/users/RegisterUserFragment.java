package com.example.monitoreoacua.views.users;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.User;
import com.example.monitoreoacua.interfaces.OnApiRequestCallback;
import com.example.monitoreoacua.service.request.RegisterUserRequest;
import com.example.monitoreoacua.service.request.UserRequest;
import com.example.monitoreoacua.service.response.UserRegisterResponse;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterUserFragment extends Fragment {
    private TextInputEditText nameEditText, emailEditText, dniEditText, roleEditText, addressEditText, contactEditText;
    private Button registerButton, closeButton;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterUserFragment newInstance(String param1, String param2) {
        RegisterUserFragment fragment = new RegisterUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        registerButton = view.findViewById(R.id.registerButton);
        closeButton = view.findViewById(R.id.closeButton);

        registerButton.setOnClickListener(v -> registerUser());
        closeButton.setOnClickListener(v -> closeFragment());

        return view;
    }

    private void registerUser() {
        nameEditText.setText("Diego");
        emailEditText.setText("dieg252@example.com");
        dniEditText.setText("767872567");
        roleEditText.setText("3");
        addressEditText.setText("Cra 34 jasjaj");
        contactEditText.setText("1121212");

        String name = Objects.requireNonNull(nameEditText.getText()).toString();
        String email = Objects.requireNonNull(emailEditText.getText()).toString();
        String dni = Objects.requireNonNull(dniEditText.getText()).toString();
        String role = Objects.requireNonNull(roleEditText.getText()).toString();
        String address = Objects.requireNonNull(addressEditText.getText()).toString();
        String contact = Objects.requireNonNull(contactEditText.getText()).toString();


        UserRequest userRequest = new UserRequest(name, email, dni, "3", address, contact);


        RegisterUserRequest request = new RegisterUserRequest();
        request.registerUser(userRequest, new OnApiRequestCallback<UserRegisterResponse, Throwable>() {
            @Override
            public void onSuccess(UserRegisterResponse response) {
                User user = response.getData();
                Toast.makeText(getContext(), "Usuario registrado: " + user.getName(), Toast.LENGTH_SHORT).show();
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
        transaction.replace(R.id.fragmentContainer, userFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}