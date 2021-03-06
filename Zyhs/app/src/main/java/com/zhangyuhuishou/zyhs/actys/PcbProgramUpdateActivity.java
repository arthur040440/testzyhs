package com.zhangyuhuishou.zyhs.actys;

import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.utils.ToastUitls;
import com.tlh.android.utils.Utils;
import com.tlh.android.widget.HorizontalProgressBar;
import com.tlh.android.widget.ImageTextButton;
import com.tlh.android.widget.TwoCircles;
import com.tlh.android.widget.dialog.SelfDialogBuilder;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.base.BaseActivity;
import com.zhangyuhuishou.zyhs.database.BucketStatusModel;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.model.TerminalModel;
import com.zhangyuhuishou.zyhs.model.UpdateModel;
import com.zhangyuhuishou.zyhs.pcb.PcbByteNumModel;
import com.zhangyuhuishou.zyhs.pcb.PcbCommand;
import com.zhangyuhuishou.zyhs.pcb.PcbModel;
import com.zhangyuhuishou.zyhs.pcb.PcbUpdateAdapter;
import com.zhangyuhuishou.zyhs.pcb.PcbUpdateModel;
import com.zhangyuhuishou.zyhs.pcb.PcbUpdateUtil;
import com.zhangyuhuishou.zyhs.presenter.IUpdateView;
import com.zhangyuhuishou.zyhs.presenter.UpdatePresenter;
import com.zhangyuhuishou.zyhs.serialport.manager.SerialPortManager;
import com.zhangyuhuishou.zyhs.serialport.manager.SerialPortUtils;
import com.zhangyuhuishou.zyhs.serialport.message.CloseDoorModel;
import com.zhangyuhuishou.zyhs.serialport.message.RecvMessage;
import com.zhangyuhuishou.zyhs.serialport.util.ByteUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.List;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * ??????:created by author:tlh
 * ??????:2019/4/26 14:02
 * ??????:tianlihui2234@live.com
 * ??????: ??????????????????
 */
public class PcbProgramUpdateActivity extends BaseActivity implements IUpdateView {

    private String TAG = PcbProgramUpdateActivity.class.getSimpleName();

    // ????????????
    @BindView(R.id.dev_code)
    TextView dev_code;

    // ????????????
    @BindView(R.id.dev_info)
    TextView dev_info;

    // ??????
    @BindView(R.id.tv_back)
    TextView tv_back;

    // ????????????
    @BindView(R.id.itb_dev)
    ImageTextButton itb_dev;

    // ???????????????
    @BindView(R.id.tv_version)
    TextView tv_version;

    // ???????????????????????????
    private TwoCircles twoCircles;

    private SerialPortUtils serialPortUtils;// ????????????

    private boolean isOpen = false;// ??????????????????

    private String array[] = null;
    private int updateIndex = 0;
    private int addressIndex = 0;

    private SelfDialogBuilder updateDialog, loadFileDialog;// ?????????,???????????????
    private HorizontalProgressBar horizontalProgressBar;// ?????????

    private Handler handler = new Handler();

    private long sendByteNum = 0;
    private long startTime;

    private DBManager dbManager;
    private List<BucketStatusModel> mList;

    private int intervalTime = 60000;// ????????????????????????

    private UpdatePresenter updatePresenter;// ??????

    private PcbUpdateAdapter pcbUpdateAdapter;// ???????????????

    // ?????????
    @BindView(R.id.tv_countdown_time)
    TextView tv_countdown_time;

    private boolean isAutoUpdate = false;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_pcb_program_update;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        super.initData();

        String rangeId = SPUtils.getString(this, Constant.CURRENT_LOCATION_NUM, "");
        String rangeSource = SPUtils.getString(this, Constant.CURRENT_LOCATION_SOURCE, "");
        updatePresenter = new UpdatePresenter(context);
        updatePresenter.attachView(this);
        if (Utils.isEmpty(rangeId) || Utils.isEmpty(rangeSource)) {
            finish();
            return;
        }
        updatePresenter.checkUpdateForPcb(rangeId, rangeSource);

        dbManager = DBManager.getInstance(context);
        mList = dbManager.getAllDataExceptGlass();

