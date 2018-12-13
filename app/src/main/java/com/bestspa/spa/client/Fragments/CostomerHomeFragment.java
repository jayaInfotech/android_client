package com.bestspa.spa.client.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.bestspa.spa.client.API.APIClient;
import com.bestspa.spa.client.API.APIInterface;
import com.bestspa.spa.client.Adapter.ExpandListAdapterCustomer;
import com.bestspa.spa.client.BussinessSettingActivity;
import com.bestspa.spa.client.Model.ServiceModel;
import com.bestspa.spa.client.Model.User;
import com.bestspa.spa.client.R;
import com.bestspa.spa.client.Utiles.GPSTracker;
import com.bestspa.spa.client.Utiles.SessionManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CostomerHomeFragment extends Fragment {


    View rootView;
    APIInterface apiInterface;
    ArrayList<User> users;
    User user;
    SessionManager sessionManager;
    GPSTracker gpsTracker;
    Intent intent;
    Activity activity;
    ExpandableListView ExpandList;
    public static ArrayList<ServiceModel> serviceModelArrayList;
    String currentLatitude,currentLongitude;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        activity = getActivity();
        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getUserDetails();
        gpsTracker = new GPSTracker(getActivity());
        currentLatitude = String.valueOf(gpsTracker.getLatitude());
        currentLongitude = String.valueOf(gpsTracker.getLongitude());

        apiInterface = APIClient.getClient().create(APIInterface.class);
        this.rootView = inflater.inflate(R.layout.customer_fragment_home, container, false);
        this.ExpandList = (ExpandableListView) this.rootView.findViewById(R.id.exp_list);

        apiInterface.GetServices().enqueue(new Callback<ArrayList<ServiceModel>>() {
            @Override
            public void onResponse(Call<ArrayList<ServiceModel>> call, Response<ArrayList<ServiceModel>> response) {
                if (response.code() == 200)
                {
                    serviceModelArrayList = response.body();
                    if (serviceModelArrayList.size()>0)
                    {
                        ExpandList.setAdapter(new ExpandListAdapterCustomer(getActivity(),serviceModelArrayList,currentLatitude,currentLongitude,user.get_id()));
                        ExpandList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                            @Override
                            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                                if (serviceModelArrayList.get(groupPosition).getSubServiceList().size() == 0)
                                {
                                    Toast.makeText(getActivity(),getString(R.string.no_service_found),Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                                return false;
                            }
                        });
                    }
                }else
                {
                    Toast.makeText(activity, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ServiceModel>> call, Throwable t) {
                Toast.makeText(activity, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}
