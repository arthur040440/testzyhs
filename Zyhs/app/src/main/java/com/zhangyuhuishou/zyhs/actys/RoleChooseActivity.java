package com.zhangyuhuishou.zyhs.actys;

import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.base.BaseActivity;
import butterknife.BindView;

/**
 * 作者:created by author:tlh
 * 日期:2018/12/12 10:41
 * 邮箱:tianlihui2234@live.com
 * 描述:角色分配
 */
public class RoleChooseActivity extends BaseActivity {

    // 倒计时
    @BindView(R.id.tv_countdown_time)
    TextView tv_countdown_time;

    // 清运人员
    @BindView(R.id.tv_clear_garbage_people)
    TextView tv_clear_garbage_people;

    // 维修人员
    @BindView(R.id.tv_maintenance_people)
    TextView tv_maintenance_people;

    private CountDownTimer countDownTimer;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_role_choose;
    }

    @Override
    protected void initData() {
        super.initData();

        countDownTimer = new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String leftTime = millisUntilFinished / 1000 + "s";
                tv_countdown_time.setText(leftTime);
            }

            @Override
            public void onFinish() {
                finish();
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        super.initListener();
        tv_clear_garbage_people.setOnClickListener(this);
        tv_maintenance_people.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.tv_clear_garbage_people:
                startActivity(new Intent(context,ClearAndTransportActivity.class));
                finish();
                break;
            case R.id.tv_maintenance_people:
                startActivity(new Intent(context,MaintainPeopleActivity.class));
                finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
