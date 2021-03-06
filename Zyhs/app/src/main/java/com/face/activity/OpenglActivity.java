package com.face.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.tts.util.TTSUtils;
import com.face.base.IFaceView;
import com.face.bean.FaceppBean;
import com.face.http.FacePresenter;
import com.face.util.CameraMatrix;
import com.face.util.ConUtil;
import com.face.util.ICamera;
import com.face.util.ImageUtils;
import com.face.util.OpenGLDrawRect;
import com.face.util.OpenGLUtil;
import com.face.util.PointsMatrix;
import com.face.util.SensorEventUtil;
import com.face.util.Util;
import com.face.widget.FaceDetectRoundView;
import com.megvii.facepp.sdk.Facepp;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.ApkUtils;
import com.tlh.android.utils.LogUtil;
import com.tlh.android.utils.LoginUtils;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.widget.ImageTextButton;
import com.tlh.android.widget.dialog.SelfDialogBuilder;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.actys.FaceTelPhoneActivity;
import com.zhangyuhuishou.zyhs.actys.ThrowThingsActivity;
import com.zhangyuhuishou.zyhs.base.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import butterknife.BindView;
import butterknife.OnClick;
import cn.finalteam.okhttpfinal.HttpRequest;

import static com.tlh.android.utils.ZXingUtils.createQRImage;

public class OpenglActivity extends BaseActivity implements PreviewCallback, Renderer, SurfaceTexture.OnFrameAvailableListener, IFaceView {

    private boolean isROIDetect, isBackCamera, isOneFaceTrackig, isFaceCompare, isShowFaceRect, is106Points, is3DPose;
    private String trackModel;
    private GLSurfaceView mGlSurfaceView;
    private ICamera mICamera;
    private Camera mCamera;
    private HandlerThread mHandlerThread = new HandlerThread("facepp");
    private Handler mHandler;
    private Facepp facepp;
    private int min_face_size = 200;
    private int detection_interval = 25;
    private HashMap<String, Integer> resolutionMap;
    private SensorEventUtil sensorUtil;
    private float roi_ratio = 0.8f;
    private boolean isScan = true;// ????????????

    private FacePresenter facePresenter;
    private FaceDetectRoundView face_detect_view;
    private CountDownTimer countDownTimer;
    private CountDownTimer dialogCountDownTimer;

    private CountDownTimer httpCountDownTimer;

    // ?????????
    @BindView(R.id.tv_countdown_time)
    TextView tv_countdown_time;

    // ??????
    @BindView(R.id.tv_gender)
    TextView tv_gender;

    // ??????
    @BindView(R.id.tv_age)
    TextView tv_age;

    // ??????
    @BindView(R.id.tv_beauty)
    TextView tv_beauty;

    // ?????????
    @BindView(R.id.tv_scan_msg)
    TextView tv_scan_msg;

    private SelfDialogBuilder faceCollectionDialog;// ?????????????????????
    private TextView tv_logout;
    private ImageTextButton itb_tip;
    private int ks_action = 0;
    private int failIndex = 0;// ????????????????????????
    private SelfDialogBuilder notKnowYouDialog;// ????????????????????????

    private int faceLeft, faceTop, faceRigth, faceBottom;// ?????????????????????

