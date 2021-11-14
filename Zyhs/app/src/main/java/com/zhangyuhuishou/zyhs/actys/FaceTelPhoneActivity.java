package com.zhangyuhuishou.zyhs.actys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.tts.util.TTSUtils;
import com.face.base.IFaceSetView;
import com.face.http.FaceSetPresenter;
import com.face.util.FaceUtils;
import com.face.util.Util;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.ClickUtils;
import com.tlh.android.utils.LoginUtils;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.widget.NoPaddingTextView;
import com.tlh.android.widget.UnderLineEditText;
import com.tlh.android.widget.dialog.SelfDialogBuilder;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.base.BaseActivity;
import com.zhangyuhuishou.zyhs.model.AuthModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

import static com.tlh.android.utils.ZXingUtils.createQRImage;

/**
 * @author tlh
 * @time 2018/11/28 18:13
 * @desc 人脸识别填充电话页面
 */

public class FaceTelPhoneActivity extends BaseActivity implements IFaceSetView {

    private String TAG = FaceTelPhoneActivity.class.getSimpleName();

    private FaceUtils mFaceUtils;// 人脸识别授权工具类

    // 倒计时
    @BindView(R.id.tv_countdown_time)
    TextView tv_countdown_time;

    private CountDownTimer countDownTimer, loginOutTimer;

    // 数字
    @BindViews({R.id.tv_one_location, R.id.tv_two_location, R.id.tv_three_location, R.id.tv_four_location, R.id.tv_five_location, R.id.tv_six_location, R.id.tv_seven_location,
            R.id.tv_eight_location, R.id.tv_nine_location, R.id.tv_ten_location, R.id.tv_eleven_location})
    List<UnderLineEditText> nums;

    // 数字4的替身
    @BindView(R.id.tv_four_location_scape)
    EditText tv_four_location_scape;

    // 数字5的替身
    @BindView(R.id.tv_five_location_scape)
    EditText tv_five_location_scape;

    // 数字6的替身
    @BindView(R.id.tv_six_location_scape)
    EditText tv_six_location_scape;

    // 数字7的替身
    @BindView(R.id.tv_seven_location_scape)
    EditText tv_seven_location_scape;

    private FaceSetPresenter faceSetPresenter;// 人脸库

