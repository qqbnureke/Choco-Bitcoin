package com.nurda.choco_bitcoin.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nurda.choco_bitcoin.R;
import com.nurda.choco_bitcoin.mvp.model.Transaction;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.Holder> {
    OnTransactionListener onTransactionListener;

    private List<Transaction> transactionList;

    public TransactionsAdapter(List<Transaction> transactionList,  OnTransactionListener listener) {
        this.transactionList = transactionList;
        onTransactionListener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.layout_history_item, viewGroup, false
        );
        return new Holder(view, onTransactionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        holder.bind(transactionList.get(i));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }


    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.tv_transaction_amount) protected TextView amount;
        @BindView(R.id.tv_transaction_date) protected TextView date;
        @BindView(R.id.tv_transaction_type) protected TextView type;
        OnTransactionListener onTransactionListener;

        public Holder(@NonNull View itemView, OnTransactionListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.onTransactionListener = listener;

            itemView.setOnClickListener(this);
        }

        public void bind(Transaction transaction) {
            amount.setText(transaction.getAmount());
            date.setText(transaction.getDate());
            type.setText(transaction.getType()==0 ? "buy" : "sell");
        }


        @Override
        public void onClick(View v) {
            onTransactionListener.onTransactionClick(getAdapterPosition());
        }
    }

    public interface OnTransactionListener {
        void onTransactionClick(int position);
    }
}
