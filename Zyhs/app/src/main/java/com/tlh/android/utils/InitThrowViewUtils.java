package com.tlh.android.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tlh.android.config.Constant;
import com.tlh.android.widget.ImageViewWithTxt;
import com.zhangyuhuishou.zyhs.database.BucketStatusModel;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.model.TerminalModel;
import com.zhangyuhuishou.zyhs.serialport.constant.NormalConstant;
import com.zhangyuhuishou.zyhs.serialport.util.ByteUtil;

import java.util.List;

// 初始化投递页面View
public class InitThrowViewUtils {

    private ImageViewWithTxt iv_drink;
    private ImageViewWithTxt iv_glass;
    private ImageViewWithTxt iv_paper;
    private ImageViewWithTxt iv_spin;
    private ImageViewWithTxt iv_metal;
    private ImageViewWithTxt iv_plastic;
    private ImageViewWithTxt iv_garbage;
    private ImageView lastView;
    private View.OnClickListener listener;
    private List<LinearLayout> typePages;
    private Context context;

    public InitThrowViewUtils(ImageViewWithTxt iv_drink, ImageViewWithTxt iv_glass, ImageViewWithTxt iv_paper, ImageViewWithTxt iv_spin
            , ImageViewWithTxt iv_metal, ImageViewWithTxt iv_plastic, ImageViewWithTxt iv_garbage, View.OnClickListener listener, List<LinearLayout> typePages, ImageView lastView) {
        this.iv_drink = iv_drink;
        this.iv_glass = iv_glass;
        this.iv_paper = iv_paper;
        this.iv_spin = iv_spin;
        this.iv_metal = iv_metal;
        this.iv_plastic = iv_plastic;
        this.iv_garbage = iv_garbage;
        this.listener = listener;
        this.typePages = typePages;
        this.lastView = lastView;
    }

    public void initView(Context context) {
        this.context = context;
        int classTypeNum = 0;
        DBManager dbManager = DBManager.getInstance(context);
        List<TerminalModel> plasticBottleList = dbManager.getDevListByType(Constant.DEV_PLASTIC_TYPE);
        List<TerminalModel> glassBottleList = dbManager.getDevListByType(Constant.DEV_GLASS_TYPE);
        List<TerminalModel> paperList = dbManager.getDevListByType(Constant.DEV_PAPER_TYPE);
        List<TerminalModel> spinList = dbManager.getDevListByType(Constant.DEV_SPIN_TYPE);
        List<TerminalModel> metalList = dbManager.getDevListByType(Constant.DEV_METAL_TYPE);
        List<TerminalModel> plasticList = dbManager.getDevListByType(Constant.DEV_CEMENT_TYPE);
        List<TerminalModel> harmfulList = dbManager.getDevListByType(Constant.DEV_POISON_TYPE);
        int bottleNum = 0;
        int maxBottle = 0;
        if (plasticBottleList != null && plasticBottleList.size() > 0) {
            classTypeNum = classTypeNum + 1;
            typePages.get(0).setVisibility(View.VISIBLE);
            maxBottle = Constant.BOTTOM_FULL_NUM;
            for (int i = 0; i < plasticBottleList.size(); i++) {
                bottleNum += plasticBottleList.get(i).getWeight();
            }
        } else {
            typePages.get(0).setVisibility(View.GONE);
        }

        if (glassBottleList != null && glassBottleList.size() > 0) {
            classTypeNum = classTypeNum + 1;
            typePages.get(1).setVisibility(View.VISIBLE);
            for (int i = 0; i < glassBottleList.size(); i++) {
                bottleNum += glassBottleList.get(i).getWeight();
            }
        } else {
            typePages.get(1).setVisibility(View.GONE);
        }

        if (paperList != null && paperList.size() > 0) {
            classTypeNum = classTypeNum + 1;
            typePages.get(2).setVisibility(View.VISIBLE);
        } else {
            typePages.get(2).setVisibility(View.GONE);
        }

        if (spinList != null && spinList.size() > 0) {
            classTypeNum = classTypeNum + 1;
            typePages.get(3).setVisibility(View.VISIBLE);
        } else {
            typePages.get(3).setVisibility(View.GONE);
        }

        if (metalList != null && metalList.size() > 0) {
            classTypeNum = classTypeNum + 1;
            typePages.get(4).setVisibility(View.VISIBLE);
        } else {
            typePages.get(4).setVisibility(View.GONE);
        }

        if (plasticList != null && plasticList.size() > 0) {
            classTypeNum = classTypeNum + 1;
            typePages.get(5).setVisibility(View.VISIBLE);
        } else {
            typePages.get(5).setVisibility(View.GONE);
        }

        if (harmfulList != null && harmfulList.size() > 0) {
            classTypeNum = classTypeNum + 1;
            typePages.get(6).setVisibility(View.VISIBLE);
        } else {
            typePages.get(6).setVisibility(View.GONE);
        }

        if (classTypeNum > 5 && lastView != null) {
            lastView.setVisibility(View.GONE);
        }

        BucketStatusModel plastic = dbManager.getModel(NormalConstant.TYPE_PLASTIC);
        BucketStatusModel glass = dbManager.getModel(NormalConstant.TYPE_GLASS);

        if (plastic != null) {
            if (bottleNum >= maxBottle && plastic.getCOLUMN_REC_BUCKET_STA() == 0) {
                iv_drink.setDoorStatus(4);// 满了
                iv_drink.setEnabled(false);
            } else {
                iv_drink.setDoorStatus(3);// 可以使用
                iv_drink.setOnClickListener(listener);

            }

            if (plasticBottleList.get(0).getCountException() == 1) {
                iv_drink.setDoorStatus(4);// 满了
                iv_drink.setEnabled(false);
            }

            if (plastic.getCOLUMN_EQU_FAULT_STA() == 1) {
                iv_drink.setDoorStatus(5);// 设备故障
                iv_drink.setEnabled(false);
            }
        }

        if (glass != null) {
            if (bottleNum >= Constant.BOTTOM_FULL_NUM && glass.getCOLUMN_REC_BUCKET_STA() == 0) {
                iv_glass.setDoorStatus(4);// 满了
                iv_glass.setEnabled(false);
            } else {
                iv_glass.setDoorStatus(3);// 可以使用
                iv_glass.setOnClickListener(listener);

            }

            if (glassBottleList.get(0).getCountException() == 1) {
                iv_glass.setDoorStatus(4);// 满了
                iv_glass.setEnabled(false);
            }

            if (glass.getCOLUMN_EQU_FAULT_STA() == 1) {
                iv_glass.setDoorStatus(5);// 设备故障
                iv_glass.setEnabled(false);
            }
        }
        for (int i = 0; paperList != null && i < paperList.size(); i++) {
            TerminalModel terminalModel = paperList.get(i);
            BucketStatusModel model = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("1" + i)));
            if (model == null || terminalModel == null) {
                continue;
            }

