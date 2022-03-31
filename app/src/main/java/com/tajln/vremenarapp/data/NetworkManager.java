package com.tajln.vremenarapp.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tajln.vremenarapp.R;
import com.tajln.vremenarapp.config.EnvVal;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class NetworkManager {
    private static NetworkManager instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Context ctx;

    private NetworkManager(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized NetworkManager getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkManager(context);
        }
        return instance;
    }

    public static void initialize(Context context){
        if (instance == null) {
            instance = new NetworkManager(context);
        }
    }

    public static synchronized NetworkManager getInstance(){
        if (instance != null) {
            return instance;
        }
        throw new java.lang.Error("Inizialize first with NetworkManager.initialize()");
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void updateToLatest(View activity){

        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url ="http://tajln.dev.uk.to:8080/podatki/latest";

        TextView lastUpdate = activity.findViewById(R.id.text_lastUpdate);
        TextView temp = activity.findViewById(R.id.text_temp);
        TextView vlaga = activity.findViewById(R.id.text_vlaga);
        TextView pritisk = activity.findViewById(R.id.text_pritisk);
        TextView svetloba = activity.findViewById(R.id.text_svetloba);
        TextView oxidacije = activity.findViewById(R.id.text_oxidacije);
        TextView redukcije = activity.findViewById(R.id.text_redukcije);
        TextView nh3 = activity.findViewById(R.id.text_nh3);

        JSONObject object = new JSONObject();
        try {
            object.put("kljuc","A35FNA1F");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                response -> {
                    try {
                        Timestamp cas = Timestamp.valueOf(response.getString("time").replaceAll("T", " ").replaceAll("[.]000[+]00:00", ""));
                        LocalDateTime triggerTime =
                                LocalDateTime.ofInstant(Instant.ofEpochMilli(cas.getTime()),
                                        TimeZone.getDefault().toZoneId());

                        int hour = triggerTime.getHour();
                        String h;
                        if(hour < 10)
                            h = "0" + hour;
                        else
                            h = String.valueOf(hour);

                        int min = triggerTime.getMinute();
                        String m;
                        if(min < 10)
                            m = "0" + min;
                        else
                            m = String.valueOf(min);

                        int sec = triggerTime.getSecond();
                        String s;
                        if(sec < 10)
                            s = "0" + sec;
                        else
                            s = String.valueOf(sec);

                        lastUpdate.setText("Osveženo: " + triggerTime.getDayOfMonth() + ". " + triggerTime.getMonthValue() + ". " + triggerTime.getYear() + " " + h + ":" + m + ":" + s);
                        temp.setText(round(response.getDouble("temperatura"), 1) + " °C");
                        vlaga.setText(round(response.getDouble("vlaga"), 1) + " %");
                        pritisk.setText(round(response.getDouble("pritisk") / 1000, 3) + " bar");
                        svetloba.setText(round(response.getDouble("svetloba"), 2) + " Lux");
                        oxidacije.setText(round(response.getDouble("oxid"),2) + " kO");
                        redukcije.setText(round(response.getDouble("redu"),2) + " kO");
                        nh3.setText(round(response.getDouble("nh3"),2) + " kO");
                    /*
                            "Vlaga: " + obj.getString("vlaga") + " %\n"+
                            "Pritisk: " + obj.getString("pritisk") + " hPa\n"+
                            "Temperatura: " + obj.getString("temperatura") + " °C\n"+
                            "Svetloba: " + obj.getString("svetloba") + " Lux\n"+
                            "Oxidacije: " + obj.getString("oxid") + " kO\n"+
                            "Redukcije: " + obj.getString("redu") + " kO\n"+
                            "NH3: " + obj.getString("nh3") + " kO");

                     */
                        //textView.setText(obj.getString("vlaga"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        lastUpdate.setText("Napaka pri obravnavanju odgovora strežnika");
                    }
                },
                error -> lastUpdate.setText("Težava pri pridobivanju podatkov"));

        queue.add(jsonObjectRequest);
    }

    public static void updatelast30(LineChart lineChart, String kaj) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url ="http://tajln.dev.uk.to:8080/podatki/last30";

        List<Entry> lineEntry = new ArrayList<>();

        String req = "";
        switch(kaj){
            case "Vlaga":
                req = "vlaga";
                break;
            case "Pritisk":
                req = "pritisk";
                break;
            case "Temperatura":
                req = "temperatura";
                break;
            case "Svetloba":
                req = "svetloba";
                break;
            case "Oxidacije":
                req = "oxid";
                break;
            case "Redukcije":
                req = "redu";
                break;
            case "NH3":
                req = "nh3";
                break;
        }

        JSONObject object = new JSONObject();
        try {
            object.put("kljuc","A35FNA1F");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String finalReq = req;
        CustomJsonArrayRequest jsonArrayRequest = new CustomJsonArrayRequest(Request.Method.POST, url, object,
                arr -> {
                    try {
                        for (int i=0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);
                            lineEntry.add(new Entry(i*20, (float) o.getDouble(finalReq)));
                        }
                        LineDataSet lineDataSet = new LineDataSet(lineEntry, kaj);
                        lineDataSet.setColors(ColorTemplate.rgb("#000000"));

                        LineData lineData = new LineData(lineDataSet);

                        lineChart.setVisibility(View.VISIBLE);
                        lineChart.setData(lineData);

                        Description description = new Description();
                        description.setText("Čas");
                        lineChart.setDescription(description);
                        lineChart.invalidate();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("Napaka pri obravnavanju odgovora strežnika");
                    }
        }, System.out::println);

        queue.add(jsonArrayRequest);
    }

    public interface VolleyCallBack {
        void onSuccess(JSONObject body);
    }

    public static void getToken(String CODE, String CODE_VERIFIER, final VolleyCallBack callBack) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url ="https://api.one-account.io/v1/oauth/token";

        StringRequest sr = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject body = new JSONObject(response);
                        System.out.println(body);
                        callBack.onSuccess(body);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("HttpClient", "error: " + error.toString()))
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("code",CODE);
                params.put("redirect_uri",EnvVal.REDIRECT_URI);
                params.put("code_verifier",CODE_VERIFIER);
                params.put("grant_type","authorization_code");
                params.put("client_id", EnvVal.CLIENT_ID);

                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
    }

    public static void getUserInfo(final VolleyCallBack callBack) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url ="https://api.one-account.io/v1/oauth/userinfo";

        StringRequest sr = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject body = new JSONObject(response);
                        callBack.onSuccess(body);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("HttpClient", "error: " + error.toString()))
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                try {
                    params.put("Authorization",EnvVal.TokenBody.getString("token_type") + " " + EnvVal.TokenBody.getString("access_token"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };
        queue.add(sr);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}