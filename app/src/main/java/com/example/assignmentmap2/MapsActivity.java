package com.example.assignmentmap2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION = 100;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        final TextView textView = (TextView) findViewById(R.id.text);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url="https://anapioficeandfire.com/api/characters/583";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                StringBuilder localities = new StringBuilder();

                try
                {
                    JSONArray data = response.getJSONArray("titles");
                    localities.append("Titles:\n\n");
                    for (int index = 0; index < data.length(); index++)
                    {
                        localities.append(data.get(index) + "\n");
                    }
                    System.err.println(data);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                textView.setText(localities.toString());

            }



        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText("That did not work!");
            }
        });

        queue.add(jsObjRequest);








        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);


        LocationRequest req = new LocationRequest();
        req.setInterval(20000); // 20 seconds
        req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted

            client.requestLocationUpdates(req, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Log.e("location:", locationResult.getLastLocation().toString());

                    Toast.makeText(MapsActivity.this, locationResult.getLastLocation().toString(), Toast.LENGTH_LONG).show();
                    LatLng currentloc = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(currentloc).title("Current Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentloc,20f));



                }
            }, null);

        }
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }


}
