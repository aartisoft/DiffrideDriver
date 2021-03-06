package com.diff.provider.Activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.diff.provider.Adapter.NotificationAdapter;
import com.diff.provider.DiffApplication;
import com.diff.provider.Helper.CustomDialog;
import com.diff.provider.Helper.SharedHelper;
import com.diff.provider.Helper.URLHelper;
import com.diff.provider.Models.AccessDetails;
import com.diff.provider.Models.NotificatonModel;
import com.diff.provider.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.diff.provider.DiffApplication.trimMessage;

public class ActivityNotification extends AppCompatActivity {

    ImageView backArrow;
    RecyclerView recyclerView;
    RelativeLayout errorLayout;
    CustomDialog customDialog;

    Context context = ActivityNotification.this;
    NotificationAdapter notificationAdapter;
    NotificatonModel notificatonModel;

    List<NotificatonModel> model = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        FindviewById();
    }

    public void FindviewById(){
        backArrow= findViewById(R.id.backArrow);
        recyclerView= findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.GONE);
        errorLayout= findViewById(R.id.errorLayout);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        getNotificationList();

    }

    private void getNotificationList() {

        try {
            customDialog = new CustomDialog(context);
            customDialog.setCancelable(false);

            if (!customDialog.isShowing()) {
                customDialog.show();
            }

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(AccessDetails.serviceurl + URLHelper.NOTIFICATION_LIST, new com.android.volley.Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        Log.e("GetServices", response.toString());
                        if (response.length() > 0) {
                            model.clear();
                            notificatonModel = new NotificatonModel();
                            for (int i=0;i<response.length();i++){
                                notificatonModel.setId(response.optJSONObject(i).optInt("id"));
                                notificatonModel.setPromoCode(response.optJSONObject(i).optString("promo_code"));
                                notificatonModel.setDiscount(response.optJSONObject(i).optInt("discount"));
                                notificatonModel.setDiscountType(response.optJSONObject(i).optString("discount_type"));
                                notificatonModel.setExpiration(response.optJSONObject(i).optString("expiration"));
                                notificatonModel.setStatus(response.optJSONObject(i).optString("status"));
                                notificatonModel.setDeletedAt(response.optJSONObject(i).optJSONObject("deleted_at"));
                                notificatonModel.setCurrency(SharedHelper.getKey(context,"currency"));

                                model.add(notificatonModel);
                            }

                            viewset();

                            if (customDialog.isShowing()) {
                                customDialog.dismiss();
                            }

                        }else {
                            if (customDialog.isShowing()) {
                                customDialog.dismiss();
//                                Toast.makeText(context,"No Notifications available",Toast.LENGTH_LONG).show();
                                recyclerView.setVisibility(View.GONE);
                                errorLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                        String json = null;
                        String Message;
                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {

                            try {
                                JSONObject errorObj = new JSONObject(new String(response.data));

                                if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                    try {
                                        Toast.makeText(context,errorObj.optString("message"),Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(context,getString(R.string.something_went_wrong),Toast.LENGTH_LONG).show();
                                    }

                                } else if (response.statusCode == 401) {

                                } else if (response.statusCode == 422) {

                                    json = trimMessage(new String(response.data));
                                    if (json != "" && json != null) {
                                        Toast.makeText(context,json,Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(context,getString(R.string.please_try_again),Toast.LENGTH_LONG).show();
                                    }

                                } else if (response.statusCode == 503) {
                                    Toast.makeText(context,getString(R.string.server_down),Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context,getString(R.string.please_try_again),Toast.LENGTH_LONG).show();

                                }

                            } catch (Exception e) {
                                Toast.makeText(context,getString(R.string.something_went_wrong),Toast.LENGTH_LONG).show();
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "Bearer "+ SharedHelper.getKey(context, "access_token"));
                    return headers;
                }
            };

            DiffApplication.getInstance().addToRequestQueue(jsonArrayRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewset(){
        if(model!=null && !model.isEmpty()) {
            notificationAdapter = new NotificationAdapter(model);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(notificationAdapter);
            recyclerView.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
        }else {
            recyclerView.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

}
