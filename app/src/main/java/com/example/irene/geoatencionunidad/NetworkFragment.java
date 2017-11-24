package com.example.irene.geoatencionunidad;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.irene.geoatencionunidad.Model.MobileUnitLog;
import com.example.irene.geoatencionunidad.Model.Networks;
import com.example.irene.geoatencionunidad.Remote.APIService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class NetworkFragment extends Fragment {

    View mView;
    Context c;
    Networks networks;
    String status;

    public NetworkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_network, container, false);
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
                    adaptarVista();
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


    public void adaptarVista(){

        final TextView id_unidad = (TextView) mView.findViewById(R.id.id_unidad);
        final TextView status_unidad = (TextView) mView.findViewById(R.id.status_unidad);
        final Button change_button = (Button) mView.findViewById(R.id.change_button);

        id_unidad.setText(networks.getCarCode());
        status_unidad.setText(networks.getStatus());

        if (networks.getStatus().equals("activo")) {
            status = "inactivo";
            change_button.setVisibility(View.VISIBLE);
            change_button.setText("Cambiar a inactivo");
        }

        if (networks.getStatus().equals("inactivo")) {
            status = "activo";
            change_button.setVisibility(View.VISIBLE);
            change_button.setText("Cambiar a activo");
        }

        change_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarStatus();
            }
        });

    }

    public void cambiarStatus(){

        //id del usuario logueado
        SharedPreferences settings = c.getSharedPreferences("perfil", c.MODE_PRIVATE);
        final String name = settings.getString("name", null);

        networks.setStatus(status);

        APIService.Factory.getIntance().updateNetwork(networks.get_id(), networks).enqueue(new Callback<Networks>() {
            @Override
            public void onResponse(Call<Networks> call, Response<Networks> response) {

                //code == 200
                if(response.isSuccessful()) {
                    Log.d("my tag", "onResponse: todo fino");
                    obtenerUnidad();
                }
            }

            @Override
            public void onFailure(Call<Networks> call, Throwable t){
                //
                Log.d("myTag", "This is my message on failure " + call.request().url());
                Log.d("myTag", "This is my message on failure " + t.toString());
            }
        });

        // Creación de log
        APIService.Factory.getIntance().createMobileUnitLog(networks.get_id(),
                networks.getCarCode(),
                "La unidad ha cambiado su status a: "+ status + ", responble de la unidad: " + name)
                .enqueue(new Callback<MobileUnitLog>() {
            @Override
            public void onResponse(Call<MobileUnitLog> call, Response<MobileUnitLog> response) {

                //code == 200
                if(response.isSuccessful()) {
                    Log.d("my tag", "onResponse: todo fino DEL LOG");
                }
            }

            @Override
            public void onFailure(Call<MobileUnitLog> call, Throwable t){
                //
                Log.d("myTag", "This is my message on failure " + call.request().url());
            }
        });


    }

}
