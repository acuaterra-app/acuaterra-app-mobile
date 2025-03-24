package com.example.monitoreoacua.views.farms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.fragments.FarmDetailsFragment;
import com.example.monitoreoacua.fragments.ListFarmsFragment;
import com.example.monitoreoacua.interfaces.OnFragmentInteractionListener;
import com.example.monitoreoacua.views.BaseActivity;
import com.example.monitoreoacua.views.menu.CloseSessionActivity;
import com.example.monitoreoacua.views.menu.SupportActivity;

/**
 * Activity for displaying the list of farms.
 * Extends BaseActivity to use common functionality.
 */
public class ListFarmsActivity extends BaseActivity 
        implements ListFarmsFragment.OnFarmSelectedListener, OnFragmentInteractionListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // BaseActivity handles all the common setup
    }

    @Override
    protected String getActivityTitle() {
        return "Granjas";
    }

    @Override
    protected void loadInitialFragment() {
        // Load the farms list fragment into the container
        ListFarmsFragment listFarmsFragment = ListFarmsFragment.newInstance();
        loadFragment(listFarmsFragment, false);
    }

    @Override
    public void navigateToProfile() {
        Intent intent = new Intent(this, SupportActivity.class);
        startActivity(intent);
    }

    @Override
    public void logout() {
        Intent intent = new Intent(this, CloseSessionActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFarmSelected(Farm farm) {
        // Create a new instance of FarmDetailsFragment with the selected farm
        FarmDetailsFragment farmDetailsFragment = FarmDetailsFragment.newInstance(farm);
        
        // Load the fragment using the method from BaseActivity
        loadFragment(farmDetailsFragment, true);
        
        // Set the activity title to the farm name
        setTitle(farm.getName());
    }

    @Override
    public void onEditFarm(Farm farm) {
        // Implementation for editing a farm
        Toast.makeText(this, "Editar granja: " + farm.getName(), Toast.LENGTH_SHORT).show();
        // TODO: Navigate to farm edit screen
    }

    @Override
    public void onDeleteFarm(Farm farm) {
        // Implementation for deleting a farm
        Toast.makeText(this, "Eliminar granja: " + farm.getName(), Toast.LENGTH_SHORT).show();
        // TODO: Show confirmation dialog and delete farm if confirmed
    }

    @Override
    public void onNavigateBack() {
        // Return to the list of farms and reset the title
        setTitle(getActivityTitle());
        onBackPressed();
    }

    @Override
    public void onViewFarmModules(Farm farm) {
        // Implementation for viewing farm modules
        Toast.makeText(this, "Ver módulos de: " + farm.getName(), Toast.LENGTH_SHORT).show();
        // TODO: Navigate to farm modules screen
    }
}
