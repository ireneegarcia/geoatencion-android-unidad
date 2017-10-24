package com.example.irene.geoatencionunidad.Services;

/**
 * Created by Irene on 22/10/2017.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.irene.geoatencionunidad.Model.Networks;
import com.example.irene.geoatencionunidad.Remote.APIService;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.irene.geoatencionunidad.MapsFragment.mCurrentLocation;
import static com.example.irene.geoatencionunidad.MapsFragment.networks;
import static com.example.irene.geoatencionunidad.MapsFragment.onMap;

public class Servicio extends Service {


    private Timer timer;
    public static boolean done = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate(){
        super.onCreate();
        timer = new Timer();
        this.stopSelf();
    }

    public void onDestroy(){
        //super.onDestroy();
        Log.d("servicio", "El servicio a Terminado");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("servicio", "Servicio iniciado...");

        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                Log.d("servicio", "Servicio corriendo");

                // enviar constantemente su ubicación
                sendLocation();

                if (done == true) {
                    Log.d("servicio", "Servicio DONE");
                    stopSelf();
                    timer.cancel();
                    timer.purge();
                }

            }

        }, 0, 3000);

        return START_NOT_STICKY;
    }

    public void sendLocation(){


        if (onMap == true) {
            Log.d("sendLocation", ""+networks.get_id());
            Log.d("sendLocation", ""+mCurrentLocation.getLatitude());
            Log.d("sendLocation", ""+mCurrentLocation.getLongitude());

            APIService.Factory.getIntance()
                    .updateNetworkLocation(networks.get_id(),
                            mCurrentLocation.getLatitude()+"",
                            mCurrentLocation.getLongitude()+"")
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
}


