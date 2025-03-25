        // Navigate to farm modules screen
        Intent intent = new Intent(this, ListModulesActivity.class);
        intent.putExtra("farm", farm);
        startActivity(intent);
