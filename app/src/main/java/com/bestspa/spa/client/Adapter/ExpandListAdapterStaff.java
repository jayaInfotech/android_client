package com.bestspa.spa.client.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bestspa.spa.client.API.APIClient;
import com.bestspa.spa.client.API.APIInterface;
import com.bestspa.spa.client.Model.ServiceModel;
import com.bestspa.spa.client.Model.SubServiceModel;
import com.bestspa.spa.client.R;
import com.bestspa.spa.client.Utiles.DatabaseHelper;
import com.bestspa.spa.client.Utiles.Links;
import com.bestspa.spa.client.Views.AlertDialogManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpandListAdapterStaff extends BaseExpandableListAdapter {

    public static ArrayList<String> alselectedNewSubservice = new ArrayList();
    String Currency = "USD";
    String CurrencySymbol = "$";
    private String Symbolname = "";
    AlertDialogManager alert = new AlertDialogManager();
    AlertDialogManager alertBox = new AlertDialogManager();
    private Activity context;
    private DatabaseHelper db;
    private String duration;
    private SharedPreferences.Editor editor;
    private int fl = 0;
    private ArrayList<ServiceModel> groups;
    private int lastExpandedPosition = -1;
    public ArrayList<String> serviceListSelected = new ArrayList();
    private SharedPreferences sharedPreferences;
    private String userId;
    APIInterface apiInterface;
    private static final String TAG = "ExpandListAdapterStaff";
    Boolean isDeleted;

    public ExpandListAdapterStaff(Activity context, ArrayList<ServiceModel> groups, String uid) {
        this.context = context;
        this.groups = groups;
        this.db = DatabaseHelper.getInstance(context);
        this.sharedPreferences = context.getSharedPreferences("MyPref", 0);
        this.editor = this.sharedPreferences.edit();
        this.userId = uid;
        apiInterface = APIClient.getClient().create(APIInterface.class);
    }

    @Override
    public int getGroupCount() {
        return this.groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ((ServiceModel) this.groups.get(groupPosition)).getSubServiceList().size() + 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return ((ServiceModel) this.groups.get(groupPosition)).getSubServiceList().size() + 1;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return (long) groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return (long) childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.service_row_staff, null);
        }
        TextView serviceName = (TextView) convertView.findViewById(R.id.serviceName);
        serviceName.setText(groups.get(groupPosition).getServiceName());
        Glide.with(context).load(Links.URL + groups.get(groupPosition).getServiceImage()).apply(new RequestOptions().transform(new RoundedCorners(72))).into((ImageView) convertView.findViewById(R.id.serviceImg));
        return convertView;

    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (childPosition == 0) {
            convertView = infalInflater.inflate(R.layout.add_last_row_subservice, null);
            ((LinearLayout) convertView.findViewById(R.id.llAddService)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ExpandListAdapterStaff.this.openAddService(groups.get(groupPosition).getServiceName(), groupPosition, childPosition);
                }
            });
            return convertView;
        }

        if (userId.equals(groups.get(groupPosition).getSubServiceList().get(childPosition-1).getMerchantId()))
        {
            convertView = infalInflater.inflate(R.layout.sub_service_row_text3, null);
            TextView subserviceName = (TextView) convertView.findViewById(R.id.subserviceName);
            TextView txtTimePrice = (TextView) convertView.findViewById(R.id.txtTimePrice);
            TextView txtDescription = (TextView) convertView.findViewById(R.id.txtDescription);
            ((TextView) convertView.findViewById(R.id.subserviceId)).setText(groups.get(groupPosition).getSubServiceList().get(childPosition - 1).get_id());
            subserviceName.setText(groups.get(groupPosition).getSubServiceList().get(childPosition - 1).getServiceName());
            txtDescription.setText(groups.get(groupPosition).getSubServiceList().get(childPosition - 1).getServiceDescription());
            txtTimePrice.setText(groups.get(groupPosition).getSubServiceList().get(childPosition - 1).getServiceDuration() + "mins " + this.Symbolname + " " + groups.get(groupPosition).getSubServiceList().get(childPosition - 1).getServicePrice());
            ((LinearLayout) convertView.findViewById(R.id.subServiceLayout)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    final Dialog editSer = new Dialog(ExpandListAdapterStaff.this.context);
                    editSer.requestWindowFeature(1);
                    editSer.setContentView(R.layout.add_service_dialog_staff);
                    editSer.show();
                    ((TextView) editSer.findViewById(R.id.txtHeader)).setText(context.getString(R.string.edit_your_service));
                    ((LinearLayout) editSer.findViewById(R.id.llDelete)).setVisibility(0);
                    Button btnDeleteSer = (Button) editSer.findViewById(R.id.btnDeleteSer);
                    ExpandListAdapterStaff.this.forSpinner((Spinner) editSer.findViewById(R.id.spService), groups.get(groupPosition).getSubServiceList().get(childPosition - 1).getServiceDuration());
                    Button btnCancelSer = (Button) editSer.findViewById(R.id.btnCancelSer);
                    final EditText etServicCost = (EditText) editSer.findViewById(R.id.etServicCost);
                    final EditText etServiceName = (EditText) editSer.findViewById(R.id.etServiceName);
                    final EditText etServicDescript = (EditText) editSer.findViewById(R.id.etServicDescript);
                    Button btnDoneSer = (Button) editSer.findViewById(R.id.btnDoneSer);
                    etServicCost.setHint(context.getString(R.string.priceName));
                    ((TextView) editSer.findViewById(R.id.txtsymbol)).setText(ExpandListAdapterStaff.this.Symbolname);
                    etServicCost.setText(groups.get(groupPosition).getSubServiceList().get(childPosition - 1).getServicePrice());
                    etServiceName.setText(groups.get(groupPosition).getSubServiceList().get(childPosition - 1).getServiceName());
                    etServicDescript.setText(groups.get(groupPosition).getSubServiceList().get(childPosition - 1).getServiceDescription());
                    btnCancelSer.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            editSer.dismiss();
                        }
                    });

                    btnDeleteSer.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            editSer.dismiss();
                            DeleteService(groups.get(groupPosition).get_id(), groups.get(groupPosition).getSubServiceList().get(childPosition - 1).get_id(), groupPosition, childPosition-1);
                        }
                    });

                    btnDoneSer.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {

                            if (etServiceName.getText().toString().equals("")) {
                                alert.showAlertDialog(context, "error", "please enter service name", Boolean.valueOf(false));
                                return;
                            }
                            if (etServicCost.getText().toString().equals("")) {
                                alert.showAlertDialog(context, "error", "please enter cost", Boolean.valueOf(false));
                                return;
                            }
                            if (!etServicCost.getText().toString().equals("") && etServicCost.getText().toString().charAt(0) == '0') {
                                alert.showAlertDialog(context, "error", "service cost should not start with 0", Boolean.valueOf(false));
                            } else if (duration.equals("Duration")) {
                                alert.showAlertDialog(context, "error", "please select duration", Boolean.valueOf(false));
                                return;
                            } else if (etServicDescript.getText().toString().equals("")) {
                                alert.showAlertDialog(context, "error", "please write description", Boolean.valueOf(false));
                                return;
                            }
                            final ProgressDialog pDialog = new ProgressDialog(context);
                            pDialog.setCanceledOnTouchOutside(false);
                            pDialog.setCancelable(false);
                            pDialog.setMessage(context.getString(R.string.pleasewait));
                            pDialog.show();

                            final SubServiceModel subServiceModel = new SubServiceModel();
                            subServiceModel.setServiceName(etServiceName.getText().toString());
                            subServiceModel.setServiceDuration(duration);
                            subServiceModel.setServicePrice(etServicCost.getText().toString());
                            subServiceModel.setServiceDescription(etServicDescript.getText().toString());
                            subServiceModel.setMerchantId(userId);

                            Log.d("update",new Gson().toJson(subServiceModel));

                            apiInterface.UpdateService(subServiceModel, groups.get(groupPosition).getServiceName(), groups.get(groupPosition).getSubServiceList().get(childPosition - 1).get_id()).enqueue(new Callback<SubServiceModel>() {
                                @Override
                                public void onResponse(Call<SubServiceModel> call, Response<SubServiceModel> response) {
                                    Log.d(TAG, response.toString());
                                    editSer.cancel();
                                    pDialog.dismiss();
                                    Toast.makeText(context, context.getString(R.string.service_update), Toast.LENGTH_SHORT).show();
                                    groups.get(groupPosition).getSubServiceList().set(childPosition-1,subServiceModel);
                                    notifyDataSetChanged();
                                }

                                @Override
                                public void onFailure(Call<SubServiceModel> call, Throwable t) {
                                    Log.d(TAG, t.getMessage());
                                    editSer.cancel();
                                    pDialog.dismiss();
                                    Toast.makeText(context, context.getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                                }
                            });
                         }
                    });
                }
            });

            return convertView;
        }
        convertView = infalInflater.inflate(R.layout.null_view,null);
        return convertView;

    }

    private void DeleteService(final String catId, String id, final int groupPosition, final int childPosition) {
        Boolean isDeleted = false;
        apiInterface.DeleteService(catId, id).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == 200) {
                    Toast.makeText(context, context.getString(R.string.service_deleted), Toast.LENGTH_SHORT).show();
                    groups.get(groupPosition).getSubServiceList().remove(childPosition);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, context.getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(context, context.getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
            }

        });
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    private void openAddService(final String catId, final int groupPosition, final int childPosition) {

        Log.d("cateId", catId);
        final Dialog browseDialogImg = new Dialog(this.context);
        browseDialogImg.requestWindowFeature(1);
        browseDialogImg.setContentView(R.layout.add_service_dialog_staff);
        browseDialogImg.show();
        Spinner spinner = (Spinner) browseDialogImg.findViewById(R.id.spService);
        forSpinner(spinner, null);
        Button btnCancelSer = (Button) browseDialogImg.findViewById(R.id.btnCancelSer);
        final EditText etServicCost = (EditText) browseDialogImg.findViewById(R.id.etServicCost);
        etServicCost.setHint("Price");
        ((TextView) browseDialogImg.findViewById(R.id.txtsymbol)).setText(this.Symbolname);
        Button btnDoneSer = (Button) browseDialogImg.findViewById(R.id.btnDoneSer);
        btnCancelSer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                browseDialogImg.dismiss();
            }
        });
        btnDoneSer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText etServicDescript = (EditText) browseDialogImg.findViewById(R.id.etServicDescript);
                EditText etServicName = (EditText) browseDialogImg.findViewById(R.id.etServiceName);

                if (etServicName.getText().toString().equals("")) {

                    alert.showAlertDialog(context, "error", "please enter service name", Boolean.valueOf(false));
                    return;
                }
                if (etServicCost.getText().toString().equals("")) {
                    alert.showAlertDialog(context, "error", "please enter cost", Boolean.valueOf(false));
                    return;
                }
                if (!etServicCost.getText().toString().equals("") && etServicCost.getText().toString().charAt(0) == '0') {
                    alert.showAlertDialog(context, "error", "service cost should not start with 0", Boolean.valueOf(false));
                } else if (duration.equals("Duration")) {
                    alert.showAlertDialog(context, "error", "please select duration", Boolean.valueOf(false));
                    return;
                } else if (etServicDescript.getText().toString().equals("")) {
                    alert.showAlertDialog(context, "error", "please write description", Boolean.valueOf(false));
                    return;
                }
                final ProgressDialog pDialog = new ProgressDialog(context);
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.setCancelable(false);
                pDialog.setMessage(context.getString(R.string.pleasewait));
                pDialog.show();

                final SubServiceModel subServiceModel = new SubServiceModel();
                subServiceModel.setServiceName(etServicName.getText().toString());
                subServiceModel.setServiceDuration(duration);
                subServiceModel.setServicePrice(etServicCost.getText().toString());
                subServiceModel.setServiceDescription(etServicDescript.getText().toString());
                subServiceModel.setMerchantId(userId);

                apiInterface.CreateService(subServiceModel, catId).enqueue(new Callback<SubServiceModel>() {
                    @Override
                    public void onResponse(Call<SubServiceModel> call, Response<SubServiceModel> response) {
                        Log.d(TAG, response.toString());
                        browseDialogImg.cancel();
                        pDialog.dismiss();
                        Toast.makeText(context, context.getString(R.string.serviceinsertedsuc), Toast.LENGTH_SHORT).show();
                        groups.get(groupPosition).getSubServiceList().add(subServiceModel);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<SubServiceModel> call, Throwable t) {
                        Log.d(TAG, t.getMessage());
                        browseDialogImg.cancel();
                        pDialog.dismiss();
                        Toast.makeText(context, context.getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void forSpinner(Spinner spStaffService, String dur) {
        duration = "Duration";
        final String[] durationCode = new String[]{"Duration", "15", "30", "45", "60", "75", "90", "105", "120", "135", "150", "165", "180"};
        ArrayList<String> alselectedTime = new ArrayList();
        alselectedTime.add("Duration");
        alselectedTime.add("15 minutes");
        alselectedTime.add("30 minutes");
        alselectedTime.add("45 minutes");
        alselectedTime.add("60 minutes");
        alselectedTime.add("75 minutes");
        alselectedTime.add("90 minutes");
        alselectedTime.add("105 minutes");
        alselectedTime.add("120 minutes");
        alselectedTime.add("135 minutes");
        alselectedTime.add("150 minutes");
        alselectedTime.add("165 minutes");
        alselectedTime.add("180 minutes");
        int index = 0;
        ArrayAdapter<String> languageAdapter = new ArrayAdapter(this.context, R.layout.my_spinner_style, alselectedTime);
        languageAdapter.setDropDownViewResource(R.layout.my_spinner_style);
        spStaffService.setAdapter(languageAdapter);
        if (dur != null) {
            int i = 0;
            while (i < durationCode.length) {
                try {
                    if (durationCode[i].equals(dur)) {
                        index = i;
                        break;
                    }
                    i++;
                } catch (Exception e) {
                }
            }
            spStaffService.setSelection(index);
        }
        spStaffService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View view, int arg2, long arg3) {
                ExpandListAdapterStaff.this.duration = durationCode[arg2];
                ((TextView) arg0.getChildAt(0)).setTextColor(ExpandListAdapterStaff.this.context.getResources().getColor(R.color.greenTxt));
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void onGroupExpanded(int groupPosition) {
        ExpandableListView ExpandList = (ExpandableListView) this.context.getWindow().getDecorView().findViewById(16908290).findViewById(R.id.exp_list);
        if (!(this.lastExpandedPosition == -1 || groupPosition == this.lastExpandedPosition)) {
            ExpandList.collapseGroup(this.lastExpandedPosition);
        }
        super.onGroupExpanded(groupPosition);
        this.lastExpandedPosition = groupPosition;
    }
}
