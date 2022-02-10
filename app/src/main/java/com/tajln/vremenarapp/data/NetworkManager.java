package com.tajln.vremenarapp.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.View;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    public static void updateToLatest(TextView textView){

        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url ="http://tajln.dev.uk.to:8080/podatki/latest";


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        textView.setText(response);
                        //textView.setText(obj.getString("vlaga"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        textView.setText("Napaka pri obravnavanju odgovora strežnika");
                    }
                }, error -> {
                    textView.setText("Težava pri pridobivanju podatkov");
                });

        queue.add(stringRequest);
    }

    public static void updatelast30(LineChart lineChart) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url ="http://tajln.dev.uk.to:8080/podatki/last30";

        List<Entry> lineEntry = new ArrayList<>();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        for (int i=0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);
                            lineEntry.add(new Entry(i*20, (float) o.getDouble("vlaga")));
                        }
                        LineDataSet lineDataSet = new LineDataSet(lineEntry, "Vlaga");
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
                }, error -> {
            System.out.println(error);
        });

        queue.add(stringRequest);
    }

    public static void updateChart(LineChart lineChart){

    }
}