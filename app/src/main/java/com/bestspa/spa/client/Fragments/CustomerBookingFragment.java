package com.bestspa.spa.client.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bestspa.spa.client.API.APIClient;
import com.bestspa.spa.client.API.APIInterface;
import com.bestspa.spa.client.ChooseServiceActivity;
import com.bestspa.spa.client.CustomerSettingActivity;
import com.bestspa.spa.client.Model.Booking;
import com.bestspa.spa.client.Model.ServiceModel;
import com.bestspa.spa.client.Model.SubServiceModel;
import com.bestspa.spa.client.Model.User;
import com.bestspa.spa.client.R;
import com.bestspa.spa.client.Utiles.Constant;
import com.bestspa.spa.client.Utiles.GPSTracker;
import com.bestspa.spa.client.Utiles.Links;
import com.bestspa.spa.client.Utiles.SessionManager;
import com.bestspa.spa.client.Utiles.UtilFun;
import com.bestspa.spa.client.Views.AlertDialogManager;
import com.bestspa.spa.client.Views.DotsProgressBar;
import com.facebook.appevents.AppEventsConstants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class CustomerBookingFragment extends Fragment {

    GPSTracker gpsTracker;
    Boolean mLocationPermissionGranted;
    SessionManager sessionManager;
    User user;
    final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    final String[] LOCATION_PERMISSION = new String[]{FINE_LOCATION, COARSE_LOCATION};
    APIInterface apiInterface;
    ArrayList<Booking> bookingArrayList;
    ArrayList<Booking> bookingWaitingArrayList;
    ArrayList<Booking> bookingConfirmedArrayList;
    private LinearLayout blankBookingConfirmedTxt;
    private LinearLayout blankBookingRecived;
    private TextView bookingConfirmCount;
    private TextView bookingRecivedCount;
    Context context;
    private String currentLatitue = "";
    private String currentLongitude = "";
    private LinearLayout cusBtnConfirm;
    private LinearLayout cusBtnReceived;
    private LinearLayout cusLayoutConfirmed;
    private LinearLayout cusLayoutReceived;
    private ViewPager myPagerConfirmed;
    private String newSeviceURL = "";
    private ProgressDialog pDialogPagerImage;
    private ViewPager pager;
    private int pagerPosition = 0;
    private ProgressDialog progressDialog;
    private String reloadMessage = "";
    private View rootView;
    SessionManager session;
    private TextView textConfirmed;
    private TextView textRecieved;
    private String uriPathLarge = "";
    private String userId;
    private String userName;
    private String userType;
    private VideoView videoView;
    private int istTimeFlagRecieve = 0;
    private int istTimeFlagConf;
    LinearLayout blankBookingLayoutCus;
    LinearLayout bookingDataLayout;
    User merchantUser;
    SubServiceModel subServiceModel;
    AlertDialogManager alert = new AlertDialogManager();
    LinearLayout llOpenDays;
    String[] days;
    Slider slider;
    View myView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.customer_booking_fragment_video, container, false);
        gpsTracker = new GPSTracker(getActivity());
        sessionManager = new SessionManager(getActivity());
        apiInterface = APIClient.getClient().create(APIInterface.class);
        user = sessionManager.getUserDetails();
        blankBookingLayoutCus = (LinearLayout) this.rootView.findViewById(R.id.blankBookingLayoutCus);
        bookingDataLayout = (LinearLayout) this.rootView.findViewById(R.id.bookingDataLayout);
        this.videoView = (VideoView) this.rootView.findViewById(R.id.videoView);
        apiInterface = APIClient.getClient().create(APIInterface.class);
