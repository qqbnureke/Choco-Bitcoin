package com.nurda.choco_bitcoin.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nurda.choco_bitcoin.MainActivity;
import com.nurda.choco_bitcoin.R;
import com.nurda.choco_bitcoin.adapter.TransactionsAdapter;
import com.nurda.choco_bitcoin.api.ApiClient;
import com.nurda.choco_bitcoin.api.ApiService;
import com.nurda.choco_bitcoin.mvp.model.Transaction;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment implements TransactionsAdapter.OnTransactionListener {
    private static final String TAG = "HistoryFragment";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    List<Transaction> transactions;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setActionBarTitle(getContext().getString(R.string.history));

        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);

        transactions = new ArrayList<>();
        makeRequest();
        return view;
    }

    private void initializeList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TransactionsAdapter adapter = new TransactionsAdapter(transactions, this);

        recyclerView.setAdapter(adapter);

    }

    private void makeRequest() {
        Call<List<Transaction>> call = ApiClient.provideBitstamp().create(ApiService.class).getTransactions();
        call.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful()){
                    transactions = response.body();
                }
                makeCount();
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                t.printStackTrace();
                Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
            }
        });

    }

    private void makeCount() {
        Log.d(TAG, "makeCount: " + transactions.size());
        Log.d(TAG, "makeCount: " + transactions.get(0).getTid());
        initializeList();
    }


    @Override
    public void onTransactionClick(int position) {
        Log.d(TAG, "onTransactionClick: Clicked hooray " + position);
    }
}
