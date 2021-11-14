package com.zhangyuhuishou.zyhs.model;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.tlh.android.config.Constant;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.widget.dialog.SelfDialogBuilder;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.serialport.message.CloseDoorModel;
import com.zhangyuhuishou.zyhs.serialport.message.ThingModel;
import com.zhangyuhuishou.zyhs.serialport.util.ByteUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

// 订单提交数据工具
public class OrderArrayModel {

    private Context context;
    private Animation animation;
    private TextView tv_num, tv_unit;

    // 当前可投递的品类对应数据
    private TerminalModel plasticTerminalModel, glassTerminalModel, paperTerminalModel, spinTerminalModel, metalTerminalModel, poisonTerminalModel, cementTerminalModel;

    // 串口返回订单数据组装
    private int plasticNum = 0;
    private int glassNum = 0;
    private int poisonNum = 0;

    private JSONObject plasticObject = new JSONObject();
    private JSONObject glassObject = new JSONObject();
    private JSONObject paperObject = new JSONObject();
    private JSONObject metalObject = new JSONObject();
    private JSONObject cementObject = new JSONObject();
    private JSONObject clothObject = new JSONObject();
    private JSONObject poisonObject = new JSONObject();

    private List<JSONObject> mClassList = new ArrayList<>();// 本地分类对象数据集合

    // 内存重量
    private int localPaperNum = 0;
    private int localMetalNum = 0;
    private int localCementNum = 0;
    private int localClothNum = 0;

    private int initPaperNum = -1;
    private int initClothNum = -1;
    private int initMetalNum = -1;
    private int initCementNum = -1;

    // 机器设备数据
    private DBManager dbManager;

    private List<TerminalModel> plasticBottleList, glassBottleList, paperList, spinList, metalList, plasticList, harmfulList;

    public OrderArrayModel(Context context) {
        this.context = context;
        animation = AnimationUtils.loadAnimation(context, R.anim.anim_big_to_norml);

        dbManager = DBManager.getInstance(context);
        plasticBottleList = dbManager.getDevListByType(Constant.DEV_PLASTIC_TYPE);
        glassBottleList = dbManager.getDevListByType(Constant.DEV_GLASS_TYPE);
        paperList = dbManager.getDevListByType(Constant.DEV_PAPER_TYPE);
        spinList = dbManager.getDevListByType(Constant.DEV_SPIN_TYPE);
        metalList = dbManager.getDevListByType(Constant.DEV_METAL_TYPE);
        plasticList = dbManager.getDevListByType(Constant.DEV_CEMENT_TYPE);
        harmfulList = dbManager.getDevListByType(Constant.DEV_POISON_TYPE);
        for (int i = 0; i < plasticBottleList.size(); i++) {
            plasticTerminalModel = plasticBottleList.get(i);
        }

        for (int i = 0; i < glassBottleList.size(); i++) {
            glassTerminalModel = glassBottleList.get(i);
        }

        if (harmfulList.size() > 0) {
            poisonTerminalModel = harmfulList.get(0);
        }
    }

    public void setTextView(TextView tv_num, TextView tv_unit) {
        this.tv_num = tv_num;
        this.tv_unit = tv_unit;
    }

    //计数投递数量
    private String orderCount = "";