//        UtilFun.ShowProgres(getActivity(),getString(R.string.pleasewait));
        apiInterface.GetBooking(user.get_id()).enqueue(new Callback<ArrayList<Booking>>() {
            @Override
            public void onResponse(Call<ArrayList<Booking>> call, Response<ArrayList<Booking>> response) {
                UtilFun.DismissProgress();
                if (response.code() == 200) {
                    Log.d("bookings", new Gson().toJson(response.body()));
                    bookingArrayList = response.body();
                    bookingWaitingArrayList = new ArrayList<>();
                    bookingConfirmedArrayList = new ArrayList<>();
                    if (bookingArrayList.size() > 0) {
                        for (int g = 0; g < bookingArrayList.size(); g++) {
                            if (bookingArrayList.get(g).getBookingstatus().equals(Constant.Pending)) {
                                bookingWaitingArrayList.add(bookingArrayList.get(g));

                            } else if (bookingArrayList.get(g).getBookingstatus().equals(Constant.Confirmed)) {
                                bookingConfirmedArrayList.add(bookingArrayList.get(g));
                            }
                        }
                        initBooking();
                        blankBookingLayoutCus.setVisibility(View.GONE);
                        bookingDataLayout.setVisibility(View.VISIBLE);
                    } else {
                        bookingArrayList = new ArrayList<>();
                        blankBookingLayoutCus.setVisibility(View.VISIBLE);
                        bookingDataLayout.setVisibility(View.GONE);
                        displayVideo();
                    }

                } else {
                    Toast.makeText(getActivity(), getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                    blankBookingLayoutCus.setVisibility(View.VISIBLE);
                    bookingDataLayout.setVisibility(View.GONE);
                    displayVideo();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Booking>> call, Throwable t) {
                UtilFun.DismissProgress();
                Toast.makeText(getActivity(), getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                bookingArrayList = new ArrayList<>();
                blankBookingLayoutCus.setVisibility(View.VISIBLE);
                bookingDataLayout.setVisibility(View.GONE);
                displayVideo();
            }
        });
        return rootView;
    }

    private void displayVideo() {
        this.uriPathLarge = "android.resource://" + getActivity().getPackageName() + "/raw/easy_spa_video";
        try {
            this.videoView.setVisibility(View.VISIBLE);
            this.videoView.setVideoURI(Uri.parse(this.uriPathLarge));
            this.videoView.start();
            this.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    CustomerBookingFragment.this.videoView.start();
                }
            });
        } catch (Exception e2) {
        }
    }

    private void initBooking() {
        LinearLayout bookingDataLayout = (LinearLayout) this.rootView.findViewById(R.id.bookingDataLayout);
        this.pager = (ViewPager) this.rootView.findViewById(R.id.pager);
        this.textRecieved = (TextView) this.rootView.findViewById(R.id.textRecieved);
        this.textConfirmed = (TextView) this.rootView.findViewById(R.id.textConfirmed);
        this.bookingConfirmCount = (TextView) this.rootView.findViewById(R.id.bookingConfirmCount);
        this.bookingRecivedCount = (TextView) this.rootView.findViewById(R.id.bookingRecivedCount);
        this.cusBtnConfirm = (LinearLayout) this.rootView.findViewById(R.id.cusBtnConfirm);
        this.cusBtnReceived = (LinearLayout) this.rootView.findViewById(R.id.cusBtnReceived);
        bookingDataLayout.setVisibility(View.VISIBLE);
        this.cusLayoutConfirmed = (LinearLayout) this.pager.findViewById(R.id.cusLayoutConfirmed);
        this.cusLayoutReceived = (LinearLayout) this.pager.findViewById(R.id.cusLayoutReceived);
        this.blankBookingConfirmedTxt = (LinearLayout) this.pager.findViewById(R.id.blankBookingConfirmedTxt);
        this.blankBookingRecived = (LinearLayout) this.pager.findViewById(R.id.blankBookingRecived);
        this.pDialogPagerImage = new ProgressDialog(getActivity());
        this.pDialogPagerImage.setCanceledOnTouchOutside(false);
        this.pDialogPagerImage.setCancelable(false);
        this.pDialogPagerImage.setMessage(getString(R.string.pleasewait));
//        this.pDialogPagerImage.show();


        try {
            CustomerBookingFragment.this.createCustomerBookingConfirmed();
            CustomerBookingFragment.this.createCustomerBookingReceived();
            if (CustomerBookingFragment.this.reloadMessage.equals("received")) {
                CustomerBookingFragment.this.pagerPosition = 0;
                CustomerBookingFragment.this.createReceiveView();
            } else if (CustomerBookingFragment.this.reloadMessage.equals("confirm")) {
                CustomerBookingFragment.this.pagerPosition = 1;
                CustomerBookingFragment.this.createConfirmView();
            } else {
                CustomerBookingFragment.this.pagerPosition = 0;
                CustomerBookingFragment.this.createReceiveView();
            }
        } catch (Exception e) {
            e.printStackTrace();
            CustomerBookingFragment.this.pagerPosition = 0;
        }

        this.cusBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerBookingFragment.this.pager.setCurrentItem(1);
            }
        });
        this.cusBtnReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerBookingFragment.this.pager.setCurrentItem(0);
            }
        });

        Log.d("pagerposition", String.valueOf(pagerPosition));
        try {
            this.pager.setAdapter(new WizardPagerAdapter());
            this.pager.setCurrentItem(this.pagerPosition);
            this.pager.setClipToPadding(false);
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        pager.setOnPageChangeListener(new PageChangeListener());
    }

    private void createCustomerBookingConfirmed() {

        cusLayoutConfirmed.removeAllViews();
        if (bookingConfirmedArrayList.size() > 0) {

            this.cusLayoutReceived.setVisibility(View.GONE);
            this.blankBookingConfirmedTxt.setVisibility(View.GONE);
            this.cusLayoutConfirmed.setVisibility(View.VISIBLE);

            for (int c = 0; c < bookingConfirmedArrayList.size(); c++) {
                final View vReceiv = ((LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.customer_staff_dec_template_new, null);
                final Booking bookingItem = bookingConfirmedArrayList.get(c);
                apiInterface.GetUser(bookingItem.getMerchantId()).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.code() == 200) {
                            merchantUser = response.body();
                            TextView txtBookDate = (TextView) vReceiv.findViewById(R.id.txtBookDate);
                            TextView txtBusinessName = (TextView) vReceiv.findViewById(R.id.txtBusinessName);
                            TextView txtAddress = (TextView) vReceiv.findViewById(R.id.txtAddress);
                            TextView txtCountry = (TextView) vReceiv.findViewById(R.id.txtCountry);
                            TextView txtCustHeading = (TextView) vReceiv.findViewById(R.id.txtCustHeading);
                            RatingBar aRatingBar = (RatingBar) vReceiv.findViewById(R.id.aRatingBar);
                            TextView txtCustReview = (TextView) vReceiv.findViewById(R.id.txtCustReview);
                            LinearLayout openingDataLayout = (LinearLayout) vReceiv.findViewById(R.id.openingDataLayout);
                            TextView txtDescription = (TextView) vReceiv.findViewById(R.id.txtDescription);
                            TextView txtserName = (TextView) vReceiv.findViewById(R.id.txtserName);
                            TextView txtserPrice = (TextView) vReceiv.findViewById(R.id.txtserPrice);
                            TextView txtserDesc = (TextView) vReceiv.findViewById(R.id.txtserDesc);
                            TextView txtserDuration = (TextView) vReceiv.findViewById(R.id.txtDuration);
                            final Button btnViewMapCust = (Button) vReceiv.findViewById(R.id.btnViewMap);
                            LinearLayout llBtnViewMap = (LinearLayout) vReceiv.findViewById(R.id.llBtnViewMap);
                            slider = (Slider) vReceiv.findViewById(R.id.myfivepanelpagerRec);

                            ((Button) vReceiv.findViewById(R.id.cancelBtnBooking)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });

                            String cName = "";
                            try {
                                if (merchantUser.getMerchantid().getBusinessName().equals("")) {
                                    txtBusinessName.setText("");
                                } else {
                                    txtBusinessName.setText(merchantUser.getMerchantid().getBusinessName());
                                }
                                txtBookDate.setText(getActivity().getString(R.string.appointment) + " " + bookingItem.getBookingtime() + " , " + bookingItem.getBookingdate());
                                ((DotsProgressBar) vReceiv.findViewById(R.id.dotsProgressBar)).setDotsCount(3);
                                if (bookingItem.getVisitType().equals(Constant.Visithome)) {
                                    txtCustHeading.setVisibility(View.VISIBLE);
                                    txtAddress.setText(user.getAddress());
                                    txtCountry.setText(user.getCountry());
                                } else {
                                    txtCustHeading.setVisibility(View.GONE);
                                    txtAddress.setText(merchantUser.getAddress());
                                    txtCountry.setText(merchantUser.getCountry());
                                }
                                aRatingBar.setRating(merchantUser.getMerchantid().getRating().size());
                                btnViewMapCust.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            if (bookingItem.getVisitType().equals(Constant.Visithome)) {
                                                btnViewMapCust.setVisibility(View.GONE);
                                            } else {
                                                CustomerBookingFragment.this.getActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://maps.google.com/maps?saddr=" + user.getLatLong().get(0) + "," + user.getLatLong().get(1) + "&daddr=" + merchantUser.getLatLong().get(0) + "," + merchantUser.getLatLong().get(1))));
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                txtDescription.setText(merchantUser.getMerchantid().getDescription());
                                for (int s = 0; s < CostomerHomeFragment.serviceModelArrayList.size(); s++) {
                                    for (int l = 0; l < CostomerHomeFragment.serviceModelArrayList.get(s).getSubServiceList().size(); l++) {
                                        if (CostomerHomeFragment.serviceModelArrayList.get(s).getSubServiceList().get(l).get_id().equals(bookingItem.getServiceId())) {
                                            subServiceModel = CostomerHomeFragment.serviceModelArrayList.get(s).getSubServiceList().get(l);
                                        }
                                    }
                                }
                                slider.setAdapter(new SliderAdapter() {
                                    @Override
                                    public int getItemCount() {
                                        return merchantUser.getUserImage().size();
                                    }

                                    @Override
                                    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
                                        imageSlideViewHolder.bindImageSlide(Links.URL + merchantUser.getUserImage().get(position));
                                    }
                                });
                                Locale defaultLocale = Locale.getDefault();
                                Currency currency = Currency.getInstance(defaultLocale);
                                txtserName.setText(subServiceModel.getServiceName());
                                txtserDesc.setText("  :  " + subServiceModel.getServiceDescription());
                                txtserPrice.setText("  :  " + subServiceModel.getServicePrice() + " " + currency.getSymbol());
                                txtserDuration.setText("  :  " + subServiceModel.getServiceDuration() + " mins");
                                ((RelativeLayout) vReceiv.findViewById(R.id.rlCall)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                            Intent callIntent = new Intent("android.intent.action.CALL");
                                            String phone = merchantUser.getPhone().substring(merchantUser.getPhone().lastIndexOf(" "));
                                            callIntent.setData(Uri.parse("tel:" + phone.split("-")[0] + phone.split("-")[1]));
                                            getActivity().startActivity(callIntent);
                                        } else {
                                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, Constant.CALL_PERMISSION_CODE);
                                        }
                                    }
                                });
                                ((RelativeLayout) vReceiv.findViewById(R.id.rlEmail)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        SendMail(merchantUser.getEmail());
                                    }
                                });

                                llOpenDays = (LinearLayout) vReceiv.findViewById(R.id.openingDataLayout);
                                llOpenDays.removeAllViews();
                                days = getResources().getStringArray(R.array.days);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMM/yyyy");
                                Date date = sdf.parse(bookingItem.getBookingdate());
                                SimpleDateFormat dayBumber = new SimpleDateFormat("u");
                                Calendar mydate = new GregorianCalendar();
                                mydate.setTime(date);
                                int day = mydate.get(Calendar.DAY_OF_WEEK);
                                for (int z = 0; z < 7; z++) {
                                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                                    View view = inflater.inflate(R.layout.template_opening_hours, null);
                                    ((TextView) view.findViewById(R.id.openingHrsDay)).setText(days[z]);
                                    if (z == day) {
                                        ((TextView) view.findViewById(R.id.openingHrsDay)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                        ((TextView) view.findViewById(R.id.openingHrsDate)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                    }
                                    switch (z) {
                                        case 0:
                                            ((TextView) view.findViewById(R.id.openingHrsDate)).setText(merchantUser.getMerchantid().getTime().getMonfrom() + " - " + merchantUser.getMerchantid().getTime().getMonto());
                                        case 1:
                                            ((TextView) view.findViewById(R.id.openingHrsDate)).setText(merchantUser.getMerchantid().getTime().getTuefrom() + " - " + merchantUser.getMerchantid().getTime().getTueto());
                                        case 2:
                                            ((TextView) view.findViewById(R.id.openingHrsDate)).setText(merchantUser.getMerchantid().getTime().getWedfrom() + " - " + merchantUser.getMerchantid().getTime().getWedto());
                                        case 3:
                                            ((TextView) view.findViewById(R.id.openingHrsDate)).setText(merchantUser.getMerchantid().getTime().getThufrom() + " - " + merchantUser.getMerchantid().getTime().getThuto());
                                        case 4:
                                            ((TextView) view.findViewById(R.id.openingHrsDate)).setText(merchantUser.getMerchantid().getTime().getFrifrom() + " - " + merchantUser.getMerchantid().getTime().getFrito());
                                        case 5:
                                            ((TextView) view.findViewById(R.id.openingHrsDate)).setText(merchantUser.getMerchantid().getTime().getSatfrom() + " - " + merchantUser.getMerchantid().getTime().getSatto());
                                        case 6:
                                            ((TextView) view.findViewById(R.id.openingHrsDate)).setText(merchantUser.getMerchantid().getTime().getSunfrom() + " - " + merchantUser.getMerchantid().getTime().getSunto());
                                    }

                                    llOpenDays.addView(view);
                                }
                                myView = vReceiv;
                                ((Button) vReceiv.findViewById(R.id.cancelBtnBooking)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        apiInterface.UpdateBooking(bookingItem).enqueue(new Callback<Booking>() {
                                            @Override
                                            public void onResponse(Call<Booking> call, Response<Booking> response) {
                                                if (response.code() == 200) {
                                                    Booking cancelBooking = response.body();
                                                    cusLayoutConfirmed.removeView(myView);
                                                    Toast.makeText(getActivity(), getString(R.string.cancelbookingsuccess), Toast.LENGTH_SHORT).show();
                                                    if (cusLayoutConfirmed.getChildCount() == 0) {
                                                        ((LinearLayout) pager.findViewById(R.id.blankBookingLayoutConfirmed)).setVisibility(View.VISIBLE);
                                                    }

                                                } else {

                                                    Toast.makeText(getActivity(), getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                                                }

                                            }

                                            @Override
                                            public void onFailure(Call<Booking> call, Throwable t) {
                                                Toast.makeText(getActivity(), getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                });
                                cusLayoutConfirmed.addView(vReceiv, 0, new LinearLayout.LayoutParams(-1, -1));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(getActivity(), getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(getActivity(), getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            ((LinearLayout) pager.findViewById(R.id.blankBookingLayoutConfirmed)).setVisibility(View.VISIBLE);

            this.blankBookingConfirmedTxt.setVisibility(View.GONE);
            this.cusLayoutConfirmed.setVisibility(View.GONE);
        }
    }

    private void createCustomerBookingReceived() {

        this.cusLayoutReceived.removeAllViews();

        if (bookingWaitingArrayList.size() > 0) {
            this.cusLayoutConfirmed.setVisibility(View.GONE);
            this.blankBookingRecived.setVisibility(View.GONE);
            this.cusLayoutReceived.setVisibility(View.VISIBLE);
            for (int r = 0; r < bookingWaitingArrayList.size(); r++) {
                final View vReceiv = ((LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.new_booking_receive, null);
                final Booking bookingItem = bookingWaitingArrayList.get(r);
                apiInterface.GetUser(bookingItem.getMerchantId()).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.code() == 200) {
                            merchantUser = response.body();
                            TextView txtBookDate = (TextView) vReceiv.findViewById(R.id.txtBookDate);
                            TextView txtBusinessName = (TextView) vReceiv.findViewById(R.id.txtBusinessName);
                            TextView txtAddress = (TextView) vReceiv.findViewById(R.id.txtAddress);
                            TextView txtCountry = (TextView) vReceiv.findViewById(R.id.txtCountry);
                            TextView txtCustHeading = (TextView) vReceiv.findViewById(R.id.txtCustHeading);
                            RatingBar aRatingBar = (RatingBar) vReceiv.findViewById(R.id.aRatingBar);
                            TextView txtCustReview = (TextView) vReceiv.findViewById(R.id.txtCustReview);
                            LinearLayout openingDataLayout = (LinearLayout) vReceiv.findViewById(R.id.openingDataLayout);
                            TextView txtDescription = (TextView) vReceiv.findViewById(R.id.txtDescription);
                            TextView txtserName = (TextView) vReceiv.findViewById(R.id.txtserName);
                            TextView txtserPrice = (TextView) vReceiv.findViewById(R.id.txtserPrice);
                            TextView txtserDesc = (TextView) vReceiv.findViewById(R.id.txtserDesc);
                            TextView txtserDuration = (TextView) vReceiv.findViewById(R.id.txtDuration);
                            final Button btnViewMapCust = (Button) vReceiv.findViewById(R.id.btnViewMap);
                            LinearLayout llBtnViewMap = (LinearLayout) vReceiv.findViewById(R.id.llBtnViewMap);
                            slider = (Slider) vReceiv.findViewById(R.id.myfivepanelpagerRec);


                            String cName = "";
                            try {
                                if (merchantUser.getMerchantid().getBusinessName().equals("")) {
                                    txtBusinessName.setText("");
                                } else {
                                    txtBusinessName.setText(merchantUser.getMerchantid().getBusinessName());
                                }
                                txtBookDate.setText(getActivity().getString(R.string.appointment) + " On " + bookingItem.getBookingtime() + " , " + bookingItem.getBookingdate());
                                ((DotsProgressBar) vReceiv.findViewById(R.id.dotsProgressBar)).setDotsCount(3);
                                if (bookingItem.getVisitType().equals(Constant.Visithome)) {
                                    txtCustHeading.setVisibility(View.VISIBLE);
                                    txtAddress.setText(user.getAddress());
                                    txtCountry.setText(user.getCountry());
                                } else {
                                    txtCustHeading.setVisibility(View.GONE);
                                    txtAddress.setText(merchantUser.getAddress());
                                    txtCountry.setText(merchantUser.getCountry());
                                }
                                aRatingBar.setRating(merchantUser.getMerchantid().getRating().size());
                                btnViewMapCust.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            if (bookingItem.getVisitType().equals(Constant.Visithome)) {
                                                btnViewMapCust.setVisibility(View.GONE);
                                            } else {
                                                CustomerBookingFragment.this.getActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://maps.google.com/maps?saddr=" + user.getLatLong().get(0) + "," + user.getLatLong().get(1) + "&daddr=" + merchantUser.getLatLong().get(0) + "," + merchantUser.getLatLong().get(1))));
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                txtDescription.setText(merchantUser.getMerchantid().getDescription());
                                for (int s = 0; s < CostomerHomeFragment.serviceModelArrayList.size(); s++) {
                                    for (int l = 0; l < CostomerHomeFragment.serviceModelArrayList.get(s).getSubServiceList().size(); l++) {
                                        if (CostomerHomeFragment.serviceModelArrayList.get(s).getSubServiceList().get(l).get_id().equals(bookingItem.getServiceId())) {
                                            subServiceModel = CostomerHomeFragment.serviceModelArrayList.get(s).getSubServiceList().get(l);
                                        }
                                    }
                                }
                                slider.setAdapter(new SliderAdapter() {
                                    @Override
                                    public int getItemCount() {
                                        return merchantUser.getUserImage().size();
                                    }

                                    @Override
                                    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
                                        imageSlideViewHolder.bindImageSlide(Links.URL + merchantUser.getUserImage().get(position));
                                    }
                                });
                                Locale defaultLocale = Locale.getDefault();
                                Currency currency = Currency.getInstance(defaultLocale);
                                txtserName.setText(subServiceModel.getServiceName());
                                txtserDesc.setText("  :  " + subServiceModel.getServiceDescription());
                                txtserPrice.setText("  :  " + subServiceModel.getServicePrice() + " " + currency.getSymbol());
                                txtserDuration.setText("  :  " + subServiceModel.getServiceDuration() + " mins");
                                ((RelativeLayout) vReceiv.findViewById(R.id.rlCall)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                            Intent callIntent = new Intent("android.intent.action.CALL");
                                            String phone = merchantUser.getPhone().substring(merchantUser.getPhone().lastIndexOf(" "));
                                            callIntent.setData(Uri.parse("tel:" + phone.split("-")[0] + phone.split("-")[1]));
                                            getActivity().startActivity(callIntent);
                                        } else {
                                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, Constant.CALL_PERMISSION_CODE);
                                        }
                                    }
                                });
                                ((RelativeLayout) vReceiv.findViewById(R.id.rlEmail)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        SendMail(merchantUser.getEmail());
                                    }
                                });

                                llOpenDays = (LinearLayout) vReceiv.findViewById(R.id.openingDataLayout);
                                llOpenDays.removeAllViews();
                                days = getResources().getStringArray(R.array.days);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy");
                                Date date = sdf.parse(bookingItem.getBookingdate());
                                SimpleDateFormat dayBumber = new SimpleDateFormat("u");
                                Calendar mydate = new GregorianCalendar();
                                mydate.setTime(date);
                                int day = mydate.get(Calendar.DAY_OF_WEEK);
                                for (int z = 0; z < 7; z++) {
                                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                                    View view = inflater.inflate(R.layout.template_opening_hours, null);
                                    ((TextView) view.findViewById(R.id.openingHrsDay)).setText(days[z]);
                                    if (z == day) {
                                        ((TextView) view.findViewById(R.id.openingHrsDay)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                        ((TextView) view.findViewById(R.id.openingHrsDate)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                    }
                                    switch (z) {
                                        case 0:
                                            ((TextView) view.findViewById(R.id.openingHrsDate)).setText(merchantUser.getMerchantid().getTime().getMonfrom() + " - " + merchantUser.getMerchantid().getTime().getMonto());
                                        case 1:
                                            ((TextView) view.findViewById(R.id.openingHrsDate)).setText(merchantUser.getMerchantid().getTime().getTuefrom() + " - " + merchantUser.getMerchantid().getTime().getTueto());
                                        case 2:
                                            ((TextView) view.findViewById(R.id.openingHrsDate)).setText(merchantUser.getMerchantid().getTime().getWedfrom() + " - " + merchantUser.getMerchantid().getTime().getWedto());
                                        case 3:
                                            ((TextView) view.findViewById(R.id.openingHrsDate)).setText(merchantUser.getMerchantid().getTime().getThufrom() + " - " + merchantUser.getMerchantid().getTime().getThuto());
                                        case 4:
                                            ((TextView) view.findViewById(R.id.openingHrsDate)).setText(merchantUser.getMerchantid().getTime().getFrifrom() + " - " + merchantUser.getMerchantid().getTime().getFrito());
                                        case 5:
                                            ((TextView) view.findViewById(R.id.openingHrsDate)).setText(merchantUser.getMerchantid().getTime().getSatfrom() + " - " + merchantUser.getMerchantid().getTime().getSatto());
                                        case 6:
                                            ((TextView) view.findViewById(R.id.openingHrsDate)).setText(merchantUser.getMerchantid().getTime().getSunfrom() + " - " + merchantUser.getMerchantid().getTime().getSunto());
                                    }

                                    llOpenDays.addView(view);
                                }

                                myView = vReceiv;
                                bookingItem.setBookingstatus(Constant.Canceled);
                                ((Button) vReceiv.findViewById(R.id.btnCancelCust)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        apiInterface.UpdateBooking(bookingItem).enqueue(new Callback<Booking>() {
                                            @Override
                                            public void onResponse(Call<Booking> call, Response<Booking> response) {
                                                if (response.code() == 200) {
                                                    Booking cancelBooking = response.body();
                                                    cusLayoutReceived.removeView(myView);
                                                    Toast.makeText(getActivity(), getString(R.string.cancelbookingsuccess), Toast.LENGTH_SHORT).show();
                                                    if (cusLayoutReceived.getChildCount() == 0) {
                                                        ((LinearLayout) pager.findViewById(R.id.blankBookingLayoutReceived)).setVisibility(View.VISIBLE);
                                                    }

                                                } else {

                                                    Toast.makeText(getActivity(), getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                                                }

                                            }

                                            @Override
                                            public void onFailure(Call<Booking> call, Throwable t) {
                                                Toast.makeText(getActivity(), getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                });

                                cusLayoutReceived.addView(vReceiv, 0, new LinearLayout.LayoutParams(-1, -1));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(getActivity(), getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(getActivity(), getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        } else {
            this.blankBookingRecived.setVisibility(View.GONE);
            this.cusLayoutReceived.setVisibility(View.GONE);
            ((LinearLayout) pager.findViewById(R.id.blankBookingLayoutReceived)).setVisibility(View.VISIBLE);
        }
    }

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
            this.alert.showAlertDialog(getActivity(), getResources().getString(R.string.error), getResources().getString(R.string.nomail), Boolean.valueOf(false));
        }
    }

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
//                    getLocation();

                } else {
                    ActivityCompat.requestPermissions(getActivity(), LOCATION_PERMISSION, Constant.REQUEST_FINE_LOCATION_CODE);
                }
            }
            case Constant.CALL_PERMISSION_CODE: {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, Constant.CALL_PERMISSION_CODE);

                }

            }
        }
    }


    class PageChangeListener implements ViewPager.OnPageChangeListener {

        public void onPageSelected(int position) {
            if (position == 0) {
                if (CustomerBookingFragment.this.istTimeFlagRecieve == 0) {
                    CustomerBookingFragment.this.pDialogPagerImage.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            CustomerBookingFragment.this.createReceiveView();
                        }
                    }, 1000);
                    return;
                }
                CustomerBookingFragment.this.createReceiveView();
            } else if (position != 1) {
            } else {
                if (CustomerBookingFragment.this.istTimeFlagConf == 0) {
                    CustomerBookingFragment.this.pDialogPagerImage.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            CustomerBookingFragment.this.createConfirmView();
                        }
                    }, 1000);
                    return;
                }
                CustomerBookingFragment.this.createConfirmView();
            }
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageScrollStateChanged(int arg0) {
        }
    }

    private void createConfirmView() {
        try {
            this.cusBtnConfirm = (LinearLayout) this.rootView.findViewById(R.id.cusBtnConfirm);
            this.cusBtnReceived = (LinearLayout) this.rootView.findViewById(R.id.cusBtnReceived);


//            this.cusBtnConfirm.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkToo));
//            this.cusBtnReceived.setBackgroundColor(getResources().getColor(R.color.btngreen));
            this.textRecieved.setTextColor(getResources().getColor(R.color.black));
            this.textConfirmed.setTextColor(getResources().getColor(R.color.white));
            this.bookingConfirmCount.setBackgroundResource(R.drawable.circle);
            this.bookingRecivedCount.setBackgroundResource(R.drawable.check_circle);
            this.bookingConfirmCount.setTextColor(getResources().getColor(R.color.black));
            this.bookingRecivedCount.setTextColor(getResources().getColor(R.color.white));

            if (this.istTimeFlagConf == 0 && (this.pDialogPagerImage.isShowing() || this.pDialogPagerImage != null)) {
                this.pDialogPagerImage.dismiss();
            }
            this.istTimeFlagConf = 1;
        } catch (Exception e) {
        }
    }

    private void createReceiveView() {
        try {
            this.cusBtnConfirm = (LinearLayout) this.rootView.findViewById(R.id.cusBtnConfirm);
            this.cusBtnReceived = (LinearLayout) this.rootView.findViewById(R.id.cusBtnReceived);

//            this.cusBtnConfirm.setBackgroundColor(getResources().getColor(R.color.btngreen));
//            this.cusBtnReceived.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkToo));
            this.textRecieved.setTextColor(getResources().getColor(R.color.white));
            this.textConfirmed.setTextColor(getResources().getColor(R.color.black));
            this.bookingConfirmCount.setBackgroundResource(R.drawable.check_circle);
            this.bookingRecivedCount.setBackgroundResource(R.drawable.circle);
            this.bookingConfirmCount.setTextColor(getResources().getColor(R.color.white));
            this.bookingRecivedCount.setTextColor(getResources().getColor(R.color.black));


            if (this.istTimeFlagRecieve == 0 && (this.pDialogPagerImage.isShowing() || this.pDialogPagerImage != null)) {
                this.pDialogPagerImage.dismiss();
            }
            this.istTimeFlagRecieve = 1;
        } catch (Exception e) {
        }
    }

    class WizardPagerAdapter extends PagerAdapter {
        WizardPagerAdapter() {
        }

        public Object instantiateItem(View collection, int position) {
            return CustomerBookingFragment.this.pager.getChildAt(position);
        }

        public int getCount() {
            return 2;
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
        }
    }
}
