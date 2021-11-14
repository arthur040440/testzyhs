package com.zhangyuhuishou.zyhs.guide.activity;

import android.os.CountDownTimer;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.base.BaseActivity;
import com.zhangyuhuishou.zyhs.guide.adapter.BaseFragmentAdapter;
import com.zhangyuhuishou.zyhs.guide.fragment.FirstLauncherFragment;
import com.zhangyuhuishou.zyhs.guide.fragment.FourthLauncherFragment;
import com.zhangyuhuishou.zyhs.guide.fragment.LauncherBaseFragment;
import com.zhangyuhuishou.zyhs.guide.fragment.SecondLauncherFragment;
import com.zhangyuhuishou.zyhs.guide.fragment.ThirdLauncherFragment;
import com.zhangyuhuishou.zyhs.guide.view.NoScrollViewPager;
import com.zhangyuhuishou.zyhs.model.LauncherModel;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 主Activity
 *
 * @author ansen
 * @create time 2015-08-07
 */
public class GuideActivity extends BaseActivity {

    private NoScrollViewPager vPager;
    private List<LauncherBaseFragment> list = new ArrayList<LauncherBaseFragment>();
    private BaseFragmentAdapter adapter;

    private int currentSelect;

    private CountDownTimer guideCountDownTimer;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_guide;
    }

    @Override
    protected void initView() {
        vPager = findViewById(R.id.viewpager_launcher);
    }

    @Override
    protected void initData() {
        super.initData();
        EventBus.getDefault().register(this);
        vPager.setNoScroll(true);
        FirstLauncherFragment firstLauncherFragment = new FirstLauncherFragment();
        SecondLauncherFragment secondLauncherFragment = new SecondLauncherFragment();
        ThirdLauncherFragment thirdLauncherFragment = new ThirdLauncherFragment();
        FourthLauncherFragment fourthLauncherFragment = new FourthLauncherFragment();
        list.add(firstLauncherFragment);
        list.add(secondLauncherFragment);
        list.add(thirdLauncherFragment);
        list.add(fourthLauncherFragment);

        adapter = new BaseFragmentAdapter(getSupportFragmentManager(), list);
        vPager.setAdapter(adapter);
        vPager.setOffscreenPageLimit(2);
        vPager.setCurrentItem(0);
        vPager.addOnPageChangeListener(changeListener);

        guideCountDownTimer = new CountDownTimer(24000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if(!isFinishing()){
                    System.out.println("我被迫执行了");
                    finish();
                }

            }
        };
        guideCountDownTimer.start();

    }

    /**
     * 监听viewpager的移动
     */
    OnPageChangeListener changeListener = new OnPageChangeListener() {
        @Override
        public void onPageSelected(int index) {
            LauncherBaseFragment fragment = list.get(index);
            list.get(currentSelect).stopAnimation();//停止前一个页面的动画
            fragment.startAnimation();//开启当前页面的动画
            currentSelect = index;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        if(guideCountDownTimer != null){
            System.out.println("我被主动取消了");
            guideCountDownTimer.cancel();
        }
    }

    // 下标
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetIndex(LauncherModel model) {
        int tempIndex = model.getLauncherIndex();
        vPager.setCurrentItem(tempIndex);
    }
}
