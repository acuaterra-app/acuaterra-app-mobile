package com.example.monitoreoacua.views.farms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.R;

public class MenuFarmsActivity extends AppCompatActivity {

    private Button btnListFarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_farms);


        btnListFarms = findViewById(R.id.btnListFarms);

        btnListFarms.setOnClickListener(v -> toGoListFarmsActivity());

    }

    public void toGoListFarmsActivity() {
        Intent intent = new Intent(MenuFarmsActivity.this, ListFarmsActivity.class);
        startActivity(intent);
    }
}