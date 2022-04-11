package com.tajln.vremenarapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.tajln.vremenarapp.config.EnvVal;
import com.tajln.vremenarapp.data.NetworkManager;
import com.tajln.vremenarapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = getSharedPreferences("Activity1", Context.MODE_PRIVATE);
        System.out.println("Shranjen kljuc = " + sharedPref.getString("kljuc", null));
        System.out.println("Shranjen token = " + sharedPref.getString("TokenBody", null));
        EnvVal.kljuc_postaje = sharedPref.getString("kljuc", null);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        NetworkManager.initialize(getApplicationContext());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        binding.appBarMain.fab.setOnClickListener(view -> {
            if(EnvVal.kljuc_postaje != null) {
                System.out.println(EnvVal.kljuc_postaje);
                if (findViewById(R.id.text_lastUpdate) != null) {
                    NetworkManager.updateToLatest(findViewById(android.R.id.content));
                    showEmAll(findViewById(android.R.id.content));
                }
                if (findViewById(R.id.chart1) != null) {
                    Spinner spinner = findViewById(R.id.spinner);
                    NetworkManager.updatelast30(findViewById(R.id.chart1), (String) spinner.getSelectedItem());
                }
                Snackbar.make(view, "Osveževanje podatkov", 1000).setAction("Action", null).show();
            }
            else {
                if (findViewById(R.id.text_lastUpdate) != null) {
                    hideEmAll(findViewById(android.R.id.content));
                }
                Snackbar.make(view, "Postaja ni nastavljena, nastavite jo v nastavitvah", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }});
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public static void hideEmAll(View root){
        ImageView droplet = root.findViewById(R.id.droplet);
        ImageView barometer = root.findViewById(R.id.barometer);
        ImageView sun = root.findViewById(R.id.sun);
        ImageView oxid = root.findViewById(R.id.oxid);
        ImageView redu = root.findViewById(R.id.redu);
        ImageView nh3 = root.findViewById(R.id.nh3);

        TextView vlaga = root.findViewById(R.id.vlaga);
        TextView pritisk = root.findViewById(R.id.pritisk);
        TextView svetloba = root.findViewById(R.id.svetloba);
        TextView oxidacije = root.findViewById(R.id.oxidacije);
        TextView redukcije = root.findViewById(R.id.redukcije);
        TextView nh3napis = root.findViewById(R.id.nh3napis);

        droplet.setVisibility(View.GONE);
        barometer.setVisibility(View.GONE);
        sun.setVisibility(View.GONE);
        oxid.setVisibility(View.GONE);
        redu.setVisibility(View.GONE);
        nh3.setVisibility(View.GONE);

        vlaga.setVisibility(View.GONE);
        pritisk.setVisibility(View.GONE);
        svetloba.setVisibility(View.GONE);
        oxidacije.setVisibility(View.GONE);
        redukcije.setVisibility(View.GONE);
        nh3napis.setVisibility(View.GONE);

    }

    public static void showEmAll(View root){
        ImageView droplet = root.findViewById(R.id.droplet);
        ImageView barometer = root.findViewById(R.id.barometer);
        ImageView sun = root.findViewById(R.id.sun);
        ImageView oxid = root.findViewById(R.id.oxid);
        ImageView redu = root.findViewById(R.id.redu);
        ImageView nh3 = root.findViewById(R.id.nh3);

        TextView vlaga = root.findViewById(R.id.vlaga);
        TextView pritisk = root.findViewById(R.id.pritisk);
        TextView svetloba = root.findViewById(R.id.svetloba);
        TextView oxidacije = root.findViewById(R.id.oxidacije);
        TextView redukcije = root.findViewById(R.id.redukcije);
        TextView nh3napis = root.findViewById(R.id.nh3napis);

        droplet.setVisibility(View.VISIBLE);
        barometer.setVisibility(View.VISIBLE);
        sun.setVisibility(View.VISIBLE);
        oxid.setVisibility(View.VISIBLE);
        redu.setVisibility(View.VISIBLE);
        nh3.setVisibility(View.VISIBLE);

        vlaga.setVisibility(View.VISIBLE);
        pritisk.setVisibility(View.VISIBLE);
        svetloba.setVisibility(View.VISIBLE);
        oxidacije.setVisibility(View.VISIBLE);
        redukcije.setVisibility(View.VISIBLE);
        nh3napis.setVisibility(View.VISIBLE);
    }
}