package com.bestspa.spa.client.map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bestspa.spa.client.Model.User;
import com.bestspa.spa.client.R;
import com.bestspa.spa.client.Utiles.Constant;
import com.bestspa.spa.client.Utiles.DatabaseHelper;
import com.bestspa.spa.client.Utiles.SessionManager;
import com.facebook.appevents.AppEventsConstants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ShowInMap extends FragmentActivity implements OnMapReadyCallback {
    private String averageRating = AppEventsConstants.EVENT_PARAM_VALUE_NO;
    private String currentLatitue = "";
    private String currentLongitude = "";
    private float currentZoom = 12.0f;
    DatabaseHelper db;
    SupportMapFragment fm;
    private HashMap<Marker, CustomMarker> mMarkersHashMap;
    private String reviewCount = AppEventsConstants.EVENT_PARAM_VALUE_NO;
    SessionManager session;
    private String staffIdName = AppEventsConstants.EVENT_PARAM_VALUE_NO;
    private String userId;
    private ByteArrayInputStream userImageStream = null;
    private String userType = "";

    /* renamed from: com.spa.easyspa.map.ShowInMap$1 */
    class C13321 implements OnClickListener {
        C13321() {
        }

        public void onClick(View arg0) {
            ShowInMap.this.finish();
        }
    }

    /* renamed from: com.spa.easyspa.map.ShowInMap$2 */
    class C13332 implements OnClickListener {
        C13332() {
        }

        public void onClick(View arg0) {
            ShowInMap.this.finish();
        }
    }

    /* renamed from: com.spa.easyspa.map.ShowInMap$3 */
    class C13343 implements OnCameraChangeListener {
        C13343() {
        }

        public void onCameraChange(CameraPosition position) {
            if (position.zoom != ShowInMap.this.currentZoom) {
                ShowInMap.this.currentZoom = position.zoom;
            }
        }
    }

    /* renamed from: com.spa.easyspa.map.ShowInMap$4 */
    class C13354 implements OnMarkerClickListener {
        C13354() {
        }

        public boolean onMarkerClick(Marker marker) {
            Log.e("TESTING", "on Marker click: " + marker.getTitle());
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

    public class MarkerInfoWindowAdapter implements InfoWindowAdapter {
        public View getInfoWindow(Marker marker) {
            return null;
        }

        public View getInfoContents(Marker marker) {
            View v1 = ShowInMap.this.getLayoutInflater().inflate(R.layout.activity_custom_marker, null);
            CustomMarker myMarker = (CustomMarker) ShowInMap.this.mMarkersHashMap.get(marker);
            TextView markerLabel = (TextView) v1.findViewById(R.id.aUserNameTxt);
            TextView txtReview = (TextView) v1.findViewById(R.id.txtReview);
            ImageView userImageIcon = (ImageView) v1.findViewById(R.id.userImage);
            try {
                if (ShowInMap.this.userImageStream != null) {
                    userImageIcon.setImageBitmap(ShowInMap.this.getRoundedCornerImage(BitmapFactory.decodeStream(ShowInMap.this.userImageStream)));
                } else {
                    userImageIcon.setBackgroundResource(R.drawable.marker_user);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                userImageIcon.setBackgroundResource(R.drawable.marker_user);
            }
            RatingBar rb = (RatingBar) v1.findViewById(R.id.aRatingBarfg);
            ((LayerDrawable) rb.getProgressDrawable()).getDrawable(2).setColorFilter(ShowInMap.this.getApplicationContext().getResources().getColor(R.color.ratingcolor), Mode.SRC_ATOP);
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

    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new Builder().permitAll().build());
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_map_show);
        this.session = new SessionManager(getApplicationContext());
        this.db = DatabaseHelper.getInstance(getApplicationContext());
        if (this.session.isLoggedIn()) {
            User user = this.session.getUserDetails();
            this.userType = (String) user.getUserTypes();
            this.userId = (String) user.getUserTypes();
        }
        ((LinearLayout) findViewById(R.id.layoutHead)).setOnClickListener(new C13321());
        this.fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        this.fm.getMapAsync(this);
        Intent in = getIntent();
        this.currentLatitue = in.getStringExtra("staffLatitude");
        this.currentLongitude = in.getStringExtra("staffLongitude");
        if (in.getStringExtra("status").equals(AppEventsConstants.EVENT_PARAM_VALUE_NO)) {
            String bookingIDM = in.getStringExtra("bookingIDM");
//            for (Notification notificationObj : this.db.getAllToNotificationForMap(this.userId, bookingIDM)) {
//                this.averageRating = notificationObj.getUserRating();
//                this.reviewCount = notificationObj.getUserReviews();
//                try {
//                    this.userImageStream = new ByteArrayInputStream(this.db.getNotificationUserImage(this.userId, bookingIDM, this.userType));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (this.userType.equals("customer")) {
//                    this.staffIdName = notificationObj.getSpaUserName();
//                } else if (this.userType.equals("staff")) {
//                    this.staffIdName = notificationObj.getCustomerName();
//                }
//            }
        }
        ((Button) findViewById(R.id.btnBack)).setOnClickListener(new C13332());
    }

    @SuppressLint("MissingPermission")
    public void onMapReady(GoogleMap map) {
        map.setMyLocationEnabled(true);
        map.setMapType(1);
        ArrayList<CustomMarker> mMyMarkersArray = new ArrayList();
        double latitude = Double.parseDouble(this.currentLatitue);
        double longitude = Double.parseDouble(this.currentLongitude);
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        map.animateCamera(CameraUpdateFactory.zoomTo(this.currentZoom));
        map.setOnCameraChangeListener(new C13343());
//        if (this.userType.equals("customer")) {
//            mMyMarkersArray.add(new CustomMarker(Double.valueOf(latitude), Double.valueOf(longitude), AppEventsConstants.EVENT_PARAM_VALUE_YES, this.staffIdName, this.averageRating, this.reviewCount, ));
//        } else if (this.userType.equals("staff")) {
//            mMyMarkersArray.add(new CustomMarker(Double.valueOf(latitude), Double.valueOf(longitude), AppEventsConstants.EVENT_PARAM_VALUE_YES, this.staffIdName, this.averageRating, this.reviewCount, ""));
//        }
//        plotMarkers(mMyMarkersArray, map);
    }

    private void plotMarkers(ArrayList<CustomMarker> markers, GoogleMap customMap) {
        if (markers.size() > 0) {
            this.mMarkersHashMap = new HashMap();
            Iterator it = markers.iterator();
            while (it.hasNext()) {
                CustomMarker myMarker = (CustomMarker) it.next();
                MarkerOptions markerOption = new MarkerOptions().position(new LatLng(myMarker.getLatitudeStr().doubleValue(), myMarker.getLongitudeStr().doubleValue()));
                if (this.userType.equals("customer")) {
                    markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.staff_map_icon_40));
                } else if (this.userType.equals("staff")) {
                    markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.cust_icon_30));
                }
                this.mMarkersHashMap.put(customMap.addMarker(markerOption), myMarker);
                customMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
                customMap.setOnMarkerClickListener(new C13354());
            }
        }
    }

    private Bitmap getRoundedCornerImage(Bitmap bitmap) {
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
        Bitmap targetBitmap = Bitmap.createBitmap(50, 50, Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, targetBitmap.getWidth(), targetBitmap.getHeight());
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#65A657"));
        canvas.drawRoundRect(rectF, 60.0f, 60.0f, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(resized, rect, rect, paint);
        if (bitmap !=null)
        {
            return targetBitmap;

        }
        resized = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.marker_user), 50, 50, true);
        targetBitmap = Bitmap.createBitmap(50, 50, Config.ARGB_8888);
        canvas = new Canvas(targetBitmap);
        paint = new Paint();
        rect = new Rect(0, 0, targetBitmap.getWidth(), targetBitmap.getHeight());
        rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#65A657"));
        canvas.drawRoundRect(rectF, 60.0f, 60.0f, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(resized, rect, rect, paint);
        canvas.drawBitmap(resized, rect, rect, paint);
        return targetBitmap;
    }
}
