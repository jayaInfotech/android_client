package com.bestspa.spa.client;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bestspa.spa.client.API.APIClient;
import com.bestspa.spa.client.API.APIInterface;
import com.bestspa.spa.client.Model.Booking;
import com.bestspa.spa.client.Model.User;
import com.bestspa.spa.client.Utiles.Constant;
import com.bestspa.spa.client.Utiles.GPSTracker;
import com.bestspa.spa.client.Utiles.SessionManager;
import com.bestspa.spa.client.Utiles.UtilFun;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingScheduleActivity extends AppCompatActivity implements View.OnClickListener {

    CalendarView calendarView;
    TextView tvSelectedDate, txtselectTime, txtVisitAdress;
    RelativeLayout rlselectTime;
    String SelectedDate;
    long CurrentDate;
    GPSTracker gpsTracker;
    String currentLatitue, currentLongitude, address, country, pincode;
    Boolean mLocationPermissionGranted;
    SessionManager sessionManager;
    User user;
    final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    final String[] LOCATION_PERMISSION = new String[]{FINE_LOCATION, COARSE_LOCATION};
    APIInterface apiInterface;
    String custumDate;
    Button visitMeBtn,gotoSpaBtn;
    String visitType = Constant.Gotospa;
    LinearLayout linMyAdress;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_schdule);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        gpsTracker = new GPSTracker(this);
        sessionManager = new SessionManager(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        user = sessionManager.getUserDetails();

        visitMeBtn = (Button) findViewById(R.id.visitMeBtn);
        gotoSpaBtn = (Button) findViewById(R.id.gotoSpaBtn);
        txtselectTime = (TextView) findViewById(R.id.selectTime);
        rlselectTime = (RelativeLayout) findViewById(R.id.rlselectTime);
        calendarView = (CalendarView) findViewById(R.id.calLayout);
        tvSelectedDate = (TextView) findViewById(R.id.tvShowDateTime);
        txtVisitAdress = (TextView) findViewById(R.id.txtVisitAdress);
        linMyAdress = (LinearLayout)findViewById(R.id.linMyAdress);
        rlselectTime.setOnClickListener(this);
        visitMeBtn.setOnClickListener(this);
        gotoSpaBtn.setOnClickListener(this);
        txtselectTime.setOnClickListener(this);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy");
        SelectedDate = sdf.format(new Date(calendarView.getDate()));
        tvSelectedDate.setText(SelectedDate);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                SelectedDate = String.valueOf(dayOfMonth) + "-" + getMonthForInt(month) + "-" + String.valueOf(year);
                tvSelectedDate.setText(SelectedDate);
            }
        });
        Calendar mcurrentTime = Calendar.getInstance();
        calendarView.setMinDate(mcurrentTime.getTime().getTime());

        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        String myTime = GetAmPmTime(hour,minute);
        txtselectTime.setText(myTime);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.GET_LOCATION_CODE)
        {
            if (!data.getStringExtra("userLatitude").equals("")) {

                currentLatitue = data.getStringExtra("userLatitude");
                currentLongitude = data.getStringExtra("userLongitude");
                address = data.getStringExtra("userAddress");
                country = data.getStringExtra("userCountry");
                pincode = data.getStringExtra("userPinCode");
                txtVisitAdress.setText(address + "\n" + country);
                linMyAdress.setVisibility(View.VISIBLE);
            }
        }
    }

    String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11) {
            month = months[num];
        }
        return month;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gotoSpaBtn:

                visitMeBtn.setBackground(getResources().getDrawable(R.drawable.round_border_buttun));
                gotoSpaBtn.setBackground(getResources().getDrawable(R.drawable.round_border_button_black));
                visitType = Constant.Gotospa;
                linMyAdress.setVisibility(View.GONE);

                break;
            case R.id.visitMeBtn:

                visitMeBtn.setBackground(getResources().getDrawable(R.drawable.round_border_buttun));
                gotoSpaBtn.setBackground(getResources().getDrawable(R.drawable.round_border_button_black));
                visitType = Constant.Visithome;
                intent = new Intent(BookingScheduleActivity.this, GetCurrentLocationMap.class);
                startActivityForResult(intent, Constant.GET_LOCATION_CODE);

                break;
            case R.id.backBtn:

                onBackPressed();

                break;
            case R.id.confirmBookingBtn:

                if (validateData())
                {
                    UtilFun.ShowProgres(BookingScheduleActivity.this,getString(R.string.submit_booking_request));

                    Booking booking = new Booking();
                    booking.setBookingdate(SelectedDate);
                    booking.setBookingstatus(Constant.Pending);
                    booking.setBookingtime(txtselectTime.getText().toString());
                    booking.setCompleted(false);
                    booking.setCustomerId(user.get_id());
                    booking.setMerchantId(ChooseServiceActivity.userMerchant.get_id());
                    booking.setServiceId(ChooseServiceActivity.serviceId);
                    booking.setVisitType(visitType);
                    ArrayList<String> address = new ArrayList<>();
                    address.add(currentLatitue);
                    address.add(currentLongitude);
                    booking.setAddress(address);
                    apiInterface.Booking(booking).enqueue(new Callback<Booking>() {
                        @Override
                        public void onResponse(Call<Booking> call, Response<Booking> response) {
                            UtilFun.DismissProgress();
                            if (response.code() == 200)
                            {
                                Toast.makeText(BookingScheduleActivity.this,getString(R.string.booking_successfull),Toast.LENGTH_SHORT).show();
                                intent = new Intent(BookingScheduleActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("position",1);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(intent);
                                    }
                                },1000);
                            }else
                            {
                                Toast.makeText(BookingScheduleActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Booking> call, Throwable t) {
                            UtilFun.DismissProgress();
                            Toast.makeText(BookingScheduleActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

                        }
                    });
                }

                break;
            case  R.id.selectTime:

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(BookingScheduleActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String myTime = GetAmPmTime(selectedHour,selectedMinute);
                        txtselectTime.setText(myTime);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

                break;
            case  R.id.layoutHead:

                onBackPressed();

                break;
        }
    }

    private String GetAmPmTime(int selectedHour, int selectedMinute) {
        int myHour = selectedHour;
        String timeSet = "";
        if (selectedHour > 12) {
            myHour -= 12;
            timeSet = "PM";
        } else if (myHour == 0) {
            myHour += 12;
            timeSet = "AM";
        } else if (myHour == 12){
            timeSet = "PM";
        }else{
            timeSet = "AM";
        }

        return  myHour + ":" + selectedMinute+ " " +timeSet;

    }

    private boolean validateData() {

        Boolean validate = true;
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        SimpleDateFormat sdfCurrent = new SimpleDateFormat("h:mm a");
        try {
            Date dateCurrent = sdfCurrent.parse(GetAmPmTime(hour,minute));
            Date dateSelect = sdfCurrent.parse(txtselectTime.getText().toString());
            if (dateCurrent.after(dateSelect))
            {
                validate = false;
                Toast.makeText(this, getString(R.string.select_valid_time), Toast.LENGTH_SHORT).show();
                return validate;
            }else if (visitType.equals(Constant.Visithome))
            {
                if (txtVisitAdress.getText().toString().equals(""))
                {
                    validate = false;
                    Toast.makeText(this, getString(R.string.select_address), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BookingScheduleActivity.this, GetCurrentLocationMap.class);
                    startActivityForResult(intent, Constant.GET_LOCATION_CODE);
                    return validate;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }


        return validate;

    }

    public void getLocation() {
        gpsTracker = new GPSTracker(this);
        currentLatitue = String.valueOf(gpsTracker.getLatitude());
        currentLongitude = String.valueOf(gpsTracker.getLongitude());
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
        }
    }
}
