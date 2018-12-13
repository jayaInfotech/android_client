package com.bestspa.spa.client;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.DropBoxManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import com.bestspa.spa.client.Utiles.Constant;
import com.bestspa.spa.client.Utiles.GPSTracker;
import com.bestspa.spa.client.map.PlaceDetailsJSONParser;
import com.bestspa.spa.client.map.PlaceJSONParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class GetCurrentLocationMap extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    Button getLocationBtn;
    Geocoder geocodernew;
    private String currentLatitue = "";
    private String currentLongitude = "";
    private float currentZoom = 12.0f;
    private String customAddress = "";
    SupportMapFragment mapFragment;
    GPSTracker gps;
    AutoCompleteTextView autoCompView;
    private HashMap<String, String> hmap;
    ImageView iv_back;
    private String selectedLatitude = "00.00";
    private String selectedLongitude = "00.00";
    int zoom;
    private DownloadTask placeDetailsDownloadTask;
    private ParserTask placeDetailsParserTask;
    private ParserTask placesParserTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_get_current_location_map);

        this.getLocationBtn = (Button) findViewById(R.id.btnLocation);
        this.geocodernew = new Geocoder(this, Locale.getDefault());

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        try {
            this.currentLatitue = getLatitude();
            this.currentLongitude = getLongitude();
        } catch (Exception e) {
            e.printStackTrace();
        }

        iv_back = (ImageView) findViewById(R.id.imageView1);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String postalCode = "";
                String userCountry = "";
                try {
                    List<Address> addressesData = GetCurrentLocationMap.this.geocodernew.getFromLocation(Double.parseDouble(GetCurrentLocationMap.this.selectedLatitude), Double.parseDouble(GetCurrentLocationMap.this.selectedLongitude), 1);
                    String addressMy = ((Address) addressesData.get(0)).getAddressLine(0);
                    String city = ((Address) addressesData.get(0)).getLocality();
                    String state = ((Address) addressesData.get(0)).getAdminArea();
                    userCountry = ((Address) addressesData.get(0)).getCountryName();
                    postalCode = ((Address) addressesData.get(0)).getPostalCode();
                    GetCurrentLocationMap.this.customAddress = addressMy + " ," + city + " ," + state;
                } catch (Exception e) {
                    e.printStackTrace();
                    postalCode = "";
                    userCountry = "";
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra("userLatitude", GetCurrentLocationMap.this.selectedLatitude);
                returnIntent.putExtra("userLongitude", GetCurrentLocationMap.this.selectedLongitude);
                returnIntent.putExtra("userAddress", GetCurrentLocationMap.this.customAddress);
                returnIntent.putExtra("userCountry", userCountry);
                returnIntent.putExtra("userPinCode", postalCode);
                GetCurrentLocationMap.this.setResult(-1, returnIntent);
                GetCurrentLocationMap.this.finish();
            }
        });

    }

    private String getLatitude() {
        String latitudeData = "00.000";
        this.gps = new GPSTracker(this);
        if (this.gps.canGetLocation()) {
            return String.valueOf(this.gps.getLatitude());
        }
        this.gps.showSettingsAlert();
        return latitudeData;
    }

    private String getLongitude() {
        String longitudeData = "00.000";
        this.gps = new GPSTracker(this);
        if (this.gps.canGetLocation()) {
            return String.valueOf(this.gps.getLongitude());
        }
        this.gps.showSettingsAlert();
        return longitudeData;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constant.REQUEST_FINE_LOCATION_CODE);
            return;
        }
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location myLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));
        mMap.setMapType(1);


        double latitude = Double.parseDouble(this.currentLatitue);
        double longitude = Double.parseDouble(this.currentLongitude);
        addMarker(mMap, latitude, longitude, "", "");
        this.selectedLatitude = Double.toString(latitude);
        this.selectedLongitude = Double.toString(longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(this.currentZoom));
        mMap.setOnCameraChangeListener(new C13535());
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {
                mMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(point);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
                mMap.addMarker(markerOptions).showInfoWindow();
                GetCurrentLocationMap.this.selectedLatitude = Double.toString(point.latitude);
                GetCurrentLocationMap.this.selectedLongitude = Double.toString(point.longitude);
            }
        });
    }

    class C13535 implements GoogleMap.OnCameraChangeListener {
        C13535() {
        }

        public void onCameraChange(CameraPosition position) {
            if (position.zoom != GetCurrentLocationMap.this.currentZoom) {
                GetCurrentLocationMap.this.currentZoom = position.zoom;
            }
            zoom = new Float(GetCurrentLocationMap.this.currentZoom).intValue();
        }
    }

    private void addMarker(GoogleMap map, double lat, double lon, String title, String snippet) {
        map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(title).snippet(snippet));
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String strSelected = (String) adapterView.getItemAtPosition(position);
        String referenceKey = "";