        String currentLocationId = SPUtils.getString(context, Constant.CURRENT_LOCATION_NUM);
        if (!TextUtils.isEmpty(currentLocationId)) {
            currentLocationId = "?????????" + currentLocationId;
            dev_code.setText(currentLocationId);
        }

        String location = SPUtils.getString(context, Constant.CURRENT_COMMUNITYNAME, "");
        if (!TextUtils.isEmpty(location)) {
            dev_info.setText(location);
        }
        itb_dev.setText("?????????????????????????????????");

        pcbUpdateAdapter = new PcbUpdateAdapter(context);
        GridView gridView = findViewById(R.id.gridview);
        gridView.setAdapter(pcbUpdateAdapter);

        isAutoUpdate = getIntent().getBooleanExtra(Constant.IS_AUTO_UPDATE, false);
        if (isAutoUpdate) {
            CountDownTimer countDownTimer = new CountDownTimer(mList.size() * intervalTime, 1000) {
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
        } else {
            tv_countdown_time.setVisibility(View.GONE);
        }

    }

    @Override
    protected void initListener() {
        super.initListener();
        tv_back.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadFileDialog = new SelfDialogBuilder(context);
        loadFileDialog.setLayoutId(R.layout.dialog_load_bin_file);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (twoCircles != null) {
                    twoCircles.stopAnimAndRemoveCallbacks();
                }
                cancelLoadDialog();
            }
        }, 10000);

        EventBus.getDefault().register(this);
        // ????????????
        if (serialPortUtils == null) {
            serialPortUtils = new SerialPortUtils();
            serialPortUtils.setPathAndBaurate("/dev/ttyS1", "9600");
        }
        serialPortUtils.openPort();
        // ?????????
        updateDialog = new SelfDialogBuilder(context);
        updateDialog.setLayoutId(R.layout.dialog_self_check);

        // ???????????????????????????
//        new ReadBinFileAsync().execute();
        // ????????????
