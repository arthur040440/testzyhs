package com.zhangyuhuishou.zyhs.actys;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.base.BaseActivity;
import com.zhangyuhuishou.zyhs.time.TimeUtils;

import butterknife.BindView;

/**
 * 作者:created by author:tlh
 * 日期:2019/3/1 14:56
 * 邮箱:tianlihui2234@live.com
 * 描述:ali广告
 */
public class AliBannerActivity extends BaseActivity {

    // 倒计时
    @BindView(R.id.tv_countdown_time)
    TextView tv_countdown_time;


    // banner显示
    @BindView(R.id.iv_banner_show)
    ImageView iv_banner_show;

    private CountDownTimer countDownTimer;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_ali_banner;
    }

    @Override
    protected void initView() {


        // 6月5日当天播放此图
        if (TimeUtils.isBelong65_show_banner(context)) {
            iv_banner_show.setImageResource(R.mipmap.ic_banner_show);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (tv_countdown_time.getVisibility() == View.GONE) {
                    tv_countdown_time.setVisibility(View.VISIBLE);
                }
                String leftTime = millisUntilFinished / 1000 + "s";
                tv_countdown_time.setText(leftTime);
            }

            @Override
            public void onFinish() {
                tv_countdown_time.setVisibility(View.GONE);
                finish();
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