    private SelfDialogBuilder faceTipBuilder;// 提示框
    private SelfDialogBuilder notKnowYouDialog;// 我不认识你对话框
    private Handler handler = new Handler();
    private String face_token;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_face_telphone;
    }

    @Override
    protected void initData() {
        super.initData();

        face_token = getIntent().getStringExtra(Constant.CURRENT_FACE_TOKEN);


        EventBus.getDefault().register(this);
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (tv_countdown_time.getVisibility() == View.GONE) {
                    tv_countdown_time.setVisibility(View.VISIBLE);
                }
                tv_countdown_time.setText(millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                cancelDialog();
                finish();
            }
        };
        countDownTimer.start();


        loginOutTimer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                LoginUtils.logout(context);
            }
        };


        mFaceUtils = new FaceUtils(context, Util.FACE_TYPE_LOGIN);

        faceSetPresenter = new FaceSetPresenter(context);
        faceSetPresenter.attachView(this);

        faceTipBuilder = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_auth_fail);

        notKnowYouDialog = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_i_do_not_know_you);

        // 语音提示（请输入手机号码）
        TTSUtils.speak(Constant.TTS_TIP_INPUT_YOUR_PHONE);

    }

    @OnClick({R.id.tv_back_to_pre, R.id.cv_tv_zero, R.id.cv_tv_one, R.id.cv_tv_two, R.id.cv_tv_three, R.id.cv_tv_four, R.id.cv_tv_five,
            R.id.cv_tv_six, R.id.cv_tv_seven, R.id.cv_tv_eight, R.id.cv_tv_nine, R.id.cv_tv_delete, R.id.cv_tv_clear})
    public void selfClick(View v) {
        if (ClickUtils.isFastClick()) {
            // 停止播放语音提示
            TTSUtils.stop();
        }
        switch (v.getId()) {
            case R.id.tv_back_to_pre:
                LoginUtils.logout(context);
                break;
            case R.id.cv_tv_zero:
                TTSUtils.speak("0");
                setPhoneNum("0");
                break;
            case R.id.cv_tv_one:
                TTSUtils.speak("1");
                setPhoneNum("1");
                break;
            case R.id.cv_tv_two:
                TTSUtils.speak("2");
                setPhoneNum("2");
                break;
            case R.id.cv_tv_three:
                TTSUtils.speak("3");
                setPhoneNum("3");
                break;
            case R.id.cv_tv_four:
                TTSUtils.speak("4");
                setPhoneNum("4");
                break;
            case R.id.cv_tv_five:
                TTSUtils.speak("5");
                setPhoneNum("5");
                break;
            case R.id.cv_tv_six:
                TTSUtils.speak("6");
                setPhoneNum("6");
                break;
            case R.id.cv_tv_seven:
                TTSUtils.speak("7");
                setPhoneNum("7");
                break;
            case R.id.cv_tv_eight:
                TTSUtils.speak("8");
                setPhoneNum("8");
                break;
            case R.id.cv_tv_nine:
                TTSUtils.speak("9");
                setPhoneNum("9");
                break;
            case R.id.cv_tv_delete:
                deletePhoneNum();
                break;
            case R.id.cv_tv_clear:
                clearAllNumbers();
                break;
        }
    }

    @Override
    protected void initView() {
    }

    // 清空所有数字
    private void clearAllNumbers() {
        tv_four_location_scape.setText("");
        tv_five_location_scape.setText("");
        tv_six_location_scape.setText("");
        tv_seven_location_scape.setText("");
        nums.get(0).setText("");
        nums.get(1).setText("");
        nums.get(2).setText("");
        nums.get(3).setText("");
        nums.get(4).setText("");
        nums.get(5).setText("");
        nums.get(6).setText("");
        nums.get(7).setText("");
        nums.get(8).setText("");
        nums.get(9).setText("");
        nums.get(10).setText("");
        nums.get(0).setEditTextColor(getResources().getColor(R.color.phone_num_underline, null));
        nums.get(1).setEditTextColor(getResources().getColor(R.color.phone_num_underline, null));
        nums.get(2).setEditTextColor(getResources().getColor(R.color.phone_num_underline, null));
        nums.get(3).setEditTextColor(getResources().getColor(R.color.phone_num_underline, null));
        nums.get(4).setEditTextColor(getResources().getColor(R.color.phone_num_underline, null));
        nums.get(5).setEditTextColor(getResources().getColor(R.color.phone_num_underline, null));
        nums.get(6).setEditTextColor(getResources().getColor(R.color.phone_num_underline, null));
        nums.get(7).setEditTextColor(getResources().getColor(R.color.phone_num_underline, null));
        nums.get(8).setEditTextColor(getResources().getColor(R.color.phone_num_underline, null));
        nums.get(9).setEditTextColor(getResources().getColor(R.color.phone_num_underline, null));
        nums.get(10).setEditTextColor(getResources().getColor(R.color.phone_num_underline, null));
    }

    // 设置值
    private void setPhoneNum(String phoneNum) {
        UnderLineEditText currentTextView = getTextView();
        if (currentTextView != null) {
            currentTextView.setTextSize(72);
            currentTextView.setText(phoneNum);
            currentTextView.setEditTextColor(getResources().getColor(R.color.phone_num_underline_press, null));
            if (currentTextView.getId() == nums.get(3).getId()) {
                tv_four_location_scape.setText(phoneNum);
            }
            if (currentTextView.getId() == nums.get(4).getId()) {
                tv_five_location_scape.setText(phoneNum);
            }
            if (currentTextView.getId() == nums.get(5).getId()) {
                tv_six_location_scape.setText(phoneNum);
            }
            if (currentTextView.getId() == nums.get(6).getId()) {
                tv_seven_location_scape.setText(phoneNum);
            }
            if (currentTextView.getId() == nums.get(10).getId()) {
                if (isMobile(getCompleteNums())) {
                    //    faceSetPresenter.getFaceset(getCompleteNums());

                    // 手机号码是否和人脸绑定/手机号码绑定的人脸是否和登录人脸匹配
                    faceSetPresenter.loginByFaceOfTelphone(face_token, SPUtils.getString(context, Constant.CURRENT_COMMUNITY_ID), getCompleteNums());

                }
            }
        }
    }

    // 得到没有填充数字的TextView
    private UnderLineEditText getTextView() {
        String oneStr = nums.get(0).getText().toString().trim();
        String twoStr = nums.get(1).getText().toString().trim();
        String threeStr = nums.get(2).getText().toString().trim();
        String fourStr = nums.get(3).getText().toString().trim();
        String fiveStr = nums.get(4).getText().toString().trim();
        String sixStr = nums.get(5).getText().toString().trim();
        String sevenStr = nums.get(6).getText().toString().trim();
        String eightStr = nums.get(7).getText().toString().trim();
        String nineStr = nums.get(8).getText().toString().trim();
        String tenStr = nums.get(9).getText().toString().trim();
        String elevenStr = nums.get(10).getText().toString().trim();

        if (TextUtils.isEmpty(oneStr)) {
            return nums.get(0);
        }
        if (TextUtils.isEmpty(twoStr)) {
            return nums.get(1);
        }
        if (TextUtils.isEmpty(threeStr)) {
            return nums.get(2);
        }
        if (TextUtils.isEmpty(fourStr)) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nums.get(3).setText("*");
                }
            }, 100);
            return nums.get(3);
        }
        if (TextUtils.isEmpty(fiveStr)) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nums.get(4).setText("*");
                }
            }, 100);
            return nums.get(4);
        }
        if (TextUtils.isEmpty(sixStr)) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nums.get(5).setText("*");
                }
            }, 100);
            return nums.get(5);
        }
        if (TextUtils.isEmpty(sevenStr)) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nums.get(6).setText("*");
                }
            }, 100);
            return nums.get(6);
        }
        if (TextUtils.isEmpty(eightStr)) {
            return nums.get(7);
        }
        if (TextUtils.isEmpty(nineStr)) {
            return nums.get(8);
        }
        if (TextUtils.isEmpty(tenStr)) {
            return nums.get(9);
        }
        if (TextUtils.isEmpty(elevenStr)) {
            return nums.get(10);
        }
        return null;
    }

    // 清空输入框中的值
    private void deletePhoneNum() {
        UnderLineEditText currentTextView = getTextViewFull();
        if (currentTextView != null) {
            currentTextView.setTextSize(72);
            currentTextView.setText("");
            currentTextView.setEditTextColor(getResources().getColor(R.color.phone_num_underline, null));
            if (currentTextView.getId() == nums.get(3).getId()) {
                tv_four_location_scape.setText("");
            }
            if (currentTextView.getId() == nums.get(4).getId()) {
                tv_five_location_scape.setText("");
            }
            if (currentTextView.getId() == nums.get(5).getId()) {
                tv_six_location_scape.setText("");
            }
            if (currentTextView.getId() == nums.get(6).getId()) {
                tv_seven_location_scape.setText("");
            }
        }
    }

    private UnderLineEditText getTextViewFull() {
        String oneStr = nums.get(0).getText().toString().trim();
        String twoStr = nums.get(1).getText().toString().trim();
        String threeStr = nums.get(2).getText().toString().trim();
        String fourStr = nums.get(3).getText().toString().trim();
        String fiveStr = nums.get(4).getText().toString().trim();
        String sixStr = nums.get(5).getText().toString().trim();
        String sevenStr = nums.get(6).getText().toString().trim();
        String eightStr = nums.get(7).getText().toString().trim();
        String nineStr = nums.get(8).getText().toString().trim();
        String tenStr = nums.get(9).getText().toString().trim();
        String elevenStr = nums.get(10).getText().toString().trim();

        if (!TextUtils.isEmpty(elevenStr)) {
            return nums.get(10);
        }

        if (!TextUtils.isEmpty(tenStr)) {
            return nums.get(9);
        }

        if (!TextUtils.isEmpty(nineStr)) {
            return nums.get(8);
        }

        if (!TextUtils.isEmpty(eightStr)) {
            return nums.get(7);
        }

        if (!TextUtils.isEmpty(sevenStr)) {
            return nums.get(6);
        }

        if (!TextUtils.isEmpty(sixStr)) {
            return nums.get(5);
        }

        if (!TextUtils.isEmpty(fiveStr)) {
            return nums.get(4);
        }

        if (!TextUtils.isEmpty(fourStr)) {
            return nums.get(3);
        }

        if (!TextUtils.isEmpty(threeStr)) {
            return nums.get(2);
        }

        if (!TextUtils.isEmpty(twoStr)) {
            return nums.get(1);
        }

        if (!TextUtils.isEmpty(oneStr)) {
            return nums.get(0);
        }
        return null;
    }

    // 判断是否输入完整
    private boolean isCompleteNums() {
        String elevenStr = nums.get(10).getText().toString().trim();
        if (!TextUtils.isEmpty(elevenStr)) {
            return true;
        }
        return false;
    }

    private String getCompleteNums() {
        String oneStr = nums.get(0).getText().toString().trim();
        String twoStr = nums.get(1).getText().toString().trim();
        String threeStr = nums.get(2).getText().toString().trim();
        String fourStr = tv_four_location_scape.getText().toString().trim();
        String fiveStr = tv_five_location_scape.getText().toString().trim();
        String sixStr = tv_six_location_scape.getText().toString().trim();
        String sevenStr = tv_seven_location_scape.getText().toString().trim();
        String eightStr = nums.get(7).getText().toString().trim();
        String nineStr = nums.get(8).getText().toString().trim();
        String tenStr = nums.get(9).getText().toString().trim();
        String elevenStr = nums.get(10).getText().toString().trim();
        System.out.println("请求的手机号码：" + oneStr + twoStr + threeStr + fourStr + fiveStr + sixStr + sevenStr + eightStr + nineStr + tenStr + elevenStr);
        return oneStr + twoStr + threeStr + fourStr + fiveStr + sixStr + sevenStr + eightStr + nineStr + tenStr + elevenStr;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelDialog();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        EventBus.getDefault().unregister(this);
        handler.removeCallbacks(noAutoRunnable);
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    public boolean isMobile(String str) {
//        String s2 = "^[1](([3][0-9])|([4][5,7,9])|([5][^4,6,9])|([6][6])|([7][3,5,6,7,8])|([8][0-9])|([9][8,9]))[0-9]{8}$";// 验证手机号
        String regex = "^[1][0-9]{10}$";// 验证手机号
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        boolean isMatch = m.matches();
        if (!isMatch) {
            showDialog("请填入正确的手机号");
            clearAllNumbers();
        }
        return isMatch;
    }

    private Bitmap mBitmap;

    @Override
    public void faceTip(String message) {
        //  System.out.println(message);
        notKnowYouDialog.setOnClickListener(R.id.tv_back_to_pre, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (!isFinishing()) {
                    notKnowYouDialog.dismiss();
                }*/
                LoginUtils.logout(context);
            }
        });
        if (!isFinishing()) {
            notKnowYouDialog.show();
            ImageView qr = (ImageView) notKnowYouDialog.findViewById(R.id.iv_qr);
            mBitmap = createQRImage(Constant.FACE_QR_INFO, 280, 280);
            qr.setImageBitmap(mBitmap);

        /*    TextView tip = (TextView) notKnowYouDialog.findViewById(R.id.itb_tip);
            tip.setText(message);*/

            TextView input_again = (TextView) notKnowYouDialog.findViewById(R.id.tv_input_phone_again);

            input_again.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelDialog();
                    clearAllNumbers();
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    countDownTimer.start();

                    if (loginOutTimer != null) {
                        loginOutTimer.cancel();
                    }
                }
            });


            loginOutTimer.start();
            //  handler.postDelayed(noAutoRunnable, 15000);
        }
    }

    // 取消弹框
    private void cancelDialog() {
        if (!isFinishing() && notKnowYouDialog != null && notKnowYouDialog.getDialog() != null && notKnowYouDialog.getDialog().isShowing()) {
            notKnowYouDialog.dismiss();
        }
        if (!isFinishing() && faceTipBuilder != null && faceTipBuilder.getDialog() != null && faceTipBuilder.getDialog().isShowing()) {
            faceTipBuilder.dismiss();
        }
    }

    private Runnable noAutoRunnable = new Runnable() {
        @Override
        public void run() {
            LoginUtils.logout(context);
        }
    };

    @Override
    public void faceSetToken(String faceset_token) {
        mFaceUtils.setPhoneNum(faceset_token);
        mFaceUtils.network();
    }

    // 错误提示
    @Override
    public void errorTip(String message) {
        showDialog(message);
        clearAllNumbers();
    }

    // 提示框
    private void showDialog(String message) {
        faceTipBuilder.show();
        NoPaddingTextView tip = (NoPaddingTextView) faceTipBuilder.findViewById(R.id.nptv_auth_tip);
        tip.setText(TextUtils.isEmpty(message) ? "验证失败" : message);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    faceTipBuilder.dismiss();
                }
            }
        }, 3000);
    }

    @Override
    public void loginSuccess() {
        // 登录成功
        startActivity(new Intent(FaceTelPhoneActivity.this, ThrowThingsActivity.class));
        sendBroadcast(new Intent(Constant.FINISH_HOME_PAGE));
        finish();
    }


    // 旷世授权与否
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAuthEvent(AuthModel model) {
        if (model.getIsAuth()) {
            finish();
        } else {
            showDialog("人脸授权失败");
        }
    }

}
