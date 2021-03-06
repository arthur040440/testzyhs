package com.zhangyuhuishou.zyhs.banner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.ApkUtils;
import com.tlh.android.utils.MD5Utils;
import com.tlh.android.widget.other.BannerImageViewWithTxt;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.banner.transformer.AccordionTransformer;
import com.zhangyuhuishou.zyhs.banner.transformer.BackgroundToForegroundTransformer;
import com.zhangyuhuishou.zyhs.banner.transformer.CubeInTransformer;
import com.zhangyuhuishou.zyhs.banner.transformer.CubeOutTransformer;
import com.zhangyuhuishou.zyhs.banner.transformer.DefaultTransformer;
import com.zhangyuhuishou.zyhs.banner.transformer.DepthPageTransformer;
import com.zhangyuhuishou.zyhs.banner.transformer.FlipHorizontalTransformer;
import com.zhangyuhuishou.zyhs.banner.transformer.FlipVerticalTransformer;
import com.zhangyuhuishou.zyhs.banner.transformer.ForegroundToBackgroundTransformer;
import com.zhangyuhuishou.zyhs.banner.transformer.RotateDownTransformer;
import com.zhangyuhuishou.zyhs.banner.transformer.RotateUpTransformer;
import com.zhangyuhuishou.zyhs.banner.transformer.ScaleInOutTransformer;
import com.zhangyuhuishou.zyhs.banner.transformer.StackTransformer;
import com.zhangyuhuishou.zyhs.banner.transformer.TabletTransformer;
import com.zhangyuhuishou.zyhs.banner.transformer.ZoomInTransformer;
import com.zhangyuhuishou.zyhs.banner.transformer.ZoomOutSlideTransformer;
import com.zhangyuhuishou.zyhs.banner.transformer.ZoomOutTranformer;
import com.zhangyuhuishou.zyhs.model.AdModel;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * ??????:created by author:tlh
 * ??????:2018/7/29 16:50
 * ??????:tianlihui2234@live.com
 * ??????:
 */

public class Banner extends RelativeLayout {

    private NoScrollViewPager viewPager;
    private final int UPDATE_VIEWPAGER = 0x1000;// ????????????????????????
    //????????????????????????
    private int imgDelyed = 5000;
    //?????????????????????????????????????????????????????????
    private int delyedTime = 2000;
    //??????????????????????????????????????????
    private int autoCurrIndex = 1;
    //??????????????????
    private boolean isAutoPlay = false;
    private List<View> views;
    private BannerViewAdapter mAdapter;
    private OnBannerListener listener;
    private List<Class<? extends ViewPager.PageTransformer>> transformers = new ArrayList<>();
    private String Tag = Banner.class.getSimpleName();
    private FixedSpeedScroller mScroller;

    private List<AdModel.DataBean> mDataList;// ????????????

    public List<AdModel.DataBean> getmDataList() {
        return mDataList;
    }

    public Banner(Context context) {
        super(context);
        init();
    }

