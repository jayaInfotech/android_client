package com.bestspa.spa.client;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bestspa.spa.client.API.APIClient;
import com.bestspa.spa.client.API.APIInterface;
import com.bestspa.spa.client.Adapter.ExpandListAdapterStaff;
import com.bestspa.spa.client.Adapter.ImageListAdapter;
import com.bestspa.spa.client.Model.Country;
import com.bestspa.spa.client.Model.MerchantModel;
import com.bestspa.spa.client.Model.ServiceModel;
import com.bestspa.spa.client.Model.SubServiceModel;
import com.bestspa.spa.client.Model.TimeModel;
import com.bestspa.spa.client.Model.User;
import com.bestspa.spa.client.Utiles.Constant;
import com.bestspa.spa.client.Utiles.DatabaseHelper;
import com.bestspa.spa.client.Utiles.GPSTracker;
import com.bestspa.spa.client.Utiles.Links;
import com.bestspa.spa.client.Utiles.SessionManager;
import com.bestspa.spa.client.Utiles.UtilFun;
import com.bestspa.spa.client.Views.AlertDialogManager;
import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.facebook.appevents.AppEventsConstants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

public class BussinessSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BussinessSettingActivit";
    SessionManager sessionManager;
    private Button btnSubmit;
    private Button btnUpload10Images;
    private TextView buttonText;
    private CheckBox chkComeBusinessAddress;
    private CheckBox chkVisitHome;
    private AutoCompleteTextView countryAutoCompleteTextViewStaff;
    private String currentLatitue = "";
    private String currentLongitude = "";
    private Dialog dialogTime;
    private EditText etAddress;
    private EditText etBusinessName;
    private EditText etFacebook;
    private EditText etPhone2;
    private EditText etShortDescription;
    private EditText etWebSite;
    int flag = 0;
    private String format = "";
    GPSTracker gps;
    private ImageView imgViewCustomerAddProfile;
    ArrayAdapter<String> phoneCodeAdapter;
    private Spinner spPhoneCode;
    private TimePicker timePicker1;
    private TextView txtFriLeft;
    private TextView txtFriRight;
    private TextView txtMonLeft;
    private TextView txtMonRight;
    private TextView txtSatLeft;
    private TextView txtSatRight;
    private TextView txtSunLeft;
    private TextView txtSunRight;
    private TextView txtTueLeft;
    private TextView txtWedLeft;
    private TextView txtWedRight;
    private TextView txtTueRight;
    private TextView txtthuLeft;
    private TextView txtthuRight;
    DatabaseHelper databaseHelper;
    EditText et_emailstaff;
    LinearLayout lin_change_password;
    Button btn_select_location;
    LinearLayout ll_header;
    TextView txt_detail;
    TextView txt_name, txt_address;
    Boolean FromStaffSignUp = false;
    final int WRITE_PERMISSION_CODE = 100;
    int i;
    Intent intent;
    ExpandableListView expandableListView;
    APIInterface apiInterface;
    ArrayList<ServiceModel> serviceModelArrayList;
    User user;
    Button btnSave;
    List<Image> images;
    List<MultipartBody.Part> parts = new ArrayList<>();
    RecyclerView rec_staffprofiles;
    ImageListAdapter imageListAdapter;
    ArrayList<String> imageList;
    ArrayList<String> countryType;
    final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    final String[] LOCATION_PERMISSION = new String[]{FINE_LOCATION, COARSE_LOCATION};
    Boolean mLocationPermissionGranted = false;
    String address, country, pincode;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_home_page);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        sessionManager = new SessionManager(this);
        databaseHelper = DatabaseHelper.getInstance(this);
        user = sessionManager.getUserDetails();
        FromStaffSignUp = getIntent().getBooleanExtra(Constant.FromStaffSignUp, false);
        images = new ArrayList<>();
        imageList = new ArrayList<>();
        imageListAdapter = new ImageListAdapter();

        if (ActivityCompat.checkSelfPermission(this, FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSION, Constant.REQUEST_FINE_LOCATION_CODE);
        } else {
            mLocationPermissionGranted = true;
            getLocation();
        }


        btnUpload10Images = (Button) findViewById(R.id.btnUpload10Images);
        imgViewCustomerAddProfile = (ImageView) findViewById(R.id.imgViewCustomerAddProfile);
        et_emailstaff = (EditText) findViewById(R.id.etEmailStaff);
        lin_change_password = (LinearLayout) findViewById(R.id.llTxtPassword);
        etBusinessName = (EditText) findViewById(R.id.etBusinessName);
        spPhoneCode = (Spinner) findViewById(R.id.spPhoneCode);
        etPhone2 = (EditText) findViewById(R.id.etPhone2);
        etAddress = (EditText) findViewById(R.id.etAddress);
        countryAutoCompleteTextViewStaff = (AutoCompleteTextView) findViewById(R.id.countryAutoCompleteTextViewStaff);
        btn_select_location = (Button) findViewById(R.id.businessLocationBtn);
        this.etBusinessName = (EditText) findViewById(R.id.etBusinessName);
        this.etPhone2 = (EditText) findViewById(R.id.etPhone2);
        this.etAddress = (EditText) findViewById(R.id.etAddress);

        this.etWebSite = (EditText) findViewById(R.id.etWebSite);
        this.etFacebook = (EditText) findViewById(R.id.etFacebook);
        this.etShortDescription = (EditText) findViewById(R.id.etShortDescription);
        this.imgViewCustomerAddProfile = (ImageView) findViewById(R.id.imgViewCustomerAddProfile);
        this.spPhoneCode = (Spinner) findViewById(R.id.spPhoneCode);
        this.txtMonLeft = (TextView) findViewById(R.id.txtMonLeft);
        this.txtMonRight = (TextView) findViewById(R.id.txtMonRight);
        this.txtTueLeft = (TextView) findViewById(R.id.txtTueLeft);
        this.txtTueRight = (TextView) findViewById(R.id.txtYueRight);
        this.txtWedLeft = (TextView) findViewById(R.id.txtWedLeft);
        this.txtWedRight = (TextView) findViewById(R.id.txtWedRight);
        this.txtthuLeft = (TextView) findViewById(R.id.txtthuLeft);
        this.txtthuRight = (TextView) findViewById(R.id.txtthuRight);
        this.txtFriLeft = (TextView) findViewById(R.id.txtFriLeft);
        this.txtFriRight = (TextView) findViewById(R.id.txtFriRight);
        this.txtSatLeft = (TextView) findViewById(R.id.txtSatLeft);
        this.txtSatRight = (TextView) findViewById(R.id.txtSatRight);
        this.txtSunLeft = (TextView) findViewById(R.id.txtSunLeft);
        this.txtSunRight = (TextView) findViewById(R.id.txtSunRight);
        this.chkVisitHome = (CheckBox) findViewById(R.id.chkVisitHome);
        this.chkComeBusinessAddress = (CheckBox) findViewById(R.id.chkComeBusinessAddress);
        LinearLayout paymentLayout = (LinearLayout) findViewById(R.id.paymentLayout);
        this.buttonText = (TextView) findViewById(R.id.buttonText);
        RelativeLayout tuesdayRelativeLayout = (RelativeLayout) findViewById(R.id.tuesdayRelativeLayout1);
        RelativeLayout wednesdayRelativeLayout = (RelativeLayout) findViewById(R.id.wednesdayRelativeLayout1);
        RelativeLayout thursdayRelativeLayout = (RelativeLayout) findViewById(R.id.thursdayRelativeLayout1);
        RelativeLayout fridayRelativeLayout = (RelativeLayout) findViewById(R.id.fridayRelativeLayout1);
        RelativeLayout saturdayRelativeLayout = (RelativeLayout) findViewById(R.id.saturdayRelativeLayout1);
        RelativeLayout sundayRelativeLayout = (RelativeLayout) findViewById(R.id.sundayRelativeLayout1);
        ll_header = (LinearLayout) findViewById(R.id.layoutHead);
        txt_detail = (TextView) findViewById(R.id.txtdetail);
        txt_name = (TextView) findViewById(R.id.txtname);
        txt_address = (TextView) findViewById(R.id.txtaddress);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        rec_staffprofiles = (RecyclerView) findViewById(R.id.staffProfileImageLayout);
        rec_staffprofiles.setLayoutManager(new LinearLayoutManager(BussinessSettingActivity.this, LinearLayoutManager.HORIZONTAL, false));

        expandableListView = (ExpandableListView) findViewById(R.id.exp_list);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSave = (Button) findViewById(R.id.btnstaffSave);
        imgViewCustomerAddProfile = (ImageView) findViewById(R.id.imgViewCustomerAddProfile);

        etAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocationPermissionGranted) {
                    Intent intent = new Intent(BussinessSettingActivity.this, GetCurrentLocationMap.class);
                    intent.putExtra(Constant.UserType, user.getUserTypes());
                    startActivityForResult(intent, Constant.GET_LOCATION_CODE);
                } else {
                    ActivityCompat.requestPermissions(BussinessSettingActivity.this, LOCATION_PERMISSION, Constant.REQUEST_FINE_LOCATION_CODE);

                }
            }
        });

        ll_header.setOnClickListener(this);
        this.txtMonLeft.setOnClickListener(this);
        this.txtMonRight.setOnClickListener(this);
        this.txtTueLeft.setOnClickListener(this);
        this.txtTueRight.setOnClickListener(this);
        this.txtWedLeft.setOnClickListener(this);
        this.txtWedRight.setOnClickListener(this);
        this.txtWedRight.setOnClickListener(this);
        this.txtthuLeft.setOnClickListener(this);
        this.txtthuRight.setOnClickListener(this);
        this.txtFriLeft.setOnClickListener(this);
        this.txtFriRight.setOnClickListener(this);
        this.txtSatLeft.setOnClickListener(this);
        this.txtSatRight.setOnClickListener(this);
        this.txtSunLeft.setOnClickListener(this);
        this.txtSunRight.setOnClickListener(this);
        this.btnSubmit.setOnClickListener(this);
        this.btnSave.setOnClickListener(this);

        txt_detail.setText(getResources().getString(R.string.please));
        txt_name.setText(getResources().getString(R.string.business));
        txt_address.setText(getResources().getString(R.string.addr_cust));
        btn_select_location.setText(getResources().getString(R.string.businessLocatiinBtn));

        try {
            Log.d("userimage", String.valueOf(user.getUserImage().size()));
            if (user.getUserImage().size() > 0) {
                for (int k = 0; k < user.getUserImage().size(); k++) {
                    imageList.add(user.getUserImage().get(k));
                    rec_staffprofiles.setAdapter(imageListAdapter);
                    rec_staffprofiles.setVisibility(View.VISIBLE);
                    findViewById(R.id.multiProfileLayoutStaffHeader).setVisibility(View.VISIBLE);
                    findViewById(R.id.progressBarLayoutStaff).setVisibility(View.GONE);
                    Log.d(TAG, "setAdapter");
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        btnUpload10Images.setOnClickListener(this);

        if (FromStaffSignUp) {
            findViewById(R.id.staffFirstTimeLayout).setVisibility(View.VISIBLE);
        }

        ArrayList<String> phonecode = new ArrayList();
        phonecode.add(getString(R.string.selectcode));
        final HashMap<String, String> spinnerMap = new HashMap();
        for (Country countryObj2 : databaseHelper.getAllToCountry()) {
            String countryDetails = countryObj2.getCountryName() + " +" + countryObj2.getCountryCode();
            phonecode.add(countryDetails);
            spinnerMap.put(countryDetails, " +" + countryObj2.getCountryCode());
        }
        this.phoneCodeAdapter = new ArrayAdapter(this, 17367048, phonecode);
        spPhoneCode.setAdapter(phoneCodeAdapter);
        this.spPhoneCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View view, int arg2, long arg3) {
                String selectedName = spPhoneCode.getSelectedItem().toString();
                if (!selectedName.equals("-- Select Code --")) {
                    ((TextView) arg0.getChildAt(0)).setText((String) spinnerMap.get(selectedName));
                }
                ((TextView) arg0.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        ((Button) findViewById(R.id.businessLocationBtn)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                if (mLocationPermissionGranted) {
                    Intent intent = new Intent(BussinessSettingActivity.this, GetCurrentLocationMap.class);
                    intent.putExtra("staffLatitude", currentLatitue);
                    intent.putExtra("staffLongitude", currentLongitude);
                    intent.putExtra(Constant.UserType, user.getUserTypes());
                    startActivityForResult(intent, Constant.GET_LOCATION_CODE);
                } else {
                    ActivityCompat.requestPermissions(BussinessSettingActivity.this, LOCATION_PERMISSION, Constant.REQUEST_FINE_LOCATION_CODE);

                }
            }
        });

        forSpinnerstaff();
        UtilFun.ShowProgres(this,getString(R.string.pleasewait));
        apiInterface.GetServices().enqueue(new Callback<ArrayList<ServiceModel>>() {
            @Override
            public void onResponse(Call<ArrayList<ServiceModel>> call, Response<ArrayList<ServiceModel>> response) {
                Log.d(TAG, new Gson().toJson(response));
                UtilFun.DismissProgress();
                serviceModelArrayList = response.body();
                expandableListView.setAdapter(new ExpandListAdapterStaff(BussinessSettingActivity.this, serviceModelArrayList, user.get_id()));
            }

            @Override
            public void onFailure(Call<ArrayList<ServiceModel>> call, Throwable t) {
                UtilFun.DismissProgress();
                serviceModelArrayList = new ArrayList<>();
                expandableListView.setAdapter(new ExpandListAdapterStaff(BussinessSettingActivity.this, serviceModelArrayList, user.get_id()));
                Log.d(TAG, t.getMessage());
            }
        });

        try {

            et_emailstaff.setText(user.getEmail());
            etBusinessName.setText(user.getMerchantid().getBusinessName());

            etPhone2.setText(user.getPhone().split("-")[1]);
            for (int m = 0; m < phonecode.size(); m++) {
                if (phonecode.get(m).equals(user.getPhone().split("-")[0])) {
                    spPhoneCode.setSelection(m);
                }
            }
            etAddress.setText(user.getAddress());
            countryAutoCompleteTextViewStaff.setText(user.getCountry());
            etWebSite.setText(user.getMerchantid().getWebsiteLink());
            etFacebook.setText(user.getMerchantid().getFacebookLink());
            etShortDescription.setText(user.getMerchantid().getDescription());
            chkVisitHome.setChecked(user.getMerchantid().getCanVisit());
            chkComeBusinessAddress.setChecked(user.getMerchantid().getCanGo());

            txtSunLeft.setText(user.getMerchantid().getTime().getSunfrom());
            txtSunRight.setText(user.getMerchantid().getTime().getSunto());
            txtMonLeft.setText(user.getMerchantid().getTime().getMonfrom());
            txtMonRight.setText(user.getMerchantid().getTime().getMonto());
            txtTueLeft.setText(user.getMerchantid().getTime().getTuefrom());
            txtTueRight.setText(user.getMerchantid().getTime().getTueto());
            txtWedLeft.setText(user.getMerchantid().getTime().getWedfrom());
            txtWedRight.setText(user.getMerchantid().getTime().getWedto());
            txtthuLeft.setText(user.getMerchantid().getTime().getThufrom());
            txtthuRight.setText(user.getMerchantid().getTime().getThuto());
            txtFriLeft.setText(user.getMerchantid().getTime().getFrifrom());
            txtFriRight.setText(user.getMerchantid().getTime().getFrito());
            txtSatLeft.setText(user.getMerchantid().getTime().getSatfrom());
            txtSatRight.setText(user.getMerchantid().getTime().getSatto());

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
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

            case Constant.WRITE_PERMISSION_CODE: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getString(R.string.you_must_grant_write_permission), Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_CODE);
                }
            }
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
    public void onBackPressed() {
        intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void forSpinnerstaff() {
        countryType = new ArrayList();
        for (Country countryObj : this.databaseHelper.getAllToCountry()) {
            countryType.add(countryObj.getCountryName());
        }
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter(this, 17367043, countryType);
        this.countryAutoCompleteTextViewStaff = (AutoCompleteTextView) findViewById(R.id.countryAutoCompleteTextViewStaff);
        this.countryAutoCompleteTextViewStaff.setAdapter(dataAdapter1);
        this.countryAutoCompleteTextViewStaff.setThreshold(1);
        this.countryAutoCompleteTextViewStaff.setOnItemClickListener(new C13277());
        ArrayList<String> phonecode = new ArrayList();
        phonecode.add("-- Select Code --");
        final HashMap<String, String> spinnerMap = new HashMap();
        for (Country countryObj2 : this.databaseHelper.getAllToCountry()) {
            String countryDetails = countryObj2.getCountryName() + " +" + countryObj2.getCountryCode();
            phonecode.add(countryDetails);
            spinnerMap.put(countryDetails, " +" + countryObj2.getCountryCode());
        }
        this.phoneCodeAdapter = new ArrayAdapter(this, 17367048, phonecode);
        this.phoneCodeAdapter.setDropDownViewResource(17367049);
        this.spPhoneCode.setAdapter(this.phoneCodeAdapter);
        this.spPhoneCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View view, int arg2, long arg3) {
                String selectedName = spPhoneCode.getSelectedItem().toString();
                if (!selectedName.equals("-- Select Code --")) {
                    ((TextView) arg0.getChildAt(0)).setText((String) spinnerMap.get(selectedName));
                }
                ((TextView) arg0.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    class C13277 implements AdapterView.OnItemClickListener {
        C13277() {
        }

        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//            staffCountryName = arg0.getItemAtPosition(arg2).toString().trim();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpload10Images:

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.create(this).limit(5).start();

                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.WRITE_PERMISSION_CODE);
                }

                break;
            case R.id.layoutHead:

                if (FromStaffSignUp) {
                    intent = new Intent(this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;
                }
                onBackPressed();

                break;
            case R.id.llTxtPassword:

//                ChangePassword();

                break;

            case R.id.txtMonLeft:
                showTimePicker(1);
                this.flag = 1;
                return;
            case R.id.txtMonRight:
                showTimePicker(2);
                this.flag = 2;
                return;
            case R.id.txtTueLeft:
                showTimePicker(3);
                this.flag = 3;
                return;
            case R.id.txtYueRight:
                showTimePicker(4);
                this.flag = 4;
                return;
            case R.id.txtWedLeft:
                showTimePicker(5);
                this.flag = 5;
                return;
            case R.id.txtWedRight:
                showTimePicker(6);
                this.flag = 6;
                return;
            case R.id.txtthuLeft:
                showTimePicker(7);
                this.flag = 7;
                return;
            case R.id.txtthuRight:
                showTimePicker(8);
                this.flag = 8;
                return;
            case R.id.txtFriLeft:
                showTimePicker(9);
                this.flag = 9;
                return;
            case R.id.txtFriRight:
                showTimePicker(10);
                this.flag = 10;
                return;
            case R.id.txtSatLeft:
                showTimePicker(11);
                this.flag = 11;
                return;
            case R.id.txtSatRight:
                showTimePicker(12);
                this.flag = 12;
                return;
            case R.id.txtSunLeft:
                showTimePicker(13);
                this.flag = 13;
                return;
            case R.id.txtSunRight:
                showTimePicker(14);
                this.flag = 14;
                return;

            case R.id.btnStaffLogout:

                if (user.getSignupwith().equals(Constant.Google))
                {
                    GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(BussinessSettingActivity.this,new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build());
                    googleSignInClient.signOut()
                            .addOnCompleteListener(BussinessSettingActivity.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        intent = new Intent(BussinessSettingActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        sessionManager.logoutUser();
                                        startActivity(intent);
                                        Log.d("logout","success");
                                    }
                                }
                            });
                }else
                {
                    intent = new Intent(BussinessSettingActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    sessionManager.logoutUser();
                    startActivity(intent);
                }

                return;
            case R.id.btnstaffSave:

                SaveData();

                return;
            case R.id.btnSubmit:

                SaveData();

                return;
            case R.id.mainHeader:


                break;
        }
    }

    private void SaveData() {

        if (ValidateData()) {

            UtilFun.ShowProgres(this,getString(R.string.updatingprofile));

            TimeModel timeModel = new TimeModel();
            timeModel.setMonfrom(txtMonLeft.getText().toString());
            timeModel.setMonto(txtMonLeft.getText().toString());
            timeModel.setTuefrom(txtTueLeft.getText().toString());
            timeModel.setTueto(txtTueRight.getText().toString());
            timeModel.setWedfrom(txtWedLeft.getText().toString());
            timeModel.setWedto(txtWedRight.getText().toString());
            timeModel.setThufrom(txtthuLeft.getText().toString());
            timeModel.setThuto(txtthuRight.getText().toString());
            timeModel.setFrifrom(txtFriLeft.getText().toString());
            timeModel.setFrito(txtFriRight.getText().toString());
            timeModel.setSatfrom(txtFriRight.getText().toString());
            timeModel.setSatto(txtFriRight.getText().toString());
            timeModel.setSunfrom(txtSunLeft.getText().toString());
            timeModel.setSunto(txtSunRight.getText().toString());

            final MerchantModel merchantModel = new MerchantModel();
            merchantModel.setBusinessName(etBusinessName.getText().toString());

            if (!etShortDescription.getText().toString().equals("")) {
                merchantModel.setDescription(etShortDescription.getText().toString());
            }
            if (!etFacebook.getText().toString().equals("")) {
                merchantModel.setFacebookLink(etFacebook.getText().toString());
            }
            if (!etWebSite.getText().toString().equals("")) {
                merchantModel.setWebsiteLink(etWebSite.getText().toString());
            }
            if (chkVisitHome.isChecked()) {
                merchantModel.setCanVisit(true);
            } else {
                merchantModel.setCanVisit(false);
            }
            if (chkComeBusinessAddress.isChecked()) {
                merchantModel.setCanGo(true);
            } else {
                merchantModel.setCanGo(false);
            }


            merchantModel.setTime(timeModel);
            merchantModel.setRating(user.getMerchantid().getRating());
            merchantModel.setDescription(etShortDescription.getText().toString());
            ArrayList<String> latLong = new ArrayList<>();
            latLong.add(currentLatitue);
            latLong.add(currentLongitude);

            user.setLatLong(latLong);
            user.setMerchantid(merchantModel);
            user.setEmail(et_emailstaff.getText().toString());
            user.setPhone(spPhoneCode.getSelectedItem().toString() + "-" + etPhone2.getText().toString());
            user.setAddress(etAddress.getText().toString());
            user.setCountry(countryAutoCompleteTextViewStaff.getText().toString());
            user.setUserImage(imageList);

            apiInterface.UpdateStaffUser(user, user.get_id()).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Log.d(TAG, response.body().toString());
                    UtilFun.DismissProgress();
                    if (response.code() == 200) {
                        User updatedUser = response.body();
                        sessionManager.createLoginSession(updatedUser);
                        Toast.makeText(BussinessSettingActivity.this, getString(R.string.informationupdated), Toast.LENGTH_SHORT).show();
                        intent = new Intent(BussinessSettingActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(BussinessSettingActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.d(TAG, t.getMessage().toString());
                    UtilFun.DismissProgress();
                }
            });
        }
    }

    private boolean ValidateData() {

        Log.d("selected code", spPhoneCode.getSelectedItem().toString());

        Boolean validate = true;
        if (imageList.size() <= 0 ) {
            validate = false;
            Toast.makeText(this, getString(R.string.image_empty), Toast.LENGTH_SHORT).show();
            return validate;
        }
        if (et_emailstaff.getText().toString().equals("")) {
            validate = false;
            Toast.makeText(this, getString(R.string.emailempty), Toast.LENGTH_SHORT).show();
            return validate;
        } else if (etBusinessName.getText().toString().equals("")) {
            validate = false;
            Toast.makeText(this, getString(R.string.businessempty), Toast.LENGTH_SHORT).show();
            return validate;
        } else if (spPhoneCode.getSelectedItem().toString().trim().equals(getString(R.string.selectcode))) {
            validate = false;
            Toast.makeText(this, getString(R.string.phonecode_valid), Toast.LENGTH_SHORT).show();
            return validate;
        } else if (etPhone2.getText().toString().equals("")) {
            validate = false;
            Toast.makeText(this, getString(R.string.phone_valid), Toast.LENGTH_SHORT).show();
            return validate;
        } else if (etAddress.getText().toString().equals("")) {
            validate = false;
            Toast.makeText(this, getString(R.string.addressempty), Toast.LENGTH_SHORT).show();
            return validate;
        } else if (countryAutoCompleteTextViewStaff.getText().toString().equals("")) {
            validate = false;
            Toast.makeText(this, getString(R.string.valid_country), Toast.LENGTH_SHORT).show();
            return validate;
        }else if (etShortDescription.getText().toString().equals(""))
        {
            validate = false;
            Toast.makeText(this, getString(R.string.enter_short_description), Toast.LENGTH_SHORT).show();
            return validate;

        } else if (!(chkVisitHome.isChecked() && chkVisitHome.isChecked())) {
            validate = false;
            Toast.makeText(this, getString(R.string.valid_selectoption), Toast.LENGTH_SHORT).show();
            return validate;
        } else if (currentLongitude.equals("") || currentLatitue.equals("")) {
            validate = false;
            Toast.makeText(this, getString(R.string.enbale_location), Toast.LENGTH_SHORT).show();
            return validate;
        }
//        else if ()
//        {
//
//        }

        return validate;
    }


    private void showTimePicker(int statusFlag) {
        this.dialogTime = new Dialog(this);
        this.dialogTime.requestWindowFeature(1);
        this.dialogTime.setContentView(R.layout.show_dialog_time);
        this.dialogTime.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        this.dialogTime.show();
        this.timePicker1 = (TimePicker) this.dialogTime.findViewById(R.id.timePicker1);
        int hour = Calendar.getInstance(Locale.US).get(11);
        int mySelectedHrs = 0;
        int mySelectedMin = 0;
        int timeFormatStatus = 0;
        if (statusFlag == 1) {
            String timeMondayLeft = this.txtMonLeft.getText().toString().trim();
            mySelectedHrs = getTimeSelectedHrs(timeMondayLeft);
            mySelectedMin = getTimeSelectedMin(timeMondayLeft);
            timeFormatStatus = getTimeFormat(timeMondayLeft);
        }
        if (statusFlag == 2) {
            String timetxtMonRight = this.txtMonRight.getText().toString().trim();
            mySelectedHrs = getTimeSelectedHrs(timetxtMonRight);
            mySelectedMin = getTimeSelectedMin(timetxtMonRight);
            timeFormatStatus = getTimeFormat(timetxtMonRight);
        }
        if (statusFlag == 3) {
            String timetxtTueLeft = this.txtTueLeft.getText().toString().trim();
            mySelectedHrs = getTimeSelectedHrs(timetxtTueLeft);
            mySelectedMin = getTimeSelectedMin(timetxtTueLeft);
            timeFormatStatus = getTimeFormat(timetxtTueLeft);
        }
        if (statusFlag == 4) {
            String timetxtTueRight = this.txtTueRight.getText().toString().trim();
            mySelectedHrs = getTimeSelectedHrs(timetxtTueRight);
            mySelectedMin = getTimeSelectedMin(timetxtTueRight);
            timeFormatStatus = getTimeFormat(timetxtTueRight);
        }
        if (statusFlag == 5) {
            String timetxtWedLeft = this.txtWedLeft.getText().toString().trim();
            mySelectedHrs = getTimeSelectedHrs(timetxtWedLeft);
            mySelectedMin = getTimeSelectedMin(timetxtWedLeft);
            timeFormatStatus = getTimeFormat(timetxtWedLeft);
        }
        if (statusFlag == 6) {
            String timetxtWedRight = this.txtWedRight.getText().toString().trim();
            mySelectedHrs = getTimeSelectedHrs(timetxtWedRight);
            mySelectedMin = getTimeSelectedMin(timetxtWedRight);
            timeFormatStatus = getTimeFormat(timetxtWedRight);
        }
        if (statusFlag == 7) {
            String timetxtthuLeft = this.txtthuLeft.getText().toString().trim();
            mySelectedHrs = getTimeSelectedHrs(timetxtthuLeft);
            mySelectedMin = getTimeSelectedMin(timetxtthuLeft);
            timeFormatStatus = getTimeFormat(timetxtthuLeft);
        }
        if (statusFlag == 8) {
            String timethuRight = this.txtthuRight.getText().toString().trim();
            mySelectedHrs = getTimeSelectedHrs(timethuRight);
            mySelectedMin = getTimeSelectedMin(timethuRight);
            timeFormatStatus = getTimeFormat(timethuRight);
        }
        if (statusFlag == 9) {
            String timetxtFriLeft = this.txtFriLeft.getText().toString().trim();
            mySelectedHrs = getTimeSelectedHrs(timetxtFriLeft);
            mySelectedMin = getTimeSelectedMin(timetxtFriLeft);
            timeFormatStatus = getTimeFormat(timetxtFriLeft);
        }
        if (statusFlag == 10) {
            String timeFriRight = this.txtFriRight.getText().toString().trim();
            mySelectedHrs = getTimeSelectedHrs(timeFriRight);
            mySelectedMin = getTimeSelectedMin(timeFriRight);
            timeFormatStatus = getTimeFormat(timeFriRight);
        }
        if (statusFlag == 11) {
            String timeSatLeft = this.txtSatLeft.getText().toString().trim();
            mySelectedHrs = getTimeSelectedHrs(timeSatLeft);
            mySelectedMin = getTimeSelectedMin(timeSatLeft);
            timeFormatStatus = getTimeFormat(timeSatLeft);
        }
        if (statusFlag == 12) {
            String timeSatRight = this.txtSatRight.getText().toString().trim();
            mySelectedHrs = getTimeSelectedHrs(timeSatRight);
            mySelectedMin = getTimeSelectedMin(timeSatRight);
            timeFormatStatus = getTimeFormat(timeSatRight);
        }
        if (statusFlag == 13) {
            String timeSunLeft = this.txtSunLeft.getText().toString().trim();
            mySelectedHrs = getTimeSelectedHrs(timeSunLeft);
            mySelectedMin = getTimeSelectedMin(timeSunLeft);
            timeFormatStatus = getTimeFormat(timeSunLeft);
        }
        if (statusFlag == 14) {
            String timeSunRight = this.txtSunRight.getText().toString().trim();
            mySelectedHrs = getTimeSelectedHrs(timeSunRight);
            mySelectedMin = getTimeSelectedMin(timeSunRight);
            timeFormatStatus = getTimeFormat(timeSunRight);
        }
        this.timePicker1.setCurrentHour(Integer.valueOf(mySelectedHrs));
        this.timePicker1.setCurrentMinute(Integer.valueOf(mySelectedMin));
        if (hour == 0) {
            this.format = "am";
        } else if (hour == 12) {
            this.format = "pm";
        } else if (hour > 12) {
            this.format = "pm";
        } else {
            this.format = "am";
        }
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            NumberPicker npMinuteSpinner = (NumberPicker) this.timePicker1.findViewById(classForid.getField("minute").getInt(null));
            npMinuteSpinner.setMinValue(0);
            npMinuteSpinner.setMaxValue(1);
            List<String> displayedValues = new ArrayList();
            for (int i = 0; i < 60; i += 30) {
                displayedValues.add(String.format("%02d", new Object[]{Integer.valueOf(i)}));
            }
            npMinuteSpinner.setDisplayedValues((String[]) displayedValues.toArray(new String[displayedValues.size()]));
            NumberPicker nphourSpinner = (NumberPicker) this.timePicker1.findViewById(classForid.getField("hour").getInt(null));
            Field ampm = null;
            try {
                ampm = classForid.getField("amPm");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            NumberPicker npampm = null;
            if (ampm != null) {
                try {
                    npampm = (NumberPicker) this.timePicker1.findViewById(ampm.getInt(null));
                    npampm.setValue(timeFormatStatus);
                } catch (IllegalArgumentException e2) {
                    e2.printStackTrace();
                } catch (IllegalAccessException e3) {
                    System.out.println("IllegalAccessException");
                    e3.printStackTrace();
                }
            }
            Class<?> numberPickerClass = null;
            try {
                numberPickerClass = Class.forName("android.widget.NumberPicker");
            } catch (ClassNotFoundException e4) {
                e4.printStackTrace();
            }
            Field selectionDivider = null;
            if (numberPickerClass != null) {
                try {
                    selectionDivider = numberPickerClass.getDeclaredField("mSelectionDivider");
                } catch (NoSuchFieldException e5) {
                    e5.printStackTrace();
                }
            }
            if (selectionDivider != null) {
                try {
                    selectionDivider.setAccessible(true);
                    selectionDivider.set(npMinuteSpinner, getResources().getDrawable(R.drawable.backgroung));
                    selectionDivider.set(nphourSpinner, getResources().getDrawable(R.drawable.backgroung));
                    selectionDivider.set(npampm, getResources().getDrawable(R.drawable.backgroung));
                } catch (IllegalArgumentException e22) {
                    e22.printStackTrace();
                } catch (Resources.NotFoundException e6) {
                    e6.printStackTrace();
                } catch (IllegalAccessException e32) {
                    e32.printStackTrace();
                }
            }
        } catch (Exception e7) {
        }
        LinearLayout btnDone = (LinearLayout) this.dialogTime.findViewById(R.id.btnDone);
        ((LinearLayout) this.dialogTime.findViewById(R.id.btnClear)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialogTime.dismiss();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialogTime.dismiss();
                showTime(timePicker1.getCurrentHour().intValue(), timePicker1.getCurrentMinute().intValue());
            }
        });
    }

    public void showTime(int hour, int min) {
        int cusMin;
        if (hour == 0) {
            hour += 12;
            this.format = "am";
        } else if (hour == 12) {
            this.format = "pm";
        } else if (hour > 12) {
            hour -= 12;
            this.format = "pm";
        } else {
            this.format = "am";
        }
        if (min == 0) {
            cusMin = 0;
        } else {
            cusMin = 30;
        }
        String finalTime = timeValidater(Integer.toString(hour) + ":" + cusMin + "" + this.format.toLowerCase());
        if (this.flag == 1) {
            this.txtMonLeft.setText(finalTime);
        } else if (this.flag == 2) {
            this.txtMonRight.setText(finalTime);
        } else if (this.flag == 3) {
            this.txtTueLeft.setText(finalTime);
        } else if (this.flag == 4) {
            this.txtTueRight.setText(finalTime);
        } else if (this.flag == 5) {
            this.txtWedLeft.setText(finalTime);
        } else if (this.flag == 6) {
            this.txtWedRight.setText(finalTime);
        } else if (this.flag == 7) {
            this.txtthuLeft.setText(finalTime);
        } else if (this.flag == 8) {
            this.txtthuRight.setText(finalTime);
        } else if (this.flag == 9) {
            this.txtFriLeft.setText(finalTime);
        } else if (this.flag == 10) {
            this.txtFriRight.setText(finalTime);
        } else if (this.flag == 11) {
            this.txtSatLeft.setText(finalTime);
        } else if (this.flag == 12) {
            this.txtSatRight.setText(finalTime);
        } else if (this.flag == 13) {
            this.txtSunLeft.setText(finalTime);
        } else if (this.flag == 14) {
            this.txtSunRight.setText(finalTime);
        }
    }

    private int getTimeSelectedHrs(String bookingDateTime) {
        bookingDateTime = bookingDateTime.replace("am", "").replace("pm", "");
        String[] timeArray = bookingDateTime.split(":");
        int hourCus = 0;
        if (!bookingDateTime.contains(":")) {
            return Integer.parseInt(bookingDateTime);
        }
        try {
            return Integer.parseInt(timeArray[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return hourCus;
        }
    }

    private int getTimeSelectedMin(String bookingDateTime) {
        bookingDateTime = bookingDateTime.replace("am", "").replace("pm", "");
        String[] timeArray = bookingDateTime.split(":");
        int minCus = 0;
        if (bookingDateTime.contains(":")) {
            try {
                minCus = Integer.parseInt(timeArray[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return minCus;
    }

    private int getTimeFormat(String bookingDateTime) {
        try {
            if (bookingDateTime.contains("am")) {
                return 0;
            }
            if (bookingDateTime.contains("pm")) {
                return 1;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String timeValidater(String startTime) {
        String newTimeStart;
        startTime = startTime.replace("am", " am").replace("pm", " pm");
        String[] startTimeArray = startTime.split(" ");
        if (startTimeArray[0].contains(":")) {
            String[] timeArray = startTimeArray[0].split(":");
            newTimeStart = startTime;
            if (timeArray[1].equals(AppEventsConstants.EVENT_PARAM_VALUE_NO)) {
                newTimeStart = timeArray[0] + " " + startTimeArray[1];
            }
        } else {
            newTimeStart = startTimeArray[0] + " " + startTimeArray[1];
        }
        return newTimeStart.replace(" am", "am").replace(" pm", "pm");
    }


//    private void ChangePassword() {
//        final Dialog chdialog = new Dialog(this);
//        chdialog.requestWindowFeature(1);
//        chdialog.setContentView(R.layout.changepass_dialog);
//        chdialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
//        chdialog.show();
//        final EditText etCurrentPassword = (EditText) chdialog.findViewById(R.id.etCurPass);
//        final EditText etNewStaffPassword = (EditText) chdialog.findViewById(R.id.etNewPass);
//        final EditText etReapeatPassword = (EditText) chdialog.findViewById(R.id.etReapeatPass);
//        Button btnDonePass = (Button) chdialog.findViewById(R.id.btnDonePass);
//        ((Button) chdialog.findViewById(R.id.btnCancelPass)).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                chdialog.dismiss();
//            }
//        });
//        btnDonePass.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                String curPass ;
//                String newPass = etNewStaffPassword.getText().toString().trim();
//                String repeatPass = etReapeatPassword.getText().toString().trim();
//                String userPreviousPassword ;
//                if (curPass.equals("")) {
//                    alert.showAlertDialog(BussinessSettingActivity.this, "error", "Please enter current password", Boolean.valueOf(false));
//                } else if (newPass.equals("")) {
//                    alert.showAlertDialog(BussinessSettingActivity.this, "error", "Please enter new password", Boolean.valueOf(false));
//                } else if (!userPreviousPassword.equals(curPass)) {
//                    alert.showAlertDialog(BussinessSettingActivity.this, "error", "Current password do not match", Boolean.valueOf(false));
//                } else if (newPass.equals(repeatPass)) {
//                    changePassWebservice(newPass, chdialog);
//                } else {
//                    alert.showAlertDialog(BussinessSettingActivity.this, "error", "New and repeat password do not match", Boolean.valueOf(false));
//                }
//            }
//        });
//    }


    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {

        try {
            if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
                // Get a list of picked images
                images.clear();
                imageListAdapter.notifyDataSetChanged();
                images = ImagePicker.getImages(data);

                if (images.size() > 0) {

                    UtilFun.ShowProgres(this,getString(R.string.uploadingimg));
                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.setType(MultipartBody.FORM);
                    parts.clear();
                    for (int i = 0; i < images.size(); i++) {
                        File file = new File(images.get(i).getPath());
                        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
                        parts.add(body);
                    }
                    RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload");

                    apiInterface.postImage(parts, name, user.get_id()).enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            UtilFun.DismissProgress();
                            try {
                                if (response.code() == 200) {
                                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                                    JSONArray files = jsonObject.getJSONArray("files");
                                    if (files.length() > 0) {
                                        for (int j = 0; j < files.length(); j++) {
                                            JSONObject file = files.getJSONObject(j);
                                            imageList.add(file.getString("filename"));
                                        }

                                        rec_staffprofiles.setVisibility(View.VISIBLE);
                                        findViewById(R.id.multiProfileLayoutStaffHeader).setVisibility(View.VISIBLE);
                                        findViewById(R.id.progressBarLayoutStaff).setVisibility(View.GONE);

                                        imageListAdapter = new ImageListAdapter();
                                        rec_staffprofiles.setAdapter(imageListAdapter);

                                    }
                                } else {
                                    Toast.makeText(BussinessSettingActivity.this, getString(R.string.file_size_more_error), Toast.LENGTH_SHORT).show();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            UtilFun.DismissProgress();
                        }
                    });
                }
            } else {
                if (!data.getStringExtra("userLatitude").equals("")) {
                    currentLatitue = data.getStringExtra("userLatitude");
                    currentLongitude = data.getStringExtra("userLongitude");
                    address = data.getStringExtra("userAddress");
                    country = data.getStringExtra("userCountry");
                    pincode = data.getStringExtra("userPinCode");

                    etAddress.setText(address);
                    countryAutoCompleteTextViewStaff.setText(country);
                }
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(BussinessSettingActivity.this).inflate(R.layout.image_show, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
            if (position == 0) {
                Glide.with(BussinessSettingActivity.this).load(Links.URL + imageList.get(position)).into(imgViewCustomerAddProfile);
            }
            Glide.with(BussinessSettingActivity.this).load(Links.URL + imageList.get(position)).into(holder.imageMain);
            holder.imageDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UtilFun.ShowProgres(BussinessSettingActivity.this,getString(R.string.deletingimage));
                    apiInterface.deleteImage(imageList.get(position), user.get_id()).enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            UtilFun.DismissProgress();
                            imageList.remove(position);
                            if ((imageList.size() == 0)) {
                                imgViewCustomerAddProfile.setImageDrawable(getResources().getDrawable(R.drawable.upload));
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            Toast.makeText(BussinessSettingActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                            UtilFun.DismissProgress();
                        }
                    });

                }
            });

        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageMain, imageDelete;

            public MyViewHolder(View itemView) {
                super(itemView);
                imageMain = (ImageView) itemView.findViewById(R.id.img_main);
                imageDelete = (ImageView) itemView.findViewById(R.id.img_remove);

            }
        }
    }

}
