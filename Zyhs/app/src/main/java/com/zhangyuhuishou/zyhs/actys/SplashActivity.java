package com.zhangyuhuishou.zyhs.actys;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.ApkUtils;
import com.tlh.android.utils.SPUtils;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.base.BaseActivity;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.model.PointModel;
import java.util.List;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 作者:created by author:tlh
 * 日期:2018/8/2 15:01
 * 邮箱:tianlihui2234@live.com
 * 描述:启动页（主要用于开启必备权限）
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
    }


    @Override
    protected void initData() {
        super.initData();
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        // 存储
        if (EasyPermissions.hasPermissions(context, perms)) {
            stepNextPage();
        } else {
            EasyPermissions.requestPermissions(SplashActivity.this, "请授予存储权限！", 0x01, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        super.onPermissionsGranted(requestCode, perms);
        stepNextPage();
    }

    // 进入下一个页面
    private void stepNextPage() {
        String channelInfo = ApkUtils.getAppMetaData(context, "UMENG_CHANNEL");
        System.out.println("渠道信息：" + channelInfo);
        if ("factory".equals(channelInfo)) {
            // 判断是否设置过点位
            String currentLocationId = SPUtils.getString(SplashActivity.this, Constant.CURRENT_LOCATION_ID, "nothing");
            if ("nothing".equals(currentLocationId)) {
                // 工厂测试使用
                String lkkLocationInfo = "{\n" +
                        "    \"code\": 0,\n" +
                        "    \"data\": [\n" +
                        "        {\n" +
                        "            \"active\": \"Y\",\n" +
                        "            \"activeName\": \"有效\",\n" +
                        "            \"activeTime\": \"2019-05-13 11:05:43\",\n" +
                        "            \"communityId\": \"86\",\n" +
                        "            \"communityName\": \"章鱼回收（老田）\",\n" +
                        "            \"createTime\": \"2018-12-04 17:27:40\",\n" +
                        "            \"lastConnectTime\": \"2019-05-21 21:55:13\",\n" +
                        "            \"lat\": 22.543759,\n" +
                        "            \"lng\": 114.075254,\n" +
                        "            \"modifyTime\": \"\",\n" +
                        "            \"rangeId\": \"ecd55c6812554e2db4e8bec5bce97a5b\",\n" +
                        "            \"rangeName\": \"工厂测试\",\n" +
                        "            \"rangeNum\": \"440304-86-20181204-1\",\n" +
                        "            \"rangeSource\": \"zyhs\",\n" +
                        "            \"rangeSourceName\": \"章鱼回收\",\n" +
                        "            \"status\": \"0\",\n" +
                        "            \"statusName\": \"未激活\",\n" +
                        "            \"terminalCount\": 6,\n" +
                        "            \"terminalList\": [\n" +
                        "                {\n" +
                        "                    \"communityId\": \"\",\n" +
                        "                    \"communityName\": \"章鱼回收（老田）\",\n" +
                        "                    \"createTime\": \"2018-12-04 17:24:12\",\n" +
                        "                    \"criticalValue\": 0,\n" +
                        "                    \"currentValue\": 0,\n" +
                        "                    \"fullValue\": 0,\n" +
                        "                    \"maxValue\": 0,\n" +
                        "                    \"modifyTime\": \"2018-12-04 17:24:12\",\n" +
                        "                    \"rangeId\": \"ecd55c6812554e2db4e8bec5bce97a5b\",\n" +
                        "                    \"rangeName\": \"工厂测试\",\n" +
                        "                    \"rangeNum\": \"440304-86-20181204-1\",\n" +
                        "                    \"remark\": \"老田工厂测试\",\n" +
                        "                    \"status\": \"used\",\n" +
                        "                    \"statusName\": \"已用\",\n" +
                        "                    \"terminalId\": \"d8d4142bf52e4e13a5e2e41c401b44dc\",\n" +
                        "                    \"terminalNum\": \"JS-20181204-001\",\n" +
                        "                    \"terminalTypeId\": \"metal\",\n" +
                        "                    \"terminalTypeName\": \"金属\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"communityId\": \"\",\n" +
                        "                    \"communityName\": \"章鱼回收（老田）\",\n" +
                        "                    \"createTime\": \"2018-12-04 17:24:00\",\n" +
                        "                    \"criticalValue\": 0,\n" +
                        "                    \"currentValue\": 0,\n" +
                        "                    \"fullValue\": 0,\n" +
                        "                    \"maxValue\": 0,\n" +
                        "                    \"modifyTime\": \"2018-12-04 17:24:00\",\n" +
                        "                    \"rangeId\": \"ecd55c6812554e2db4e8bec5bce97a5b\",\n" +
                        "                    \"rangeName\": \"工厂测试\",\n" +
                        "                    \"rangeNum\": \"440304-86-20181204-1\",\n" +
                        "                    \"remark\": \"老田工厂测试\",\n" +
                        "                    \"status\": \"used\",\n" +
                        "                    \"statusName\": \"已用\",\n" +
                        "                    \"terminalId\": \"dd3b58f767a649ae8555e155371c16d2\",\n" +
                        "                    \"terminalNum\": \"SLZP-20181204-001\",\n" +
                        "                    \"terminalTypeId\": \"plastic\",\n" +
                        "                    \"terminalTypeName\": \"塑料制品\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"communityId\": \"\",\n" +
                        "                    \"communityName\": \"章鱼回收（老田）\",\n" +
                        "                    \"createTime\": \"2018-12-04 17:23:41\",\n" +
                        "                    \"criticalValue\": 0,\n" +
                        "                    \"currentValue\": 0,\n" +
                        "                    \"fullValue\": 0,\n" +
                        "                    \"maxValue\": 0,\n" +
                        "                    \"modifyTime\": \"2018-12-04 17:23:41\",\n" +
                        "                    \"rangeId\": \"ecd55c6812554e2db4e8bec5bce97a5b\",\n" +
                        "                    \"rangeName\": \"工厂测试\",\n" +
                        "                    \"rangeNum\": \"440304-86-20181204-1\",\n" +
                        "                    \"remark\": \"老田工厂测试\",\n" +
                        "                    \"status\": \"used\",\n" +
                        "                    \"statusName\": \"已用\",\n" +
                        "                    \"terminalId\": \"ab3a36735dd742158fb5f7a110dad0e0\",\n" +
                        "                    \"terminalNum\": \"FZP-20181204-001\",\n" +
                        "                    \"terminalTypeId\": \"spin\",\n" +
                        "                    \"terminalTypeName\": \"纺织品\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"communityId\": \"\",\n" +
                        "                    \"communityName\": \"章鱼回收（老田）\",\n" +
                        "                    \"createTime\": \"2018-12-04 17:22:57\",\n" +
                        "                    \"criticalValue\": 0,\n" +
                        "                    \"currentValue\": 0,\n" +
                        "                    \"fullValue\": 0,\n" +
                        "                    \"maxValue\": 0,\n" +
                        "                    \"modifyTime\": \"2018-12-04 17:22:57\",\n" +
                        "                    \"rangeId\": \"ecd55c6812554e2db4e8bec5bce97a5b\",\n" +
                        "                    \"rangeName\": \"工厂测试\",\n" +
                        "                    \"rangeNum\": \"440304-86-20181204-1\",\n" +
                        "                    \"remark\": \"老田工厂测试\",\n" +
                        "                    \"status\": \"used\",\n" +
                        "                    \"statusName\": \"已用\",\n" +
                        "                    \"terminalId\": \"1269d0b6a8f94645b3c48d98741472bf\",\n" +
                        "                    \"terminalNum\": \"FZ-20181204-001\",\n" +
                        "                    \"terminalTypeId\": \"paper\",\n" +
                        "                    \"terminalTypeName\": \"纸张\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"communityId\": \"\",\n" +
                        "                    \"communityName\": \"章鱼回收（老田）\",\n" +
                        "                    \"createTime\": \"2018-12-04 17:22:43\",\n" +
                        "                    \"criticalValue\": 0,\n" +
                        "                    \"currentValue\": 0,\n" +
                        "                    \"fullValue\": 0,\n" +
                        "                    \"maxValue\": 0,\n" +
                        "                    \"modifyTime\": \"2018-12-04 17:23:07\",\n" +
                        "                    \"rangeId\": \"ecd55c6812554e2db4e8bec5bce97a5b\",\n" +
                        "                    \"rangeName\": \"工厂测试\",\n" +
                        "                    \"rangeNum\": \"440304-86-20181204-1\",\n" +
                        "                    \"remark\": \"老田工厂测试\",\n" +
                        "                    \"status\": \"used\",\n" +
                        "                    \"statusName\": \"已用\",\n" +
                        "                    \"terminalId\": \"018a0802476a42649ff9c72bd0f5087f\",\n" +
                        "                    \"terminalNum\": \"SLP-20181204-001\",\n" +
                        "                    \"terminalTypeId\": \"plasticBottle\",\n" +
                        "                    \"terminalTypeName\": \"塑料瓶\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"communityId\": \"\",\n" +
                        "                    \"communityName\": \"章鱼回收（老田）\",\n" +
                        "                    \"createTime\": \"2018-12-04 17:22:32\",\n" +
                        "                    \"criticalValue\": 0,\n" +
                        "                    \"currentValue\": 0,\n" +
                        "                    \"fullValue\": 0,\n" +
                        "                    \"maxValue\": 0,\n" +
                        "                    \"modifyTime\": \"2018-12-04 17:23:15\",\n" +
                        "                    \"rangeId\": \"ecd55c6812554e2db4e8bec5bce97a5b\",\n" +
                        "                    \"rangeName\": \"工厂测试\",\n" +
                        "                    \"rangeNum\": \"440304-86-20181204-1\",\n" +
                        "                    \"remark\": \"老田工厂测试\",\n" +
                        "                    \"status\": \"used\",\n" +
                        "                    \"statusName\": \"已用\",\n" +
                        "                    \"terminalId\": \"d3004655ca3f4401b17a2ef3d41c5d00\",\n" +
                        "                    \"terminalNum\": \"BLP-20181204-001\",\n" +
                        "                    \"terminalTypeId\": \"glassBottle\",\n" +
                        "                    \"terminalTypeName\": \"玻璃瓶\"\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"version\": \"\"\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"message\": \"\"\n" +
                        "}";
                try{
                    Gson gson = new Gson();
                    PointModel pointModel = gson.fromJson(lkkLocationInfo, PointModel.class);
                    List<PointModel.DataBean> points = pointModel.getData();
                    PointModel.DataBean model = points.get(0);
                    // 存储点位Id
                    SPUtils.putString(context, Constant.CURRENT_LOCATION_ID, model.getRangeId());
                    SPUtils.putString(context, Constant.CURRENT_LOCATION_NUM, model.getRangeNum());
                    SPUtils.putString(context, Constant.CURRENT_COMMUNITYNAME, model.getCommunityName());
                    SPUtils.putString(context, Constant.CURRENT_RANGENAME, model.getRangeName());
                    SPUtils.putString(context, Constant.CURRENT_LOCATION_SOURCE, model.getRangeSource());
                    DBManager dbManager = DBManager.getInstance(context);
                    // 重新设置数据
                    dbManager.clearDevList();
                    List<PointModel.DataBean.TerminalListBean> terminals = model.getTerminalList();
                    for (int i = 0; i < terminals.size(); i++) {
                        dbManager.insertDev(terminals.get(i));
                    }
                    startActivity(new Intent(context, MainActivity.class));
                    finish();
                    return;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }


        // 判断是否设置过点位
        String currentLocationId = SPUtils.getString(SplashActivity.this, Constant.CURRENT_LOCATION_ID, "nothing");
        if ("nothing".equals(currentLocationId)) {
            Intent intent = new Intent(SplashActivity.this, SettingActivity.class);
            intent.putExtra(Constant.LAUNCH_COME_FROM, Constant.LAUNCH_COME_FROM_SPLASH);
            startActivity(intent);
            finish();
            return;
        }

        startActivity(new Intent(context, MainActivity.class));
        finish();
    }
}
