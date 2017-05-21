/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.utils;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.Result;
import com.ks.ecmanager.ecouriermanager.R;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public abstract class FragmentDialogQRScanner extends DialogFragment implements ZXingScannerView.ResultHandler {

    private String id, title, message;

    private ZXingScannerView mScannerView;

    public void setArgs(String id, String title, String message) {
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("title", title);
        args.putString("message", message);
        setArguments(args);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.TransparentDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mScannerView = new ZXingScannerView(getActivity());

        init();

        return mScannerView;
    }

    private void init() {
        id = getArguments().getString("id");
        title = getArguments().getString("title");
        message = getArguments().getString("message");

        getDialog().setTitle(title);

        mScannerView.setFlash(false);
        mScannerView.setAutoFocus(true);
        mScannerView.setKeepScreenOn(true);
    }

    @Override
    public void handleResult(Result rawResult) {
        success(rawResult.getText(), rawResult.getBarcodeFormat().toString());
        dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    public abstract void success(String value, String format_type);

    public abstract void error();

}
