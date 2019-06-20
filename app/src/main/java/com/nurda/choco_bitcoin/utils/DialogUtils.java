package com.nurda.choco_bitcoin.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class DialogUtils {
    public static ProgressDialog create(Context context, String message){
        ProgressDialog m_Dialog = new ProgressDialog(context);
        m_Dialog.setMessage(message);
        m_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_Dialog.setCancelable(false);
        m_Dialog.show();
        return m_Dialog;
    }
}
