package com.bestspa.spa.client;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bestspa.spa.client.API.APIClient;
import com.bestspa.spa.client.API.APIInterface;
import com.bestspa.spa.client.Adapter.NavDrawerListAdapter;
import com.bestspa.spa.client.Fragments.StaffBookingFragment;
import com.bestspa.spa.client.Fragments.CostomerHomeFragment;
import com.bestspa.spa.client.Fragments.CustomerBookingFragment;
import com.bestspa.spa.client.Fragments.FavouritesFragment;
import com.bestspa.spa.client.Fragments.StaffHomeFragment;
import com.bestspa.spa.client.Fragments.CustomerNotificationFragment;
import com.bestspa.spa.client.Model.NavDrawerItem;
import com.bestspa.spa.client.Model.User;
import com.bestspa.spa.client.Utiles.Constant;
import com.bestspa.spa.client.Utiles.Links;
import com.bestspa.spa.client.Utiles.SessionManager;
import com.bestspa.spa.client.Utiles.UtilFun;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.tt.whorlviewlibrary.WhorlView;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ArrayList<NavDrawerItem> navDrawerItems;
    String[] navMenuTitles;
    TypedArray navMenuIcons;
    NavDrawerListAdapter navDrawerListAdapter;
    ListView listDrawer;
    SessionManager sessionManager;
    Intent intent;
    String newToken;
    Boolean notiUserType = false;
    DrawerLayout drawerLayout;
    LinearLayout drawerll;
    User user;
    Toolbar toolbar;
    APIInterface apiInterface;
    private static final String TAG = "BussinessSettingActivit";
    android.app.Fragment app_fragment;
    Fragment fragment;
    Boolean mLocationPermissionGranted;
    CircleImageView iv_profile;
    final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    final String[] LOCATION_PERMISSION = new String[]{FINE_LOCATION, COARSE_LOCATION};
    int fragPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
                if(user.getFcmToken().equals(newToken))
                {
                    Toast.makeText(MainActivity.this,"toast match",Toast.LENGTH_SHORT).show();
                }
                try {

                    if((!user.getFcmToken().equals(newToken)) && (!TextUtils.isEmpty(newToken)))
                        apiInterface.UpdateToken(newToken,user.get_id()).enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if (response.code() == 200)
                                {
                                    user = response.body();
                                    sessionManager.createLoginSession(user);
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {

                            }
                        });

                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        UtilFun.ShowProgres(this, getString(R.string.pleasewait));

        fragPosition = getIntent().getIntExtra("position", 0);
        notiUserType  = getIntent().getBooleanExtra(Constant.FromNotiUserType,false);
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
                Log.e("DB", "PERMISSION GRANTED");
            }
            mLocationPermissionGranted = true;
        }

        sessionManager = new SessionManager(this);
        user = sessionManager.getUserDetails();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface.GetUser(user.get_id()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                UtilFun.DismissProgress();
                if (response.code() == 200) {
                    user = response.body();
                    if (TextUtils.isEmpty(user.getFcmToken()))
                    {
                        apiInterface.UpdateToken(newToken,user.get_id()).enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if (response.code() == 200)
                                {
                                    user = response.body();
                                    sessionManager.createLoginSession(user);
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {

                            }
                        });
                    }
                    sessionManager.createLoginSession(user);
                    try {
                        if (user.getUserTypes().equals(Constant.Customer)) {
                            if (user.getUserImage().size() == 0 ||
                                    user.getEmail().equals("") ||
                                    user.getUserName().equals("") ||
                                    user.getPhone().equals("") ||
                                    user.getAddress().equals("") ||
                                    user.getCountry().equals("") ||
                                    user.getLatLong().size() == 0 )
                            {
                                Toast.makeText(MainActivity.this, getString(R.string.youmust_provide), Toast.LENGTH_SHORT).show();
                                intent = new Intent(MainActivity.this, CustomerSettingActivity.class);
                                intent.setFlags(67108864);
                                startActivity(intent);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, getString(R.string.youmust_provide), Toast.LENGTH_SHORT).show();
                        intent = new Intent(MainActivity.this, CustomerSettingActivity.class);
                        intent.setFlags(67108864);
                        startActivity(intent);
                    }

                    try {
                        if (user.getUserTypes().equals(Constant.Business)) {
                            if (user.getUserImage().size() == 0 ||
                                    user.getEmail().equals("") ||
                                    user.getMerchantid().getBusinessName().equals("") ||
                                    user.getPhone().equals("") ||
                                    user.getAddress().equals("") ||
                                    user.getCountry().equals("") ||
                                    user.getLatLong().size() == 0)
                            {
                                Toast.makeText(MainActivity.this, getString(R.string.youmust_provide), Toast.LENGTH_SHORT).show();
                                intent = new Intent(MainActivity.this, BussinessSettingActivity.class);
                                intent.setFlags(67108864);
                                startActivity(intent);
                            }
                        }

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, getString(R.string.youmust_provide), Toast.LENGTH_SHORT).show();
                        intent = new Intent(MainActivity.this, BussinessSettingActivity.class);
                        intent.setFlags(67108864);
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                UtilFun.DismissProgress();
                Toast.makeText(MainActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar)));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.hamburgerw));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        iv_profile = (CircleImageView) findViewById(R.id.imageView_round);
        listDrawer = (ListView) findViewById(R.id.list_slidermenu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerll = (LinearLayout) findViewById(R.id.drawerll);
        navDrawerItems = new ArrayList<>();
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        this.navDrawerItems.add(new NavDrawerItem(this.navMenuTitles[1], this.navMenuIcons.getResourceId(1, -1)));
        this.navDrawerItems.add(new NavDrawerItem(this.navMenuTitles[2], this.navMenuIcons.getResourceId(2, -1)));
        this.navDrawerItems.add(new NavDrawerItem(this.navMenuTitles[3], this.navMenuIcons.getResourceId(3, -1)));
        this.navDrawerItems.add(new NavDrawerItem(this.navMenuTitles[4], this.navMenuIcons.getResourceId(4, -1)));
        this.navDrawerItems.add(new NavDrawerItem(this.navMenuTitles[5], this.navMenuIcons.getResourceId(5, -1)));
        this.navDrawerItems.add(new NavDrawerItem(this.navMenuTitles[6], this.navMenuIcons.getResourceId(6, -1)));
        this.navMenuIcons.recycle();
        navDrawerListAdapter = new NavDrawerListAdapter(this, navDrawerItems);
        listDrawer.setAdapter(navDrawerListAdapter);
        listDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                displayView(position);
            }
        });
        if (user.getUserImage().size() > 0) {
//            Glide.with(this).load(Links.URL+user.getUserImage().get(0)).into(iv_profile);

            Glide.with(this).load(Links.URL + user.getUserImage().get(0)).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    iv_profile.setImageDrawable(getResources().getDrawable(R.drawable.upload));
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(iv_profile);
        }
        if (user.getUserTypes().equals(Constant.Customer)) {
            ((TextView) findViewById(R.id.userName)).setText(user.getUserName());
        } else {
            ((TextView) findViewById(R.id.userName)).setText(user.getMerchantid().getBusinessName());
        }



        if(notiUserType)
        {
            fragPosition = 1;
        }

        displayView(fragPosition);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void displayView(int position) {
        fragPosition = position;
        switch (position) {
            case 0:
                setTitle(getString(R.string.home));

                if (user.getUserTypes().equals(Constant.Customer)) {
                    fragment = new CostomerHomeFragment();

                } else {

                    app_fragment = new StaffHomeFragment();
                }
                break;
            case 1:
                setTitle(getString(R.string.bookings));

                Bundle bundle;
                if (user.getUserTypes().equals(Constant.Customer)) {
                    fragment = new CustomerBookingFragment();
                    bundle = new Bundle();
                    bundle.putString("reloadMessage", "received");
                    fragment.setArguments(bundle);
                    break;
                }
                Log.d("fragemtn", "StaffBookingFragment");
                fragment = new StaffBookingFragment();
                bundle = new Bundle();
                bundle.putString("reloadMessage", "awt");
                fragment.setArguments(bundle);
                break;
            case 2:
                setTitle(getString(R.string.notificationheader));
                fragment = new CustomerNotificationFragment();
                break;
            case 3:
                fragment = new FavouritesFragment();
                break;
            case 4:
                setTitle(getString(R.string.settings));
//                fragment = new SettingsFragment();
                if (user.getUserTypes().equals(Constant.Customer)) {
                    intent = new Intent(MainActivity.this, CustomerSettingActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, BussinessSettingActivity.class);
                }
                startActivity(intent);

                break;
            case 5:
                shareEasySpa();
                break;
            case 6:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", getString(R.string.adminMailId), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.getting_support_from) + " " + user.getUserTypes());
                startActivity(Intent.createChooser(emailIntent, "Send email..."));

                break;
        }
        Log.d("getFragmentManager", String.valueOf(getFragmentManager().getBackStackEntryCount()));
        Log.d("getSupportFragmentManag", String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));

        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.frame_container)).commit();
        }
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.frame_container)).commit();
        }
        if (fragment != null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).addToBackStack("fragment").commit();
            this.listDrawer.setItemChecked(position, true);
            this.listDrawer.setSelection(position);
            setTitle(this.navMenuTitles[position]);
            this.drawerLayout.closeDrawer(Gravity.LEFT);
        } else if (app_fragment != null) {

            getFragmentManager().beginTransaction().replace(R.id.frame_container, app_fragment).addToBackStack("appfragment").commit();
            this.listDrawer.setItemChecked(position, true);
            this.listDrawer.setSelection(position);
            setTitle(this.navMenuTitles[position]);
            this.drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void onBackPressed() {
        if (user.getUserTypes().equals(Constant.Business)) {
            if (getFragmentManager().getBackStackEntryCount() > 0 && fragPosition != 0) {
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.frame_container)).commit();
                }
                fragPosition = 0;
                app_fragment = new StaffHomeFragment();
                getFragmentManager().beginTransaction().replace(R.id.frame_container, app_fragment).addToBackStack("appfragment").commit();
                this.listDrawer.setItemChecked(fragPosition, true);
                this.listDrawer.setSelection(fragPosition);
                setTitle(this.navMenuTitles[fragPosition]);
                this.drawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                super.onBackPressed();
            }
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.frame_container)).commit();
                fragPosition = 0;
                fragment = new CostomerHomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).addToBackStack("fragment").commit();
                this.listDrawer.setItemChecked(fragPosition, true);
                this.listDrawer.setSelection(fragPosition);
                setTitle(this.navMenuTitles[fragPosition]);
                this.drawerLayout.closeDrawer(Gravity.LEFT);
            }else
            {
                super.onBackPressed();
            }

        }


    }

    private void shareEasySpa() {
        Intent sharingIntent = new Intent();
        sharingIntent.setAction("android.intent.action.SEND");
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra("android.intent.extra.SUBJECT", "EasySpa");
        sharingIntent.putExtra("android.intent.extra.TEXT", "EasySpa - Spa & Salon booking mobile app. Download and Sign up!\n https://play.google.com/store/apps/details?id=easyspa.spa.com.easyspa2  \n\nhttp://appstore.com/easyspa\nhttp://www.easyspa.org");
        startActivity(Intent.createChooser(sharingIntent, "Share via EasySpa"));
    }


    public void setTitle(CharSequence title) {
        toolbar.setTitle(title);
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

                } else {
                    ActivityCompat.requestPermissions(this, LOCATION_PERMISSION, Constant.REQUEST_FINE_LOCATION_CODE);

                }
            }

            case Constant.WRITE_PERMISSION_CODE: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getString(R.string.you_must_grant_write_permission), Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.WRITE_PERMISSION_CODE);
                }
            }
        }
    }
}
