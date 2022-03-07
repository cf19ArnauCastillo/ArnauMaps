package com.example.arnaumaps;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
/*
public class ApiThread extends AsyncTask<Void, Void, String> {

    private JSONObject jObject;
    private double latitude;
    private double longitude;

    public ApiThread(double Latitude, double Longitude) {
        latitude = Latitude;
        longitude = Longitude;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL url = new URL("https://api.sunrise-sunset.org/json?lat="+latitude+"&lng="+longitude);
            Log.i("logtest", "https://api.sunrise-sunset.org/json?lat=+"+latitude+"lng="+longitude);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String data = bufferedReader.readLine();
            return data;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String data) {
        try {
            jObject = new JSONObject(data);
            jObject = jObject.getJSONObject("results");

            String sunrise = jObject.getString("sunrise");
            Log.i("logtest", "------>" + sunrise);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
*/
public class ApiThread extends AsyncTask<Void, Void, Void> {
    private final double lat;
    private final double lng;

    public ApiThread(double lat,double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try{
            URL url = new URL("https://api.sunrise-sunset.org/json?lat="+lat+"&lng="+lng);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String data = bufferedReader.readLine();
            onPostExecute(data);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Void onPostExecute(String data) {
        try{
            JSONObject jObject = new JSONObject(data);
            jObject = jObject.getJSONObject("results");
            String sunrise = jObject.getString("sunrise");
            Log.i("logtest", "------>" + sunrise);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
