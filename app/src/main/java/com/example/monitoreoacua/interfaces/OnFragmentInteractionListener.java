package com.example.monitoreoacua.interfaces;

import com.example.monitoreoacua.business.models.Farm;

/**
 * This interface must be implemented by activities that contain fragments
 * to allow interaction between the fragments and the activity.
 * 
 * It provides methods for handling farm-related actions such as editing and
 * deleting a farm.
 */
public interface OnFragmentInteractionListener {
    /**
     * Called when the user requests to edit a farm
     * @param farm The farm to be edited
     */
    void onEditFarm(Farm farm);
    
    /**
     * Called when the user requests to delete a farm
     * @param farm The farm to be deleted
     */
    void onDeleteFarm(Farm farm);
    
    /**
     * Called when the user selects a farm
     * @param farm The selected farm
     */
    void onFarmSelected(Farm farm);
    
    /**
     * Called when the user navigates back from fragment
     */
    void onNavigateBack();
    
    /**
     * Called when the user wants to view the modules of a farm
     * @param farm The farm whose modules are to be viewed
     */
    void onViewFarmModules(Farm farm);
}