            if (model.getCOLUMN_EQU_FAULT_STA() == 1) {
                iv_paper.setDoorStatus(5);// 设备故障
                iv_paper.setEnabled(false);
                continue;
            }

            if (terminalModel.getWeight() > Constant.MAX_BUCKET_PAPER_WEIGHT && model.getCOLUMN_REC_BUCKET_STA() == 0) {
                iv_paper.setDoorStatus(4);// 满了
                iv_paper.setEnabled(false);
                break;
            } else {
                iv_paper.setDoorStatus(3);// 可以使用
                iv_paper.setOnClickListener(listener);
                iv_paper.setEnabled(true);
                break;
            }
        }

        for (int i = 0; spinList != null && i < spinList.size(); i++) {
            TerminalModel terminalModel = spinList.get(i);
            BucketStatusModel model = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("2" + i)));
            if (model == null || terminalModel == null) {
                continue;
            }

            if (model.getCOLUMN_EQU_FAULT_STA() == 1) {
                iv_spin.setDoorStatus(5);// 设备故障
                iv_spin.setEnabled(false);
                continue;
            }

            if (terminalModel.getWeight() > Constant.MAX_BUCKET_SPIN_WEIGHT && model.getCOLUMN_REC_BUCKET_STA() == 0) {
                iv_spin.setDoorStatus(4);// 满了
                iv_spin.setEnabled(false);
                break;
            } else {
                iv_spin.setDoorStatus(3);// 可以使用
                iv_spin.setOnClickListener(listener);
                iv_spin.setEnabled(true);
                break;
            }
        }

        for (int i = 0; metalList != null && i < metalList.size(); i++) {
            TerminalModel terminalModel = metalList.get(i);
            BucketStatusModel model = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("3" + i)));
            if (model == null || terminalModel == null) {
                continue;
            }

            if (model.getCOLUMN_EQU_FAULT_STA() == 1) {
                iv_metal.setDoorStatus(5);// 设备故障
                iv_metal.setEnabled(false);
                continue;
            }

            if (terminalModel.getWeight() > Constant.MAX_BUCKET_METAL_WEIGHT && model.getCOLUMN_REC_BUCKET_STA() == 0) {
                iv_metal.setDoorStatus(4);// 满了
                iv_metal.setEnabled(false);
                break;
            } else {
                iv_metal.setDoorStatus(3);// 可以使用
                iv_metal.setOnClickListener(listener);
                iv_metal.setEnabled(true);
                break;
            }
        }

        for (int i = 0; plasticList != null && i < plasticList.size(); i++) {
            TerminalModel terminalModel = plasticList.get(i);
            BucketStatusModel model = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("4" + i)));
            if (model == null || terminalModel == null) {
                continue;
            }

            if (model.getCOLUMN_EQU_FAULT_STA() == 1) {
                iv_plastic.setDoorStatus(5);// 设备故障
                iv_plastic.setEnabled(false);
                continue;
            }

            if (terminalModel.getWeight() > Constant.MAX_BUCKET_PLASTIC_WEIGHT && model.getCOLUMN_REC_BUCKET_STA() == 0) {
                iv_plastic.setDoorStatus(4);// 满了
                iv_plastic.setEnabled(false);
                break;
            } else {
                iv_plastic.setDoorStatus(3);// 可以使用
                iv_plastic.setOnClickListener(listener);
                iv_plastic.setEnabled(true);
                break;
            }
        }

        iv_garbage.setDoorStatus(3);// 可以使用
        iv_garbage.setOnClickListener(listener);
    }
}
