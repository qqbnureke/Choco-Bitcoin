package com.nurda.choco_bitcoin.fragments;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.nurda.choco_bitcoin.MainActivity;
import com.nurda.choco_bitcoin.R;
import com.nurda.choco_bitcoin.api.ApiClient;
import com.nurda.choco_bitcoin.api.ApiService;
import com.nurda.choco_bitcoin.mvp.model.GraphData;
import com.nurda.choco_bitcoin.mvp.model.Transaction;
import com.nurda.choco_bitcoin.utils.DateHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "InfoFragment";

    @BindView(R.id.spinner)
    Spinner spinner;

    @BindView(R.id.tv_bitcoin_currency)
    TextView tv_currency;

    @BindView(R.id.line_chart)
    LineChart lineChart;

    @BindView(R.id.btn_week)
    Button btn_week;

    @BindView(R.id.btn_month)
    Button btn_month;

    @BindView(R.id.btn_year)
    Button btn_year;

    String[] currencyNames;
    ArrayList<GraphData> graphData;
    ArrayList<Float> strArr;

    String currentCurrency ;


    public InfoFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setActionBarTitle(getContext().getString(R.string.info));

        View view = inflater.inflate(R.layout.fragment_info, container, false);
        ButterKnife.bind(this, view);

        currencyNames = getResources().getStringArray(R.array.currency);
        currentCurrency = currencyNames[0];
        graphData = new ArrayList<>();


        setSpinner();

        return view;
    }

    public void setSpinner(){
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, currencyNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.notifyDataSetChanged();
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentCurrency = parent.getSelectedItem().toString();
        makeRequestForCurrentPrice(parent.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void makeRequestForCurrentPrice(final String currentCurrency){
        Call call = ApiClient.provideCoindesk().create(ApiService.class)
                .getCurrentPrice(currentCurrency);


        call.enqueue(new Callback() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    JSONObject json = new JSONObject(new Gson().toJson(response.body()));
                    JSONObject bpi = (JSONObject) json.get("bpi");
                    JSONObject currency = (JSONObject) bpi.get(currentCurrency);
                    tv_currency.setText(currency.getString("rate"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void makeRequestForLastDate(String startDate,final int cur){
        Log.d(TAG, "makeRequestForLastDate: startDate::: " +startDate);
        String endDate = DateHelper.getCurrentTime();
        Log.d(TAG, "makeRequestForLastDate: endDate::: " +endDate);

        graphData.clear();
        Call call = ApiClient.provideCoindesk().create(ApiService.class)
                .getHistoricalDate(startDate, endDate, currentCurrency);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    graphData.clear();
                    try {
                        JSONObject json = new JSONObject(new Gson().toJson(response.body()));
                        JSONObject bpi = json.getJSONObject("bpi");
                        Iterator it = bpi.keys();
                        ArrayList<String> dateList = new ArrayList<>();

                        while (it.hasNext()) {
                            dateList.add((String) it.next());
                        }
                        for (String str : dateList) {
                            double price = bpi.getDouble(str);
                            graphData.add(new GraphData(str, price));
                        }

                        if (cur == 0)
                            setWeekLine();
                        else if (cur == 1)
                            setMonthLine();
                        else if (cur == 2)
                            setYearLine();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    @OnClick(R.id.btn_week)
    void setWeekConfigurations(){
        Log.d(TAG, "setWeekConfigurations: OnClick");
        lineChart.removeAllViews();
        makeRequestForLastDate(DateHelper.getLastWeek(),0);
    }

    @OnClick(R.id.btn_month)
    void setMonthConfigurations(){
        lineChart.removeAllViews();
        makeRequestForLastDate(DateHelper.getLastMonth(),1);
    }

    @OnClick(R.id.btn_year)
    void setYearConfigurations(){
        lineChart.removeAllViews();
        makeRequestForLastDate(DateHelper.getLastYear(),2);
    }

    private void configureLineChart(ArrayList<Float> data, String[] xAxisLabels){
        Log.d(TAG, "configureLineChart: setting line chart");
        lineChart.setBorderColor(Color.parseColor("#fafafa"));
        lineChart.setGridBackgroundColor(Color.parseColor("#fafafa"));
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(false);

        ArrayList<Entry> yValues = new ArrayList<>();
        for (int i=0;i<data.size();i++){
            yValues.add(new Entry(i, data.get(i)));
        }

        LineDataSet set1 = new LineDataSet(yValues, "");
        set1.setFillAlpha(110);
        set1.setDrawFilled(true);


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData lineData = new LineData(dataSets);
        lineData.setDrawValues(false);

        lineChart.setData(lineData);
        lineChart.setScaleEnabled(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        xAxis.setGranularity(1);
        xAxis.setLabelCount(xAxisLabels.length, true);
        
        lineChart.invalidate();

    }

    private void setWeekLine(){
        Log.d(TAG, "setWeekLine:  calculating");
        strArr = new ArrayList<>();
        for (GraphData data : graphData)
            strArr.add((float) data.getPrice());
        configureLineChart(strArr, getResources().getStringArray(R.array.days_name));

        btn_week.setBackgroundResource(R.drawable.button_background);
        btn_week.setTextColor(Color.parseColor("#ffffff"));
        btn_month.setBackgroundResource(R.color.white);
        btn_month.setTextColor(Color.parseColor("#d8d8d8"));

        btn_year.setBackgroundResource(R.color.white);
        btn_year.setTextColor(Color.parseColor("#d8d8d8"));
    }

    private void setMonthLine(){
        strArr = new ArrayList<>();
        int count = 0;
        float d = 0f;
        for (int i=0;i<graphData.size();i++){
            count++;
            d += graphData.get(i).getPrice();
            if (count==7){
                strArr.add((d/7));
                count = 0;
                d=0f;
            }
        }

        btn_month.setBackgroundResource(R.drawable.button_background);
        btn_month.setTextColor(Color.parseColor("#ffffff"));
        btn_year.setBackgroundResource(R.color.white);
        btn_year.setTextColor(Color.parseColor("#d8d8d8"));
        btn_week.setBackgroundResource(R.color.white);
        btn_week.setTextColor(Color.parseColor("#d8d8d8"));

        Log.d(TAG, "setMonthLine: strArrSizeLL: " + strArr.size());
        Log.d(TAG, "setMonthLine: graphSize:: " + graphData.size());
        configureLineChart(strArr, getResources().getStringArray(R.array.weeks));
    }

    private void setYearLine(){
        strArr = new ArrayList<>();
        int count = 0;
        float d = 0f;
        for (int i=0;i<graphData.size();i++){
            count++;
            d += graphData.get(i).getPrice();
            if (count==30){
                strArr.add((d/30));
                count = 0;
                d=0f;
            }
        }

        btn_year.setBackgroundResource(R.drawable.button_background);
        btn_year.setTextColor(Color.parseColor("#ffffff"));
        btn_month.setBackgroundResource(R.color.white);
        btn_month.setTextColor(Color.parseColor("#d8d8d8"));
        btn_week.setBackgroundResource(R.color.white);
        btn_week.setTextColor(Color.parseColor("#d8d8d8"));

        Log.d(TAG, "setYearLine: " + strArr.size());
        configureLineChart(strArr, getResources().getStringArray(R.array.months_name));
    }





}
