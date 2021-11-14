package com.tlh.android.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.zhangyuhuishou.zyhs.R;

/**
 * @author tlh
 * @time 2017/5/14 18:12
 * @desc 中间弹框
 */
public class SelfDialogBuilder implements OnClickListener {

    private Context mContext;
    private Dialog mDialog;
    private int mLayoutId;
    private View mLayoutView;
    protected DisplayMetrics displayMetrics;
    private SparseArray<OnClickListener> mClickMaps;

    private boolean dismiss = false;// 点击之后对话框是否消失


    public SelfDialogBuilder(Context context) {
        mContext = context;
        displayMetrics = context.getResources().getDisplayMetrics();
        mClickMaps = new SparseArray<OnClickListener>();
    }

    public SelfDialogBuilder setLayoutId(int layoutId) {
        mLayoutId = layoutId;
        return this;
    }

    public SelfDialogBuilder setLayoutId(View view) {
        mLayoutView = view;
        return this;
    }

    public SelfDialogBuilder setLayoutIdVisible(int id) {
        View button = mLayoutView.findViewById(id);
        button.setVisibility(View.GONE);
        return this;
    }

    public SelfDialogBuilder setOnClickListener(int buttonId, OnClickListener listener) {
        mClickMaps.put(buttonId, listener);
        return this;
    }

    public SelfDialogBuilder setClickDismiss(boolean dismiss) {
        this.dismiss = dismiss;
        return this;
    }

    private Dialog create() {

        if (mDialog == null) {
            mDialog = new Dialog(mContext, R.style.gc_botttom_menu_dialog);
            if (mLayoutView == null) {
                mDialog.setContentView(mLayoutId);
            } else {
                mDialog.setContentView(mLayoutView);
            }
        }

        // 设置默认位置
        setGravity(Gravity.CENTER);

        // 设置默认动画
        setAnimation(R.style.dialogWindowAnim);

        // 设置默认点击外部不消失
        setClickDismiss(this.dismiss);

        for (int i = 0; i < mClickMaps.size(); i++) {
            int buttonId = mClickMaps.keyAt(i);
            View button = mDialog.findViewById(buttonId);
            if (button != null) {
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(this);
            }
        }

        return mDialog;
    }

    public Dialog show() {
        if (mDialog == null)
            create();
        if (!((Activity) mContext).isFinishing()) {
            mDialog.show();
        }
        return mDialog;
    }

    // 点击对话框外部是否消失
    public SelfDialogBuilder setCanceledOnTouchOutside(boolean cancel) {
        mDialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    // 设置对话框的显示位置
    public SelfDialogBuilder setGravity(int gravity) {
        Window window = mDialog.getWindow();
        window.setGravity(gravity);
        return this;
    }

    // 设置对话框动画
    public SelfDialogBuilder setAnimation(int style) {
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        window.setWindowAnimations(style);
        return this;
    }

    // 设置点击返回按钮消失或者不消失
    public SelfDialogBuilder setCancelable(boolean flag) {
        if (mDialog != null) {
            mDialog.setCancelable(flag);
        }
        return this;
    }

    public void setTextViewInfo(int viewId, String info) {
        TextView tv = (TextView) findViewById(viewId);
        tv.setVisibility(View.VISIBLE);
        tv.setText(info);
    }

    public void hideButton(int viewId, boolean hide) {
        TextView tv = (TextView) findViewById(viewId);
        tv.setVisibility(hide ? View.VISIBLE : View.GONE);
    }

    public void setEditHint(int viewId, String msg) {
        EditText et = (EditText) findViewById(viewId);
        et.setHint(msg);
    }

    // 设置输入文字长度
    public void setTextLength(int viewId, int textLength) {
        TextView et = (TextView) findViewById(viewId);
        et.setFilters(new InputFilter[]{
                        new InputFilter() {
                            @Override
                            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                                if (source.equals(" ")) {
                                    return "";
                                } else {
                                    return null;
                                }
                            }
                        },
                        new InputFilter.LengthFilter(textLength)/*这里限制输入的长度为5个字母*/
                }
        );
    }


    @Override
    public void onClick(View v) {
        int buttonId = v.getId();
        OnClickListener listener = mClickMaps.get(buttonId);
        if (listener != null)
            listener.onClick(v);
        if (!dismiss) {
            mDialog.dismiss();
        }
    }

    public Dialog getDialog() {
        return mDialog;
    }

    public View findViewById(int id) {
        return mDialog.findViewById(id);
    }

    public void dismiss() {
        if (!((Activity) mContext).isFinishing() && mDialog != null) {
            mDialog.dismiss();
        }
    }

    protected int convertDP2PX(int sizeDP) {
        return (int) (sizeDP * displayMetrics.density);
    }

    public boolean isShowing() {
        return mDialog.isShowing();
    }


}
