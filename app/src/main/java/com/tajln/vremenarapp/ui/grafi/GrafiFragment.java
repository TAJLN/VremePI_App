package com.tajln.vremenarapp.ui.grafi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.LineChart;
import com.tajln.vremenarapp.R;
import com.tajln.vremenarapp.data.NetworkManager;

public class GrafiFragment extends Fragment{

    private LineChart lineChart;
    private View view;

    public GrafiFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_grafi, container, false);

        String[] arraySpinner = new String[] {
                "Vlaga", "Pritisk", "Temperatura", "Svetloba", "Oxidacije", "Redukcije", "NH3"
        };
        Spinner s = view.findViewById(R.id.spinner);
        if(s != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(),
                    android.R.layout.simple_spinner_item, arraySpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            s.setAdapter(adapter);
            s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    NetworkManager.updatelast30(lineChart, String.valueOf(s.getSelectedItem()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        lineChart = view.findViewById(R.id.chart1);
        this.view = view;

        getGrowth();
        return view;
    }

    public void getGrowth(){
        Spinner spinner = view.findViewById(R.id.spinner);
        NetworkManager.updatelast30(lineChart, (String) spinner.getSelectedItem());
    }
}