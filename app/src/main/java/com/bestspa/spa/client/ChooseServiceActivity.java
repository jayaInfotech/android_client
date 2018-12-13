package com.bestspa.spa.client;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bestspa.spa.client.API.APIClient;
import com.bestspa.spa.client.API.APIInterface;
import com.bestspa.spa.client.Model.User;
import com.bestspa.spa.client.Utiles.Constant;
import com.bestspa.spa.client.Utiles.GPSTracker;
import com.bestspa.spa.client.Utiles.Links;
import com.bestspa.spa.client.Utiles.SessionManager;
import com.bestspa.spa.client.Views.AlertDialogManager;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class ChooseServiceActivity extends AppCompatActivity {


    SessionManager sessionManager;
    GPSTracker gpsTracker;
    Intent intent;
    public static User userMerchant;
    APIInterface apiInterface;
    public static String merchantId,serviceId;
    String latitude,longitude;
    Double currentLatitue,currentLongitude;
    Boolean mLocationPermissionGranted;
    final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    final String[] LOCATION_PERMISSION = new String[]{FINE_LOCATION,COARSE_LOCATION};
    Slider simpleViewPager;
    LinearLayout llChooseTret,llSocial,llWebsite,llFacebook,llOpenDays;
    AlertDialogManager alert = new AlertDialogManager();
    String[] days ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_service);

        merchantId = getIntent().getStringExtra(Constant.MerchantID);
        serviceId = getIntent().getStringExtra(Constant.ServiceId);

        Log.d("merchantId",merchantId);
        Log.d("serviceId",serviceId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
        {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        LOCATION_PERMISSION,
                        Constant.REQUEST_FINE_LOCATION_CODE);
            } else {
                mLocationPermissionGranted = true;
                getLocation();
                Log.e("DB", "PERMISSION GRANTED");
            }
            getLocation();
            mLocationPermissionGranted = true;
        }

        sessionManager = new SessionManager(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        apiInterface.GetUser(merchantId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200)
                {
                    userMerchant = response.body();
                    initData();
                }else
                {
                    Toast.makeText(ChooseServiceActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ChooseServiceActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initData() {

        ((TextView)findViewById(R.id.txtBusinessName)).setText(userMerchant.getMerchantid().getBusinessName());
        ((TextView)findViewById(R.id.txtAddress)).setText(userMerchant.getAddress());
        ((TextView)findViewById(R.id.txtCountry)).setText(userMerchant.getCountry());
        ((RatingBar)findViewById(R.id.aRatingBar)).setRating(userMerchant.getMerchantid().getRating().size());
        ((Button)findViewById(R.id.btnViewMap)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://maps.google.com/maps?saddr=" + ChooseServiceActivity.this.currentLatitue + "," + ChooseServiceActivity.this.currentLongitude + "&daddr=" + latitude + "," + longitude + "")));
            }
        });
        simpleViewPager = (Slider) findViewById(R.id.simple_view_pager);
        simpleViewPager.setAdapter(new MainSliderAdapter());
        ((TextView)findViewById(R.id.txtDescription)).setText(userMerchant.getMerchantid().getDescription());
        llChooseTret = (LinearLayout) findViewById(R.id.llChooseTret);
        llChooseTret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ((RelativeLayout)findViewById(R.id.rlCall)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userMerchant.getPhone().contains("null")) {
                    Toast.makeText(ChooseServiceActivity.this.getApplicationContext(), "Phone no incorrect or have no phone number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ActivityCompat.checkSelfPermission(ChooseServiceActivity.this,Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                {
                    Intent callIntent = new Intent("android.intent.action.CALL");
                    String phone = userMerchant.getPhone().substring(userMerchant.getPhone().lastIndexOf(" "));
                    callIntent.setData(Uri.parse("tel:" +phone.split("-")[0]+phone.split("-")[1]));
                    ChooseServiceActivity.this.startActivity(callIntent);
                }else
                {
                    ActivityCompat.requestPermissions(ChooseServiceActivity.this,new String[]{Manifest.permission.CALL_PHONE},Constant.CALL_PERMISSION_CODE);
                }

            }
        });
        ((RelativeLayout)findViewById(R.id.rlEmail)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMail(userMerchant.getEmail());
            }
        });
        llSocial = (LinearLayout)findViewById(R.id.llSocialMain);
        llWebsite = (LinearLayout)findViewById(R.id.llWebsite);
        llFacebook = (LinearLayout)findViewById(R.id.llFb);

        try{
            if (!userMerchant.getMerchantid().getWebsiteLink().equals(""))
            {
               llWebsite.setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.txtWebsite)).setText(userMerchant.getMerchantid().getWebsiteLink());
            }

        }catch (Exception e)
        {
            llWebsite.setVisibility(View.GONE);
            e.printStackTrace();
        }

        try{
            if (userMerchant.getMerchantid().getFacebookLink().equals(""))
            {
                llFacebook.setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.txtFb)).setText(userMerchant.getMerchantid().getFacebookLink());
            }

        }catch (Exception e)
        {
            llFacebook.setVisibility(View.GONE);
            e.printStackTrace();
        }

        llOpenDays = (LinearLayout) findViewById(R.id.openingDataLayout);
        llOpenDays.removeAllViews();
        days = getResources().getStringArray(R.array.days);
        for (int z = 0; z < 7 ; z++) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.template_opening_hours,null);
            ((TextView)view.findViewById(R.id.openingHrsDay)).setText(days[z]);
            switch (z)
            {
                case 0:((TextView)view.findViewById(R.id.openingHrsDate)).setText(userMerchant.getMerchantid().getTime().getMonfrom()+" - "+userMerchant.getMerchantid().getTime().getMonto());
                case 1:((TextView)view.findViewById(R.id.openingHrsDate)).setText(userMerchant.getMerchantid().getTime().getTuefrom()+" - "+userMerchant.getMerchantid().getTime().getTueto());
                case 2:((TextView)view.findViewById(R.id.openingHrsDate)).setText(userMerchant.getMerchantid().getTime().getWedfrom()+" - "+userMerchant.getMerchantid().getTime().getWedto());
                case 3:((TextView)view.findViewById(R.id.openingHrsDate)).setText(userMerchant.getMerchantid().getTime().getThufrom()+" - "+userMerchant.getMerchantid().getTime().getThuto());
                case 4:((TextView)view.findViewById(R.id.openingHrsDate)).setText(userMerchant.getMerchantid().getTime().getFrifrom()+" - "+userMerchant.getMerchantid().getTime().getFrito());
                case 5:((TextView)view.findViewById(R.id.openingHrsDate)).setText(userMerchant.getMerchantid().getTime().getSatfrom()+" - "+userMerchant.getMerchantid().getTime().getSatto());
                case 6:((TextView)view.findViewById(R.id.openingHrsDate)).setText(userMerchant.getMerchantid().getTime().getSunfrom()+" - "+userMerchant.getMerchantid().getTime().getSunto());
            }
            llOpenDays.addView(view);
        }

        ((Button)findViewById(R.id.btnChooseNext)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ChooseServiceActivity.this,BookingScheduleActivity.class);
                startActivity(intent);

            }
        });
    }

