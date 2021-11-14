package com.zhangyuhuishou.zyhs.presenter;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.NetworkUtils;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.utils.Utils;
import com.zhangyuhuishou.zyhs.base.BasePresenter;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.model.ClassificationModel;
import com.zhangyuhuishou.zyhs.model.OrderDetailModel;
import com.zhangyuhuishou.zyhs.model.UserModel;
import com.zhangyuhuishou.zyhs.serialport.constant.NormalConstant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;

// 订单相关
public class OrderPresenter extends BasePresenter<IOrderView> {

    private Context context;

    public OrderPresenter(Context context) {
        this.context = context;
    }

    // 创建订单
    public void createOrderNew(final String rangeId, final JSONArray orderInfo, final boolean isShow, final String token) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }

            if (orderInfo.length() == 0) {
                return;
            }
            final long faceDetectTime_action = System.currentTimeMillis();

            DealHttp.createOrderNew(rangeId, orderInfo, token, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        long algorithmTime = System.currentTimeMillis() - faceDetectTime_action;
                        System.out.println("创建订单时间：" + algorithmTime);
                        System.out.println("创建订单返回数据" + o.toString());
                        if (isShow) {
                            return;
                        }
                        Gson gson = new Gson();
                        OrderDetailModel model = gson.fromJson(o.toString(), OrderDetailModel.class);
                        if (model.getCode() == 0) {
                            if (model.getData().getStealStatus() == 1) {
                                EventBus.getDefault().post(NormalConstant.STEAL_RECYCLE_EXCEPTION);
                            } else {
                                if (model.getData().getStatus() == 1) {
                                    EventBus.getDefault().post(model);
                                } else {
                                    EventBus.getDefault().post(NormalConstant.INTERFACE_ACCESS_SUCCESS);
                                    getBaseView().showOrderDetail(model);
                                }
                            }
                        } else {
                            System.out.println(model.getMessage());
                            DBManager.getInstance(context).insertOrderData(token, orderInfo.toString());
                            EventBus.getDefault().post(NormalConstant.INTERFACE_ACCESS_FAILURE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
//                        DBManager.getInstance(context).insertOrderData(SPUtils.getString(context, Constant.USER_TOKEN,""),orderInfo.toString());
                        EventBus.getDefault().post(NormalConstant.INTERFACE_ACCESS_FAILURE);
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                    System.out.println("接口访问失败");
                    System.out.println("丢失订单信息：" + orderInfo.toString());
                    DBManager.getInstance(context).insertOrderData(token, orderInfo.toString());
                    EventBus.getDefault().post(NormalConstant.INTERFACE_ACCESS_FAILURE);
                }
            });
        } else {
            System.out.println("丢失订单信息：" + orderInfo.toString());
            DBManager.getInstance(context).insertOrderData(token, orderInfo.toString());
            EventBus.getDefault().post(NormalConstant.INTERFACE_ACCESS_FAILURE);
        }
    }

    // 创建订单（失败为请求成功的订单）
    public void createOrderMiss(final String rangeId, final int id, final String token, final JSONArray orderInfo) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            DealHttp.createOrder(rangeId, token, orderInfo, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        System.out.println("丢失订单：" + o.toString());
                        Gson gson = new Gson();
                        OrderDetailModel model = gson.fromJson(o.toString(), OrderDetailModel.class);
                        if (model.getCode() == 0) {
                            DBManager.getInstance(context).deleteByTokenAndData(id);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                }
            });
        }
    }


    // 获取回收物价格
    public void getList(String rangeId, String communityId) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {
            if (!isViewAttached()) {
                return;
            }

            DealHttp.getList(rangeId, communityId, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    Log.e("ClassificationModel", o.toString());
                    try {
                        Gson gson = new Gson();
                        ClassificationModel model = gson.fromJson(o.toString(), ClassificationModel.class);
                        if (model.getCode() == 0) {
                            getBaseView().getRecyclerPrice(model.getData());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                }
            });
        }
    }

}
