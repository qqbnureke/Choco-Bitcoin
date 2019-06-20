package com.nurda.choco_bitcoin.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nurda.choco_bitcoin.R;
import com.nurda.choco_bitcoin.mvp.model.Transaction;
import com.nurda.choco_bitcoin.utils.DateHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.Holder> {
    private List<Transaction> transactionList;

    public TransactionsAdapter(List<Transaction> transactionList) {
        this.transactionList =  transactionList.subList(0, 500);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.layout_history_item, viewGroup, false
        );
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        if (i>=500) return;
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
        @BindView(R.id.tv_transaction_number) protected TextView number;
        @BindView(R.id.iv_trade) protected ImageView trade_type;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Transaction transaction) {
            number.setText(String.valueOf(getAdapterPosition()+1));
            amount.setText(transaction.getAmount()+" BCH");

            long milliseconds = 1000L * Long.parseLong(transaction.getDate());
            date.setText(DateHelper.millisecondsToTime(milliseconds));

            if (transaction.getType().equals("0")){
                type.setText("Buy");
                trade_type.setImageResource(R.drawable.ic_up_arrow);
            }else{
                type.setText("Sell");
                trade_type.setImageResource(R.drawable.ic_down_arrow);
            }

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            //onTransactionListener.onTransactionClick(getAdapterPosition());
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            View view = LayoutInflater.from(v.getContext()).inflate(R.layout.layout_description,null);
            TextView tv_tid = view.findViewById(R.id.tv_tid_text);
            tv_tid.setText(transactionList.get(getAdapterPosition()).getTid());
            TextView tv_date = view.findViewById(R.id.tv_date_text);
            tv_date.setText(DateHelper.millisecondsToTime(
                    1000L * Long.parseLong(transactionList.get(getAdapterPosition()).getDate())));
            TextView tv_amount = view.findViewById(R.id.tv_amount_text);
            tv_amount.setText(transactionList.get(getAdapterPosition()).getAmount() + " BCH");
            TextView tv_price = view.findViewById(R.id.tv_price_text);
            tv_price.setText(transactionList.get(getAdapterPosition()).getPrice()+" USD");


            builder.setTitle(transactionList.get(getAdapterPosition()).getType().equals("0") ? "Buy" : "Sell")
                    .setView(view)
                    .create()
                    .show();

        }
    }

}