    // 处理订单数据
    public String dealOrderData(ThingModel model) {

        if (tv_num != null) {
            tv_num.setAnimation(animation);
            tv_num.startAnimation(animation);
        }
        String IDS = ByteUtil.hexStr2decimal(model.getSourceId()) + "";
        if (IDS.length() == 1) {
            IDS = "0" + IDS;
        }

        String devType = IDS.substring(0, 1);
        int devNum = Integer.valueOf(IDS.substring(1, 2));
        switch (Integer.valueOf(devType)) {
            case 0:// 塑料瓶和玻璃瓶
                if (devNum == 1 && plasticBottleList.size() > 0) {
                    plasticNum = plasticNum + model.getNum();
                    tv_num.setText(plasticNum + "");
                    tv_unit.setText("个");
                    dbManager.updateDev(plasticTerminalModel.getId(), plasticTerminalModel.getWeight() + model.getNum());
                    plasticTerminalModel.setWeight(plasticTerminalModel.getWeight() + model.getNum());
                    try {
                        if (mClassList.contains(plasticObject)) {
                            mClassList.remove(plasticObject);
                        }
                        plasticObject.put("terminalId", plasticTerminalModel.getTerminalId());
                        plasticObject.put("sourceId", plasticTerminalModel.getTerminalTypeId());
                        plasticObject.put("sourceCount", plasticNum);
                        mClassList.add(plasticObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (devNum == 2 && glassBottleList.size() > 0) {
                    glassNum = glassNum + model.getNum();
                    tv_num.setText(glassNum + "");
                    tv_unit.setText("个");
                    dbManager.updateDev(glassTerminalModel.getId(), glassTerminalModel.getWeight() + model.getNum());
                    glassTerminalModel.setWeight(glassTerminalModel.getWeight() + model.getNum());
                    try {
                        if (mClassList.contains(glassObject)) {
                            mClassList.remove(glassObject);
                        }
                        glassObject.put("terminalId", glassTerminalModel.getTerminalId());
                        glassObject.put("sourceId", glassTerminalModel.getTerminalTypeId());
                        glassObject.put("sourceCount", glassNum);
                        mClassList.add(glassObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 1:// 废纸张
                if (paperList.size() > 0 && devNum < paperList.size()) {
                    localPaperNum = model.getNum();
                    System.out.println("纸硬件返回数据：" + localPaperNum);
                    int displayPaperNum = paperTerminalModel.getWeight() - initPaperNum + localPaperNum;
                    if (displayPaperNum < 0) {
                        tv_num.setText("0");
                    } else {
                        tv_num.setText(dealWeightData(displayPaperNum));
                    }
                    tv_unit.setText("公斤");
                }
                break;
            case 2:// 织物
                if (spinList.size() > 0 && devNum < spinList.size()) {
                    localClothNum = model.getNum();
                    System.out.println("织物硬件返回数据：" + localClothNum);
                    int displayClothNum = spinTerminalModel.getWeight() - initClothNum + localClothNum;
                    if (displayClothNum < 0) {
                        tv_num.setText("0");
                    } else {
                        tv_num.setText(dealWeightData(displayClothNum));
                    }
                    tv_unit.setText("公斤");
                }
                break;
            case 3:// 金属
                if (metalList.size() > 0 && devNum < metalList.size()) {
                    localMetalNum = model.getNum();
                    System.out.println("金属硬件返回数据：" + localMetalNum);
                    int displayMetalNum = metalTerminalModel.getWeight() - initMetalNum + localMetalNum;
                    if (displayMetalNum < 0) {
                        tv_num.setText("0");
                    } else {
                        tv_num.setText(dealWeightData(displayMetalNum));
                    }
                    tv_unit.setText("公斤");
                }
                break;
            case 4:// 塑料制品
                if (plasticList.size() > 0 && devNum < plasticList.size()) {
                    localCementNum = model.getNum();
                    System.out.println("塑料制品硬件返回数据：" + localCementNum);
                    int displayCementNum = cementTerminalModel.getWeight() - initCementNum + localCementNum;
                    if (displayCementNum < 0) {
                        tv_num.setText("0");
                    } else {
                        tv_num.setText(dealWeightData(displayCementNum));
                    }
                    tv_unit.setText("公斤");
                }
                break;
            case 5:// 有害物质
                if (harmfulList.size() > 0 && devNum < harmfulList.size()) {
                    poisonNum = poisonNum + model.getNum();
                    tv_num.setText(poisonNum + "");
                    tv_unit.setText("次");
                }
                break;

        }
        return tv_num.getText().toString();

    }

    // 获取递交订单数据
    public JSONArray getOrderArray() {
        JSONArray orderArray = new JSONArray();
        for (int i = 0; i < mClassList.size(); i++) {
            JSONObject jsonObject = mClassList.get(i);
            orderArray.put(jsonObject);
        }

        System.out.println(orderArray.toString());

        return orderArray;
    }

    public int getOrderLength() {
        return mClassList.size();
    }

    public int getPlasticNum() {
        return plasticNum;
    }

    public int getGlassNum() {
        return glassNum;
    }

    public int getPoisonNum() {
        return this.poisonNum;
    }

    public int getLocalPaperNum() {
        if (localPaperNum != -1 && paperTerminalModel != null) {
            return paperTerminalModel.getWeight() - initPaperNum;
        }
        return localPaperNum;
    }

    public int getLocalMetalNum() {
        if (localMetalNum != -1 && metalTerminalModel != null) {
            return metalTerminalModel.getWeight() - initMetalNum;
        }
        return localMetalNum;
    }

    public int getLocalCementNum() {
        if (localCementNum != -1 && cementTerminalModel != null) {
            return cementTerminalModel.getWeight() - initCementNum;
        }
        return localCementNum;
    }

    public int getLocalClothNum() {
        if (localClothNum != -1 && spinTerminalModel != null) {
            return spinTerminalModel.getWeight() - initClothNum;
        }
        return localClothNum;
    }

    public void setPaperTerminalModel(TerminalModel paperTerminalModel) {
        if (initPaperNum == -1) {
            this.paperTerminalModel = paperTerminalModel;
            initPaperNum = paperTerminalModel.getWeight();
        }
    }

    public void setSpinTerminalModel(TerminalModel spinTerminalModel) {
        if (initClothNum == -1) {
            this.spinTerminalModel = spinTerminalModel;
            initClothNum = spinTerminalModel.getWeight();
        }
    }

    public void setMetalTerminalModel(TerminalModel metalTerminalModel) {
        if (initMetalNum == -1) {
            this.metalTerminalModel = metalTerminalModel;
            initMetalNum = metalTerminalModel.getWeight();
        }
    }

    public void setCementTerminalModel(TerminalModel cementTerminalModel) {
        if (initCementNum == -1) {
            this.cementTerminalModel = cementTerminalModel;
            initCementNum = cementTerminalModel.getWeight();
        }
    }

    // 处理称重数据
    public String dealWeightData(int weight) {
        float num = Float.valueOf(weight + "f") / 1000;
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(num);
    }

    // 展示订单详情
    public void showOrderDetail(OrderDetailModel model, SelfDialogBuilder orderDialog, boolean isReview) {
        String communityName = SPUtils.getString(context, Constant.CURRENT_COMMUNITYNAME, "");
        String rangName = SPUtils.getString(context, Constant.CURRENT_RANGENAME, "");

        // 创建订单提交信息（请求接口）
        orderDialog.show();
        TextView tv_dev_num = (TextView) orderDialog.findViewById(R.id.tv_dev_num);
        String currentLocationId = SPUtils.getString(context, Constant.CURRENT_LOCATION_NUM);
        tv_dev_num.setText(communityName);
        TextView tv_current_time = (TextView) orderDialog.findViewById(R.id.tv_current_time);
        tv_current_time.setText(model.getData().getCreateTime());
        TextView tv_dev_des = (TextView) orderDialog.findViewById(R.id.tv_dev_des);
        tv_dev_des.setText(rangName);

        TextView tv_total_zyb = (TextView) orderDialog.findViewById(R.id.tv_total_zyb);
        tv_total_zyb.setText(String.valueOf(model.getData().getIntegral()));
        TextView tv_service_review = (TextView) orderDialog.findViewById(R.id.tv_service_review);
        int tv_experience_num = 0;
        ConstraintLayout include_yl = (ConstraintLayout) orderDialog.findViewById(R.id.include_yl);
        ConstraintLayout include_glass = (ConstraintLayout) orderDialog.findViewById(R.id.include_glass);
        ConstraintLayout include_paper = (ConstraintLayout) orderDialog.findViewById(R.id.include_paper);
        ConstraintLayout include_spin = (ConstraintLayout) orderDialog.findViewById(R.id.include_spin);
        ConstraintLayout include_plastic = (ConstraintLayout) orderDialog.findViewById(R.id.include_plastic);
        ConstraintLayout include_metal = (ConstraintLayout) orderDialog.findViewById(R.id.include_metal);
        ConstraintLayout include_poison = (ConstraintLayout) orderDialog.findViewById(R.id.include_poison);
        for (int i = 0; i < model.getData().getOrderDetails().size(); i++) {
            OrderDetailModel.DataBean.OrderDetailsBean m = model.getData().getOrderDetails().get(i);
            tv_experience_num = tv_experience_num + m.getTotalExperience();
            String terminalId = m.getTerminalId();
            if (plasticTerminalModel != null && terminalId.equals(plasticTerminalModel.getTerminalId())) {// 塑料瓶
                include_yl.setVisibility(View.VISIBLE);
                TextView bottle_num = (TextView) orderDialog.findViewById(R.id.bottle_num);
                bottle_num.setText(m.getSourceCount());
                TextView zyb_num = (TextView) orderDialog.findViewById(R.id.zyb_num_yl);
                zyb_num.setText(String.valueOf(m.getTotalIntegral()));
                TextView bottle_unit = (TextView) orderDialog.findViewById(R.id.bottle_unit);
                bottle_unit.setText(m.getUnitDesc());
            } else if (glassTerminalModel != null && terminalId.equals(glassTerminalModel.getTerminalId())) {// 玻璃瓶
                include_glass.setVisibility(View.VISIBLE);
                TextView glass_num = (TextView) orderDialog.findViewById(R.id.glass_num);
                glass_num.setText(m.getSourceCount());
                TextView zyb_num = (TextView) orderDialog.findViewById(R.id.zyb_num_glass);
                zyb_num.setText(String.valueOf(m.getTotalIntegral()));
                TextView glass_unit = (TextView) orderDialog.findViewById(R.id.glass_unit);
                glass_unit.setText(m.getUnitDesc());
            } else if (paperTerminalModel != null && terminalId.equals(paperTerminalModel.getTerminalId())) {// 纸张
                include_paper.setVisibility(View.VISIBLE);
                TextView paper_num = (TextView) orderDialog.findViewById(R.id.paper_num);
                paper_num.setText(m.getSourceCount());
                TextView zyb_num = (TextView) orderDialog.findViewById(R.id.zyb_num_paper);
                zyb_num.setText(String.valueOf(m.getTotalIntegral()));
                TextView paper_unit = (TextView) orderDialog.findViewById(R.id.paper_unit);
                paper_unit.setText(m.getUnitDesc());
            } else if (spinTerminalModel != null && terminalId.equals(spinTerminalModel.getTerminalId())) {// 纺织品
                include_spin.setVisibility(View.VISIBLE);
                TextView spin_num = (TextView) orderDialog.findViewById(R.id.cloth_num);
                spin_num.setText(m.getSourceCount());
                TextView zyb_num = (TextView) orderDialog.findViewById(R.id.zyb_num_cloth);
                zyb_num.setText(String.valueOf(m.getTotalIntegral()));
                TextView cloth_unit = (TextView) orderDialog.findViewById(R.id.cloth_unit);
                cloth_unit.setText(m.getUnitDesc());
            } else if (metalTerminalModel != null && terminalId.equals(metalTerminalModel.getTerminalId())) {// 金属
                include_metal.setVisibility(View.VISIBLE);
                TextView metal_num = (TextView) orderDialog.findViewById(R.id.metal_num);
                metal_num.setText(m.getSourceCount());
                TextView zyb_num = (TextView) orderDialog.findViewById(R.id.zyb_num_metal);
                zyb_num.setText(String.valueOf(m.getTotalIntegral()));
                TextView metal_unit = (TextView) orderDialog.findViewById(R.id.metal_unit);
                metal_unit.setText(m.getUnitDesc());
            } else if (poisonTerminalModel != null && terminalId.equals(poisonTerminalModel.getTerminalId())) {// 有害物质
                include_poison.setVisibility(View.VISIBLE);
                TextView poison_num = (TextView) orderDialog.findViewById(R.id.poison_num);
                poison_num.setText(m.getSourceCount());
                TextView zyb_num = (TextView) orderDialog.findViewById(R.id.zyb_num_poison);
                zyb_num.setText(String.valueOf(m.getTotalIntegral()));
                TextView poison_unit = (TextView) orderDialog.findViewById(R.id.poison_unit);
                poison_unit.setText(m.getUnitDesc());
            } else if (cementTerminalModel != null && terminalId.equals(cementTerminalModel.getTerminalId())) {// 塑料制品
                include_plastic.setVisibility(View.VISIBLE);
                TextView palstic_num = (TextView) orderDialog.findViewById(R.id.palstic_num);
                palstic_num.setText(m.getSourceCount());
                TextView zyb_num = (TextView) orderDialog.findViewById(R.id.zyb_num_plastic);
                zyb_num.setText(String.valueOf(m.getTotalIntegral()));
                TextView plastic_unit = (TextView) orderDialog.findViewById(R.id.plastic_unit);
                plastic_unit.setText(m.getUnitDesc());
            }
        }
        if (isReview) {
            tv_service_review.setVisibility(View.VISIBLE);
        }
    }

    // 存储称重数据
    public void saveData(CloseDoorModel model, List<ClassificationModel.DataBean> priceList) {
        if (model == null) {
            return;
        }
        if ("01".equals(model.getSourceId()) || "02".equals(model.getSourceId())) {
            return;
        }
        String IDS = ByteUtil.hexStr2decimal(model.getSourceId()) + "";
        if ("-1".equals(IDS)) {
            return;
        }
        String devType = IDS.substring(0, 1);
        switch (Integer.valueOf(devType)) {
            case 1:// 纸
                System.out.println("纸最终定版数据：" + localPaperNum);
                if (paperTerminalModel == null) {
                    localPaperNum = 0;
                    return;
                }

                if (priceList != null && priceList.size() > 0) {
                    if (localPaperNum > priceList.get(3).getSecurityValue() && localPaperNum < 0) {
                        return;
                    }
                }

                paperTerminalModel.setWeight(paperTerminalModel.getWeight() + localPaperNum);
                if (paperTerminalModel.getWeight() > 0) {
                    dbManager.updateDev(paperTerminalModel.getId(), paperTerminalModel.getWeight());
                } else {
                    dbManager.updateDev(paperTerminalModel.getId(), 0);
                }
                if (localPaperNum != 0) {
                    try {
                        if (mClassList.contains(paperObject)) {
                            mClassList.remove(paperObject);
                        }
                        paperObject.put("terminalId", paperTerminalModel.getTerminalId());
                        paperObject.put("sourceId", paperTerminalModel.getTerminalTypeId());
                        paperObject.put("sourceCount", paperTerminalModel.getWeight() - initPaperNum);
                        mClassList.add(paperObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                localPaperNum = 0;
                break;
            case 2:// 织物
                System.out.println("织物最终定版数据：" + localClothNum);
                if (spinTerminalModel == null) {
                    localClothNum = 0;
                    return;
                }

                if (priceList != null && priceList.size() > 0) {
                    if (localClothNum > priceList.get(4).getSecurityValue() && localClothNum < 0) {
                        return;
                    }
                }

                spinTerminalModel.setWeight(spinTerminalModel.getWeight() + localClothNum);
                if (spinTerminalModel.getWeight() > 0) {
                    dbManager.updateDev(spinTerminalModel.getId(), spinTerminalModel.getWeight());
                } else {
                    dbManager.updateDev(spinTerminalModel.getId(), 0);
                }
                if (localClothNum != 0) {
                    try {
                        if (mClassList.contains(clothObject)) {
                            mClassList.remove(clothObject);
                        }
                        clothObject.put("terminalId", spinTerminalModel.getTerminalId());
                        clothObject.put("sourceId", spinTerminalModel.getTerminalTypeId());
                        clothObject.put("sourceCount", spinTerminalModel.getWeight() - initClothNum);
                        mClassList.add(clothObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                localClothNum = 0;
                break;
            case 3:// 金属
                System.out.println("金属最终定版数据：" + localMetalNum);
                if (metalTerminalModel == null) {
                    localMetalNum = 0;
                    return;
                }

                if (priceList != null && priceList.size() > 0) {
                    if (localMetalNum > priceList.get(6).getSecurityValue() && localMetalNum < 0) {
                        return;
                    }
                }

                metalTerminalModel.setWeight(metalTerminalModel.getWeight() + localMetalNum);
                if (metalTerminalModel.getWeight() > 0) {
                    dbManager.updateDev(metalTerminalModel.getId(), metalTerminalModel.getWeight());
                } else {
                    dbManager.updateDev(metalTerminalModel.getId(), 0);
                }
                if (localMetalNum != 0) {
                    try {
                        if (mClassList.contains(metalObject)) {
                            mClassList.remove(metalObject);
                        }
                        metalObject.put("terminalId", metalTerminalModel.getTerminalId());
                        metalObject.put("sourceId", metalTerminalModel.getTerminalTypeId());
                        metalObject.put("sourceCount", metalTerminalModel.getWeight() - initMetalNum);
                        mClassList.add(metalObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                localMetalNum = 0;
                break;
            case 4:// 塑料制品
                System.out.println("塑料制品最终定版数据：" + localCementNum);
                if (cementTerminalModel == null) {
                    localCementNum = 0;
                    return;
                }

                if (priceList != null && priceList.size() > 0) {
                    if (localCementNum > priceList.get(5).getSecurityValue() && localCementNum < 0) {
                        return;
                    }
                }

                cementTerminalModel.setWeight(cementTerminalModel.getWeight() + localCementNum);
                if (cementTerminalModel.getWeight() > 0) {
                    dbManager.updateDev(cementTerminalModel.getId(), cementTerminalModel.getWeight());
                } else {
                    dbManager.updateDev(cementTerminalModel.getId(), 0);
                }
                if (localCementNum != 0) {
                    try {
                        if (mClassList.contains(cementObject)) {
                            mClassList.remove(cementObject);
                        }
                        cementObject.put("terminalId", cementTerminalModel.getTerminalId());
                        cementObject.put("sourceId", cementTerminalModel.getTerminalTypeId());
                        cementObject.put("sourceCount", cementTerminalModel.getWeight() - initCementNum);
                        mClassList.add(cementObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                localCementNum = 0;
                break;
        }
    }

    public void setPoisonOrder() {
        if (poisonTerminalModel != null) {
            try {
                if (mClassList.contains(poisonObject)) {
                    mClassList.remove(poisonObject);
                }
                poisonObject.put("terminalId", poisonTerminalModel.getTerminalId());
                poisonObject.put("sourceId", Constant.DEV_POISON_TYPE);
                poisonObject.put("sourceCount", getPoisonNum());
                mClassList.add(poisonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
