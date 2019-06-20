package com.nurda.choco_bitcoin.fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class ExchangeFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "ExchangeFragment";

    private String currentCurrency = "USD";

    @BindView(R.id.et_amount)
    EditText et_amount;

    @BindView(R.id.exchange_spinner)
    Spinner spinner;

    @BindView(R.id.tv_bitcoin_amount)
    TextView tv_bitcoin;

    public ExchangeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setActionBarTitle(getContext().getString(R.string.exchange));
        View view = inflater.inflate(R.layout.fragment_exchange, container, false);
        ButterKnife.bind(this, view);

        et_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: " + s);
                makeRequest(String.valueOf(s));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setSpinner();

        return view;
    }


    public void setSpinner(){
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.currency));
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerAdapter.notifyDataSetChanged();
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected: " + parent.getSelectedItem());
        currentCurrency = parent.getSelectedItem().toString();
        makeRequest(et_amount.getText().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void makeRequest(final String s){
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
                    double rate = Double.parseDouble(currency.getString("rate").replaceAll(",",""));
                    if (s.length()==0 || s.equals("0")) {
                        tv_bitcoin.setText("0");
                    }else{
                        double bitcoin = Long.parseLong(s) / rate;
                        tv_bitcoin.setText(String.format("%f", bitcoin));
                    }
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
