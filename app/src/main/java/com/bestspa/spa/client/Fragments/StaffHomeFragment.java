package com.bestspa.spa.client.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bestspa.spa.client.API.APIClient;
import com.bestspa.spa.client.API.APIInterface;
import com.bestspa.spa.client.Model.ServiceModel;
import com.bestspa.spa.client.Model.User;
import com.bestspa.spa.client.R;
import com.bestspa.spa.client.Utiles.Constant;
import com.bestspa.spa.client.Utiles.DatabaseHelper;
import com.bestspa.spa.client.Utiles.GPSTracker;
import com.bestspa.spa.client.Utiles.Links;
import com.bestspa.spa.client.Utiles.SessionManager;
import com.bestspa.spa.client.map.CustomMarker;
import com.bumptech.glide.Glide;
import com.facebook.appevents.AppEventsConstants;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffHomeFragment extends Fragment implements OnMapReadyCallback {

    private ArrayList<ServiceModel> ExpListItems;
    private ExpandableListView ExpandList;
    private String businessName = "";
    private Context cont;
    private int count = 0;
    private String currentLatitue = "";
    private String currentLongitude = "";
    private float currentZoom = 15.0f;
    DatabaseHelper db;
    SharedPreferences.Editor editorService;
    GPSTracker gps;
    private Handler handler = new Handler();
    LayoutInflater inflater1;
    private double latitudeStaff = 0.0d;
    private double longitudeStaff = 0.0d;
    private MapFragment mMapFragment;
    private HashMap<Marker, CustomMarker> mMarkersHashMap;
    private ArrayList<CustomMarker> mMyMarkersArray;
    SharedPreferences prefService;
    private ProgressDialog progressDialog;
    private String ratingCount = "0";
    private String reviewCount = "0";
    private View rootView;
    SessionManager session;
    private Timer timer = new Timer();
    private String userId;
    private String userNameStr;
    private String userType;
    SupportMapFragment supportMapFragment;
    APIInterface apiInterface;
    ArrayList<User> users;
    User user;
    SessionManager sessionManager;
    GPSTracker gpsTracker;
    Intent intent;
    Activity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        activity = getActivity();
        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getUserDetails();
        gpsTracker = new GPSTracker(getActivity());
        this.rootView = inflater.inflate(R.layout.staff_fragment_home, container, false);
        inflater1 = inflater;
        cont = getActivity();
        this.mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, this.mMapFragment);
        fragmentTransaction.commit();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        mMapFragment.getMapAsync(this);
        return rootView;

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d("onMapReady", "yes");
        try {
            latitudeStaff = Double.valueOf(user.getLatLong().get(0));
            longitudeStaff = Double.valueOf(user.getLatLong().get(1));

            googleMap.setMapType(1);
            googleMap.setMyLocationEnabled(true);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(this.latitudeStaff, this.longitudeStaff)));
            int intZoomG = new Float(this.currentZoom).intValue();
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(intZoomG));

            googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                public void onCameraChange(CameraPosition position) {
                    if (position.zoom != currentZoom) {
                        currentZoom = position.zoom;
                    }
                }
            });

        } catch (Exception e) {
            latitudeStaff = gpsTracker.getLatitude();
            longitudeStaff = gpsTracker.getLongitude();

        }

        apiInterface.GetAllUser().enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Call<ArrayList<User>> call, Response<ArrayList<User>> response) {

                Log.d("onresponse",new Gson().toJson(response));

                users = response.body();
                mMyMarkersArray = new ArrayList<>();

                if (response.code() == 200) {
                    for (int i = 0; i < users.size(); i++) {

                        Log.d("userame",users.get(i).getUserName());

                        int totalofMedLevels = 0;
                        int averageMedLevel = 0;

                        try{

                            if (users.get(i).getMerchantid().getRating().size() > 0) {
                                for (int k = 0; k < users.get(i).getMerchantid().getRating().size(); k++) {
                                    totalofMedLevels += users.get(i).getMerchantid().getRating().get(k);
                                }
                                averageMedLevel = totalofMedLevels / users.get(i).getMerchantid().getRating().size();
                                if (users.get(i).getUserTypes().equals(Constant.Customer))
                                {
                                    mMyMarkersArray.add(new CustomMarker(Double.valueOf(users.get(i).getLatLong().get(0)), Double.valueOf(users.get(i).getLatLong().get(1)), users.get(i).get_id(), users.get(i).getUserName(), String.valueOf(averageMedLevel), String.valueOf(users.get(i).getMerchantid().getRating().size()), users.get(i).getUserImage().get(0),users.get(i).getUserTypes()));
                                }else
                                {
                                    mMyMarkersArray.add(new CustomMarker(Double.valueOf(users.get(i).getLatLong().get(0)), Double.valueOf(users.get(i).getLatLong().get(1)), users.get(i).get_id(), users.get(i).getMerchantid().getBusinessName(), String.valueOf(averageMedLevel), String.valueOf(users.get(i).getMerchantid().getRating().size()), users.get(i).getUserImage().get(0),users.get(i).getUserTypes()));
                                }
                                googleMap.clear();
                                plotMarkers(mMyMarkersArray, googleMap);
                            }

                        }catch (Exception e) {
                            e.printStackTrace();
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(gpsTracker.getLatitude(),gpsTracker.getLongitude())));
                        }
                    }
                }else
                {
                    Toast.makeText(activity, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable t) {
                Toast.makeText(getActivity(), getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void plotMarkers(ArrayList<CustomMarker> markers, GoogleMap customMap) {
        if (markers.size() > 0) {
            this.mMarkersHashMap = new HashMap();
            int counter = 0;
            Iterator it = markers.iterator();
            while (it.hasNext()) {
                CustomMarker myMarker = (CustomMarker) it.next();
                Log.d("usertype",myMarker.getUserType());

                MarkerOptions markerOption = new MarkerOptions().position(new LatLng(myMarker.getLatitudeStr().doubleValue(), myMarker.getLongitudeStr().doubleValue()));
                if (myMarker.getUserType().equals(Constant.Customer)) {
                    markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.cust_icon_30));
                } else {
                    markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.u_icon));
                }
                this.mMarkersHashMap.put(customMap.addMarker(markerOption), myMarker);
                customMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
                customMap.setOnMarkerClickListener(new C12759());
                counter++;
            }
        }
    }

    private Bitmap getRoundedCornerImage(Bitmap bitmap) {

        Bitmap resized, targetBitmap;
        Canvas canvas;
        Rect rect;
        Paint paint;
        RectF rectF;

        if (bitmap != null) {
            resized = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
            targetBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(targetBitmap);
            paint = new Paint();
            rect = new Rect(0, 0, targetBitmap.getWidth(), targetBitmap.getHeight());
            rectF = new RectF(rect);
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.parseColor("#65A657"));
            canvas.drawRoundRect(rectF, 60.0f, 60.0f, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(resized, rect, rect, paint);
            return targetBitmap;
        }
        resized = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.cont.getResources(), R.drawable.marker_user), 50, 50, true);
        targetBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(targetBitmap);
        paint = new Paint();
        rect = new Rect(0, 0, targetBitmap.getWidth(), targetBitmap.getHeight());
        rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#65A657"));
        canvas.drawRoundRect(rectF, 60.0f, 60.0f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(resized, rect, rect, paint);
        canvas.drawBitmap(resized, rect, rect, paint);
        return targetBitmap;
    }


    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        public View getInfoWindow(Marker marker) {
            return null;
        }

        public View getInfoContents(Marker marker) {

            View v1 = inflater1.inflate(R.layout.activity_custom_marker, null);
            v1.setLayoutParams(new ViewGroup.LayoutParams((int) cont.getResources().getDimension(R.dimen.marker300dp), -2));
            CustomMarker myMarker = (CustomMarker) mMarkersHashMap.get(marker);
            TextView markerLabel = (TextView) v1.findViewById(R.id.aUserNameTxt);
            TextView txtReview = (TextView) v1.findViewById(R.id.txtReview);
            ImageView userImageIcon = (ImageView) v1.findViewById(R.id.userImage);
            String str = AppEventsConstants.EVENT_PARAM_VALUE_NO;
            try {
                str = myMarker.getUserImage();
            } catch (Exception ex) {
                ex.printStackTrace();
                str = AppEventsConstants.EVENT_PARAM_VALUE_NO;
            }
            try {
                Glide.with(activity).load(Links.URL+str).into(userImageIcon);
//                if (str.equals(AppEventsConstants.EVENT_PARAM_VALUE_NO)) {
//                    userImageIcon.setImageBitmap(getRoundedCornerImage(BitmapFactory.decodeResource(cont.getResources(), R.drawable.marker_user)));
//                } else {
//                    userImageIcon.setImageBitmap(getRoundedCornerImage(BitmapFactory.decodeStream(new URL(str).openConnection().getInputStream())));
//                }
            } catch (Exception ex2) {
                ex2.printStackTrace();
                userImageIcon.setImageBitmap(getRoundedCornerImage(BitmapFactory.decodeResource(cont.getResources(), R.drawable.marker_user)));
            }
            RatingBar rb = (RatingBar) v1.findViewById(R.id.aRatingBarfg);
            ((LayerDrawable) rb.getProgressDrawable()).getDrawable(2).setColorFilter(cont.getResources().getColor(R.color.ratingcolor), PorterDuff.Mode.SRC_ATOP);
            if (!(myMarker.getUserRating().equals("") || myMarker.getUserRating().equals("null"))) {
                int creditValueInt;
                try {
                    creditValueInt = Math.round(Float.parseFloat(myMarker.getUserRating()));
                } catch (Exception e) {
                    creditValueInt = 0;
                }
                rb.setRating((float) creditValueInt);
            }
            markerLabel.setText(myMarker.getUserNameStr());
            txtReview.setText(myMarker.getUserReviewCount());
            return v1;
        }
    }


    class C12759 implements GoogleMap.OnMarkerClickListener {
        C12759() {
        }

        public boolean onMarkerClick(Marker marker) {
            try {
                if (marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                } else {
                    marker.showInfoWindow();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}
