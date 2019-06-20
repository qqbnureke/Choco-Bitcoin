package com.nurda.choco_bitcoin.fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.nurda.choco_bitcoin.MainActivity;
import com.nurda.choco_bitcoin.R;
import com.nurda.choco_bitcoin.api.ApiClient;
import com.nurda.choco_bitcoin.api.ApiService;
import com.nurda.choco_bitcoin.mvp.model.GraphResponse;
import com.nurda.choco_bitcoin.utils.DateHelper;
import com.nurda.choco_bitcoin.utils.DialogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoFragment extends Fragment implements AdapterView.OnItemSelectedListener,
        View.OnClickListener {

    private static final String TAG = "InfoFragment";
    private final int WEEK_BUTTON = 1;
    private final int MONTH_BUTTON = 2;
    private final int YEAR_BUTTON = 3;

    private int currentButton = WEEK_BUTTON;

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
    ArrayList<GraphResponse> graphResponses;
    ArrayList<Float> lineChartData;

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
        graphResponses = new ArrayList<>();

        btn_week.setOnClickListener(this);
        btn_month.setOnClickListener(this);
        btn_year.setOnClickListener(this);

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
        setLastQuery();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void makeRequestForCurrentPrice(final String currentCurrency){
        final ProgressDialog dialog = DialogUtils.create(getContext(),getString(R.string.loading));

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
                    tv_currency.setText(currency.getString("rate") + " " + currentCurrency);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                t.printStackTrace();
                dialog.dismiss();
            }
        });
    }

    private void makeRequestForLastDate(String startDate){
        final ProgressDialog dialog = DialogUtils.create(getContext(),getString(R.string.loading));
        String endDate = DateHelper.getCurrentTime();

        graphResponses.clear();
        Call call = ApiClient.provideCoindesk().create(ApiService.class)
                .getHistoricalDate(startDate, endDate, currentCurrency);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    graphResponses.clear();
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
                            graphResponses.add(new GraphResponse(str, price));
                        }

                        prepareDataForChart();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                t.printStackTrace();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        lineChart.removeAllViews();

        switch (v.getId()){
            case R.id.btn_week:
                currentButton = WEEK_BUTTON;
                Log.d(TAG, "onClick: changed");
                break;
            case R.id.btn_month:
                currentButton = MONTH_BUTTON;
                Log.d(TAG, "onClick: changed");
                break;
            case R.id.btn_year:
                currentButton = YEAR_BUTTON;
                Log.d(TAG, "onClick: changed");
        }

        changeButtonsBackground();
        setLastQuery();
    }

    private void setLastQuery(){
        Log.d(TAG, "setLastQuery: ");
        switch (currentButton){
            case WEEK_BUTTON:
                makeRequestForLastDate(DateHelper.getLastWeek());
                break;
            case MONTH_BUTTON:
                makeRequestForLastDate(DateHelper.getLastMonth());
                break;
            case YEAR_BUTTON:
                makeRequestForLastDate(DateHelper.getLastYear());

        }
    }

    private void prepareDataForChart(){
        switch (currentButton){
            case WEEK_BUTTON:
                setWeekLine(); break;
            case MONTH_BUTTON:
                setMonthLine(); break;
            case YEAR_BUTTON:
                setYearLine();

        }
    }

    private void setWeekLine(){
        lineChartData = new ArrayList<>();
        for (GraphResponse data : graphResponses)
            lineChartData.add((float) data.getPrice());
        configureLineChart(lineChartData, getResources().getStringArray(R.array.days_name));
    }

    private void setMonthLine(){
        lineChartData = new ArrayList<>();
        int count = 0;
        float d = 0f;
        for (int i = 0; i< graphResponses.size(); i++){
            count++;
            d += graphResponses.get(i).getPrice();
            if (count==7){
                lineChartData.add((d/7));
                count = 0;
                d=0f;
            }
        }
        configureLineChart(lineChartData, getResources().getStringArray(R.array.weeks));
    }

    private void setYearLine(){
        lineChartData = new ArrayList<>();
        int count = 0;
        float d = 0f;
        for (int i = 0; i< graphResponses.size(); i++){
            count++;
            d += graphResponses.get(i).getPrice();
            if (count==30){
                lineChartData.add((d/30));
                count = 0;
                d=0f;
            }
        }
        configureLineChart(lineChartData, getResources().getStringArray(R.array.months_name));
    }

    private void configureLineChart(ArrayList<Float> data, String[] xAxisLabels){
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

    private void changeButtonsBackground(){
        Log.d(TAG, "changeButtonsBackground: ");
        btn_month.setBackgroundResource(R.drawable.button_white_background);
        btn_month.setTextColor(getResources().getColor(R.color.grey));
        btn_year.setBackgroundResource(R.drawable.button_white_background);
        btn_year.setTextColor(getResources().getColor(R.color.grey));
        btn_week.setBackgroundResource(R.drawable.button_white_background);
        btn_week.setTextColor(getResources().getColor(R.color.grey));

        switch (currentButton){
            case WEEK_BUTTON:
                btn_week.setBackgroundResource(R.drawable.button_background);
                btn_week.setTextColor(getResources().getColor(R.color.white));
                break;
            case MONTH_BUTTON:
                btn_month.setBackgroundResource(R.drawable.button_background);
                btn_month.setTextColor(getResources().getColor(R.color.white));
                break;
            case YEAR_BUTTON:
                btn_year.setBackgroundResource(R.drawable.button_background);
                btn_year.setTextColor(getResources().getColor(R.color.white));
        }
    }

}
