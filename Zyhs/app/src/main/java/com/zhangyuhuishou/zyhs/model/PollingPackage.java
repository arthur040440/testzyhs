package com.zhangyuhuishou.zyhs.model;

import android.content.Context;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.ApkUtils;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.utils.Utils;
import com.zhangyuhuishou.zyhs.database.BucketStatusModel;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.presenter.PointPresenter;
import com.zhangyuhuishou.zyhs.serialport.constant.NormalConstant;
import com.zhangyuhuishou.zyhs.serialport.util.ByteUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PollingPackage {

    private Context context;
    private DBManager dbManager;

    public PollingPackage(Context context) {
        this.context = context;
        dbManager = DBManager.getInstance(context);
    }

    // 投递界面发送心跳包
    public void sendPollingPackage(String terminalType, PointPresenter pointPresenter) {
        if(Utils.isEmpty(terminalType) || pointPresenter == null){
            return;
        }

        JSONArray terminalInfo = new JSONArray();
        List<TerminalModel> plasticBottleList = dbManager.getDevListByType(Constant.DEV_PLASTIC_TYPE);
        List<TerminalModel> glassBottleList = dbManager.getDevListByType(Constant.DEV_GLASS_TYPE);
        List<TerminalModel> paperList = dbManager.getDevListByType(Constant.DEV_PAPER_TYPE);
        List<TerminalModel> spinList = dbManager.getDevListByType(Constant.DEV_SPIN_TYPE);
        List<TerminalModel> metalList = dbManager.getDevListByType(Constant.DEV_METAL_TYPE);
        List<TerminalModel> plasticList = dbManager.getDevListByType(Constant.DEV_CEMENT_TYPE);
        List<TerminalModel> harmfulList = dbManager.getDevListByType(Constant.DEV_POISON_TYPE);

        BucketStatusModel plastic = dbManager.getModel(NormalConstant.TYPE_PLASTIC);
        BucketStatusModel glass = dbManager.getModel(NormalConstant.TYPE_GLASS);
        String terminalStatus = "";
        int tempI = 0;
        int bottleNum = 0;
        String IDS = "";
        String devType = "";
        int devNum;

        if (plasticBottleList != null && plasticBottleList.size() > 0) {
            for (int i = 0; i < plasticBottleList.size(); i++) {
                bottleNum += plasticBottleList.get(i).getWeight();
            }
        }

        if (glassBottleList != null && glassBottleList.size() > 0) {
            for (int i = 0; i < glassBottleList.size(); i++) {
                bottleNum += glassBottleList.get(i).getWeight();
            }
        }

        // 找出PCB最小版本号的值
        int pcbVersion = 1;
        List<Integer> pcbVersionList = new ArrayList<>();
        List<BucketStatusModel> mList = dbManager.getAllDataExceptGlass();
        for (int i = 0; i < mList.size(); i++) {
            BucketStatusModel bucketStatusModel = mList.get(i);
            if (Utils.isEmpty(bucketStatusModel.getVersion())) {
                pcbVersionList.add(0);
            } else {
                pcbVersionList.add(Integer.valueOf(bucketStatusModel.getVersion()));
            }
        }
        if (pcbVersionList.size() != 0) {
            pcbVersion = Collections.min(pcbVersionList);
        }
        String mobileVersion = ApkUtils.getVersion(context, context.getPackageName());

        IDS = ByteUtil.hexStr2decimal(terminalType) + "";
        if (IDS.length() == 1) {
            IDS = "0" + IDS;
        }
        devType = IDS.substring(0, 1);
        try{
            devNum = Integer.valueOf(IDS.substring(1, 2));
            switch (Integer.parseInt(devType)) {
                case 0:
                    if (devNum == 1 && plasticBottleList != null && plasticBottleList.size() > 0) {
                        for (int i = 0; i < plasticBottleList.size(); i++) {
                            if (plastic != null) {
                                if (plastic.getCOLUMN_EQU_FAULT_STA() == 1) {
                                    // 设备故障
                                    terminalStatus = "fault";
                                } else {
                                    if (bottleNum >= Constant.BOTTOM_FULL_NUM && plastic.getCOLUMN_REC_BUCKET_STA() == 0) {
                                        terminalStatus = "full";
                                    } else {
                                        terminalStatus = "running";
                                    }
                                }
                            }

                            // 后续会加上桶满判断（可能存在多个串联桶）
                            TerminalModel model = plasticBottleList.get(i);
                            try {
                                JSONObject object = new JSONObject();
                                object.put("terminalId", model.getTerminalId());
                                object.put("sourceCount", model.getWeight());
                                object.put("terminalRunStatus", terminalStatus);
                                terminalInfo.put(tempI, object);
                                tempI = tempI + 1;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if ("full".equals(terminalStatus)) {
                                pointPresenter.sendPolling(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID), terminalInfo, mobileVersion, pcbVersion + "");
                            }
                        }
                    }
                    if (devNum == 2 && glassBottleList != null && glassBottleList.size() > 0) {
                        for (int i = 0; i < glassBottleList.size(); i++) {
                            if (glass != null) {
                                if (glass.getCOLUMN_EQU_FAULT_STA() == 1) {
                                    // 设备故障
                                    terminalStatus = "fault";
                                } else {
                                    if (bottleNum >= Constant.BOTTOM_FULL_NUM && glass.getCOLUMN_REC_BUCKET_STA() == 0) {
                                        terminalStatus = "full";
                                    } else {
                                        terminalStatus = "running";
                                    }
                                }
                            }

                            TerminalModel model = glassBottleList.get(i);
                            try {
                                JSONObject object = new JSONObject();
                                object.put("terminalId", model.getTerminalId());
                                object.put("sourceCount", model.getWeight());
                                object.put("terminalRunStatus", terminalStatus);
                                terminalInfo.put(tempI, object);
                                tempI = tempI + 1;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if ("full".equals(terminalStatus)) {
                                pointPresenter.sendPolling(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID), terminalInfo, mobileVersion, pcbVersion + "");
                            }
                        }
                    }

                    break;
                case 1:// 纸张
                    for (int i = 0; i < paperList.size(); i++) {
                        TerminalModel terminalModel = paperList.get(i);
                        BucketStatusModel bucketStatusModel = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("1" + i)));
                        if (terminalModel == null || bucketStatusModel == null) {
                            continue;
                        }
                        if (bucketStatusModel.getCOLUMN_EQU_FAULT_STA() == 1) {
                            // 设备故障
                            terminalStatus = "fault";
                        } else {
                            if (terminalModel.getWeight() > Constant.MAX_BUCKET_PAPER_WEIGHT && bucketStatusModel.getCOLUMN_REC_BUCKET_STA() == 0) {
                                // 满了
                                terminalStatus = "full";
                            } else {
                                terminalStatus = "running";
                            }
                        }
                        try {
                            JSONObject object = new JSONObject();
                            object.put("terminalId", terminalModel.getTerminalId());
                            object.put("sourceCount", terminalModel.getWeight());
                            object.put("terminalRunStatus", terminalStatus);
                            terminalInfo.put(tempI, object);
                            tempI = tempI + 1;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if ("full".equals(terminalStatus)) {
                            pointPresenter.sendPolling(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID), terminalInfo, mobileVersion, pcbVersion + "");
                        }
                    }
                    break;
                case 2:// 织物
                    for (int i = 0; i < spinList.size(); i++) {
                        TerminalModel terminalModel = spinList.get(i);
                        BucketStatusModel bucketStatusModel = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("2" + i)));

                        if (terminalModel == null || bucketStatusModel == null) {
                            continue;
                        }

                        if (bucketStatusModel.getCOLUMN_EQU_FAULT_STA() == 1) {
                            // 设备故障
                            terminalStatus = "fault";
                        } else {
                            if (terminalModel.getWeight() > Constant.MAX_BUCKET_SPIN_WEIGHT && bucketStatusModel.getCOLUMN_REC_BUCKET_STA() == 0) {
                                // 满了
                                terminalStatus = "full";
                            } else {
                                terminalStatus = "running";
                            }
                        }

                        try {
                            JSONObject object = new JSONObject();
                            object.put("terminalId", terminalModel.getTerminalId());
                            object.put("sourceCount", terminalModel.getWeight());
                            object.put("terminalRunStatus", terminalStatus);
                            terminalInfo.put(tempI, object);
                            tempI = tempI + 1;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if ("full".equals(terminalStatus)) {
                            pointPresenter.sendPolling(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID), terminalInfo, mobileVersion, pcbVersion + "");
                        }
                    }
                    break;
                case 3:// 金属
                    for (int i = 0; i < metalList.size(); i++) {
                        TerminalModel terminalModel = metalList.get(i);
                        BucketStatusModel bucketStatusModel = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("3" + i)));

                        if (terminalModel == null || bucketStatusModel == null) {
                            continue;
                        }

                        if (bucketStatusModel.getCOLUMN_EQU_FAULT_STA() == 1) {
                            // 设备故障
                            terminalStatus = "fault";
                        } else {
                            if (terminalModel.getWeight() > Constant.MAX_BUCKET_METAL_WEIGHT && bucketStatusModel.getCOLUMN_REC_BUCKET_STA() == 0) {
                                // 满了
                                terminalStatus = "full";
                            } else {
                                terminalStatus = "running";
                            }
                        }

                        try {
                            JSONObject object = new JSONObject();
                            object.put("terminalId", terminalModel.getTerminalId());
                            object.put("sourceCount", terminalModel.getWeight());
                            object.put("terminalRunStatus", terminalStatus);
                            terminalInfo.put(tempI, object);
                            tempI = tempI + 1;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if ("full".equals(terminalStatus)) {
                            pointPresenter.sendPolling(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID), terminalInfo, mobileVersion, pcbVersion + "");
                        }
                    }
                    break;
                case 4:// 塑料制品
                    for (int i = 0; i < plasticList.size(); i++) {
                        TerminalModel terminalModel = plasticList.get(i);
                        BucketStatusModel bucketStatusModel = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("4" + i)));

                        if (terminalModel == null || bucketStatusModel == null) {
                            continue;
                        }

                        if (bucketStatusModel.getCOLUMN_EQU_FAULT_STA() == 1) {
                            // 设备故障
                            terminalStatus = "fault";
                        } else {
                            if (terminalModel.getWeight() > Constant.MAX_BUCKET_PLASTIC_WEIGHT && bucketStatusModel.getCOLUMN_REC_BUCKET_STA() == 0) {
                                // 满了
                                terminalStatus = "full";
                            } else {
                                terminalStatus = "running";
                            }
                        }
                        try {
                            JSONObject object = new JSONObject();
                            object.put("terminalId", terminalModel.getTerminalId());
                            object.put("sourceCount", terminalModel.getWeight());
                            object.put("terminalRunStatus", terminalStatus);
                            terminalInfo.put(tempI, object);
                            tempI = tempI + 1;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if ("full".equals(terminalStatus)) {
                            pointPresenter.sendPolling(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID), terminalInfo, mobileVersion, pcbVersion + "");
                        }
                    }
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    // 清运界面发送心跳包
    public void sendPollingPackage(PointPresenter pointPresenter) {
        if(pointPresenter == null){
            return;
        }

        JSONArray terminalInfo = new JSONArray();
        List<TerminalModel> plasticBottleList = dbManager.getDevListByType(Constant.DEV_PLASTIC_TYPE);
        List<TerminalModel> glassBottleList = dbManager.getDevListByType(Constant.DEV_GLASS_TYPE);
        List<TerminalModel> paperList = dbManager.getDevListByType(Constant.DEV_PAPER_TYPE);
        List<TerminalModel> spinList = dbManager.getDevListByType(Constant.DEV_SPIN_TYPE);
        List<TerminalModel> metalList = dbManager.getDevListByType(Constant.DEV_METAL_TYPE);
        List<TerminalModel> plasticList = dbManager.getDevListByType(Constant.DEV_CEMENT_TYPE);

        BucketStatusModel plastic = dbManager.getModel(NormalConstant.TYPE_PLASTIC);
        BucketStatusModel glass = dbManager.getModel(NormalConstant.TYPE_GLASS);
        String terminalStatus = "";
        int tempI = 0;
        int bottleNum = 0;

        if (plasticBottleList != null && plasticBottleList.size() > 0) {
            for (int i = 0; i < plasticBottleList.size(); i++) {
                bottleNum += plasticBottleList.get(i).getWeight();
            }
        }

        if (glassBottleList != null && glassBottleList.size() > 0) {
            for (int i = 0; i < glassBottleList.size(); i++) {
                bottleNum += glassBottleList.get(i).getWeight();
            }
        }

        // 找出PCB最小版本号的值
        int pcbVersion = 1;
        List<Integer> pcbVersionList = new ArrayList<>();
        List<BucketStatusModel> mList = dbManager.getAllDataExceptGlass();
        for (int i = 0; i < mList.size(); i++) {
            BucketStatusModel bucketStatusModel = mList.get(i);
            if (Utils.isEmpty(bucketStatusModel.getVersion())) {
                pcbVersionList.add(0);
            } else {
                pcbVersionList.add(Integer.valueOf(bucketStatusModel.getVersion()));
            }
        }
        if (pcbVersionList.size() != 0) {
            pcbVersion = Collections.min(pcbVersionList);
        }
        String mobileVersion = ApkUtils.getVersion(context, context.getPackageName());

        for (int i = 0; plasticBottleList != null && i < plasticBottleList.size(); i++) {
            if (plastic != null) {
                if (plastic.getCOLUMN_EQU_FAULT_STA() == 1) {
                    // 设备故障
                    terminalStatus = "fault";
                } else {
                    if (bottleNum >= Constant.BOTTOM_FULL_NUM && plastic.getCOLUMN_REC_BUCKET_STA() == 0) {

                        terminalStatus = "full";
                    } else {
                        terminalStatus = "running";
                    }
                }
            }

            // 后续会加上桶满判断（可能存在多个串联桶）
            TerminalModel model = plasticBottleList.get(i);
            try {
                JSONObject object = new JSONObject();
                object.put("terminalId", model.getTerminalId());
                object.put("sourceCount", model.getWeight());
                object.put("terminalRunStatus", terminalStatus);
                terminalInfo.put(tempI, object);
                tempI = tempI + 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; glassBottleList != null && i < glassBottleList.size(); i++) {
            if (glass != null) {
                if (glass.getCOLUMN_EQU_FAULT_STA() == 1) {
                    // 设备故障
                    terminalStatus = "fault";
                } else {
                    if (bottleNum >= Constant.BOTTOM_FULL_NUM && glass.getCOLUMN_REC_BUCKET_STA() == 0) {
                        terminalStatus = "full";
                    } else {
                        terminalStatus = "running";
                    }
                }
            }

            TerminalModel model = glassBottleList.get(i);
            try {
                JSONObject object = new JSONObject();
                object.put("terminalId", model.getTerminalId());
                object.put("sourceCount", model.getWeight());
                object.put("terminalRunStatus", terminalStatus);
                terminalInfo.put(tempI, object);
                tempI = tempI + 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < paperList.size(); i++) {
            TerminalModel terminalModel = paperList.get(i);
            BucketStatusModel bucketStatusModel = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("1" + i)));

            if (terminalModel == null || bucketStatusModel == null) {
                continue;
            }

            if (bucketStatusModel.getCOLUMN_EQU_FAULT_STA() == 1) {
                // 设备故障
                terminalStatus = "fault";
            } else {
                if (terminalModel.getWeight() > Constant.MAX_BUCKET_PAPER_WEIGHT && bucketStatusModel.getCOLUMN_REC_BUCKET_STA() == 0) {
                    // 满了
                    terminalStatus = "full";
                } else {
                    terminalStatus = "running";
                }
            }

            try {
                JSONObject object = new JSONObject();
                object.put("terminalId", terminalModel.getTerminalId());
                object.put("sourceCount", terminalModel.getWeight());
                object.put("terminalRunStatus", terminalStatus);
                terminalInfo.put(tempI, object);
                tempI = tempI + 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < spinList.size(); i++) {
            TerminalModel terminalModel = spinList.get(i);
            BucketStatusModel bucketStatusModel = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("2" + i)));

            if (terminalModel == null || bucketStatusModel == null) {
                continue;
            }

            if (bucketStatusModel.getCOLUMN_EQU_FAULT_STA() == 1) {
                // 设备故障
                terminalStatus = "fault";
            } else {
                if (terminalModel.getWeight() > Constant.MAX_BUCKET_SPIN_WEIGHT && bucketStatusModel.getCOLUMN_REC_BUCKET_STA() == 0) {
                    // 满了
                    terminalStatus = "full";
                } else {
                    terminalStatus = "running";
                }
            }

            try {
                JSONObject object = new JSONObject();
                object.put("terminalId", terminalModel.getTerminalId());
                object.put("sourceCount", terminalModel.getWeight());
                object.put("terminalRunStatus", terminalStatus);
                terminalInfo.put(tempI, object);
                tempI = tempI + 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < metalList.size(); i++) {
            TerminalModel terminalModel = metalList.get(i);
            BucketStatusModel bucketStatusModel = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("3" + i)));

            if (terminalModel == null || bucketStatusModel == null) {
                continue;
            }

            if (bucketStatusModel.getCOLUMN_EQU_FAULT_STA() == 1) {
                // 设备故障
                terminalStatus = "fault";
            } else {
                if (terminalModel.getWeight() > Constant.MAX_BUCKET_METAL_WEIGHT && bucketStatusModel.getCOLUMN_REC_BUCKET_STA() == 0) {
                    // 满了
                    terminalStatus = "full";
                } else {
                    terminalStatus = "running";
                }
            }

            try {
                JSONObject object = new JSONObject();
                object.put("terminalId", terminalModel.getTerminalId());
                object.put("sourceCount", terminalModel.getWeight());
                object.put("terminalRunStatus", terminalStatus);
                terminalInfo.put(tempI, object);
                tempI = tempI + 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < plasticList.size(); i++) {
            TerminalModel terminalModel = plasticList.get(i);
            BucketStatusModel bucketStatusModel = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("4" + i)));

            if (terminalModel == null || bucketStatusModel == null) {
                continue;
            }

            if (bucketStatusModel.getCOLUMN_EQU_FAULT_STA() == 1) {
                // 设备故障
                terminalStatus = "fault";
            } else {
                if (terminalModel.getWeight() > Constant.MAX_BUCKET_PLASTIC_WEIGHT && bucketStatusModel.getCOLUMN_REC_BUCKET_STA() == 0) {
                    // 满了
                    terminalStatus = "full";
                } else {
                    terminalStatus = "running";
                }
            }
            try {
                JSONObject object = new JSONObject();
                object.put("terminalId", terminalModel.getTerminalId());
                object.put("sourceCount", terminalModel.getWeight());
                object.put("terminalRunStatus", terminalStatus);
                terminalInfo.put(tempI, object);
                tempI = tempI + 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        pointPresenter.sendPolling(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID), terminalInfo, mobileVersion, pcbVersion + "");
    }

}
