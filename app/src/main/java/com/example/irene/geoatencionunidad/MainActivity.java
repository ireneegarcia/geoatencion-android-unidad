package com.example.irene.geoatencionunidad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.irene.geoatencionunidad.Model.NotificationFirebase;
import com.example.irene.geoatencionunidad.Services.Servicio;

import static com.example.irene.geoatencionunidad.Services.Servicio.done;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //static Toolbar toolbar;
    View mView;
    Context c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // notificación push con firebase
        if (getIntent().getExtras() != null) {
            Log.d("firebase", "DATOS RECIBIDOS MAIN ACTIVITY");
            Log.d("firebase", "Latitud del cliente: " + getIntent().getExtras().getString("clientLatitude"));
            Log.d("firebase", "Logintud del cliente: " + getIntent().getExtras().getString("clientLongitude"));
            Log.d("firebase", "Direccion del cliente: " + getIntent().getExtras().getString("clientAddress"));
            Log.d("firebase", "Direccion del cliente: " + getIntent().getExtras().getString("clientName"));
            Log.d("firebase", "Status: " + getIntent().getExtras().getString("status"));

            NotificationFirebase notification;
            notification = new NotificationFirebase(getIntent().getExtras().getString("clientLatitude"),
                    getIntent().getExtras().getString("clientLongitude"),
                    getIntent().getExtras().getString("clientAddress"),
                    getIntent().getExtras().getString("clientName"),
                    getIntent().getExtras().getString("status"));
            MapsFragment.AgregarMarcadorPush(notification);
        }

        SharedPreferences settings = getSharedPreferences("perfil", MODE_PRIVATE);
        if (settings.getString("id", null)==null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // inicio del servicio
        done = false;
        startService(new Intent(this, Servicio.class));

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d("main-service", "Se de tiene el servicio: ");
            stopService(new Intent(this, Servicio.class));
            done = true;
            Intent intent = new Intent (this, LoginActivity.class);
            startActivityForResult(intent, 0);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Fragment fragment = new ProfileFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();

        }else if (id == R.id.nav_map) {
            Fragment fragment = new MapsFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();

        } else if (id == R.id.nav_alarm) {
            Fragment fragment = new AlarmFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();

        } else if (id == R.id.nav_network) {
            Fragment fragment = new NetworkFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
