package com.bestspa.spa.client.Fragments;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bestspa.spa.client.API.APIClient;
import com.bestspa.spa.client.API.APIInterface;
import com.bestspa.spa.client.Model.Booking;
import com.bestspa.spa.client.Model.ServiceUserModel;
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
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffBookingFragment extends Fragment {

    View rootView;
    GPSTracker gps;
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
    private LinearLayout staffBtnAwaiting;
    private LinearLayout staffBtnConfirm;
    private LinearLayout staffLayoutAwaiting;
    private LinearLayout staffLayoutConfirmed;
    private TextView staffTxtVisibleAwait;
    private TextView staffTxtVisibleConfirm;
    private TextView textAwt;
    private TextView textConfirmed;
    int totalSize;
    private String uriPathLarge = "";
    private String userId;
    private String userName;
    private String userType;
    private VideoView videoView;
    LinearLayout blankBookingStaff, bookingDataLayoutStaff;
    private TextView bookingAwaitingCountStaff;
    private TextView bookingConfirmCountStaff;
    ViewPager pager;
    private int pagerPosition = 0;
    DotsProgressBar dotsProgressBarUnder, progressBar;
    LinearLayout llProgressTopReceived;
    SubServiceModel subServiceModel;
    User custUser;
    ServiceModel serviceModel;
    AlertDialogManager alert = new AlertDialogManager();
    String currentLatitue, currentLongitude;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.staff_fragment_booking_video, container, false);

        Log.d("fragemtn", "StaffBookingFragment_in");

        gps = new GPSTracker(getActivity());
        sessionManager = new SessionManager(getActivity());
        apiInterface = APIClient.getClient().create(APIInterface.class);
        user = sessionManager.getUserDetails();
        if (ActivityCompat.checkSelfPermission(getActivity(), FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), LOCATION_PERMISSION, Constant.REQUEST_FINE_LOCATION_CODE);
        } else {
            mLocationPermissionGranted = true;
            getLocation();
        }

        initBooking();
        bookingArrayList = new ArrayList<>();
