package com.nurda.choco_bitcoin.fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.google.gson.Gson;
import com.nurda.choco_bitcoin.MainActivity;
import com.nurda.choco_bitcoin.R;
import com.nurda.choco_bitcoin.api.ApiClient;
import com.nurda.choco_bitcoin.api.ApiService;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoFragment extends Fragment implements AdapterView.OnItemSelectedListener {

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
    //String currentCurrency = currencyNames[0];

    public InfoFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setActionBarTitle(getContext().getString(R.string.info));

        View view = inflater.inflate(R.layout.fragment_info, container, false);
        ButterKnife.bind(this, view);

        currencyNames = getResources().getStringArray(R.array.currency);

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
        makeRequest(parent.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void makeRequest(final String s){
        Call call = ApiClient.provideCoindesk().create(ApiService.class)
                .currentPrice(s);


        call.enqueue(new Callback() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    JSONObject json = new JSONObject(new Gson().toJson(response.body()));
                    JSONObject bpi = (JSONObject) json.get("bpi");
                    JSONObject currency = (JSONObject) bpi.get(s);
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
}
