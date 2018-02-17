package com.example.irene.geoatencionunidad;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.irene.geoatencionunidad.Model.Alarma;
import com.example.irene.geoatencionunidad.Model.Alarmas;
import com.example.irene.geoatencionunidad.Model.Logs;
import com.example.irene.geoatencionunidad.Model.MobileUnitLog;
import com.example.irene.geoatencionunidad.Model.Networks;
import com.example.irene.geoatencionunidad.Model.Users;
import com.example.irene.geoatencionunidad.Remote.APIService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlarmFragment extends Fragment {

    View mView;
    Context c;
    ArrayList<Alarmas> alarma;
    //List<Alarma> alarma;
    Networks networks;
    Users user;

    public AlarmFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_alarm, container, false);
        c = (Context)getActivity();

        obtenerUnidad();

        return mView;
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
                        if(response.body().get(i).getServiceUser() != null && response.body().get(i).getServiceUser().equals(mId)){
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
        if (networks != null) {

            alarma = new ArrayList<>();
            APIService.Factory.getIntance().listAlarms().enqueue(new Callback<List<Alarmas>>() {

                @Override
                public void onResponse(Call<List<Alarmas>> call, Response<List<Alarmas>> response) {
                    //Logs.d("myTag", "--->bien " + call.request().url());

                    if (response.isSuccessful()) {

                        for (int i = 0; i < response.body().size(); i++) {
                            // si la alarma pertenece al usuario
                            if (response.body().get(i).getNetwork().equals(networks.get_id()) &&
                                    response.body().get(i).getStatus().equals("en atencion")) {
                                alarma.add(response.body().get(i));
                            }
                        }

                        if (alarma.size() != 0) {
                            obtenerCliente();
                            Log.d("respuesta", "no hay alarma");
                        } else {
                            statusAtencion();
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Alarmas>> call, Throwable t) {
                    Log.d("AlarmaFragment", "This is my message on failure " + call.request().url());
                    Log.d("myTag", "This is my message on failure " + t.toString());
                }
            });
        }
    }

    public void obtenerCliente(){

        APIService.Factory.getIntance().getClient(alarma.get(0).getUser().getId()).enqueue(new Callback<Users>() {

            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                //Logs.d("myTag", "--->bien " + call.request().url());

                if(response.isSuccessful()) {

                    user = response.body();
                    statusAtencion();

                    //filtrado(response.body());

                    Log.d("AlarmaFragment", "--->on reponse " + response.body().toString());
                    //Logs.d("myTag", "--->on reponse " + call.request().url());
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Log.d("AlarmaFragment", "This is my message on failure " + call.request().url());
                Log.d("myTag", "This is my message on failure " + t.toString());
            }
        });
    }

    public void actualizarUnidad(){
        networks.setStatus("activo");

        // Actualizar la unidad de atencion
        APIService.Factory.getIntance().updateNetwork(networks.get_id(), networks).enqueue(new Callback<Networks>() {
            @Override
            public void onResponse(Call<Networks> call, Response<Networks> response) {

                //code == 200
                if(response.isSuccessful()) {
                    Log.d("respuesta", "actualizada la unidad");
                    obtenerAlarmas();
                }
            }

            @Override
            public void onFailure(Call<Networks> call, Throwable t){
                //
                Log.d("myTag", "This is my message on failure " + call.request().url());
            }
        });
    }

    public void actualizarAlarma(String status, String icon, String textLog){

        final RelativeLayout message = mView.findViewById(R.id.message);
        final TableLayout button_done = (TableLayout) mView.findViewById(R.id.button_done);
        final LinearLayout linearLayout = (LinearLayout) mView.findViewById(R.id.linearLayout);
        final ImageView imageView3 = (ImageView) mView.findViewById(R.id.imageView3);
        final TextView textView7 = (TextView) mView.findViewById(R.id.textView7);
        final ProgressBar progreso = (ProgressBar) mView.findViewById(R.id.progressBarMessage);

        progreso.setVisibility(View.VISIBLE);
        message.setVisibility(View.GONE);
        button_done.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        imageView3.setVisibility(View.GONE);
        textView7.setVisibility(View.GONE);
        String networkID;

        if (status.equals("cancelado por la unidad")) {
            networkID = "";
        }else{
            networkID = alarma.get(0).getNetwork();
        }

        Alarma enviarAlarma = new Alarma(alarma.get(0).get_id(),
                alarma.get(0).getUser().getId(),
                alarma.get(0).getCategoryService(),
                status,
                alarma.get(0).getLatitude(),
                alarma.get(0).getLongitude(),
                alarma.get(0).getAddress(),
                alarma.get(0).getCreated(),
                alarma.get(0).getRating(),
                alarma.get(0).getOrganism(),
                icon,
                networkID);

        Logs log = new Logs(textLog,
                alarma.get(0).get_id(),
                networkID,
                alarma.get(0).getUser().getId(),
                alarma.get(0).getOrganism());


        // Actualización de alarma

        APIService.Factory.getIntance().updateAlarm(enviarAlarma.get_id(), enviarAlarma).enqueue(new Callback<Alarma>() {
            @Override
            public void onResponse(Call<Alarma> call, Response<Alarma> response) {

                //code == 200
                if(response.isSuccessful()) {
                    Log.d("respuesta", "Actualizada la alarma");
                    actualizarUnidad();
                }
            }

            @Override
            public void onFailure(Call<Alarma> call, Throwable t){
                //
                Log.d("myTag", "This is my message on failure " + call.request().url());
            }
        });
        // Creación de log
        APIService.Factory.getIntance().createLog(
                log.getDescription(),
                log.getAlarm(),
                log.getNetwork(),
                log.getClient(),
                log.getOrganism()).enqueue(new Callback<Logs>() {
            @Override
            public void onResponse(Call<Logs> call, Response<Logs> response) {

                //code == 200
                if(response.isSuccessful()) {
                    // Log.d("my tag", "onResponse: todo fino DEL LOG");
                }
            }

            @Override
            public void onFailure(Call<Logs> call, Throwable t){
                // Log.d("myTag", "This is my message on failure " + call.request().url());
            }
        });

        // Creación de log
        APIService.Factory.getIntance().createMobileUnitLog(
                log.getNetwork(),
                networks.getCarCode(),
                log.getDescription()).enqueue(new Callback<MobileUnitLog>() {
            @Override
            public void onResponse(Call<MobileUnitLog> call, Response<MobileUnitLog> response) {

                //code == 200
                if(response.isSuccessful()) {
                    // Log.d("my tag", "onResponse: todo fino DEL LOG");
                }
            }

            @Override
            public void onFailure(Call<MobileUnitLog> call, Throwable t){
                //
                Log.d("myTag", "This is my message on failure " + call.request().url());
            }
        });
        //obtenerUnidad();
    }

    public AlertDialog createSimpleDialogCancelar() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(c);
        final LayoutInflater inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.layout_request, null);
        builder.setView(layout);

        //nombre del usuario logueado
        SharedPreferences settings = c.getSharedPreferences("perfil", c.MODE_PRIVATE);
        final String name = settings.getString("name", null);

        builder.setTitle("Cancelar solicitud de atención");
        builder.setMessage("\n¿Está seguro de que desea realizar esta acción?");
        builder.setPositiveButton("Si, cancelar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
                actualizarAlarma("cancelado por la unidad",
                        "/modules/panels/client/img/cancelbynetwork.png",
                        "Ha sido cancelada la solicitud de atención: " + alarma.get(0).get_id() +
                                " del cliente: " + alarma.get(0).getUser().getDisplayName() +
                                ", por la unidad: "+networks.getCarCode()+
                                ", cuyo responsable es: "+name);
            }}
        );

        builder.setNegativeButton("Volver atrás", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }}
        );

        return builder.create();
    }

    public AlertDialog createSimpleDialogAtendido() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(c);
        final LayoutInflater inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.layout_request, null);
        builder.setView(layout);

        //nombre del usuario logueado
        SharedPreferences settings = c.getSharedPreferences("perfil", c.MODE_PRIVATE);
        final String name = settings.getString("name", null);

        builder.setTitle("Terminado atención");
        builder.setMessage("\n¿Ha terminado exitosamente la atención de esta solicitud?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
                actualizarAlarma("atendido",
                        "/modules/panels/client/img/done.png",
                        "Ha sido atendido exitosamente la solicitud de atención:" + alarma.get(0).get_id() +
                                " del cliente: " + user.getDisplayName()+
                                ", por la unidad: "+networks.getCarCode()+
                                ", cuyo responsable es: "+name);
            }}
        );

        builder.setNegativeButton("Volver atrás", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }}
        );

        return builder.create();
    }

    public void statusAtencion(){

        final ProgressBar progreso = (ProgressBar) mView.findViewById(R.id.progressBarMessage);
        final TextView status = (TextView) mView.findViewById(R.id.textViewMessage);
        final TextView status1 = (TextView) mView.findViewById(R.id.textViewMessage1);
        final TextView status2 = (TextView) mView.findViewById(R.id.textViewMessage2);
        final ImageView imageStatusA = (ImageView) mView.findViewById(R.id.imageViewAway);
        final ImageView imageStatusP = (ImageView) mView.findViewById(R.id.imageViewProcesed);
        final ImageView imageStatusA1 = (ImageView) mView.findViewById(R.id.imageViewAway1);
        final ImageView imageStatusP1 = (ImageView) mView.findViewById(R.id.imageViewProcesed1);
        final ImageView imageStatusA2 = (ImageView) mView.findViewById(R.id.imageViewAway2);
        final ImageView imageStatusP3 = (ImageView) mView.findViewById(R.id.imageViewProcesed2);
        final TableRow row = (TableRow) mView.findViewById(R.id.row_status);
        final RelativeLayout message = mView.findViewById(R.id.message);
        final LinearLayout linearLayout = (LinearLayout) mView.findViewById(R.id.linearLayout);
        final Button cancelar = (Button) mView.findViewById(R.id.cancelar);
        final Button atendido = (Button) mView.findViewById(R.id.atendido);
        final TableLayout button_done = (TableLayout) mView.findViewById(R.id.button_done);
        final TextView textView7 = (TextView) mView.findViewById(R.id.textView7);
        final ImageView imageView3 = (ImageView) mView.findViewById(R.id.imageView3);
        final TextView nombre = (TextView) mView.findViewById(R.id.nombre);
        final TextView telefono = (TextView) mView.findViewById(R.id.telefono);
        final TextView correo = (TextView) mView.findViewById(R.id.email);

        //nombre del usuario logueado
        SharedPreferences settings = c.getSharedPreferences("perfil", c.MODE_PRIVATE);
        final String name = settings.getString("name", null);

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog alert = createSimpleDialogCancelar();
                alert.show();
            }
        });

        atendido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog alert = createSimpleDialogAtendido();
                alert.show();

            }
        });
        // Log.d("AlarmaFragment", "statusAtencion: "+alarma.size());
        progreso.setVisibility(View.GONE);
        message.setVisibility(View.VISIBLE);
        if (alarma.size() == 0) {
            imageStatusA2.setVisibility(View.VISIBLE);
            status2.setText("Sin asignación de atención");

            textView7.setVisibility(View.GONE);
            imageView3.setVisibility(View.GONE);
            button_done.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
        }else{
            if (alarma.get(0).getStatus().equals("en atencion")){
                status.setText("Alarma recibida de manera exitosa");
                status1.setText("Atención en proceso");
                status2.setText("Esperando culminación de atención");
                imageStatusA.setVisibility(View.GONE);
                imageStatusP.setVisibility(View.VISIBLE);
                imageStatusA1.setVisibility(View.GONE);
                imageStatusP1.setVisibility(View.VISIBLE);
                cancelar.setVisibility(View.VISIBLE);
                atendido.setVisibility(View.VISIBLE);

                textView7.setVisibility(View.VISIBLE);
                imageView3.setVisibility(View.VISIBLE);
                button_done.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                nombre.setText(user.getDisplayName());
                telefono.setText(user.getPhone());
                correo.setText(user.getEmail());
            }
        }

    }

}
