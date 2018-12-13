package com.bestspa.spa.client.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bestspa.spa.client.API.APIClient;
import com.bestspa.spa.client.API.APIInterface;
import com.bestspa.spa.client.MainActivity;
import com.bestspa.spa.client.Model.Booking;
import com.bestspa.spa.client.Model.User;
import com.bestspa.spa.client.R;
import com.bestspa.spa.client.Utiles.Constant;
import com.bestspa.spa.client.Utiles.Links;
import com.bestspa.spa.client.Utiles.SessionManager;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerNotificationFragment extends Fragment {

    View rootView;
    SessionManager sessionManager;
    User user;
    private LinearLayout blankNotificationLayoutCus;
    private Dialog contactDialog;
    Context context;
    private ScrollView custNotificationScroll;
    ArrayList<Booking> bookingArrayList;
    ArrayList<Booking> bookingWaitingArrayList;
    ArrayList<Booking> bookingConfirmedArrayList;
    ArrayList<Booking> bookingCanceledArrayList;
    APIInterface apiInterface;
    LinearLayout staffNotificationLayout, blankNotificationLayout;
    ScrollView staffNotificationScroll;
    VideoView videoView;
    private String uriPathLarge = "";
    int i;
    Button btnViewDetail, btnConfirmBooking, btnCompleteBooking, btnCancelBooking;
    String visitType;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getUserDetails();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        if (user.getUserTypes().equals(Constant.Business)) {
            rootView = inflater.inflate(R.layout.staff_fragment_notifications_video, container, false);
            initBusinessNotification();

        } else {
            rootView = inflater.inflate(R.layout.customer_fragment_notifications_video, container, false);
            initCustomerNotification();

        }


        return rootView;
    }

    private void initBusinessNotification() {

        apiInterface.GetMerchantBooking(user.get_id()).enqueue(new Callback<ArrayList<Booking>>() {
            @Override
            public void onResponse(Call<ArrayList<Booking>> call, Response<ArrayList<Booking>> response) {
                if (response.code() == 200) {
                    bookingArrayList = response.body();
                    staffNotificationScroll = (ScrollView) rootView.findViewById(R.id.staffNotificationScroll);
                    staffNotificationLayout = (LinearLayout) rootView.findViewById(R.id.staffNotificationLayout);
                    blankNotificationLayout = (LinearLayout) rootView.findViewById(R.id.blankNotificationLayout);
                    videoView = (VideoView) rootView.findViewById(R.id.videoView);
                    if (bookingArrayList.size() == 0) {
                        blankNotificationLayout.setVisibility(0);
                        staffNotificationScroll.setVisibility(8);
                        uriPathLarge = "android.resource://" + getActivity().getPackageName() + "/raw/easy_spa_video";
                        try {
                            videoView.setVisibility(0);
                            videoView.setVideoURI(Uri.parse(uriPathLarge));
                            videoView.start();
                            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    videoView.start();
                                }
                            });
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                    blankNotificationLayout.setVisibility(8);
                    videoView.setVisibility(8);
                    staffNotificationScroll.setVisibility(0);
                    staffNotificationLayout.removeAllViews();
                    if (bookingArrayList.size() > 0) {
                        bookingCanceledArrayList = new ArrayList<>();
                        bookingConfirmedArrayList = new ArrayList<>();
                        bookingWaitingArrayList = new ArrayList<>();
                        for (i = 0; i < bookingArrayList.size(); i++) {
                            if (bookingArrayList.get(i).getBookingstatus().equals(Constant.Confirmed)) {
                                bookingConfirmedArrayList.add(bookingArrayList.get(i));
                            } else if (bookingArrayList.get(i).getBookingstatus().equals(Constant.Canceled)) {
                                bookingCanceledArrayList.add(bookingArrayList.get(i));
                            }
                            if (bookingArrayList.get(i).getVisitType().equals(Constant.Declained))
                            {
                                Log.d("calling","yes");
                                
                                bookingArrayList.remove(bookingArrayList.get(i).getBookingstatus().equals(Constant.Declained));
                                continue;
                            }
                            Log.d("booking size",String.valueOf(bookingArrayList.size()));
                            final Booking booking = bookingArrayList.get(i);

                            apiInterface.GetUser(booking.getCustomerId()).enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    if (response.code() == 200) {
                                        User custUser = response.body();
                                        final View view;
                                        View v = ((LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.staff_notification_template, null);
                                        final TextView headerTxt = (TextView) v.findViewById(R.id.headerTxt);
                                        ((TextView) v.findViewById(R.id.bookingIdTxt)).setText(booking.get_id());
                                        ((TextView) v.findViewById(R.id.customerUserName)).setText(custUser.getUserName());
                                        ((TextView) v.findViewById(R.id.cusTumUserId)).setText(custUser.get_id());
                                        String bookingTypeString = "";
                                        TextView readStatus = (TextView) v.findViewById(R.id.readStatus);
                                        ((ImageView) v.findViewById(R.id.deleteBookingReceived)).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                            }
                                        });
                                        TextView custPlandText = (TextView) v.findViewById(R.id.bookingPlaceTxtC);
                                        CircleImageView circleImageView = (CircleImageView) v.findViewById(R.id.userImage);

                                        if (booking.getVisitType().equals(Constant.Visithome)) {
                                            visitType = getString(R.string.staff_will_visit);
                                        } else {
                                            visitType = getString(R.string.you_will_visit_spa);
                                        }
                                        custPlandText.setText(visitType);
                                        try {
                                            Glide.with(getActivity()).load(Links.URL+custUser.getUserImage().get(0)).into(circleImageView);
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy");
                                            Calendar calendar = Calendar.getInstance();
                                            calendar.setTime(sdf.parse(booking.getBookingdate()));

                                            ((TextView) v.findViewById(R.id.bookingDateTxtC)).setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                                            ((TextView) v.findViewById(R.id.bookingMonthTxtC)).setText(String.valueOf(calendar.get(Calendar.MONTH)));
                                            ((TextView) v.findViewById(R.id.bookingYearTxtC)).setText(String.valueOf(calendar.get(Calendar.YEAR)));
                                            ((TextView) v.findViewById(R.id.txtTimeNoti)).setText(booking.getBookingtime());

                                            btnViewDetail = (Button) v.findViewById(R.id.ViewDetailsBtn);
                                            btnCancelBooking = (Button) v.findViewById(R.id.cancelledBtn);
                                            btnCompleteBooking = (Button) v.findViewById(R.id.completeBtn);
                                            btnConfirmBooking = (Button) v.findViewById(R.id.confirmBookingBtn1);

                                            if (booking.getBookingstatus().equals(Constant.Canceled)) {
                                                btnCancelBooking.setVisibility(View.VISIBLE);
                                                headerTxt.setText(Constant.BOOKING_CANCEL);
                                                btnCancelBooking.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        ((MainActivity) getActivity()).displayView(0);
                                                    }
                                                });
                                            } else if (booking.getBookingstatus().equals(Constant.Pending)) {
                                                btnViewDetail.setVisibility(View.VISIBLE);
                                                headerTxt.setText(Constant.BOOKING_RECEIVE);
                                                btnViewDetail.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        ((MainActivity) getActivity()).displayView(1);
                                                    }
                                                });
                                            } else if (booking.getBookingstatus().equals(Constant.Confirmed)) {
                                                btnConfirmBooking.setVisibility(View.VISIBLE);
                                                headerTxt.setText(Constant.BOOKING_CONFIRM);
                                                btnConfirmBooking.setOnClickListener(new View.OnClickListener() {
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
                                                            calendar.setTime(sdf.parse(booking.getBookingdate() + " " + booking.getBookingtime()));
                                                            styr = calendar.get(Calendar.YEAR);
                                                            startday = calendar.get(Calendar.DAY_OF_MONTH);
                                                            stHour = calendar.get(Calendar.HOUR);
                                                            stMin = calendar.get(Calendar.MINUTE);
                                                            stmonth = calendar.get(Calendar.MONTH);
                                                            description = user.getMerchantid().getDescription();
                                                            location = user.getAddress();
                                                            addCalendarEvent(styr, stmonth, startday, stHour, stMin, visitType, description, location);

                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            }

                                            if (booking.getCompleted()) {
                                                Log.d("onLog", "onCompleted");
                                                headerTxt.setText(Constant.BOOKING_COMPLETED);
                                                btnViewDetail.setVisibility(View.GONE);
                                                btnConfirmBooking.setVisibility(View.GONE);
                                                btnCancelBooking.setVisibility(View.GONE);
                                                btnCompleteBooking.setVisibility(View.VISIBLE);
                                                btnCompleteBooking.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        // Write review of this jobs

                                                    }
                                                });
                                            }

                                            staffNotificationLayout.addView(v, 0, new LinearLayout.LayoutParams(-1, -2));

                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {

                                }
                            });
                        }
                    }

                } else {
                    Toast.makeText(getActivity(), getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ArrayList<Booking>> call, Throwable t) {

                Toast.makeText(getActivity(), getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void initCustomerNotification() {
        apiInterface.GetMerchantBooking(user.get_id()).enqueue(new Callback<ArrayList<Booking>>() {
            @Override
            public void onResponse(Call<ArrayList<Booking>> call, Response<ArrayList<Booking>> response) {
                if (response.code() == 200) {
                    bookingArrayList = response.body();
                    if (bookingArrayList.size() > 0) {
                        bookingConfirmedArrayList = new ArrayList<>();
                        bookingWaitingArrayList = new ArrayList<>();
                        for (int i = 0; i < bookingArrayList.size(); i++) {
                            if (bookingArrayList.get(i).getBookingstatus().equals(Constant.Confirmed)) {
                                bookingConfirmedArrayList.add(bookingArrayList.get(i));

                            } else if (bookingArrayList.get(i).getBookingstatus().equals(Constant.Canceled)) {
                                bookingCanceledArrayList.add(bookingArrayList.get(i));
                            }
                        }

                    }

                } else {

                }
            }

            @Override
            public void onFailure(Call<ArrayList<Booking>> call, Throwable t) {

            }
        });
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
}
