package com.example.odometro;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.security.Provider;


public class OdometerService extends Service {

    private final IBinder binder = new OdometerBinder();
     private LocationListener listener;
    private LocationManager locManager;
    private static double distanceInMeters;
    private static Location lastLocation = null;

    public static final String PERMISSION_STRING = Manifest.permission.ACCESS_FINE_LOCATION;



    public class OdometerBinder extends Binder {
        OdometerService getOdometer(){
            return  OdometerService.this;

        }

    }

    public void onCreate() {

        super.onCreate();
        listener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                if (lastLocation == null) {
                    lastLocation = location;
                }
                distanceInMeters += location.distanceTo(lastLocation);
                lastLocation = location;
            }
        };

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, PERMISSION_STRING) == PackageManager.PERMISSION_GRANTED) {
            String provider = locManager.getBestProvider(new Criteria(), true);
            if (provider != null) {
                locManager.requestLocationUpdates(provider, 1000, 1, listener);
            }
        }
    }




    @Override
    public IBinder onBind(Intent intent) {
        return binder;

    }

    public void onDestroy(){
        super.onDestroy();
        if (locManager != null && listener != null){
            if (ContextCompat.checkSelfPermission(this,PERMISSION_STRING) == PackageManager.PERMISSION_GRANTED){
                locManager.removeUpdates(listener);
            }
            locManager = null;
            listener = null;
        }
    }



    public double getDistance(){
        return this.distanceInMeters ;
    }


}