//        sendPolling();

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeMessages(0);
        EventBus.getDefault().unregister(this);
        if (serialPortUtils != null) {
            serialPortUtils.closePort();
            serialPortUtils = null;
        }
        cancelDialog();
        cancelLoadDialog();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
        }
    }

    // ????????????
    private void cancelDialog() {
        if (!isFinishing() && updateDialog != null && updateDialog.getDialog() != null && updateDialog.getDialog().isShowing()) {
            updateDialog.dismiss();
        }
    }

    // ??????????????????
    private void cancelLoadDialog() {
        if (!isFinishing() && loadFileDialog != null && loadFileDialog.getDialog() != null && loadFileDialog.getDialog().isShowing()) {
            loadFileDialog.dismiss();
        }
    }

    // ????????????????????????
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPort(CloseDoorModel model) {
        switch (model.getTerminalId()) {
            case "100":
                isOpen = true;
                break;
        }
    }

    // ??????????????????
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RecvMessage message) {
        Log.i("SerialPortManager", "?????????????????????" + message.getCommand());
        PcbCommand.dataCallBack(message.getCommand());
        handler.removeCallbacks(uploadDialogRunnable);
    }

    private BucketStatusModel bucketStatusModel;

    // ???????????????????????????????????????
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEventThing(final TerminalModel model) {
        if (!isOpen) {
            ToastUitls.toastMessage("??????????????????????????????");
            return;
        }
        bucketStatusModel = dbManager.getModel(model.getID_D());
        if(bucketStatusModel == null){
            ToastUitls.toastMessage("??????????????????????????????");
            return;
        }

        String bucketVersion = bucketStatusModel.getVersion();
        if(Utils.isEmpty(bucketVersion)){
            bucketVersion = "0";
        }
        if (serverVersion <= Integer.valueOf(bucketVersion)) {
            ToastUitls.toastMessage("????????????????????????");
            return;
        }
        startTime = System.currentTimeMillis();
        if (isOpen) {
            SerialPortManager.instance().sendCommand(PcbCommand.startUpdate(model.getID_D()));
        }
        updateDialog.show();
        horizontalProgressBar = (HorizontalProgressBar) updateDialog.findViewById(R.id.hpb_progress);
        horizontalProgressBar.startAnim(44 * 1000);

        TextView tv_normal_tip = (TextView) updateDialog.findViewById(R.id.tv_normal_tip);
        tv_normal_tip.setText("???????????????????????????~~~");
        startUpdateByAndBy(bucketStatusModel);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("????????????????????????????????????????????????");
                cancelDialog();
            }
        },45000);
    }

    // ????????????????????????????????????????????????
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final PcbUpdateModel model) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (model.isUpdate()) {
                    addressIndex = addressIndex + 128;
                    String addressHex = ByteUtil.decimal2fitHex(addressIndex, 4);
                    updateIndex = updateIndex + 1;
                    if (updateIndex < array.length) {
                        String arrayUpdateSize = ByteUtil.decimal2fitHex(array[updateIndex].toString().length() / 4, 8);
                        int addressAll = ByteUtil.makeChecksum2("08020000") + ByteUtil.makeChecksum2(addressHex);
                        SerialPortManager.instance().sendCommand(PcbCommand.flashProgram(model.getMachineAddress(), ByteUtil.decimal2fitHex(addressAll, 8) + arrayUpdateSize + array[updateIndex], arrayUpdateSize));
                        sendByteNum = sendByteNum + ByteUtil.hexStr2decimal(arrayUpdateSize);
                        handler.postDelayed(uploadDialogRunnable, 1000);
                    } else {
                        long costTime = System.currentTimeMillis() - startTime;
                        System.out.println("?????????????????????" + costTime);
                        System.out.println("????????????????????????:" + ByteUtil.decimal2fitHex(sendByteNum, 8));
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SerialPortManager.instance().sendCommand(PcbCommand.readByteAndCode(model.getMachineAddress()));
                                handler.postDelayed(uploadDialogRunnable, 1000);
                            }
                        }, 2000);
                    }
                }
            }
        }, 10);
    }

    private int testCishu = 0;

    // ??????????????????
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PcbByteNumModel model) {
        // ???1?????????PCB?????????????????????
        // ???2?????????????????????????????????
        handler.removeCallbacks(uploadDialogRunnable);
        Log.i("SerialPortManager", "?????????????????????" + model.getByteNum());
        if (ByteUtil.decimal2fitHex(sendByteNum, 8).equals(model.getByteNum())) {
            SerialPortManager.instance().sendCommand(PcbCommand.writeUpdateStatus(model.getMachineAddress(), model.getByteNum()));
            testCishu = testCishu + 1;
            System.out.println("?????????????????????" + testCishu);
            if(bucketStatusModel != null){
                dbManager.update_bucket_version(bucketStatusModel.getCOLUMN_ID(),serverVersion + "");
            }
            pcbUpdateAdapter.freshDataList();
            pcbUpdateAdapter.notifyDataSetChanged();
        } else {
            SerialPortManager.instance().sendCommand(PcbCommand.writeUpdateStatus(model.getMachineAddress(), "FFFFFFFF"));
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // ?????????????????????
                initDoingParams();
            }
        }, 1000);

    }

    private int serverVersion = 0;

    // ????????????
    @Override
    public void updateInfo(UpdateModel model) {

    }

    @Override
    public void updateErrorInfo(String message) {
        finish();
    }

    @Override
    public void updatePcbInfo(PcbModel model) {
        if (model == null) {
            finish();
            return;
        }
        try {
            String bucketVersion = model.getData().getVersion();
            if(Utils.isEmpty(bucketVersion)){
                bucketVersion = "0";
            }
            serverVersion = Integer.valueOf(bucketVersion);
            tv_version.setText("??????????????????" + serverVersion);
            String fileUrl = model.getData().getFileUrl();
            if (Utils.isEmpty(fileUrl)) {
                finish();
                return;
            }
            updatePresenter.downBinFile(fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void downPcbOk(boolean isOk) {
        if (!isOk) {
            finish();
            return;
        }
        new ReadBinFileAsync().execute();
    }
    // ????????????

    private class ReadBinFileAsync extends AsyncTask<Void, Void, Exception> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadFileDialog.show();
            twoCircles = (TwoCircles) loadFileDialog.findViewById(R.id.two_circle);
            twoCircles.startAnim();
        }

        @Override
        protected Exception doInBackground(Void... voids) {
            byte[] bt = PcbUpdateUtil.readSdcardFile(context);
            if (bt == null) {
                finish();
                return null;
            }
            String hexData = PcbUpdateUtil.format(bt);
            array = hexData.split("\\n");
            System.out.println("???????????????" + array.length);
            final long startTime = System.currentTimeMillis();
            for (int i = 0; i < array.length; i++) {
                Log.i("Test", array[i]);
                if (i == array.length - 1) {
                    long costTime = System.currentTimeMillis() - startTime;
                    System.out.println("?????????????????????" + costTime);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);
            if (twoCircles != null) {
                twoCircles.stopAnimAndRemoveCallbacks();
            }
            cancelLoadDialog();
            if (!isAutoUpdate) {
                return;
            }
            // ?????????????????????????????????
            for (int i = 0; i < mList.size(); i++) {
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bucketStatusModel = mList.get(k);
                        System.out.println("???????????????" + bucketStatusModel.getVersion() + "?????????????????????" + serverVersion);
                        String bucketVersion = bucketStatusModel.getVersion();
                        if(Utils.isEmpty(bucketVersion)){
                            bucketVersion = "0";
                        }
                        if (Integer.valueOf(bucketVersion) < serverVersion && isOpen) {
                            startUpdateByAndBy(bucketStatusModel);
                        }
                    }
                }, i * 60000);
            }
        }
    }

    // ????????????
    private void startUpdateByAndBy(final BucketStatusModel model) {
        if(model == null){
            return;
        }
        SerialPortManager.instance().sendCommand(PcbCommand.startUpdate(model.getCOLUMN_ID()));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (serialPortUtils != null) {
                    serialPortUtils.closePort();
                    serialPortUtils = null;
                    serialPortUtils = new SerialPortUtils();
                    serialPortUtils.setPathAndBaurate("/dev/ttyS1", "19200");
                }
                serialPortUtils.openPort();
            }
        }, 1000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(array == null || array.length == 0){
                    handler.postDelayed(uploadDialogRunnable, 1000);
                    ToastUitls.toastMessage("???????????????????????????????????????????????????");
                    return;
                }
                String addressHex = ByteUtil.decimal2fitHex(addressIndex, 4);
                int addressAll = ByteUtil.makeChecksum2("08020000") + ByteUtil.makeChecksum2(addressHex);
                String arrayUpdateSize = ByteUtil.decimal2fitHex(array[updateIndex].toString().length() / 4, 8);
                sendByteNum = sendByteNum + ByteUtil.hexStr2decimal(arrayUpdateSize);
                SerialPortManager.instance().sendCommand(PcbCommand.flashProgram(model.getCOLUMN_ID(), ByteUtil.decimal2fitHex(addressAll, 8) + arrayUpdateSize + array[updateIndex], arrayUpdateSize));
                handler.postDelayed(uploadDialogRunnable, 1000);
            }
        }, 2200);
    }


    // ??????????????????
    private Runnable uploadDialogRunnable = new Runnable() {
        @Override
        public void run() {
            if(bucketStatusModel != null){
                SerialPortManager.instance().sendCommand(PcbCommand.writeUpdateStatus(bucketStatusModel.getCOLUMN_ID(), "FFFFFFFF"));
            }
            initDoingParams();
        }
    };

    private void initDoingParams() {
        if (horizontalProgressBar != null) {
            horizontalProgressBar.stopAnim();
        }
        cancelDialog();
        initUpdateParams();
    }

    // ?????????????????????????????????
    private void initUpdateParams() {
        updateIndex = 0;
        sendByteNum = 0;
        startTime = System.currentTimeMillis();
        addressIndex = 0;
        bucketStatusModel = null;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (serialPortUtils != null) {
                    serialPortUtils.closePort();
                    serialPortUtils = null;
                    serialPortUtils = new SerialPortUtils();
                    serialPortUtils.setPathAndBaurate("/dev/ttyS1", "9600");
                }
                serialPortUtils.openPort();
            }
        }, 1000);
    }

    // ????????????
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private void sendPolling() {
        mCompositeDisposable.clear();
        DisposableObserver<Long> disposableObserver = getTimeDemoObserver();
        Observable.interval(180, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }

    private DisposableObserver getTimeDemoObserver() {
        return new DisposableObserver<Object>() {
            @Override
            public void onNext(Object o) {
                try {
                    for (int i = 0; i < mList.size(); i++) {
                        final int k = i;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (isOpen) {
                                    bucketStatusModel = mList.get(k);
                                    startUpdateByAndBy(bucketStatusModel);
                                }
                            }
                        }, i * 60000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }
}
