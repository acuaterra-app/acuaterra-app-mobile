        // Handle navigation to the farm modules screen
        // Launch ListModulesActivity with the farm object
        Intent intent = new Intent(this, ListModulesActivity.class);
        intent.putExtra("farm", farm);
        startActivity(intent);
