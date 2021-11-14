package com.tlh.android.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.tlh.android.config.Constant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class ApkUtils {
    public static final String TAG = ApkUtils.class.getName();

    // 获取ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {

        }
        return apiKey;
    }

    public static int getVersionNum(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi.versionCode;
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getVersionNum(Context context) {
        return getVersionNum(context, context.getPackageName());
    }

    public static String getVersion(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi.versionName;
        } catch (Exception e) {
            return "1.0.0";
        }
    }

    public static String getVersion(Context context) {
        return getVersion(context, context.getPackageName());
    }

    public static final String KEY_VERSION = "kVersion";

    // 首次运行该软件 -CC
    public static boolean isFirstRun(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        int oldVersion = prefs.getInt(KEY_VERSION, 0);
        int curVersion = getVersionNum(context);
        if (curVersion != oldVersion) {
            prefs.edit().putInt(KEY_VERSION, curVersion).commit();
            return true;
        }

        return false;
    }

    public static boolean isPkgInstalled(Context context, String pkgName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(pkgName, 0);
            return info != null;
        } catch (NameNotFoundException ex) {
            return false;
        }
    }

    // 设备Id
    public static String getDevId() {
        String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits
        return m_szDevIDShort;
    }

    // 设备型号 -CC
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    // 设备厂商/品牌 -CC
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    // SDK版本 -CC
    public static int getDeviceVersion() {
        return Build.VERSION.SDK_INT;
    }

    // 手机制造商
    public static String getProduct() {
        return Build.PRODUCT;
    }

    // 硬件制造商
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    // 平台信息
    public static String getHardWare() {
        String result = Build.HARDWARE;
        if (result.matches("qcom")) {
            Log.d(TAG, "Qualcomm platform");
            result = "高通平台(Qualcomm) - " + result;
        } else if (result.matches("mt[0-9]*")) {
            result = "MTK平台(MediaTek) - " + result;
        }
        return result;
    }

    // Android 系统版本
    public static String getAndroidVersion() {
        return Build.VERSION.RELEASE;
    }

    // CPU 指令集，可以查看是否支持64位
    public static String getCpuAbi() {
        return Build.CPU_ABI;
    }

    // 显示模块
    public static String getDisplay() {
        return Build.DISPLAY;
    }

    // SDK 当前版本号
    public static int getCurSDK() {
        return Build.VERSION.SDK_INT;
    }

    public static void startAppByPkg(Context context, String pkgName) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkgName);
            context.startActivity(intent);
        } catch (Exception ex) {
            Toast.makeText(context, "未找到应用程序", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 是否是系统软件或者是系统软件的更新软件
     *
     * @return
     */
    public static boolean isSystemApp(PackageInfo pInfo) {
        return (pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    public static boolean isSystemUpdateApp(PackageInfo pInfo) {
        return (pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
    }

    public static boolean isUserApp(PackageInfo pInfo) {
        return (!isSystemApp(pInfo) && !isSystemUpdateApp(pInfo));
    }

    // 当前APP语言包类型
    public static String getLang(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    /**
     * 判断是否安装目标应用
     *
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     * @author tlh
     * @time 2016/12/28 13:56
     */

    public static boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }


    /**
     * 获取组件名称
     *
     * @param packageName  包名
     * @param activityName 类名
     * @return 返回启动组件
     * @author tlh
     * @time 2016/12/28 13:59
     */

    public static ComponentName getComponentName(String packageName, String activityName) {
        return new ComponentName(packageName, activityName);
    }

    /**
     * 获取包信息.
     *
     * @param context the context
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            String packageName = context.getPackageName();
            info = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 通过url下载图片视频至SD卡
     *
     * @author tlh
     * @time 2016/12/28 14:12
     */

    public static boolean downloadPicToSDCard(String pathName, Context context, String url) {
        File destDir = new File(pathName);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        File file;
        // 视频
        if (MimeTypeMap.getFileExtensionFromUrl(url).equals("mp4")) {
            file = getMovieFileByFileUrl(url);
            if (file.exists()) {
                // 视频已经存在
                return true;
            } else {
                return downloadMovieToSDCard(url);
            }
        } else {
            // 图片
            if (MimeTypeMap.getFileExtensionFromUrl(url).equals("gif")) {
                file = getGifFileByFileUrl(url);
            } else {
                file = getAdFileByFileUrl(pathName + "/", url);
            }
            if (file.exists()) {
                // 图片已经存在
                return true;
            } else {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                try {
                    Response response = client.newCall(request).execute();
                    System.out.print(response.code());
                    BufferedSink sink = Okio.buffer(Okio.sink(file));
                    sink.writeAll(response.body().source());
                    sink.close();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 通过url下载Bin文件至SD卡（硬件程序文件）
     *
     * @author tlh
     * @time 2016/12/28 14:12
     */

    public static boolean downloadBinFileToSDCard(Context context, String url) {
        File destDir = new File(Constant.BIN_PATH);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        if (MimeTypeMap.getFileExtensionFromUrl(url).equals("bin")) {
            File file = getBinFileByFileUrl(url);
            if (file.exists()) {
                file.delete();
            }
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            try {
                Response response = client.newCall(request).execute();
                System.out.print(response.code());
                BufferedSink sink = Okio.buffer(Okio.sink(file));
                sink.writeAll(response.body().source());
                sink.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Bitmap getBitmap(String url) {
        Bitmap bm = null;
        try {
            URL iconUrl = new URL(url);
            URLConnection conn = iconUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) conn;
            int length = http.getContentLength();
            conn.connect();
            // 获得图像的字符流
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, length);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();// 关闭流
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

    /**
     * 通过url获取File文件
     *
     * @author tlh
     * @time 2016/12/28 14:13
     */
    public static File getFileByUrl(Context context, String url) {
        String filename = MD5Utils.md5(url);
        return new File(context.getExternalCacheDir(), filename + ".jpg");
    }

    /**
     * 通过url获取File文件
     *
     * @author tlh
     * @time 2016/12/28 14:13
     */
    public static File getAdFileByFileUrl(String pathName, String url) {
        String filename = MD5Utils.md5(url);
        return new File(pathName, filename + ".jpg");
    }

    public static File getMovieFileByFileUrl(String url) {
        String filename = MD5Utils.md5(url);
        return new File(Constant.VIDEO_PIC_PATH + "/", filename + ".mp4");
    }

    public static File getGifFileByFileUrl(String url) {
        String filename = MD5Utils.md5(url);
        return new File(Constant.VIDEO_PIC_PATH + "/", filename + ".gif");
    }

    public static File getBinFileByFileUrl(String url) {
//        String filename = MD5Utils.md5(url);
        return new File(Constant.BIN_PATH + "/", "RTC.bin");
    }

    /**
     * 获取相册中的图片
     *
     * @author tlh
     * @time 2016/12/28 14:13
     */
    public static File getFileUrlByDcim(Context context, String url) {
        String filename = MD5Utils.md5(url);
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Camera", filename + ".jpg");
    }

    // 下载视频到本地
    public static boolean downloadMovieToSDCard(String url) {
        File file = getMovieFileByFileUrl(url);
        if (file.exists()) {
            return true;
        } else {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            try {
                Response response = client.newCall(request).execute();
                System.out.print(response.code());
                BufferedSink sink = Okio.buffer(Okio.sink(file));
                sink.writeAll(response.body().source());
                sink.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // 获取本地视频路径
    public static String getRouteByDcim(Context context, String url) {
        String filename = MD5Utils.md5(url);
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Camera/" + filename + ".mp4";
    }

    public static File getMovieFileByUrl(Context context, String url) {
        String filename = MD5Utils.md5(url);
        return new File(context.getExternalCacheDir(), filename + ".mp4");
    }


    public static void downMovieByUrl(Context context, String movieUrl) {
        try {
            File file = getMovieFileByUrl(context, movieUrl);
            if (file.exists()) {
                return;
            }
            URL url = new URL(movieUrl);
            // 打开连接
            URLConnection conn = url.openConnection();
            // 打开输入流
            InputStream is = conn.getInputStream();
            // 创建字节流
            byte[] bs = new byte[1024];
            int len;
            OutputStream os = new FileOutputStream(file);
            // 写数据
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            // 完成后关闭流
            Log.e(TAG, "download-finish");
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "e.getMessage() --- " + e.getMessage());
        }
    }

    /**
     * 从一个apk文件去获取该文件的版本信息
     *
     * @param context         本应用程序上下文
     * @param archiveFilePath APK文件的路径。如：/sdcard/download/XX.apk
     * @return
     */
    public static String getVersionNameFromApk(Context context, String archiveFilePath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packInfo = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
        String version = packInfo.versionName;
        return version;
    }

    /**
     * 获取application中指定的meta-data。本例中，调用方法时key就是UMENG_CHANNEL
     *
     * @return 如果没有获取成功(没有对应值 ， 或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || key.equals("") || key == null) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }
}
