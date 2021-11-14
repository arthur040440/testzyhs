package com.zhangyuhuishou.zyhs.guide.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.model.LauncherModel;
import org.greenrobot.eventbus.EventBus;

/**
 * 作者:created by author:tlh
 * 日期:2019/2/20 23:10
 * 邮箱:tianlihui2234@live.com
 * 描述:第一个展示页面
 */
public class FirstLauncherFragment extends LauncherBaseFragment implements View.OnClickListener {

    private String Tag = FirstLauncherFragment.class.getSimpleName();

    private ImageView iv_launcher_des, iv_launcher_next;

    private Animation desAnimation, nextAnimation;

    private boolean started;//是否开启动画

    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rooView = inflater.inflate(R.layout.fragment_first_launcher, null);
        iv_launcher_des = rooView.findViewById(R.id.iv_launcher_des);
        iv_launcher_next = rooView.findViewById(R.id.iv_launcher_next);
        iv_launcher_next.setOnClickListener(this);
        startAnimation();
        return rooView;
    }

    public void stopAnimation() {
        // 动画开启标示符设置成false
        started = false;
        // 清空所有控件上的动画
        iv_launcher_des.clearAnimation();
        iv_launcher_next.clearAnimation();
    }


    public void startAnimation() {
        started = true;
        // 每次开启动画前先隐藏控件
        iv_launcher_des.setVisibility(View.GONE);
        iv_launcher_next.setVisibility(View.GONE);
        handler.postDelayed(new Runnable() {
            // 延时0.5秒之后开启
            @Override
            public void run() {
                if (started)
                    desAnimation();
            }
        }, 500);
    }

    /**
     * 描述
     */
    private void desAnimation() {

        iv_launcher_des.setVisibility(View.VISIBLE);
        desAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.big_to_common);
        iv_launcher_des.startAnimation(desAnimation);//开启动画
        desAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 监听动画结束
                if (started)
                    next();
            }
        });
    }

    /**
     * 下一步
     */
    private void next() {
        iv_launcher_next.setVisibility(View.VISIBLE);
        nextAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.big_to_common);
        iv_launcher_next.startAnimation(nextAnimation);
        nextAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 监听动画结束
                if (started)
                    handler.postDelayed(new Runnable() {
                        // 延时5秒之后进入到下一页
                        @Override
                        public void run() {
                            if (started)
                                System.out.println("我执行了:" + Tag);
                            EventBus.getDefault().post(new LauncherModel(1));
                        }
                    }, 5000);
            }
        });
    }

    @Override
    public void onClick(View v) {
        handler.removeMessages(0);
        handler.removeCallbacksAndMessages(null);
        EventBus.getDefault().post(new LauncherModel(1));
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
        handler.removeMessages(0);
    }
}
