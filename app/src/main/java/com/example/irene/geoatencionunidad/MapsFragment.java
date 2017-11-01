package com.example.irene.geoatencionunidad;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.irene.geoatencionunidad.Model.Alarma;
import com.example.irene.geoatencionunidad.Model.Alarmas;
import com.example.irene.geoatencionunidad.Model.Logs;
import com.example.irene.geoatencionunidad.Model.Networks;
import com.example.irene.geoatencionunidad.Model.NotificationFirebase;
import com.example.irene.geoatencionunidad.Model.RouteGet;
import com.example.irene.geoatencionunidad.Model.RouteSet;
import com.example.irene.geoatencionunidad.Remote.APIService;
import com.example.irene.geoatencionunidad.Remote.APIServiceRoute;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment {



    View mView;
    Context c;
    static Context cp;
    static String notificacion = "";
    static String notificacionUbicacion = "";
    ListView categorias;
    private static SetLocationPush slp = null;

    // alarma en proceso
    public static ArrayList<Alarmas> alarma = new ArrayList<>();
    // la unidad presente
    public static Networks networks;
    // si hay atencion en proceso
    public Boolean isprocess = false;

    MapView mMapView;
    private static GoogleMap googleMap;

    //Coordenadas de ubicación
    public static Location mCurrentLocation;
    public static Location mCurrentLocationPush;

    /*Para verificar que primer se carguen las
        coordenadas inicial y luego se envien en el servicio*/
    public static Boolean onMap = false;

    //dirección de la ubicación
    public static String address;

    //Ultima vez en actualizar las coordenadas de ubicación
    //Hora
    private static String mLastUpdateTime;
    //fecha
    private static String mLastUpdateDate;

    // Objetos para traer el json de la api de google map y fragmentar el polyline
    RouteSet routeSet = new RouteSet();
    RouteGet routeGet = new RouteGet();



    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_maps, container, false);
        c = (Context)getActivity();
        cp = (Context)getActivity();
        onMap = false;
        isprocess = false;

        obtenerUnidad();
        mMapView = (MapView) mView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately
        // listarCategorias();
        FloatingActionButton fab = (FloatingActionButton) mView.findViewById(R.id.request);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog alert = createSimpleDialog("");
                alert.show();
            }
        });

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationStart();

                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);


            }
        });
        return mView;

    }



    private void locationStart() {
        LocationManager mlocManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        // Local.setMainActivity(c);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) c, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);

        // mensaje1.setText("Localizacion agregada");
        // mensaje2.setText("");
        Log.d("my tag", "Localizacion agregada");
        Log.d("my tag", "");
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }

    public void setLocation() {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (mCurrentLocation.getLatitude() != 0.0 && mCurrentLocation.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(c, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    /*mensaje2.setText("Mi direccion es: \n"
                            + DirCalle.getAddressLine(0));*/
                    Log.d("my tag", "setLocation Mi direccion es: \n"
                            + DirCalle.getAddressLine(0));
                    address = DirCalle.getAddressLine(0);
                    agregarMarcador(DirCalle.getAddressLine(0));
                    SharedPreferences sp = c.getSharedPreferences("perfil", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("latitude", String.valueOf(mCurrentLocation.getLatitude()));
                    editor.putString("longitude", String.valueOf(mCurrentLocation.getLongitude()));
                    editor.putString("address", String.valueOf(DirCalle.getAddressLine(0)));
                    editor.commit();                }

                // bandera para el servicio
                onMap = true;

                sendLocation();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void sendLocation(){


        if (onMap == true) {
            Log.d("sendLocation", ""+networks.get_id());
            Log.d("sendLocation", ""+mCurrentLocation.getLatitude());
            Log.d("sendLocation", ""+mCurrentLocation.getLongitude());

            APIService.Factory.getIntance()
                    .updateNetworkLocation(networks.get_id(),
                            mCurrentLocation.getLatitude()+"",
                            mCurrentLocation.getLongitude()+"",
                            address)
                    .enqueue(new Callback<Networks>() {

                        @Override
                        public void onResponse(Call<Networks> call, Response<Networks> response) {

                            //code == 200
                            if(response.isSuccessful()) {
                                Log.d("sendLocation", "onResponse: todo fino");
                            }
                        }

                        @Override
                        public void onFailure(Call<Networks> call, Throwable t){
                            //
                            Log.d("sendLocation", "This is my message on failure " + call.request().url());
                            Log.d("sendLocation", "This is my message on failure " + t.toString());
                        }
                    });

        }


    }

    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {


        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion


            if (!notificacion.equals("")) {
                AlertDialog alert = createSimpleDialog(notificacion);
                alert.show();
                Log.d("notificacion", "paso por aqui, notificacion: "+notificacion);
                notificacion = "";

            }
            /*if (!notificacionUbicacion.equals("esperando") && !notificacionUbicacion.equals("en atencion")) {
                Log.d("my tag", "paso por aqui");
            }*/

            obtenerAlarmas();
            googleMap.clear();
            loc.getLatitude();
            loc.getLongitude();
            mCurrentLocation = loc;
            String Text = "onLocationChanged Mi ubicacion actual es: " + "\n Lat = "
                    + mCurrentLocation.getLatitude() + "\n Long = " + mCurrentLocation.getLongitude();
            //  mensaje1.setText(Text);
            Log.d("my tag", Text);

            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            mLastUpdateDate = DateFormat.getDateInstance().format(new Date());
            setLocation();

        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            //mensaje1.setText("GPS Desactivado");
            Log.d("my tag", "GPS Desactivado");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            //mensaje1.setText("GPS Activado");
            Log.d("my tag", "GPS Activado");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    public void obtenerUnidad(){

        //id del usuario logueado
        SharedPreferences settings = c.getSharedPreferences("perfil", c.MODE_PRIVATE);
        final String mId = settings.getString("id", null);

        APIService.Factory.getIntance().listNetworks().enqueue(new Callback<List<Networks>>() {
            @Override
            public void onResponse(Call<List<Networks>> call, Response<List<Networks>> response) {

                //code == 200
                if(response.isSuccessful()) {
                    Log.d("my tag", "onResponse: todo fino");
                    for (int i = 0; i< response.body().size(); i++){
                        // si la unidad pertenece al usuario
                        if(response.body().get(i).getServiceUser().equals(mId)){
                            networks = response.body().get(i);

                        }
                    }
                    obtenerAlarmas();
                }
            }

            @Override
            public void onFailure(Call<List<Networks>> call, Throwable t){
                //
                Log.d("myTag", "This is my message on failure " + call.request().url());
                Log.d("myTag", "This is my message on failure " + t.toString());
            }
        });

    }

    public void obtenerAlarmas(){

        alarma = new ArrayList<>();
        APIService.Factory.getIntance().listAlarms().enqueue(new Callback<List<Alarmas>>() {

            @Override
            public void onResponse(Call<List<Alarmas>> call, Response<List<Alarmas>> response) {
                //Logs.d("myTag", "--->bien " + call.request().url());

                if(response.isSuccessful()) {

                    for (int i = 0; i< response.body().size(); i++){
                        // si la alarma pertenece al usuario
                        if(response.body().get(i).getNetwork().equals(networks.get_id()) &&
                                response.body().get(i).getStatus().equals("en atencion")){
                            alarma.add(response.body().get(i));

                            isprocess = true;

                            MarkerOptions options = new MarkerOptions();
                            IconGenerator iconFactory = new IconGenerator(cp);
                            iconFactory.setStyle(IconGenerator.STYLE_BLUE);
                            options.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(DateFormat.getTimeInstance().format(new Date()))));
                            options.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
                            //options.title("Mi posición actual");
                            options.snippet(response.body().get(i).getAddress());

                            LatLng currentLatLng = new LatLng(Double.parseDouble(response.body().get(i).getLatitude()), Double.parseDouble(response.body().get(i).getLongitude()));
                            options.position(currentLatLng);
                            Marker mapMarker = googleMap.addMarker(options);
                            mapMarker.setTitle(response.body().get(i).getUser().getDisplayName());
                            Log.d("my tag", "Marcador añadido.............................");
                            // For zooming automatically to the location of the marker
                            /*googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,
                                    14));*/
                            Log.d("my tag", "Zoom hecho.............................");
                            Log.d("makeurl", " "+ response.body().get(i).getLatitude());
                            Log.d("makeurl", " "+ response.body().get(i).getLongitude());
                            Log.d("makeurl", " "+ networks.getLatitude());
                            Log.d("makeurl", " "+ networks.getLongitude());
                            Log.d("makeurl", " ");
                            makeURL(Double.parseDouble(response.body().get(i).getLatitude()),
                                    Double.parseDouble(response.body().get(i).getLongitude()),
                                    Double.parseDouble(networks.getLatitude()),
                                    Double.parseDouble(networks.getLongitude()));
                        }
                        /*if ((response.body().get(i).getStatus().equals("cancelado por el cliente") ||
                                response.body().get(i).getStatus().equals("cancelado por el operador"))
                                && isprocess == true) {

                            isprocess = false;
                            NotificationFirebase notification;
                            notification = new NotificationFirebase(
                                    response.body().get(i).getLatitude(),
                                    response.body().get(i).getLongitude(),
                                    response.body().get(i).getAddress(),
                                    response.body().get(i).getUser().getDisplayName(),
                                    response.body().get(i).getStatus());

                            AgregarMarcadorPush(notification);
                        }*/
                    }

                    //filtrado(response.body());

                    //Logs.d("AlarmaFragment", "--->on reponse " + response.body().toString());
                    //Logs.d("myTag", "--->on reponse " + call.request().url());
                }
            }

            @Override
            public void onFailure(Call<List<Alarmas>> call, Throwable t) {
                Log.d("AlarmaFragment", "This is my message on failure " + call.request().url());
                Log.d("myTag", "This is my message on failure " + t.toString());
            }
        });

    }

    public void makeURL(final double destlat, final double destlog,
                        final double sourcelat, final double sourcelog) {

        routeSet.setAlternatives("true");
        routeSet.setDestination(destlat + "," + destlog);
        routeSet.setMode("driving");
        routeSet.setOrigin(sourcelat + "," + sourcelog);
        routeSet.setSensor("false");
        APIServiceRoute.FactoryRoute.getIntance().getRoute(routeSet.getOrigin(),routeSet.getDestination(),routeSet.getSensor(),routeSet.getMode(),routeSet.getAlternatives()).enqueue(new Callback<RouteGet>() {
            @Override
            public void onResponse(Call<RouteGet> call, Response<RouteGet> response) {

                //code == 200
                if(response.isSuccessful() && response.body().getRoutes().size() != 0) {
                    routeGet = response.body();

                    Log.d("routeGet", " "+routeGet.getRoutes().toString());
                    Log.d("routeGet", " ");
                    for (int i = 0; i< routeGet.getRoutes().get(0).getLegs().get(0).getSteps().size(); i++){
                        Log.d("routes", "onResponse: "+i);
                        /*Log.d("jsonroute", "Lat" + routeGet.getRoutes().get(0).getLegs().get(0).getSteps().get(i).getStartLocation().getLat().toString());
                        Log.d("jsonroute", "Long" + routeGet.getRoutes().get(0).getLegs().get(0).getSteps().get(i).getStartLocation().getLng().toString());*/

                        Polyline line = googleMap.addPolyline(new PolylineOptions()
                                .add(new LatLng(Double.parseDouble(routeGet.getRoutes().get(0).getLegs().get(0).getSteps().get(i).getStartLocation().getLat()),
                                                Double.parseDouble(routeGet.getRoutes().get(0).getLegs().get(0).getSteps().get(i).getStartLocation().getLng())),
                                     new LatLng(Double.parseDouble(routeGet.getRoutes().get(0).getLegs().get(0).getSteps().get(i).getEndLocation().getLat()),
                                                Double.parseDouble(routeGet.getRoutes().get(0).getLegs().get(0).getSteps().get(i).getEndLocation().getLng())))
                                .width(5)
                                .color(Color.RED)
                                .geodesic(true));

                        // routeGet.getRoutes().get(0).getLegs().get(7).getSteps().toString();
                    }
                }
            }

            @Override
            public void onFailure(Call<RouteGet> call, Throwable t){
                //
                Log.d("myTag", "This is my message on failure " + call.request().url());
            }
        });
    }

    public void actualizarAlarma(){

        //nombre del usuario logueado
        SharedPreferences settings = c.getSharedPreferences("perfil", c.MODE_PRIVATE);
        final String name = settings.getString("name", null);

        Alarma enviarAlarma = new Alarma(alarma.get(0).get_id(),
                alarma.get(0).getUser().getId(),
                alarma.get(0).getCategoryService(),
                "cancelado por la unidad",
                alarma.get(0).getLatitude(),
                alarma.get(0).getLongitude(),
                alarma.get(0).getAddress(),
                alarma.get(0).getCreated(),
                alarma.get(0).getRating(),
                alarma.get(0).getOrganism(),
                "/modules/panels/client/img/cancelbynetwork.png",
                "");
        // Actualización de alarma

        APIService.Factory.getIntance().updateAlarm(enviarAlarma.get_id(), enviarAlarma).enqueue(new Callback<Alarma>() {
            @Override
            public void onResponse(Call<Alarma> call, Response<Alarma> response) {

                //code == 200
                if(response.isSuccessful()) {
                    Log.d("my tag", "onResponse: todo fino");
                    nuevaAlerta();
                }
            }

            @Override
            public void onFailure(Call<Alarma> call, Throwable t){
                //
                Log.d("myTag", "This is my message on failure " + call.request().url());
            }
        });

        networks.setStatus("activo");

        // Actualizar la unidad de atencion
        APIService.Factory.getIntance().updateNetwork(networks.get_id(), networks).enqueue(new Callback<Networks>() {
            @Override
            public void onResponse(Call<Networks> call, Response<Networks> response) {

                //code == 200
                if(response.isSuccessful()) {
                    Log.d("my tag", "onResponse: todo fino DEL LOG");
                }
            }

            @Override
            public void onFailure(Call<Networks> call, Throwable t){
                //
                Log.d("myTag", "This is my message on failure " + call.request().url());
            }
        });

        // Creación de log
        APIService.Factory.getIntance().createLog(
                "La solicitud de atención ha sido cancelada por la unidad: " + networks.getCarCode()+", cuyo responsable es: "+name,
                alarma.get(0).get_id(),
                "",
                alarma.get(0).getUser().getId(),
                alarma.get(0).getOrganism()).enqueue(new Callback<Logs>() {
            @Override
            public void onResponse(Call<Logs> call, Response<Logs> response) {

                //code == 200
                if(response.isSuccessful()) {
                    Log.d("my tag", "onResponse: todo fino DEL LOG");
                }
            }

            @Override
            public void onFailure(Call<Logs> call, Throwable t){
                //
                Log.d("myTag", "This is my message on failure " + call.request().url());
            }
        });
    }
    public void nuevaAlerta(){
        alarma.clear();
        AlertDialog alert = createSimpleDialog("");
        alert.show();
    }

    public AlertDialog createSimpleDialog(String statusAtencion) {

        Log.d("nuevaAlerta: ", "nuevaAlerta: "+alarma.size());

        if (alarma.size() != 0) {
            statusAtencion = alarma.get(0).getStatus();
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(cp);
        LayoutInflater inflater = (LayoutInflater)cp.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //id del usuario logueado
        SharedPreferences settings = cp.getSharedPreferences("perfil", cp.MODE_PRIVATE);
        final String mId = settings.getString("id", null);

        View layout = inflater.inflate(R.layout.layout_request, null);
        builder.setView(layout);


        final TextView status = (TextView) layout.findViewById(R.id.textViewMessage);
        final TextView status1 = (TextView) layout.findViewById(R.id.textViewMessage1);
        final TextView status2 = (TextView) layout.findViewById(R.id.textViewMessage2);
        final ProgressBar progreso = (ProgressBar) layout.findViewById(R.id.progressBarMessage);
        final ImageView imageStatusA = (ImageView) layout.findViewById(R.id.imageViewAway);
        final ImageView imageStatusP = (ImageView) layout.findViewById(R.id.imageViewProcesed);
        final ImageView imageStatusA1 = (ImageView) layout.findViewById(R.id.imageViewAway1);
        final ImageView imageStatusP1 = (ImageView) layout.findViewById(R.id.imageViewProcesed1);
        final ImageView imageStatusA2 = (ImageView) layout.findViewById(R.id.imageViewAway2);
        final ImageView imageStatusP3 = (ImageView) layout.findViewById(R.id.imageViewProcesed2);
        final TableRow row = (TableRow) layout.findViewById(R.id.row_status);
        final RelativeLayout message = layout.findViewById(R.id.message);
        final Button cancelar = (Button) layout.findViewById(R.id.cancelar);
        //final Button nueva_alarma = (Button) layout.findViewById(R.id.nueva_alarma);

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarAlarma();
            }
        });

        categorias = (ListView) layout.findViewById(R.id.listViewCategorias);


        if (statusAtencion.equals("cancelado por el cliente")) {

            googleMap.clear();
            status.setText("Alarma recibida de manera exitosa");
            status1.setText("Atención cancelada por el cliente");
            imageStatusA.setVisibility(View.GONE);
            imageStatusP.setVisibility(View.VISIBLE);
            imageStatusA1.setVisibility(View.GONE);
            imageStatusP1.setVisibility(View.VISIBLE);
            progreso.setVisibility(View.GONE);
            row.setVisibility(View.GONE);
            categorias.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
        }
        else{

            if (statusAtencion.equals("en atencion")){
                status.setText("Alarma recibida de manera exitosa");
                status1.setText("Atención en proceso");
                status2.setText("Esperando culminación de atención");
                imageStatusA.setVisibility(View.GONE);
                imageStatusP.setVisibility(View.VISIBLE);
                imageStatusA1.setVisibility(View.GONE);
                imageStatusP1.setVisibility(View.VISIBLE);
                progreso.setVisibility(View.GONE);
                categorias.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                cancelar.setVisibility(View.VISIBLE);
            }
            else if (statusAtencion.equals("cancelado por el operador")){
                status.setText("Alarma recibida de manera exitosa");
                status1.setText("Atencion cancelada por el operador");
                imageStatusA.setVisibility(View.GONE);
                imageStatusP.setVisibility(View.VISIBLE);
                imageStatusA1.setVisibility(View.GONE);
                imageStatusP1.setVisibility(View.VISIBLE);
                progreso.setVisibility(View.GONE);
                row.setVisibility(View.GONE);
                categorias.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                googleMap.clear();
            }else{
                status.setText("Sin asignación de atención");
                status1.setVisibility(View.GONE);
                imageStatusA.setVisibility(View.GONE);
                imageStatusP.setVisibility(View.VISIBLE);
                imageStatusA1.setVisibility(View.GONE);
                imageStatusP1.setVisibility(View.GONE);
                row.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                progreso.setVisibility(View.GONE);
                googleMap.clear();
            }
        }


        return builder.create();
    }

    public static void AgregarMarcadorPush(NotificationFirebase noti){
        // For dropping a marker at a point on the Map
        // LatLng currentLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
        slp = new SetLocationPush(noti);
        slp.execute((Void) null);
    }

    private void agregarMarcador(String address){
        // For dropping a marker at a point on the Map
        // LatLng currentLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
        MarkerOptions options = new MarkerOptions();
        IconGenerator iconFactory = new IconGenerator(c);
        iconFactory.setStyle(IconGenerator.STYLE_ORANGE);
        options.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(mLastUpdateTime)));
        options.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
        //options.title("Mi posición actual");
        options.snippet(address);

        LatLng currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        options.position(currentLatLng);
        Marker mapMarker = googleMap.addMarker(options);
        long atTime = mCurrentLocation.getTime();
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date(atTime));
        mapMarker.setTitle("Mi posición actual");
        Log.d("my tag", "Marcador añadido.............................");
        // For zooming automatically to the location of the marker
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,
                14));
        Log.d("my tag", "Zoom hecho.............................");

    }

    public static class SetLocationPush extends AsyncTask<Void, Void, Boolean> {
        NotificationFirebase noti;

        SetLocationPush(NotificationFirebase n){
            noti = n;
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            /*noti.setNetwork(params[0].toString());
            noti.setNetworkLatitude(params[1].toString());
            noti.setNetworkLongitude(params[2].toString());
            noti.setStatus(params[3].toString());*/
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (noti.getStatus().equals("en atencion")) {
                MarkerOptions options = new MarkerOptions();
                IconGenerator iconFactory = new IconGenerator(cp);
                iconFactory.setStyle(IconGenerator.STYLE_BLUE);
                options.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(DateFormat.getTimeInstance().format(new Date()))));
                options.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
                //options.title("Mi posición actual");
                options.snippet(noti.getClientAddress());

                LatLng currentLatLng = new LatLng(Double.parseDouble(noti.getClientLatitude()), Double.parseDouble(noti.getClientLongitude()));
                options.position(currentLatLng);
                Marker mapMarker = googleMap.addMarker(options);
                mapMarker.setTitle(noti.getClientName());
                Log.d("my tag", "Marcador añadido.............................");
                // For zooming automatically to the location of the marker
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,
                        14));
                Log.d("my tag", "Zoom hecho.............................");
            }
            notificacion = (noti.getStatus());
            //notificacionId| = (noti.getI);
            notificacionUbicacion = (noti.getStatus());
            //createSimpleDialog(notificacion);
        }

        @Override
        protected void onCancelled() {
        }
    }
}
