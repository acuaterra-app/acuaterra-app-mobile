    private Farm farm;
    private ListModulesFragment modulesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get farm from intent
        farm = getIntent().getParcelableExtra("farm", Farm.class);
        if (farm == null) {
            finish(); // Close activity if farm is missing
            return;
        }
    @Override
    protected void loadInitialFragment() {
        // Create and load the ListModulesFragment
        modulesFragment = ListModulesFragment.newInstance(farm.getId());
        loadFragment(modulesFragment, false);
    }
    @Override
    public void onRegisterNewModule() {
        // Navigate to RegisterModulesActivity
        Intent intent = new Intent(this, RegisterModulesActivity.class);
        intent.putExtra("farm", farm);
        startActivity(intent);
    }