    public Banner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Banner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Banner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        viewPager = new NoScrollViewPager(getContext());
        LinearLayout.LayoutParams vp_param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        viewPager.setLayoutParams(vp_param);
        viewPager.setNoScroll(true);
        this.addView(viewPager);
        try {
            // ??????class????????????mScroller??????
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            mScroller = new FixedSpeedScroller(viewPager.getContext(), new AccelerateInterpolator());
            mField.set(viewPager, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ??????????????????
        transformers.add(DefaultTransformer.class);
        transformers.add(AccordionTransformer.class);
        transformers.add(BackgroundToForegroundTransformer.class);
        transformers.add(ForegroundToBackgroundTransformer.class);
        transformers.add(CubeInTransformer.class);//?????????????????????
        transformers.add(CubeOutTransformer.class);
        transformers.add(DepthPageTransformer.class);
        transformers.add(FlipHorizontalTransformer.class);
        transformers.add(FlipVerticalTransformer.class);
        transformers.add(RotateDownTransformer.class);
        transformers.add(RotateUpTransformer.class);
        transformers.add(ScaleInOutTransformer.class);
        transformers.add(StackTransformer.class);
        transformers.add(TabletTransformer.class);
        transformers.add(ZoomInTransformer.class);
        transformers.add(ZoomOutTranformer.class);
        transformers.add(ZoomOutSlideTransformer.class);
    }

    public void setDataList(List<AdModel.DataBean> dataList) {

        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
        mDataList.clear();

        //?????????????????????
        if (views == null) {
            views = new ArrayList<>();
        } else {
            views.clear();
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //????????????????????????????????????
        if (dataList.size() > 1) {
            autoCurrIndex = 1;
            //??????????????????????????????????????????
            for (int i = 0; i < dataList.size() + 2; i++) {
                AdModel.DataBean model = new AdModel.DataBean();
                String url;
                int showTime;
                if (i == 0) {
                    url = dataList.get(dataList.size() - 1).getFileUrl();
                    showTime = dataList.get(dataList.size() - 1).getDuration();
                    model.setFileUrl(url);
                    model.setDuration(showTime);
                } else if (i == dataList.size() + 1) {
                    url = dataList.get(0).getFileUrl();
                    showTime = dataList.get(0).getDuration();
                    model.setFileUrl(url);
                    model.setDuration(showTime);
                } else {
                    url = dataList.get(i - 1).getFileUrl();
                    showTime = dataList.get(i - 1).getDuration();
                    model.setFileUrl(url);
                    model.setDuration(showTime);
                }
                mDataList.add(model);

                if (MimeTypeMap.getFileExtensionFromUrl(url).equals("mp4")) {
                    File file = ApkUtils.getMovieFileByFileUrl(url);
                    if (file.exists()) {
                        url = Constant.VIDEO_PIC_PATH + "/" + MD5Utils.md5(url) + ".mp4";
                    }
                    final MVideoView videoView = new MVideoView(getContext());
                    videoView.setLayoutParams(lp);
                    videoView.setVideoURI(Uri.parse(url));
                    views.add(videoView);
                } else if (MimeTypeMap.getFileExtensionFromUrl(url).equals("gif")) {
                    File file = ApkUtils.getGifFileByFileUrl(url);
                    if (file.exists()) {
                        url = Constant.VIDEO_PIC_PATH + "/" + MD5Utils.md5(url) + ".gif";
                    }
                    BannerImageViewWithTxt imageView = new BannerImageViewWithTxt(getContext());
                    imageView.setLayoutParams(lp);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    if (getContext() != null && !((Activity) getContext()).isDestroyed()) {
                        Glide.with(getContext()).load(url).asGif().into(imageView);
                    }
                    views.add(imageView);
                } else if (MimeTypeMap.getFileExtensionFromUrl(url).equals("jpg") || MimeTypeMap.getFileExtensionFromUrl(url).equals("png") ||
                        MimeTypeMap.getFileExtensionFromUrl(url).equals(".jpeg")) {
                    File file = ApkUtils.getAdFileByFileUrl(Constant.VIDEO_PIC_PATH + "/",url);
                    if (file.exists()) {
                        url = Constant.VIDEO_PIC_PATH + "/" + MD5Utils.md5(url) + ".jpg";
                    }
                    BannerImageViewWithTxt imageView = new BannerImageViewWithTxt(getContext());
                    imageView.setLayoutParams(lp);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    if (getContext() != null && !((Activity) getContext()).isDestroyed()) {
                        Glide.with(getContext()).load(url).placeholder(R.mipmap.ic_banner_start).error(R.mipmap.ic_banner_start).override(1080, 1820).into(imageView);
                    }
                    views.add(imageView);
                } else {
                    File file = ApkUtils.getAdFileByFileUrl(Constant.VIDEO_PIC_PATH + "/", url);
                    if (file.exists()) {
                        url = Constant.VIDEO_PIC_PATH + "/" + MD5Utils.md5(url) + ".jpg";
                    }
                    BannerImageViewWithTxt imageView = new BannerImageViewWithTxt(getContext());
                    imageView.setLayoutParams(lp);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    if (getContext() != null && !((Activity) getContext()).isDestroyed()) {
                        Glide.with(getContext()).load(url).placeholder(R.mipmap.ic_banner_start).error(R.mipmap.ic_banner_start).override(1080, 1820).into(imageView);
                    }
                    views.add(imageView);
                }
            }
        } else if (dataList.size() == 1) {
            AdModel.DataBean model = new AdModel.DataBean();
            autoCurrIndex = 0;
            String url = dataList.get(0).getFileUrl();
            int showTime = dataList.get(0).getDuration();
            model.setFileUrl(url);
            model.setDuration(showTime);
            mDataList.add(model);
            if (MimeTypeMap.getFileExtensionFromUrl(url).equals("mp4")) {
                final File file = ApkUtils.getMovieFileByFileUrl(url);
                if (file.exists()) {
                    url = Constant.VIDEO_PIC_PATH + "/" + MD5Utils.md5(url) + ".mp4";
                }
                final MVideoView videoView = new MVideoView(getContext());
                videoView.setLayoutParams(lp);
                videoView.setVideoURI(Uri.parse(url));
                //??????????????????????????????
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mPlayer) {
                        mPlayer.start();
//                        mPlayer.setLooping(true);
                    }
                });
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        videoView.start();
                        mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                            @Override
                            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
                                    videoView.setBackgroundColor(Color.TRANSPARENT);
                                return true;
                            }
                        });
                    }
                });
                videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        EventBus.getDefault().post("?????????????????????");
                        videoView.stopPlayback(); //????????????????????????????????????????????????????????????
                        return true;
                    }
                });
                views.add(videoView);
            } else {
                File file = ApkUtils.getAdFileByFileUrl(Constant.VIDEO_PIC_PATH + "/", url);
                if (file.exists()) {
                    url = Constant.VIDEO_PIC_PATH + "/" + MD5Utils.md5(url) + ".jpg";
                }
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(lp);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                if (getContext() != null && !((Activity) getContext()).isDestroyed()) {
                    Glide.with(getContext()).load(url).placeholder(R.mipmap.ic_banner_start).error(R.mipmap.ic_banner_start).override(1080, 1820).into(imageView);
                }
                views.add(imageView);

            }
        }

        for (int i = 0; i < views.size(); i++) {
            View v = views.get(i);
            final int x = i;
            if (v instanceof MVideoView) {
                v.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                break;
                            case MotionEvent.ACTION_MOVE:
                                break;
                            case MotionEvent.ACTION_UP:
                                listener.OnBannerClick(x);
                                break;
                        }
                        return true;
                    }
                });
            } else {
                v.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.OnBannerClick(x);
                        }
                    }
                });
            }
        }
    }

    public Banner setBannerAnimation(Class<? extends ViewPager.PageTransformer> transformer) {
        try {
            setPageTransformer(true, transformer.newInstance());
        } catch (Exception e) {
            Log.e(Tag, "Please set the PageTransformer class");
        }
        return this;
    }

    /**
     * Set a {@link ViewPager.PageTransformer} that will be called for each attached page whenever
     * the scroll position is changed. This allows the application to apply custom property
     * transformations to each page, overriding the default sliding look and feel.
     *
     * @param reverseDrawingOrder true if the supplied PageTransformer requires page views
     *                            to be drawn from last to first instead of first to last.
     * @param transformer         PageTransformer that will modify each page's animation properties
     * @return Banner
     */
    public Banner setPageTransformer(boolean reverseDrawingOrder, ViewPager.PageTransformer transformer) {
        viewPager.setPageTransformer(reverseDrawingOrder, transformer);
        return this;
    }

    public void setImgDelyed(int imgDelyed) {
        this.imgDelyed = imgDelyed;
    }

    public void setAutoPlay(boolean isAutoPlay) {
        this.isAutoPlay = isAutoPlay;
    }

    public void setOnBannerListener(OnBannerListener listener) {
        this.listener = listener;
    }

    public void startBanner() {
        mAdapter = new BannerViewAdapter(views);
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(autoCurrIndex);
//        setBannerAnimation(transformers.get(8));
//        setBannerAnimation(transformers.get(7));
//        setBannerAnimation(transformers.get(2));
        setBannerAnimation(transformers.get(15));
//        setBannerAnimation(transformers.get(14));
//        setBannerAnimation(transformers.get(13));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("Tag", "????????????:" + position);
                //????????????
                autoCurrIndex = position;
                getDelayedTime(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("Tag", "???????????????" + state);
                //??????????????????
                mHandler.removeCallbacks(runnable);
                //ViewPager??????
                int pageIndex = autoCurrIndex;
                if (autoCurrIndex == 0) {
                    pageIndex = views.size() - 2;
                } else if (autoCurrIndex == views.size() - 1) {
                    pageIndex = 1;
                }
                if (pageIndex != autoCurrIndex) {
                    //??????????????????????????????
                    viewPager.setCurrentItem(pageIndex, false);
                }

                //???????????????????????????????????????
                if (state == 0 && isAutoPlay && views.size() > 1) {
                    if (mDataList.get(pageIndex).getDuration() <= 0) {
                        delyedTime = imgDelyed;
                    } else {
                        delyedTime = mDataList.get(pageIndex).getDuration();
                    }
                    mHandler.postDelayed(runnable, delyedTime);
                    Log.d("TAG", "" + pageIndex + "--" + autoCurrIndex);
                }
            }
        });
    }

    /**
     * ????????????????????????
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            mHandler.sendEmptyMessage(UPDATE_VIEWPAGER);
        }
    };


    /**
     * ??????delyedTime
     *
     * @param position ????????????
     */
    private void getDelayedTime(int position) {
        System.out.println("???????????????" + position);
        System.out.println("???????????????" + autoCurrIndex);
        View view = views.get(position);
        if (view instanceof VideoView) {
            final VideoView videoView = (VideoView) view;
            videoView.setVisibility(VISIBLE);

            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    videoView.stopPlayback(); //????????????????????????????????????????????????????????????
                    return true;
                }
            });
            videoView.start();
            videoView.seekTo(0);

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    videoView.setVisibility(GONE);
                }
            });
        }


        if (mDataList.get(position).getDuration() <= 0) {
            delyedTime = imgDelyed;
        } else {
            delyedTime = mDataList.get(position).getDuration();
        }
    }


    //??????????????????
    public void startAutoPlay() {
        mHandler.removeCallbacks(runnable);
        isAutoPlay = true;
        if (views.size() > 1) {
            getDelayedTime(autoCurrIndex);
            if (delyedTime <= 0) {
                mHandler.postDelayed(runnable, imgDelyed);
            } else {
                mHandler.postDelayed(runnable, delyedTime);
            }
        }
    }

    // ????????????adapter
    public BannerViewAdapter getmAdapter() {
        return mAdapter;
    }

    //????????????????????????
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VIEWPAGER:
                    viewPager.setCurrentItem(autoCurrIndex + 1, true);
                    break;
            }
        }
    };

    public void stopBanner() {
        mHandler.removeCallbacks(runnable);
    }

    public void dataChange(List<AdModel.DataBean> list) {
        if (list != null && list.size() > 0) {
            //?????????????????????????????????????????????????????????????????????????????????????????????
            //??????delyedTime????????????????????????????????????delyedTime
            stopBanner();
            try {
                setDataList(list);
                mAdapter.setDataList(views);
                mAdapter.notifyDataSetChanged();
                viewPager.setCurrentItem(autoCurrIndex, false);
                //????????????
                if (isAutoPlay && views.size() > 1) {
                    getDelayedTime(autoCurrIndex);
                    if (delyedTime <= 0) {
                        mHandler.postDelayed(runnable, imgDelyed);
                    } else {
                        mHandler.postDelayed(runnable, delyedTime);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
