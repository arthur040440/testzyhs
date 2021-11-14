package com.zhangyuhuishou.zyhs.actys;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import com.tlh.android.utils.DataCleanManager;
import com.tlh.android.utils.ToastUitls;
import com.tlh.android.utils.Utils;
import com.tlh.android.utils.WebViewUtil;
import com.tlh.android.widget.dialog.SelfDialogBuilder;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.base.BaseActivity;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者:created by author:tlh
 * 日期:2018/8/16 14:40
 * 邮箱:tianlihui2234@live.com
 * 描述:网络设置
 */

public class WifiSettingActivity extends BaseActivity {

    @BindView(R.id.wv)
    WebView webView;

    private String userName, password;// 用户名，密码
    private SelfDialogBuilder loginDialog;// 登录对话框
    private EditText et_user, et_password;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_wifi_setting;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        super.initData();

        loginDialog = new SelfDialogBuilder(WifiSettingActivity.this);
        loginDialog.setLayoutId(R.layout.dialog_wifi_setting);
        loginDialog.setOnClickListener(R.id.tv_login, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userName = et_user.getText().toString().trim();
                password = et_password.getText().toString().trim();
                if (Utils.isEmpty(userName)) {
                    loginDialog.setClickDismiss(true);
                    ToastUitls.toastMessage("请输入账号");
                    return;
                }

                if (Utils.isEmpty(password)) {
                    loginDialog.setClickDismiss(true);
                    ToastUitls.toastMessage("请输入密码");
                    return;
                }
                loginDialog.setClickDismiss(false);
                loadWebUrl();

            }
        });
        loginDialog.show();
        et_user = (EditText) loginDialog.findViewById(R.id.et_account);
        et_password = (EditText) loginDialog.findViewById(R.id.et_password);

    }

    @OnClick(R.id.tv_back)
    public void backPre() {
        if (webView.canGoBack()) {
            webView.goBack();
        }
    }

    @OnClick(R.id.tv_finish)
    public void backFinish() {
        DataCleanManager.cleanWebviewCache(WifiSettingActivity.this);
        if (webView != null) {
            webView.setVisibility(View.GONE);
            webView.onPause();
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
        userName = "";
        password = "";
        finish();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebUrl() {
        WebViewUtil webViewUtil = new WebViewUtil(WifiSettingActivity.this);
        webViewUtil.setWebviewProperty(webView);
        webView.loadUrl("http://192.168.169.1");// 加载路由器设置页面
        webView.setWebViewClient(new MyWebViewClient());
    }

    public class MyWebViewClient extends WebViewClient {

        //override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
                handler.proceed(userName, password);
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            loginDialog.show();
        }

        //override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
            view.loadUrl(url);
            Log.d("MyWebViewClient", "shouldOverrideUrlLoading");
            // 记得消耗掉这个事件。给不知道的朋友再解释一下，Android中返回True的意思就是到此为止吧,事件就会不会冒泡传递了，我们称之为消耗掉
            return true;
        }
    }
}
