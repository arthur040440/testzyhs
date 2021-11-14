package com.tlh.android.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebViewUtil {

    private Context context;

    public WebViewUtil(Context context) {
        this.context = context;
    }

    public void setWebviewProperty(final WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true); // 设置支持javascript脚本
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(false);// 不支持缩放
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);// 设置缓存模式，根据cache-control决定是否从网络上取数据。
        settings.setDatabaseEnabled(true);// 设置可以访问数据库
        //开启 Application Caches 功能
        settings.setAppCacheEnabled(true);
        settings.setBuiltInZoomControls(false);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setBlockNetworkImage(false);
        webView.requestFocus();
        webView.setWebChromeClient(new MyWebChromeClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
    }

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onGeolocationPermissionsHidePrompt() {
            super.onGeolocationPermissionsHidePrompt();
        }


        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("提示").setMessage(message).setPositiveButton("确定", null);

            // 不需要绑定按键事件
            // 屏蔽keycode等于84之类的按键
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    Log.v("onJsAlert", "keyCode==" + keyCode + "event=" + event);
                    return true;
                }
            });
            // 禁止响应按back键的事件
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();
            result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            System.out.println(newProgress + "");
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            //为解决6.0系统上，这个api会调用两次，而且第一次是显示url的系统bug
        }
    }

}