package com.tajln.vremenarapp.ui.grafi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tajln.vremenarapp.R;
import com.tajln.vremenarapp.data.NetworkManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GrafiFragment extends Fragment {

    private LineChart lineChart;
    private View view;

    public GrafiFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_grafi, container, false);

        lineChart = view.findViewById(R.id.chart1);
        this.view = view;

        getGrowth();
        return view;
    }

    public void getGrowth(){
        NetworkManager.updatelast30(lineChart);
    }
}