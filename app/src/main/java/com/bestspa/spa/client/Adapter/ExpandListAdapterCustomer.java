package com.bestspa.spa.client.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bestspa.spa.client.ChooseServiceActivity;
import com.bestspa.spa.client.Model.ServiceModel;
import com.bestspa.spa.client.Model.SubServiceModel;
import com.bestspa.spa.client.R;
import com.bestspa.spa.client.Utiles.Constant;
import com.bestspa.spa.client.Utiles.DatabaseHelper;
import com.bestspa.spa.client.Utiles.InternetCheck;
import com.bestspa.spa.client.Utiles.Links;
import com.bestspa.spa.client.Utiles.SessionManager;
import com.bestspa.spa.client.Views.AlertDialogManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

public class ExpandListAdapterCustomer extends BaseExpandableListAdapter {

    AlertDialogManager alert = new AlertDialogManager();
    private Activity context;
    private String curentLat = "0.00";
    private String curentLong = "0.00";
    private DatabaseHelper db;
    private ArrayList<ServiceModel> groups;
    private int lastExpandedPosition = -1;
    private String userId;
    SubServiceModel child;

    public ExpandListAdapterCustomer(Activity context, ArrayList<ServiceModel> groups, String curentLat, String curentLong, String userId) {
        this.context = context;
        this.groups = groups;
        this.curentLat = curentLat;
        this.curentLong = curentLong;
        this.userId = userId;
    }


    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
//        return groups.get(groupPosition).getSubServiceList().size()+1;
        return groups.get(groupPosition).getSubServiceList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
//        return ((ServiceModel) this.groups.get(groupPosition)).getSubServiceList().size() + 1;
        return ((ServiceModel) this.groups.get(groupPosition)).getSubServiceList().size() ;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            Activity activity = this.context;
            convertView = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.service_row, null);
        }
        TextView serviceName = (TextView) convertView.findViewById(R.id.serviceName);
        ((TextView) convertView.findViewById(R.id.serviceId)).setText(groups.get(groupPosition).get_id());
        serviceName.setText(groups.get(groupPosition).getServiceName());
        Glide.with(context).load(Links.URL + groups.get(groupPosition).getServiceImage()).apply(new RequestOptions().transform(new RoundedCorners(72))).into((ImageView) convertView.findViewById(R.id.serviceImg));
        return convertView;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Activity activity = this.context;
        LayoutInflater infalInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        if (childPosition == 0 && groups.get(groupPosition).getSubServiceList().size() == 0) {
//            return infalInflater.inflate(R.layout.please_wait_staff, null);
//        } else {

        ServiceModel group = groups.get(groupPosition);
        child = group.getSubServiceList().get(childPosition);
        convertView = infalInflater.inflate(R.layout.customer_home_new_service2, null);
        Glide.with(this.context).load(Links.URL + group.getServiceImage()).into((ImageView) convertView.findViewById(R.id.imgViewBack));
        ((Button) convertView.findViewById(R.id.btnNeatSer)).setOnClickListener(new View.OnClickListener() {

            /* renamed from: com.spa.easyspa.customer.ExpandListAdapter$1$1 */
            class C12301 implements DialogInterface.OnClickListener {
                C12301() {
                }

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }

            public void onClick(View v) {
                if (new InternetCheck(context).isNetworkAvalable()) {
                    Intent inNext = new Intent(context, ChooseServiceActivity.class);
                    inNext.putExtra(Constant.ServiceId, child.get_id() );
                    inNext.putExtra(Constant.MerchantID,child.getMerchantId());
                    context.startActivity(inNext);
                    return;
                }
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle(context.getResources().getString(R.string.message));
                alertDialog.setMessage("You have no internet connection.Please enable internet ");
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setButton("OK", new C12301());
                alertDialog.show();
            }
        });
        TextView businessName = (TextView) convertView.findViewById(R.id.businessName);
        TextView txtDuartion = (TextView) convertView.findViewById(R.id.txtDuration);
        TextView txtPrice = (TextView) convertView.findViewById(R.id.txtPrice);
        RatingBar aRatingBarCust = (RatingBar) convertView.findViewById(R.id.aRatingBarCust);
        ((LayerDrawable) aRatingBarCust.getProgressDrawable()).getDrawable(2).setColorFilter(this.context.getResources().getColor(R.color.ratingcolor), PorterDuff.Mode.SRC_ATOP);
        businessName.setText(child.getServiceName().toString().toUpperCase());
        String avgrating = String.valueOf(child.getServiceRatings().size());
        if (avgrating.contains(".")) {
            String[] avgratingArr = avgrating.split("\\.");
            int rating = Integer.parseInt(avgratingArr[0]);
            if (rating < 5 && Double.parseDouble(avgratingArr[1]) >= 0.5d) {
                rating++;
            }
            aRatingBarCust.setRating((float) rating);
        } else {
            aRatingBarCust.setRating((float) Integer.parseInt(avgrating));
        }
        Locale defaultLocale = Locale.getDefault();
        Currency currency = Currency.getInstance(defaultLocale);
        txtDuartion.setText(":  "+child.getServiceDuration()+" min");
        txtPrice.setText(":  "+child.getServicePrice()+" "+currency.getSymbol());

//        RelativeLayout rlEmail = (RelativeLayout) convertView.findViewById(R.id.rlEmail);
//        ((RelativeLayout) convertView.findViewById(R.id.rlCall)).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                if (child.getPhoneCust().contains("null")) {
//                    Toast.makeText(context, "Phone no incorrect or have no phone number", 1).show();
//                    return;
//                }
//                String number = "+" + child.getPhoneCust().replaceFirst("\\+", "");
//                Intent callIntent = new Intent("android.intent.action.CALL");
//                callIntent.setData(Uri.parse("tel:" + number));
//                context.startActivity(callIntent);
//            }
//        });
//        rlEmail.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                if (child.getEmailCust().contains("null")) {
//                    Toast.makeText(context, "Staff have no email id", 1).show();
//                } else {
//                    SendMail(child.getEmailCust());
//                }
//            }
//        });
//    }
        return convertView;   
    }

    private void openUrl(String url) {
        try {
            Intent i = new Intent("android.intent.action.VIEW");
            i.setData(Uri.parse(url));
            this.context.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SendMail(String toMail) {
        Intent emailIntent = new Intent("android.intent.action.SEND");
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra("android.intent.extra.EMAIL", new String[]{toMail});
        emailIntent.putExtra("android.intent.extra.SUBJECT", "EasySpa Staff");
        emailIntent.putExtra("android.intent.extra.TEXT", " ");
        try {
            this.context.startActivity(Intent.createChooser(emailIntent, this.context.getResources().getString(R.string.sentmail)));
        } catch (ActivityNotFoundException e) {
            this.alert.showAlertDialog(this.context, this.context.getResources().getString(R.string.error), this.context.getResources().getString(R.string.nomail), Boolean.valueOf(false));
        }
    }
   
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
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
