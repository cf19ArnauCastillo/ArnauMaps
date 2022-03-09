package com.example.arnaumaps;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.arnaumaps.Model.ApiCall;
import com.example.arnaumaps.Model.ModelApi;
import com.example.arnaumaps.ModelFlickr.Photo;
import com.example.arnaumaps.ModelFlickr.Results;
import com.example.arnaumaps.Slider.SliderActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.arnaumaps.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;

import android.location.Location;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback  {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private String TAG = "Maps: ";
    private boolean permissionDenied = false;
    private int MapRequestCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MapRequestCode);
            }
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap map) {

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        Log.d(TAG, "onMapClick");
                        getAddress(latLng.latitude, latLng.longitude);
                    }
                });

                map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                    @Override
                    public void onCameraMoveStarted(int reason) {
                        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                            Log.d(TAG, "onCameraMoveStarted");
                        }
                    }
                });
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        String msg = marker.getTitle();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        return true;
                    }
                });

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();
    }

    public void getAddress(double lat, double lng) {
        try {
            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(lat, lng, 1);
            if (addresses.isEmpty()) {
                Toast.makeText(this, "No s’ha trobat informació", Toast.LENGTH_LONG).show();
            } else {
                if (addresses.size() > 0) {
                    String msg =addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();

                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    LatLng ubi = new LatLng(lat, lng);
                    mMap.addMarker(new MarkerOptions().position(ubi).title("Marker in " + addresses.get(0).getFeatureName()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(ubi));

                    ApiThread thread = new ApiThread(ubi);
                    thread.execute();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://api.sunrise-sunset.org/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    ApiCall apiCall = retrofit.create(ApiCall.class);
                    Call<ModelApi> call = apiCall.getAddress(String.valueOf(lat), String.valueOf(lng));

                    call.enqueue(new Callback<ModelApi>(){
                        @Override
                        public void onResponse(Call<ModelApi> call, Response<ModelApi> response) {
                            if(response.code()!=200){
                                Log.i("testApi", "checkConnection");
                                return;
                            }

                            Log.i("testApi", response.body().getStatus() + " - " + response.body().getResults().getSunrise());
                        }

                        @Override
                        public void onFailure(Call<ModelApi> call, Throwable t) {
                            Log.i("testApi", "fail");
                        }
                    });

                    Retrofit retrofit_flickr = new Retrofit.Builder()
                            .baseUrl("https://www.flickr.com/services/rest/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    ApiCall apiCall_flickr = retrofit_flickr.create(ApiCall.class);

                    Log.d("latlng", "latitud: " + String.valueOf(lat) + "longitud: " + lng);
                    Call<Results> call_flickr = apiCall_flickr.getData(String.valueOf(lat), String.valueOf(lng));

                    call_flickr.enqueue(new Callback<Results>(){
                        @Override
                        public void onResponse(Call<Results> call, Response<Results> response) {
                            if(response.code()!=200){
                                Log.i("Apitest", "checkConnection");
                                return;
                            }

                            ArrayList<Photo> photos = response.body().getPhotos().getPhotos();
                            ArrayList<String> images = getImages(photos);
                            for(int i = 0; i < images.size(); i++){
                                Log.i("images", images.get(i).toString());
                            }

                            Intent intent = new Intent(getApplicationContext(), SliderActivity.class);
                            intent.putExtra("images", images);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<Results> call, Throwable t) {
                            Log.i("Apitest", "fail");
                        }
                    });
                }
            }
        }
        catch(Exception e){
            Toast.makeText(this, "No Location Name Found", Toast.LENGTH_LONG).show();
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MapRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<String> getImages(ArrayList<Photo> photos){
        ArrayList<String> images = new ArrayList<String>();
        if(images.size() < 10) {
            for (int i = 0; i < 10; i++) {
                Photo photo = photos.get(i);

                String url = "https://live.staticflickr.com/" + photo.getServer() + "/" + photo.getId() + "_" + photo.getSecret() + ".jpg";
                images.add(url);
            }
        }
        return images;
    }
}