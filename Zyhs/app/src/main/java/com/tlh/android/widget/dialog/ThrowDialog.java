package com.tlh.android.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.zhangyuhuishou.zyhs.scanner.ScanGunKeyEventHelper;

public class ThrowDialog extends Dialog implements ScanGunKeyEventHelper.OnScanSuccessListener {

    private ScanGunKeyEventHelper mScannerKeyEventHelper;

    public ThrowDialog(@NonNull Context context) {
        super(context);
    }

    public ThrowDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerKeyEventHelper = new ScanGunKeyEventHelper(this);
    }

    /**
     * 扫描成功回调
     *
     * @param barcode
     */
    @Override
    public void onScanSuccess(String barcode) {
        onScanDialogListener.onScanResult(barcode);
    }


    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_UP) {
            mScannerKeyEventHelper.analysisKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void onStop() {
        super.onStop();
        mScannerKeyEventHelper.onDestroy();
    }


    private OnScanDialogListener onScanDialogListener;

    public interface OnScanDialogListener {
        void onScanResult(String scanValue);
    }

    public void setOnScanDialogListener(OnScanDialogListener listener) {
        onScanDialogListener = listener;
    }

}