//    private void openChooseTreatdialog(String treatment, String cid) {
//        Dialog browseDialogImg = new Dialog(this);
//        browseDialogImg.requestWindowFeature(1);
//        browseDialogImg.setContentView(R.layout.choose_service_dialog);
//        browseDialogImg.getWindow().getAttributes().windowAnimations = R.style.DialogUpDown;
//        browseDialogImg.show();
//        String currencyCodeSym = "";
//        String currencyCode = "";
//
//        ListView lv = (ListView) browseDialogImg.findViewById(R.id.list);
//        lv.setAdapter(new SimpleAdapter(this, this.alTreatList, R.layout.list_item_service, new String[]{"name", "dis", "desc"}, new int[]{R.id.name, R.id.distance_treat, R.id.txtDescDialog}));
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//
//            }
//        });
//    }
    
    
    private void SendMail(String toMail) {
        Intent emailIntent = new Intent("android.intent.action.SEND");
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra("android.intent.extra.EMAIL", new String[]{toMail});
        emailIntent.putExtra("android.intent.extra.SUBJECT", getString(R.string.umaspa_staff));
        emailIntent.putExtra("android.intent.extra.TEXT", " ");
        try {
            startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.sentmail)));
        } catch (ActivityNotFoundException e) {
            this.alert.showAlertDialog(this, getResources().getString(R.string.error), getResources().getString(R.string.nomail), Boolean.valueOf(false));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.CALL_PERMISSION_CODE)
        {

        }
    }


    public void getLocation()
    {
        gpsTracker = new GPSTracker(this);
        currentLatitue = gpsTracker.getLatitude();
        currentLongitude = gpsTracker.getLongitude();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case Constant.REQUEST_FINE_LOCATION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getLocation();

                } else {
                    ActivityCompat.requestPermissions(this, LOCATION_PERMISSION, Constant.REQUEST_FINE_LOCATION_CODE);
                }
            }
            case Constant.CALL_PERMISSION_CODE:{

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                }else
                {
                    ActivityCompat.requestPermissions(ChooseServiceActivity.this,new String[]{Manifest.permission.CALL_PHONE},Constant.CALL_PERMISSION_CODE);

                }

            }
        }
    }

    public class MainSliderAdapter extends SliderAdapter {

        @Override
        public int getItemCount() {
            return userMerchant.getUserImage().size();
        }

        @Override
        public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {
            viewHolder.bindImageSlide(Links.URL+userMerchant.getUserImage().get(position));
        }
    }
}