    private Handler handler = new Handler();

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_opengl;
    }

    @Override
    protected void initView() {
        face_detect_view = findViewById(R.id.face_detect_view);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                face_detect_view.startAnim();

            }
        }, 1000);
        init();
        facePresenter = new FacePresenter(context);
        facePresenter.attachView(this);
        countDownTimer = new CountDownTimer(30000 + 500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (tv_countdown_time.getVisibility() == View.GONE) {
                    tv_countdown_time.setVisibility(View.VISIBLE);
                }
                tv_countdown_time.setText(millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                tv_countdown_time.setVisibility(View.GONE);
                cancelDialog();
                finish();
            }
        };
        countDownTimer.start();

        dialogCountDownTimer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                cancelDialog();
                finish();
            }
        };

        faceCollectionDialog = new SelfDialogBuilder(OpenglActivity.this);
        faceCollectionDialog.setLayoutId(R.layout.dialog_face_collection);
        faceCollectionDialog.setOnClickListener(R.id.tv_logout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialog();
                if (ks_action == Util.FACE_CALLBACE_NO_FACE || ks_action == Util.FACE_CALLBACE_NOT_SAME_PEOPLE) {
                    if (failIndex == 5) {
                        cancleHttpRequest();
                        LoginUtils.logout(OpenglActivity.this);
                        return;
                    }
                    recoveryScan();
                    return;
                }
            }
        });

        notKnowYouDialog = new SelfDialogBuilder(OpenglActivity.this);
        notKnowYouDialog.setLayoutId(R.layout.dialog_i_do_not_know_you);

        httpCountDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                System.out.println("???????????????" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                showDetectMessage("????????????????????????");
            }
        };

        // ??????????????????????????????????????????????????????
        TTSUtils.speak(Constant.TTS_TIP_SCAN_FACE_INFO);
    }

    // ???????????????
    private void cancelDialog() {
        if (!isFinishing() && notKnowYouDialog != null && notKnowYouDialog.getDialog() != null && notKnowYouDialog.getDialog().isShowing()) {
            notKnowYouDialog.dismiss();
        }

        if (!isFinishing() && faceCollectionDialog != null && faceCollectionDialog.getDialog() != null && faceCollectionDialog.getDialog().isShowing()) {
            faceCollectionDialog.dismiss();
        }
    }

    private void init() {


        // Log.e("====", SPUtils.getString(context, Constant.CURRENT_COMMUNITY_ID));

        isROIDetect = false;
        isBackCamera = true;
        is106Points = true;
        isOneFaceTrackig = false;
        is3DPose = false;
        isFaceCompare = false;
        trackModel = "Fast";
        min_face_size = 100;
        detection_interval = 10;
        resolutionMap = null;

        facepp = new Facepp();
        sensorUtil = new SensorEventUtil(this);

        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        mGlSurfaceView = findViewById(R.id.opengl_layout_surfaceview);
        mGlSurfaceView.setEGLContextClientVersion(2);// ????????????OpenGL ES 2.0
        mGlSurfaceView.setRenderer(this);// ?????????????????????gl
        // RENDERMODE_CONTINUOUSLY????????????
        // RENDERMODE_WHEN_DIRTY????????????????????????????????? glSurfaceView.requestRender() ??????????????????
        mGlSurfaceView.setRenderMode(mGlSurfaceView.RENDERMODE_WHEN_DIRTY);// ?????????????????????
        mICamera = new ICamera();
        mCamera = mICamera.openCamera(isBackCamera, this, resolutionMap);
        new OpenglAsync().execute();
    }

    private class OpenglAsync extends AsyncTask<Void, Void, Exception> {

        @Override
        protected Exception doInBackground(Void... voids) {
            if (mCamera != null) {
                Angle = 360 - mICamera.Angle;
                if (isBackCamera)
                    Angle = mICamera.Angle;
                int width = mICamera.cameraWidth;
                int height = mICamera.cameraHeight;
                int left = 0;
                int top = 0;
                int right = width;
                int bottom = height;
                facepp.init(OpenglActivity.this, ConUtil.getFileContent(OpenglActivity.this, R.raw.megviifacepp_0_5_2_model), isOneFaceTrackig ? 1 : 0);
                Facepp.FaceppConfig faceppConfig = facepp.getFaceppConfig();
                faceppConfig.interval = detection_interval;
                faceppConfig.minFaceSize = min_face_size;
                faceppConfig.roi_left = left;
                faceppConfig.roi_top = top;
                faceppConfig.roi_right = right;
                faceppConfig.roi_bottom = bottom;
                String[] array = getResources().getStringArray(R.array.tracking_mode_array);
                if (trackModel.equals(array[0]))
                    faceppConfig.detectionMode = Facepp.FaceppConfig.DETECTION_MODE_TRACKING_FAST;
                else if (trackModel.equals(array[1]))
                    faceppConfig.detectionMode = Facepp.FaceppConfig.DETECTION_MODE_TRACKING_ROBUST;
                else if (trackModel.equals(array[2])) {
                    faceppConfig.detectionMode = Facepp.FaceppConfig.MG_FPP_DETECTIONMODE_TRACK_RECT;
                    isShowFaceRect = true;
                }
                facepp.setFaceppConfig(faceppConfig);
            }
            return null;
        }
    }

    private int Angle;

    private void setConfig(int rotation) {
        Facepp.FaceppConfig faceppConfig = facepp.getFaceppConfig();
        if (faceppConfig.rotation != rotation) {
            faceppConfig.rotation = rotation;
            facepp.setFaceppConfig(faceppConfig);
        }
    }

    // ?????????????????????
    @OnClick({R.id.detect_close})
    public void pageClick(View v) {
        switch (v.getId()) {
            case R.id.detect_close:
                cancleHttpRequest();
                LoginUtils.logout(OpenglActivity.this);
                break;
        }
    }

    // ??????????????????
    private void cancleHttpRequest() {
        // ??????????????????????????????
        HttpRequest.cancel(Util.CN_DETECT_URL);
        HttpRequest.cancel(Util.CN_SERACH_URL);
        HttpRequest.cancel(Constant.BASE_URL_NEW + Constant.LOGIN_BY_FACE);
        HttpRequest.cancel(Constant.BASE_URL_NEW + Constant.USER_INFO);
    }

    /**
     * ????????????
     */
    private void drawShowRect() {
        mPointsMatrix.vertexBuffers = OpenGLDrawRect.drawCenterShowRect(isBackCamera, mICamera.cameraWidth, mICamera.cameraHeight, roi_ratio);
    }

    int rotation = Angle;
    float confidence;
    float pitch, yaw, roll;

    private int max = 0;
    private Facepp.Face faceTemp = null;

    @Override
    public void onPreviewFrame(final byte[] imgData, final Camera camera) {
        //????????????????????????????????????????????????
        int width = mICamera.cameraWidth;
        int height = mICamera.cameraHeight;
        final int orientation = sensorUtil.orientation;
        if (orientation == 0)
            rotation = Angle;
        else if (orientation == 1)
            rotation = 0;
        else if (orientation == 2)
            rotation = 180;
        else if (orientation == 3)
            rotation = 360 - Angle;
        setConfig(rotation);

        if (isScan) {
            final Facepp.Face[] faces = facepp.detect(imgData, width, height, Facepp.IMAGEMODE_NV21);
            if (faces != null) {
                ArrayList<ArrayList> pointsOpengl = new ArrayList<ArrayList>();
                ArrayList<FloatBuffer> rectsOpengl = new ArrayList<FloatBuffer>();
                if (faces.length > 0) {
                    System.out.println("???????????????" + faces.length);
                    max = (faces[0].rect.right - faces[0].rect.left) * (faces[0].rect.bottom - faces[0].rect.top);
                    faceTemp = faces[0];
                    for (int c = 0; c < faces.length; c++) {
                        final Facepp.Face face = faces[c];
                        int other = (face.rect.right - face.rect.left) * (face.rect.bottom - face.rect.top);
                        if (max < other) {
                            faceTemp = face;
                        }
                    }

//                    if (is106Points)
//                        facepp.getLandmarkRaw(faceTemp, Facepp.FPP_GET_LANDMARK106);
//                    else
//                        facepp.getLandmarkRaw(faceTemp, Facepp.FPP_GET_LANDMARK81);
//                    if (is3DPose) {
//                        facepp.get3DPose(faceTemp);
//                    }
//
//                    ArrayList<FloatBuffer> triangleVBList = new ArrayList<FloatBuffer>();
//                    for (int i = 0; i < faceTemp.points.length; i++) {
//                        float x = (faceTemp.points[i].x / width) * 2 - 1;
//                        if (isBackCamera)
//                            x = -x;
//                        float y = (faceTemp.points[i].y / height) * 2-1;
//                        float[] pointf = new float[]{y, x, 0.0f};
//                        FloatBuffer fb = mCameraMatrix.floatBufferUtil(pointf);
//                        triangleVBList.add(fb);
//                    }
//                    pointsOpengl.add(triangleVBList);
//                    if (mPointsMatrix.isShowFaceRect) {
//                        facepp.getRect(faceTemp);
//                        FloatBuffer buffer = calRectPostion(faceTemp.rect, mICamera.cameraWidth, mICamera.cameraHeight);
//                        rectsOpengl.add(buffer);
//                    }

                    confidence = faceTemp.confidence;
                    if (confidence > 0.5) {
                        pitch = faceTemp.pitch;
                        yaw = faceTemp.yaw;
                        roll = faceTemp.roll;
                        faceLeft = faceTemp.rect.left;
                        faceTop = faceTemp.rect.top;
                        faceRigth = faceTemp.rect.right;
                        faceBottom = faceTemp.rect.bottom;
                        System.out.println(faceLeft + "," + faceTop + "," + faceRigth + "," + faceBottom);

                        if (faceLeft < 100 || faceTop < 70 || faceRigth > 550 || faceBottom > 500) {
                            isScan = false;
                            tv_scan_msg.setText("???????????????????????????");
                            handler.postDelayed(recoverRunnable, 3000);
                            return;
                        }

                        // ??????????????????
                        face_detect_view.drawPeopleRect(faceTemp.rect);

//                        if(faceRigth - faceLeft < 80 && faceBottom - faceTop < 80){
//                            isScan = false;
//                            tv_scan_msg.setText("???????????????1?????????????????????");
//                            handler.postDelayed(recoverRunnable, 3000);
//                            return;
//                        }

                        tv_scan_msg.setVisibility(View.GONE);
                        isScan = false;
                        if (countDownTimer != null) {
                            tv_countdown_time.setVisibility(View.GONE);
                            tv_countdown_time.setText("");
                            countDownTimer.cancel();
                        }

                        try {
                            Camera.Size previewSize = camera.getParameters().getPreviewSize();
                            YuvImage image = new YuvImage(imgData, ImageFormat.NV21, previewSize.width, previewSize.height, null);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            image.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 80, stream);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                            visitFaceService(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showDetectMessage("???????????????????????????");
                        }
                        faceTemp = null;
                    }
                }

//                synchronized (mPointsMatrix) {
//                    if (faces.length > 0 && is3DPose)
//                        mPointsMatrix.bottomVertexBuffer = OpenGLDrawRect.drawBottomShowRect(0.15f, 0, -0.7f, pitch, -yaw, roll, rotation);
//                    else
//                        mPointsMatrix.bottomVertexBuffer = null;
//                    mPointsMatrix.points = pointsOpengl;
//                    mPointsMatrix.faceRects = rectsOpengl;
//                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mICamera != null) {
            mICamera.closeCamera();
        }
        // ???????????????????????????
        handler.removeCallbacks(recoverRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelDialog();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                facepp.release();
            }
        });
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        if (dialogCountDownTimer != null) {
            dialogCountDownTimer.cancel();
        }

        if (mBitmap != null) {
            mBitmap.recycle();
        }
    }

    private int mTextureID = -1;
    private SurfaceTexture mSurface;
    private CameraMatrix mCameraMatrix;
    private PointsMatrix mPointsMatrix;

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mGlSurfaceView.requestRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // ????????????
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        surfaceInit();
    }

    private void surfaceInit() {
        mTextureID = OpenGLUtil.createTextureID();
        mSurface = new SurfaceTexture(mTextureID);
        // ?????????????????????????????????????????????????????????????????????onFrameAvailable??????
        mSurface.setOnFrameAvailableListener(this);// ?????????????????????????????????
        mCameraMatrix = new CameraMatrix(mTextureID);
        mPointsMatrix = new PointsMatrix(isFaceCompare);
        mPointsMatrix.isShowFaceRect = isShowFaceRect;
        mICamera.startPreview(mSurface);// ??????????????????
        mICamera.actionDetect(this);
        if (isROIDetect)
            drawShowRect();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // ?????????????????????
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        ratio = 1; // ??????OpenGL????????????????????????????????????????????????????????????
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    @Override
    public void onDrawFrame(GL10 gl) {
        final long actionTime = System.currentTimeMillis();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);// ???????????????????????????
        float[] mtx = new float[16];
        mSurface.getTransformMatrix(mtx);
        mCameraMatrix.draw(mtx);
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1f, 0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        mPointsMatrix.draw(mMVPMatrix);
        mSurface.updateTexImage();// ??????image????????????onFrameAvailable??????
    }

    // ????????????????????????
    private void visitFaceService(Bitmap bmp) {
        try {
            if (faceBottom - faceTop <= 0) {
                recoveryScan();
                return;
            }

            String base64Url = "";
            bmp = Bitmap.createBitmap(bmp, faceLeft, faceTop, faceRigth - faceLeft, faceBottom - faceTop);
            base64Url = ImageUtils.bitmapToBase64(bmp);
            bmp.recycle();
            if (TextUtils.isEmpty(base64Url)) {
                recoveryScan();
                return;
            }
            facePresenter.detectFace(base64Url);
            httpCountDownTimer.start();
            LogUtil.e("Face", base64Url);
        } catch (Exception e) {
            e.printStackTrace();
            recoveryScan();
        }
    }

    // ????????????
    private void recoveryScan() {
        // ???????????????????????????
        face_detect_view.stopDrawPeopleRect();
        dialogCountDownTimer.cancel();
        tv_scan_msg.setVisibility(View.VISIBLE);
        face_detect_view.startAnim();
        // ????????????
        if (countDownTimer != null) {
            countDownTimer.start();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isScan = true;
            }
        }, 3000);
        tv_scan_msg.setText("?????????????????????????????????");
    }

    private Bitmap mBitmap;

    @Override
    public void faceTip(String faceTipMessage, int code, String face_token) {
        System.out.println(faceTipMessage);
        httpCountDownTimer.cancel();
        face_detect_view.stopAnimaion();
        dialogCountDownTimer.start();
        switch (code) {
            // ????????????????????????
            case Util.FACE_CALLBACE_NO_FACE:
                showDetectMessage("????????????????????????");
                break;
            // ??????????????????/?????????????????????
            case Util.FACE_CALLBACE_NOT_SAME_PEOPLE:
            case Util.FACE_CALLBACE_NOT_THE_PEOPLE:
                notKnowYouDialog.setOnClickListener(R.id.tv_back_to_pre, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelDialog();
                        cancleHttpRequest();
                        LoginUtils.logout(OpenglActivity.this);
                    }
                });
                notKnowYouDialog.show();
                ImageView qr = (ImageView) notKnowYouDialog.findViewById(R.id.iv_qr);
                mBitmap = createQRImage(Constant.FACE_QR_INFO, 280, 280);
                qr.setImageBitmap(mBitmap);
                break;
            case Util.FACE_CALLBACE_PARSE_ERROR:
                showDetectMessage("??????????????????");
                break;
            case Util.FACE_CALLBACE_ERROR:
                showDetectMessage("????????????????????????");
                break;

        }
    }

    @Override
    public void detectSuccess(FaceppBean.FacesBean bean) {
        dialogCountDownTimer.start();
        httpCountDownTimer.cancel();

        int age = bean.getAttributes().getAge().getValue();
        String gender = bean.getAttributes().getGender().getValue();
        double male_score = bean.getAttributes().getBeauty().getMale_score();
        double female_score = bean.getAttributes().getBeauty().getFemale_score();
        tv_gender.setText(gender.equals("Male") ? "????????????" : "????????????");
        tv_age.setText("?????????" + age);
        tv_beauty.setText(gender.equals("Male") ? "?????????" + (male_score + "").split("\\.")[0] : "?????????" + (female_score + "").split("\\.")[0]);

        FaceppBean.FacesBean.AttributesBean.HeadposeBean headposeBean = bean.getAttributes().getHeadpose();
        if (Math.abs(headposeBean.getYaw_angle()) > Util.faceOffset ||
                Math.abs(headposeBean.getPitch_angle()) > Util.faceOffset ||
                Math.abs(headposeBean.getRoll_angle()) > Util.faceOffset) {
            showDetectMessage("???????????????????????????");
            return;
        }

        FaceppBean.FacesBean.AttributesBean.BlurBean.BlurnessBean blurnessBean = bean.getAttributes().getBlur().getBlurness();
        if (blurnessBean.getValue() > blurnessBean.getThreshold()) {
            showDetectMessage("??????????????????,???????????????");
            return;
        }

        FaceppBean.FacesBean.AttributesBean.MouthstatusBean mouthstatusBean = bean.getAttributes().getMouthstatus();
        if (mouthstatusBean.getSurgical_mask_or_respirator() > Util.faceOffset || mouthstatusBean.getOther_occlusion() > Util.faceOffset) {
            showDetectMessage("?????????????????????");
            return;
        }

        FaceppBean.FacesBean.AttributesBean.EyestatusBean eyestatusBean = bean.getAttributes().getEyestatus();
        if (eyestatusBean.getLeft_eye_status().getOcclusion() > Util.faceOffset || eyestatusBean.getRight_eye_status().getOcclusion() > Util.faceOffset) {
            showDetectMessage("?????????????????????????????????");
            return;
        }

        FaceppBean.FacesBean.AttributesBean.FacequalityBean facequalityBean = bean.getAttributes().getFacequality();

        if (facequalityBean.getValue() < facequalityBean.getThreshold()) {
            showDetectMessage("????????????, ???????????????");
            return;
        }

        // facePresenter.searchFace(bean.getFace_token(), phoneNum);

        // ??????????????????????????????
        facePresenter.loginByFaceOfCommunity(bean.getFace_token(), SPUtils.getString(context, Constant.CURRENT_COMMUNITY_ID));
        face_detect_view.setmBaseSweepAngle(240);
        httpCountDownTimer.start();
    }

    @Override
    public void saveFaceOk() {
        // ??????????????????
    }

    @Override
    public void getUserId(String face_token, String userId) {

    }

    @Override
    public void inputPhoneNumber(final String face_token, String message) {
        httpCountDownTimer.cancel();

      /*  notKnowYouDialog.setOnClickListener(R.id.tv_back_to_pre, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  cancelDialog();
                cancleHttpRequest();
                LoginUtils.logout(OpenglActivity.this);
            }
        });
        notKnowYouDialog.show();
        ImageView qr = (ImageView) notKnowYouDialog.findViewById(R.id.iv_qr);
        mBitmap = CodeUtils.createImage(Constant.FACE_QR_INFO, 280, 280, null);
        qr.setImageBitmap(mBitmap);

        TextView tip = (TextView) notKnowYouDialog.findViewById(R.id.itb_tip);
        tip.setText(message);

        TextView confirm_phone = (TextView) notKnowYouDialog.findViewById(R.id.tv_input_phone_again);
        confirm_phone.setText("?????????????????????");
        confirm_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancleHttpRequest();
                Intent intent = new Intent(OpenglActivity.this, FaceTelPhoneActivity.class);
                intent.putExtra(Constant.CURRENT_FACE_TOKEN, face_token);
                startActivity(intent);
                finish();
            }
        });*/

        cancleHttpRequest();
        Intent intent = new Intent(OpenglActivity.this, FaceTelPhoneActivity.class);
        intent.putExtra(Constant.CURRENT_FACE_TOKEN, face_token);
        startActivity(intent);
        finish();
    }

    @Override
    public void loginSuccess() {
        // ????????????
        httpCountDownTimer.cancel();
        startActivity(new Intent(OpenglActivity.this, ThrowThingsActivity.class));
        sendBroadcast(new Intent(Constant.FINISH_HOME_PAGE));
        finish();
    }

    // ????????????
    private Runnable recoverRunnable = new Runnable() {
        @Override
        public void run() {
            tv_scan_msg.setText("?????????????????????????????????");
            isScan = true;
        }
    };

    // ??????????????????
    private void showDetectMessage(String msg) {
        if (this.isFinishing()) {// ??????Activity??????????????????
            return;
        }
        faceCollectionDialog.show();
        if (tv_logout == null) {
            tv_logout = (TextView) faceCollectionDialog.findViewById(R.id.tv_logout);
        }
        if (itb_tip == null) {
            itb_tip = (ImageTextButton) faceCollectionDialog.findViewById(R.id.itb_tip);
        }
        failIndex = failIndex + 1;
        ks_action = Util.FACE_CALLBACE_NO_FACE;
        itb_tip.setText(msg);
        tv_logout.setText(failIndex == 5 ? "?????????????????????" : "????????????");
    }

    private FloatBuffer calRectPostion(Rect rect, float width, float height) {
        float top = 1 - (rect.top * 1.0f / height) * 2;
        float left = (rect.left * 1.0f / width) * 2 - 1;
        float right = (rect.right * 1.0f / width) * 2 - 1;
        float bottom = 1 - (rect.bottom * 1.0f / height) * 2;

        // ?????????
        float x1 = -top;
        float y1 = left;

        // ?????????
        float x2 = -bottom;
        float y2 = right;

        if (isBackCamera) {
            y1 = -y1;
            y2 = -y2;
        }

        float[] tempFace = {
                x1, y2, 0.0f,
                x1, y1, 0.0f,
                x2, y1, 0.0f,
                x2, y2, 0.0f,
        };

        FloatBuffer buffer = mCameraMatrix.floatBufferUtil(tempFace);
        return buffer;
    }

}