//        for (DropBoxManager.Entry mentry : this.hmap.entrySet()) {
//            if (mentry.getKey().toString().equals(strSelected)) {
//                referenceKey = mentry.getValue().toString();
//            }
//        }
//        this.placeDetailsDownloadTask = new DownloadTask(1);
//        String url = getPlaceDetailsUrl(referenceKey);
//        this.placeDetailsDownloadTask.execute(new String[]{url});
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }


    private class DownloadTask extends AsyncTask<String, Void, String> {
        private int downloadType = 0;

        public DownloadTask(int type) {
            this.downloadType = type;
        }

        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = GetCurrentLocationMap.this.downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            switch (this.downloadType) {
                case 0:
                    GetCurrentLocationMap.this.placesParserTask = new ParserTask(0);
                    GetCurrentLocationMap.this.placesParserTask.execute(new String[]{result});
                    return;
                case 1:
                    GetCurrentLocationMap.this.placeDetailsParserTask = new ParserTask(1);
                    GetCurrentLocationMap.this.placeDetailsParserTask.execute(new String[]{result});
                    return;
                default:
                    return;
            }
        }
    }


    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        int parserType = 0;

        /* renamed from: com.spa.easyspa.staff.GetCurrentLocationMap$ParserTask$1 */
        class C13581 implements GoogleMap.OnCameraChangeListener {
            C13581() {
            }

            public void onCameraChange(CameraPosition position) {
                if (position.zoom != GetCurrentLocationMap.this.currentZoom) {
                    GetCurrentLocationMap.this.currentZoom = position.zoom;
                }
                int intZoom = new Float(GetCurrentLocationMap.this.currentZoom).intValue();
            }
        }

        public ParserTask(int type) {
            this.parserType = type;
        }

        protected List<HashMap<String, String>> doInBackground(String... jsonData) {
            try {
                JSONObject jObject = new JSONObject(jsonData[0]);
                switch (this.parserType) {
                    case 0:
                        return new PlaceJSONParser().parse(jObject);
                    case 1:
                        return new PlaceDetailsJSONParser().parse(jObject);
                    default:
                        return null;
                }
            } catch (Exception e) {
                Log.d("Exception", e.toString());
                return null;
            }
        }

        protected void onPostExecute(List<HashMap<String, String>> result) {
            switch (this.parserType) {
                case 0:
//                    GetCurrentLocationMap.this.atvPlaces.setAdapter(new SimpleAdapter(GetCurrentLocationMap.this.getBaseContext(), result, 17367043, new String[]{PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_DESCRIPTION}, new int[]{16908308}));
                    return;
                case 1:
                    HashMap<String, String> hm = (HashMap) result.get(0);
                    double latitude = Double.parseDouble((String) hm.get("lat"));
                    double longitude = Double.parseDouble((String) hm.get("lng"));
                    final GoogleMap newMap = mMap;
                    newMap.setMapType(1);
                    newMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
                    newMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));
                    GetCurrentLocationMap.this.selectedLatitude = Double.toString(latitude);
                    GetCurrentLocationMap.this.selectedLongitude = Double.toString(longitude);
                    newMap.setOnCameraChangeListener(new C13581());
                    newMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        public void onMapClick(LatLng point) {
                            newMap.clear();
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(point);
                            newMap.animateCamera(CameraUpdateFactory.newLatLng(point));
                            newMap.addMarker(markerOptions).showInfoWindow();
                            GetCurrentLocationMap.this.selectedLatitude = Double.toString(point.latitude);
                            GetCurrentLocationMap.this.selectedLongitude = Double.toString(point.longitude);
                        }
                    });
                    return;
                default:
                    return;
            }
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(strUrl).openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String str = "";
            while (true) {
                str = br.readLine();
                if (str == null) {
                    break;
                }
                sb.append(str);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


}