//        UtilFun.ShowProgres(getActivity(),getString(R.string.pleasewait));
        apiInterface.GetMerchantBooking(user.get_id()).enqueue(new Callback<ArrayList<Booking>>() {
            @Override
            public void onResponse(Call<ArrayList<Booking>> call, Response<ArrayList<Booking>> response) {
                UtilFun.DismissProgress();
                if (response.code() == 200) {
                    bookingArrayList = response.body();
                    Log.d("staffBooking", response.body().toString());

                    if (bookingArrayList.size() > 0) {
                        blankBookingStaff.setVisibility(View.GONE);
                        bookingDataLayoutStaff.setVisibility(View.VISIBLE);
                        videoView.setVisibility(View.GONE);

                        bookingConfirmedArrayList = new ArrayList<>();
                        bookingWaitingArrayList = new ArrayList<>();
                        for (int i = 0; i < bookingArrayList.size(); i++) {
                            if (bookingArrayList.get(i).getBookingstatus().equals(Constant.Pending)) {
                                bookingWaitingArrayList.add(bookingArrayList.get(i));

                            } else if (bookingArrayList.get(i).getBookingstatus().equals(Constant.Confirmed)) {
                                bookingConfirmedArrayList.add(bookingArrayList.get(i));
                            }
                        }
                        if (bookingWaitingArrayList.size() > 0) {
                            pagerPosition = 1;
                            CreateAwaitView();
                            Log.d("calling", "bookingWaitingArrayList");

                        } else if (bookingConfirmedArrayList.size() > 0) {
                            pagerPosition = 0;
                            CreateConfirmView();

                        } else {
                            initVideoView();
                        }
                        if (bookingWaitingArrayList.size() == 0) {
                            ((LinearLayout) pager.findViewById(R.id.blankBookingLayoutReceived)).setVisibility(View.VISIBLE);
                        }
                        if (bookingConfirmedArrayList.size() == 0) {
                            ((LinearLayout) pager.findViewById(R.id.blankBookingLayoutConfirmed)).setVisibility(View.VISIBLE);
                        }
                        createStaffBookingConfirm();
                        createStaffBookingAwaiting();

                        try {
                            pager.setAdapter(new WizardPagerAdapter());
                            pager.setCurrentItem(pagerPosition);
                            pager.setClipToPadding(false);
                            pager.setOnPageChangeListener(new C11742());
                        } catch (Exception e32) {
                            e32.printStackTrace();
                        }
                    } else {
//                        initVideoView();

                    }

                } else {

                }
            }

            @Override
            public void onFailure(Call<ArrayList<Booking>> call, Throwable t) {
                UtilFun.DismissProgress();
                Log.d("staffBookingFailer", t.getMessage());
                initVideoView();
            }
        });

        return rootView;
    }

    private void initBooking() {
        this.videoView = (VideoView) this.rootView.findViewById(R.id.videoView);
        blankBookingStaff = (LinearLayout) this.rootView.findViewById(R.id.blankBookingStaff);
        bookingDataLayoutStaff = (LinearLayout) this.rootView.findViewById(R.id.bookingDataLayoutStaff);
        this.pager = (ViewPager) this.rootView.findViewById(R.id.pager);
        this.staffBtnConfirm = (LinearLayout) this.rootView.findViewById(R.id.staffBtnConfirm);
        this.staffBtnAwaiting = (LinearLayout) this.rootView.findViewById(R.id.staffBtnAwaiting);
        this.staffLayoutAwaiting = (LinearLayout) this.pager.findViewById(R.id.staffLayoutAwaiting);
        this.staffLayoutConfirmed = (LinearLayout) this.pager.findViewById(R.id.staffLayoutConfirmed);
        this.staffTxtVisibleConfirm = (TextView) this.rootView.findViewById(R.id.staffTxtVisibleConfirm);
        this.staffTxtVisibleAwait = (TextView) this.rootView.findViewById(R.id.staffTxtVisibleAwait);
        this.staffTxtVisibleConfirm.setBackgroundColor(getResources().getColor(R.color.latest_body_text_grey));
        this.staffTxtVisibleAwait.setBackgroundColor(getResources().getColor(R.color.green));
        this.textConfirmed = (TextView) this.rootView.findViewById(R.id.textConfirmed);
        this.textAwt = (TextView) this.rootView.findViewById(R.id.textAwt);
        this.bookingConfirmCountStaff = (TextView) this.rootView.findViewById(R.id.bookingConfirmCountStaff);

        this.staffBtnAwaiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(1);
            }
        });
        this.staffBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(0);
            }
        });


    }

    public void initVideoView() {
        blankBookingStaff.setVisibility(0);
        bookingDataLayoutStaff.setVisibility(8);
        this.uriPathLarge = "android.resource://" + getActivity().getPackageName() + "/raw/easy_spa_video";
        try {
            this.videoView.setVisibility(0);
            this.videoView.setVideoURI(Uri.parse(this.uriPathLarge));
            this.videoView.start();
            this.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    videoView.start();
                }
            });
        } catch (Exception e2) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bookingArrayList.size() == 0) {
            initVideoView();
        }
    }

    private void CreateConfirmView() {
        this.staffLayoutConfirmed.setVisibility(0);
        this.staffLayoutAwaiting.setVisibility(8);
//        this.staffBtnConfirm.setBackgroundColor(getResources().getColor(R.color.white));
//        this.staffBtnAwaiting.setBackgroundColor(getResources().getColor(R.color.btngreen));
        this.textConfirmed.setTextColor(getResources().getColor(R.color.white));
        this.textAwt.setTextColor(getResources().getColor(R.color.black));

//        this.textAwt.setTextColor(getResources().getColor(R.color.white));
//        this.bookingConfirmCountStaff.setTextColor(getResources().getColor(R.color.black));
//        this.bookingAwaitingCountStaff.setTextColor(getResources().getColor(R.color.white));
//        this.bookingConfirmCountStaff.setBackgroundResource(R.drawable.circle);
//        this.bookingAwaitingCountStaff.setBackgroundResource(R.drawable.check_circle);
    }

    private void CreateAwaitView() {
        this.staffLayoutConfirmed.setVisibility(8);
        this.staffLayoutAwaiting.setVisibility(0);
//        this.staffBtnConfirm.setBackgroundColor(getResources().getColor(R.color.btngreen));
//        this.staffBtnAwaiting.setBackgroundColor(getResources().getColor(R.color.white));
        this.textConfirmed.setTextColor(getResources().getColor(R.color.black));
        this.textAwt.setTextColor(getResources().getColor(R.color.white));
//        this.textAwt.setTextColor(getResources().getColor(R.color.black));
//        this.bookingConfirmCountStaff.setTextColor(getResources().getColor(R.color.white));
//        this.bookingAwaitingCountStaff.setTextColor(getResources().getColor(R.color.black));
//        this.bookingConfirmCountStaff.setBackgroundResource(R.drawable.check_circle);
//        this.bookingAwaitingCountStaff.setBackgroundResource(R.drawable.circle);
    }

    public void addCalendarEvent(int styear, int stmonth, int stday, int stHour, int stMin, String title, String description, String location) {
        try {
            Calendar beginCal = Calendar.getInstance(Locale.US);
            beginCal.set(styear, stmonth, stday, stHour, stMin);
            Calendar endCal = Calendar.getInstance();
            endCal.set(styear, stmonth, stday, stHour, stMin);
            Intent intent = new Intent("android.intent.action.INSERT");
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            intent.putExtra("eventLocation", location);
            intent.putExtra("beginTime", beginCal.getTimeInMillis());
            intent.putExtra("endTime", endCal.getTimeInMillis());
            intent.putExtra("allDay", 0);
            intent.putExtra("eventStatus", 1);
            intent.putExtra("visible", 0);
            intent.putExtra("hasAlarm", 1);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createStaffBookingConfirm() {

        this.staffLayoutConfirmed.removeAllViews();
        staffLayoutConfirmed.setVisibility(View.VISIBLE);
        bookingDataLayoutStaff.setVisibility(View.VISIBLE);
        for (int r = 0; r < bookingConfirmedArrayList.size(); r++) {
            Log.d("waitingItem", new Gson().toJson(bookingConfirmedArrayList.get(r)));
            final Booking myBooking = bookingConfirmedArrayList.get(r);
            final View v = ((LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.staff_booking_awaiting_template, null);
            llProgressTopReceived = (LinearLayout) v.findViewById(R.id.llProgressTop);
            llProgressTopReceived.setVisibility(View.GONE);
            this.progressBar = (DotsProgressBar) v.findViewById(R.id.dotsProgressBar);
            this.dotsProgressBarUnder = (DotsProgressBar) v.findViewById(R.id.dotsProgressBarUnder);
            this.progressBar.setDotsCount(3);
            this.dotsProgressBarUnder.setDotsCount(3);
            String bookingDate = "";
            RatingBar rb = (RatingBar) v.findViewById(R.id.aRatingBar);
            ((LayerDrawable) rb.getProgressDrawable()).getDrawable(2).setColorFilter(getActivity().getResources().getColor(R.color.ratingcolor), PorterDuff.Mode.SRC_ATOP);

            apiInterface.GetServiceAndUserById(myBooking.getServiceId(), myBooking.getCustomerId()).enqueue(new Callback<ServiceUserModel>() {
                @Override
                public void onResponse(Call<ServiceUserModel> call, Response<ServiceUserModel> response) {
                    if (response.code() == 200) {
                        ServiceUserModel serviceUserModel = response.body();
                        custUser = serviceUserModel.getUser();
                        serviceModel = serviceUserModel.getService();

                        Log.d("custUser", new Gson().toJson(response.body()));
                        Log.d("custUser", new Gson().toJson(custUser));
                        Log.d("ServiceModel", new Gson().toJson(serviceModel));

                        Glide.with(getActivity()).load(Links.URL + custUser.getUserImage().get(0)).into((ImageView) v.findViewById(R.id.imageUser));
                        TextView aUserNameTxt = (TextView) v.findViewById(R.id.aUserNameTxt);
                        aUserNameTxt.setText(custUser.getUserName());
                        TextView txtserName = (TextView) v.findViewById(R.id.txtserName);
                        TextView txtserPrice = (TextView) v.findViewById(R.id.txtserPrice);
                        TextView txtserDesc = (TextView) v.findViewById(R.id.txtserDesc);
                        TextView txtserDuration = (TextView) v.findViewById(R.id.txtDuration);
                        TextView aBookingDateTxt = (TextView) v.findViewById(R.id.aBookingDateTxt);
                        TextView aBookingTimeTxt = (TextView) v.findViewById(R.id.aBookingTimeTxt);
                        TextView aAddressTxt = (TextView) v.findViewById(R.id.aAddressTxt);
                        RelativeLayout btnCall = (RelativeLayout) v.findViewById(R.id.btnCall);
                        RelativeLayout btnEmail = (RelativeLayout) v.findViewById(R.id.btnEmail);
                        TextView aBookingTypeTxt = (TextView) v.findViewById(R.id.aBookingTypeTxt);
                        TextView txtStrtP = (TextView) v.findViewById(R.id.txtStrtP);
                        LinearLayout aLayoutHome = (LinearLayout) v.findViewById(R.id.aLayoutHome);
                        final EditText etCommToCustSpa = (EditText) v.findViewById(R.id.etCommToCustSpa);
                        ((LinearLayout) v.findViewById(R.id.llStartP)).setVisibility(View.VISIBLE);
                        ((LinearLayout) v.findViewById(R.id.llCalendar)).setVisibility(View.VISIBLE);
                        ((LinearLayout) v.findViewById(R.id.linComment)).setVisibility(View.GONE);
                        ((LinearLayout) v.findViewById(R.id.linPrice)).setVisibility(View.GONE);
                        Button cancelBtnBookingForConfirm = v.findViewById(R.id.cancelBtnBookingForConfirm);
                        cancelBtnBookingForConfirm.setVisibility(View.VISIBLE);

                        String bookingTypeString = "";
                        String bookingAddress = "";
                        String country = "";
                        String zipCode = "";
                        final String staffLatitude = user.getLatLong().get(0);
                        final String staffLongitude = user.getLatLong().get(1);

                        if (myBooking.getVisitType().equals(Constant.Visithome)) {
                            bookingTypeString = getResources().getString(R.string.visitcu);
                            aLayoutHome.setVisibility(0);

                        } else if (myBooking.getVisitType().equals(Constant.Gotospa)) {
                            bookingTypeString = getResources().getString(R.string.cust_will_i);
                            aLayoutHome.setVisibility(8);
                        }
                        ((Button) v.findViewById(R.id.btnViewMap)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://maps.google.com/maps?saddr=" + currentLatitue + "," + currentLongitude + "&daddr=" + staffLatitude + "," + staffLongitude)));
                            }
                        });
                        ((Button) v.findViewById(R.id.btnAddCalendar)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int styr;
                                int startday;
                                int stHour;
                                int stMin;
                                int stmonth;
                                String description;
                                String location;
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy HH:mm aa");
                                try {
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(sdf.parse(myBooking.getBookingdate() + " " + myBooking.getBookingtime()));
                                    styr = calendar.get(Calendar.YEAR);
                                    startday = calendar.get(Calendar.DAY_OF_MONTH);
                                    stHour = calendar.get(Calendar.HOUR);
                                    stMin = calendar.get(Calendar.MINUTE);
                                    stmonth = calendar.get(Calendar.MONTH);
                                    description = user.getMerchantid().getDescription();
                                    location = custUser.getAddress();
                                    addCalendarEvent(styr, stmonth, startday, stHour, stMin, serviceModel.getServiceName() + " booking ", description, location);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        aBookingTypeTxt.setText(bookingTypeString);

                        btnEmail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SendMail(custUser.getEmail());
                            }
                        });
                        aAddressTxt.setText(custUser.getAddress() + "\n" + custUser.getCountry());
                        btnCall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                    Intent callIntent = new Intent("android.intent.action.CALL");
                                    String phone = custUser.getPhone().substring(custUser.getPhone().lastIndexOf(" "));
                                    callIntent.setData(Uri.parse("tel:" + phone.split("-")[0] + phone.split("-")[1]));
                                    getActivity().startActivity(callIntent);
                                } else {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, Constant.CALL_PERMISSION_CODE);
                                }
                            }
                        });
                        final View myView = v;

                        cancelBtnBookingForConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                apiInterface.UpdateBooking(myBooking).enqueue(new Callback<Booking>() {
                                    @Override
                                    public void onResponse(Call<Booking> call, Response<Booking> response) {
                                        if (response.code() == 200) {
                                            Booking cancelBooking = response.body();
                                            staffLayoutConfirmed.removeView(myView);
                                            Toast.makeText(getActivity(), getString(R.string.cancelbookingsuccess), Toast.LENGTH_SHORT).show();
                                            if (staffLayoutConfirmed.getChildCount() == 0) {
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


                        ((Button) v.findViewById(R.id.acceptConfirmBtn)).setVisibility(View.GONE);
                        ((LinearLayout) v.findViewById(R.id.layoutRemove)).setVisibility(View.GONE);
                        aBookingDateTxt.setText(myBooking.getBookingdate());
                        aBookingTimeTxt.setText(myBooking.getBookingtime());
                        Locale defaultLocale = Locale.getDefault();
                        Currency currency = Currency.getInstance(defaultLocale);

                        txtserName.setText(serviceModel.getSubServiceList().get(0).getServiceName());
                        txtserDesc.setText("  :  " + serviceModel.getSubServiceList().get(0).getServiceDescription());
                        txtserPrice.setText("  :  " + serviceModel.getSubServiceList().get(0).getServicePrice() + " " + currency.getSymbol());
                        txtStrtP.setText("  :  " + serviceModel.getSubServiceList().get(0).getServiceDescription());

                        txtserDuration.setText("  :  " + serviceModel.getSubServiceList().get(0).getServiceDuration() + " mins");
                        staffLayoutConfirmed.addView(v, 0, new LinearLayout.LayoutParams(-1, -1));
                    }
                }

                @Override
                public void onFailure(Call<ServiceUserModel> call, Throwable t) {
                    Log.d("onfail", t.getMessage());
                }
            });

        }

    }


    private void createStaffBookingAwaiting() {

        this.staffLayoutAwaiting.removeAllViews();
        staffLayoutAwaiting.setVisibility(View.VISIBLE);
        bookingDataLayoutStaff.setVisibility(View.VISIBLE);
        Log.d("bookingWaitingArrayList", String.valueOf(bookingWaitingArrayList.size()));
        for (int r = 0; r < bookingWaitingArrayList.size(); r++) {
            Log.d("waitingItem", new Gson().toJson(bookingWaitingArrayList.get(r)));
            final Booking myBooking = bookingWaitingArrayList.get(r);
            final View v = ((LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.staff_booking_awaiting_template, null);
            llProgressTopReceived = (LinearLayout) v.findViewById(R.id.llProgressTop);
            llProgressTopReceived.setVisibility(View.GONE);
            this.progressBar = (DotsProgressBar) v.findViewById(R.id.dotsProgressBar);
            this.dotsProgressBarUnder = (DotsProgressBar) v.findViewById(R.id.dotsProgressBarUnder);
            this.progressBar.setDotsCount(3);
            this.dotsProgressBarUnder.setDotsCount(3);
            String bookingDate = "";
            RatingBar rb = (RatingBar) v.findViewById(R.id.aRatingBar);
            ((LayerDrawable) rb.getProgressDrawable()).getDrawable(2).setColorFilter(getActivity().getResources().getColor(R.color.ratingcolor), PorterDuff.Mode.SRC_ATOP);

            apiInterface.GetServiceAndUserById(myBooking.getServiceId(), myBooking.getCustomerId()).enqueue(new Callback<ServiceUserModel>() {
                @Override
                public void onResponse(Call<ServiceUserModel> call, Response<ServiceUserModel> response) {
                    if (response.code() == 200) {
                        ServiceUserModel serviceUserModel = response.body();
                        custUser = serviceUserModel.getUser();
                        serviceModel = serviceUserModel.getService();

                        Log.d("custUser", new Gson().toJson(response.body()));
                        Log.d("custUser", new Gson().toJson(custUser));
                        Log.d("ServiceModel", new Gson().toJson(serviceModel));

                        Glide.with(getActivity()).load(Links.URL + custUser.getUserImage().get(0)).into((ImageView) v.findViewById(R.id.imageUser));
                        TextView aUserNameTxt = (TextView) v.findViewById(R.id.aUserNameTxt);
                        aUserNameTxt.setText(custUser.getUserName());
                        TextView txtserName = (TextView) v.findViewById(R.id.txtserName);
                        TextView txtserPrice = (TextView) v.findViewById(R.id.txtserPrice);
                        TextView txtserDesc = (TextView) v.findViewById(R.id.txtserDesc);
                        TextView txtserDuration = (TextView) v.findViewById(R.id.txtDuration);
                        TextView aBookingDateTxt = (TextView) v.findViewById(R.id.aBookingDateTxt);
                        TextView aBookingTimeTxt = (TextView) v.findViewById(R.id.aBookingTimeTxt);
                        TextView aAddressTxt = (TextView) v.findViewById(R.id.aAddressTxt);
                        RelativeLayout btnCall = (RelativeLayout) v.findViewById(R.id.btnCall);
                        RelativeLayout btnEmail = (RelativeLayout) v.findViewById(R.id.btnEmail);
                        TextView aBookingTypeTxt = (TextView) v.findViewById(R.id.aBookingTypeTxt);
                        LinearLayout aLayoutHome = (LinearLayout) v.findViewById(R.id.aLayoutHome);
                        final EditText etCommToCustSpa = (EditText) v.findViewById(R.id.etCommToCustSpa);
                        String bookingTypeString = "";
                        String bookingAddress = "";
                        String country = "";
                        String zipCode = "";
                        final String staffLatitude = user.getLatLong().get(0);
                        final String staffLongitude = user.getLatLong().get(1);

                        if (myBooking.getVisitType().equals(Constant.Visithome)) {
                            bookingTypeString = getResources().getString(R.string.visitcu);
                            aLayoutHome.setVisibility(0);

                        } else if (myBooking.getVisitType().equals(Constant.Gotospa)) {
                            bookingTypeString = getResources().getString(R.string.cust_will_i);
                            aLayoutHome.setVisibility(8);
                        }
                        ((Button) v.findViewById(R.id.btnViewMap)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://maps.google.com/maps?saddr=" + currentLatitue + "," + currentLongitude + "&daddr=" + staffLatitude + "," + staffLongitude)));
                            }
                        });
                        aBookingTypeTxt.setText(bookingTypeString);

                        btnEmail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SendMail(custUser.getEmail());
                            }
                        });
                        aAddressTxt.setText(custUser.getAddress() + "\n" + custUser.getCountry());
                        btnCall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                    Intent callIntent = new Intent("android.intent.action.CALL");
                                    String phone = custUser.getPhone().substring(custUser.getPhone().lastIndexOf(" "));
                                    callIntent.setData(Uri.parse("tel:" + phone.split("-")[0] + phone.split("-")[1]));
                                    getActivity().startActivity(callIntent);
                                } else {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, Constant.CALL_PERMISSION_CODE);
                                }
                            }
                        });
                        final View myView = v;

                        ((Button) v.findViewById(R.id.acceptConfirmBtn)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myBooking.setBookingstatus(Constant.Confirmed);
                                if (!etCommToCustSpa.getText().toString().trim().equals("")) {
                                    myBooking.setComment(etCommToCustSpa.getText().toString().trim());
                                }

                                apiInterface.UpdateBooking(myBooking).enqueue(new Callback<Booking>() {
                                    @Override
                                    public void onResponse(Call<Booking> call, Response<Booking> response) {
                                        if (response.code() == 200) {
                                            Booking updateBooking = response.body();
                                            staffLayoutAwaiting.removeView(myView);
                                            bookingConfirmedArrayList.add(updateBooking);
                                        }
                                        if (staffLayoutAwaiting.getChildCount() == 0) {
                                            ((LinearLayout) pager.findViewById(R.id.blankBookingLayoutReceived)).setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Booking> call, Throwable t) {

                                    }
                                });
                            }
                        });
                        ((LinearLayout) v.findViewById(R.id.layoutRemove)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myBooking.setBookingstatus(Constant.Declained);
                                apiInterface.UpdateBooking(myBooking).enqueue(new Callback<Booking>() {
                                    @Override
                                    public void onResponse(Call<Booking> call, Response<Booking> response) {
                                        if (response.code() == 200) {
                                            Booking updateBooking = response.body();
                                            staffLayoutAwaiting.removeView(myView);

                                            if (staffLayoutAwaiting.getChildCount() == 0) {
                                                ((LinearLayout) pager.findViewById(R.id.blankBookingLayoutReceived)).setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Booking> call, Throwable t) {

                                    }
                                });
                            }
                        });
                        aBookingDateTxt.setText(myBooking.getBookingdate());
                        aBookingTimeTxt.setText(myBooking.getBookingtime());
                        Locale defaultLocale = Locale.getDefault();
                        Currency currency = Currency.getInstance(defaultLocale);

                        txtserName.setText(serviceModel.getSubServiceList().get(0).getServiceName());
                        txtserDesc.setText("  :  " + serviceModel.getSubServiceList().get(0).getServiceDescription());
                        txtserPrice.setText("  :  " + serviceModel.getSubServiceList().get(0).getServicePrice() + " " + currency.getSymbol());
                        txtserDuration.setText("  :  " + serviceModel.getSubServiceList().get(0).getServiceDuration() + " mins");
                        staffLayoutAwaiting.addView(v, 0, new LinearLayout.LayoutParams(-1, -1));
                    }
                }

                @Override
                public void onFailure(Call<ServiceUserModel> call, Throwable t) {
                    Log.d("onfail", t.getMessage());
                }
            });

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

    class WizardPagerAdapter extends PagerAdapter {
        WizardPagerAdapter() {
        }

        public Object instantiateItem(View collection, int position) {
            return pager.getChildAt(position);
        }

        public int getCount() {
            return 2;
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }

//        public void destroyItem(ViewGroup container, int position, Object object) {
//        }
    }

    class C11742 implements ViewPager.OnPageChangeListener {
        C11742() {
        }

        public void onPageSelected(int position) {
            Log.d("pagePosition", String.valueOf(position));
            if (position == 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CreateConfirmView();
                    }
                }, 1000);
            } else if (position == 1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CreateAwaitView();
                    }
                }, 1000);

            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CreateAwaitView();
                    }
                }, 1000);
            }
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageScrollStateChanged(int arg0) {
        }
    }

    private void getLocation() {
        this.currentLatitue = getLatitude();
        this.currentLongitude = getLongitude();
        Log.d("currentLatitue", getLatitude());
        Log.d("currentLongitude", getLongitude());

    }

    private String getLatitude() {
        String latitudeData = "00.000";
        this.gps = new GPSTracker(getActivity());
        if (this.gps.canGetLocation()) {
            return String.valueOf(this.gps.getLatitude());
        }
        this.gps.showSettingsAlert();
        return latitudeData;
    }

    private String getLongitude() {
        String longitudeData = "00.000";
        this.gps = new GPSTracker(getActivity());
        if (this.gps.canGetLocation()) {
            return String.valueOf(this.gps.getLongitude());
        }
        this.gps.showSettingsAlert();
        return longitudeData;
    }
}
