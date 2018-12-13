package com.bestspa.spa.client;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bestspa.spa.client.API.APIClient;
import com.bestspa.spa.client.API.APIInterface;
import com.bestspa.spa.client.Adapter.ExpandListAdapterStaff;
import com.bestspa.spa.client.Model.Country;
import com.bestspa.spa.client.Model.MerchantModel;
import com.bestspa.spa.client.Model.ServiceModel;
import com.bestspa.spa.client.Model.TimeModel;
import com.bestspa.spa.client.Model.User;
import com.bestspa.spa.client.Utiles.Constant;
import com.bestspa.spa.client.Utiles.DatabaseHelper;
import com.bestspa.spa.client.Utiles.GPSTracker;
import com.bestspa.spa.client.Utiles.Links;
import com.bestspa.spa.client.Utiles.SessionManager;
import com.bestspa.spa.client.Utiles.UtilFun;
import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CustomerSettingActivity";
    SessionManager sessionManager;
    private Button btnSubmit;
    private Button btnUpload10Images;
    private TextView buttonText;
    private AutoCompleteTextView countryAutoCompleteTextViewStaff;
    private String currentLatitue = "";
    private String currentLongitude = "";
    private EditText etAddress;
    private EditText etBusinessName;
    private EditText etPhone2;
    GPSTracker gps;
    private ImageView imgViewCustomerAddProfile;
    ArrayAdapter<String> phoneCodeAdapter;
    private Spinner spPhoneCode;
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
    CustomerSettingActivity.ImageListAdapter imageListAdapter;
    ArrayList<String> imageList;
    ArrayList<String> countryType;
    final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    final String[] LOCATION_PERMISSION = new String[]{FINE_LOCATION, COARSE_LOCATION};
    Boolean mLocationPermissionGranted = false;
    String address, country, pincode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_setting);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        sessionManager = new SessionManager(this);
        databaseHelper = DatabaseHelper.getInstance(this);
        user = sessionManager.getUserDetails();
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
        btnSave = (Button) findViewById(R.id.btnstaffSave);
        ll_header = (LinearLayout) findViewById(R.id.layoutHead);
        this.imgViewCustomerAddProfile = (ImageView) findViewById(R.id.imgViewCustomerAddProfile);
        txt_detail = (TextView) findViewById(R.id.txtdetail);
        txt_name = (TextView) findViewById(R.id.txtname);
        txt_address = (TextView) findViewById(R.id.txtaddress);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        rec_staffprofiles = (RecyclerView) findViewById(R.id.staffProfileImageLayout);
        rec_staffprofiles.setLayoutManager(new LinearLayoutManager(CustomerSettingActivity.this, LinearLayoutManager.HORIZONTAL, false));
        imgViewCustomerAddProfile = (ImageView) findViewById(R.id.imgViewCustomerAddProfile);
        expandableListView = (ExpandableListView) findViewById(R.id.exp_list);
        etAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocationPermissionGranted) {
                    Intent intent = new Intent(CustomerSettingActivity.this, GetCurrentLocationMap.class);
                    intent.putExtra(Constant.UserType, user.getUserTypes());
                    startActivityForResult(intent, 25);
                } else {
                    ActivityCompat.requestPermissions(CustomerSettingActivity.this, LOCATION_PERMISSION, Constant.REQUEST_FINE_LOCATION_CODE);
                }
            }
        });

        ll_header.setOnClickListener(this);
        this.btnSave.setOnClickListener(this);

        txt_detail.setText(getResources().getString(R.string.please));
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
                    Intent intent = new Intent(CustomerSettingActivity.this, GetCurrentLocationMap.class);
                    intent.putExtra("staffLatitude", currentLatitue);
                    intent.putExtra("staffLongitude", currentLongitude);
                    intent.putExtra(Constant.UserType, user.getUserTypes());
                    startActivityForResult(intent, 25);
                } else {
                    ActivityCompat.requestPermissions(CustomerSettingActivity.this, LOCATION_PERMISSION, Constant.REQUEST_FINE_LOCATION_CODE);

                }
            }
        });

        forSpinnerstaff();

        try {
            Log.d("imagesize", String.valueOf(user.getUserImage()));
            et_emailstaff.setText(user.getEmail());
            etBusinessName.setText(user.getUserName());
            if (user.getUserImage().size() > 0) {
                for (int g = 0; g < user.getUserImage().size(); g++) {
                    images.add(new Image(g, "", user.getUserImage().get(g)));
                }
            }
            etPhone2.setText(user.getPhone().split("-")[1]);
            for (int m = 0; m < phonecode.size(); m++) {
                if (phonecode.get(m).equals(user.getPhone().split("-")[0])) {
                    spPhoneCode.setSelection(m);
                }
            }

            etAddress.setText(user.getAddress());
            countryAutoCompleteTextViewStaff.setText(user.getCountry());

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {

        try {

            if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
                // Get a list of picked images
                images.clear();
                imageListAdapter.notifyDataSetChanged();
                images = ImagePicker.getImages(data);

                if (images.size() > 0) {
                    UtilFun.ShowProgres(this, getString(R.string.uploadingimg));
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
                                    Toast.makeText(CustomerSettingActivity.this, getString(R.string.file_size_more_error), Toast.LENGTH_SHORT).show();

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
            } else if (!data.getStringExtra("userLatitude").equals("")) {

                currentLatitue = data.getStringExtra("userLatitude");
                currentLongitude = data.getStringExtra("userLongitude");
                address = data.getStringExtra("userAddress");
                country = data.getStringExtra("userCountry");
                pincode = data.getStringExtra("userPinCode");

                etAddress.setText(address);
                countryAutoCompleteTextViewStaff.setText(country);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onActivityResult(requestCode, resultCode, data);
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
        this.countryAutoCompleteTextViewStaff.setOnItemClickListener(new CustomerSettingActivity.C13277());
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
                    sessionManager.setLogin(false);
                    startActivity(intent);
                    break;
                }
                onBackPressed();

                break;
            case R.id.llTxtPassword:

//                ChangePassword();

                break;

            case R.id.btnStaffLogout:

                sessionManager.logoutUser();
                intent = new Intent(this, LoginActivity.class);
                intent.addFlags(67108864);
                startActivity(intent);

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

            UtilFun.ShowProgres(this, getString(R.string.updatingprofile));
            ArrayList<String> latLong = new ArrayList<>();
            latLong.add(currentLatitue);
            latLong.add(currentLongitude);
            user.setLatLong(latLong);
            user.setEmail(et_emailstaff.getText().toString());
            user.setPhone(spPhoneCode.getSelectedItem().toString() + "-" + etPhone2.getText().toString());
            user.setAddress(etAddress.getText().toString());
            user.setCountry(countryAutoCompleteTextViewStaff.getText().toString());
            user.setUserImage(imageList);
            apiInterface.UpdateStaffUser(user, user.get_id()).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    UtilFun.DismissProgress();
                    Log.d(TAG, response.body().toString());
                    if (response.code() == 200) {
                        User updatedUser = response.body();
                        sessionManager.createLoginSession(updatedUser);
                        Toast.makeText(CustomerSettingActivity.this, getString(R.string.informationupdated), Toast.LENGTH_SHORT).show();
                        intent = new Intent(CustomerSettingActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(CustomerSettingActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
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
        if (images.size() <= 0) {
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
        } else if (currentLongitude.equals("") || currentLatitue.equals("")) {
            validate = false;
            Toast.makeText(this, getString(R.string.enbale_location), Toast.LENGTH_SHORT).show();
            return validate;
        }

        return validate;
    }


    public class ImageListAdapter extends RecyclerView.Adapter<CustomerSettingActivity.ImageListAdapter.MyViewHolder> {

        @NonNull
        @Override
        public CustomerSettingActivity.ImageListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CustomerSettingActivity.ImageListAdapter.MyViewHolder(LayoutInflater.from(CustomerSettingActivity.this).inflate(R.layout.image_show, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull CustomerSettingActivity.ImageListAdapter.MyViewHolder holder, final int position) {
            if (position == 0) {
                Glide.with(CustomerSettingActivity.this).load(Links.URL + imageList.get(position)).into(imgViewCustomerAddProfile);
            }
            Glide.with(CustomerSettingActivity.this).load(Links.URL + imageList.get(position)).into(holder.imageMain);
            holder.imageDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UtilFun.ShowProgres(CustomerSettingActivity.this, getString(R.string.deletingimage));
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
                            UtilFun.DismissProgress();
                            Toast.makeText(CustomerSettingActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
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
