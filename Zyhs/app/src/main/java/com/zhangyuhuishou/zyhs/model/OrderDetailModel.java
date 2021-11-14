package com.zhangyuhuishou.zyhs.model;

import java.util.List;

public class OrderDetailModel {


    /**
     * code : 0
     * data : {"auditNickName":"","auditStatus":0,"auditTime":"","communityId":"78","communityName":"贝利集团","createTime":"2019-10-11 15:06:24","experience":0,"integral":554,"lat":"30.41201019","lng":"120.31374359","nickName":"琦琦","orderAuditLogId":"","orderDetailList":[],"orderDetails":[{"experience":0,"integral":0,"orderId":"7115","orderNum":"","sourceCount":"5.542","sourceId":"paper","sourceName":"纸张","status":0,"terminalId":"2f385907ebbe4bd7a390647c7e3a73d3","totalCarbon":0,"totalExperience":0,"totalIntegral":554,"unitDesc":"公斤"}],"orderId":7115,"orderNum":"6588318706672209920","rangeId":"F591EAF8216145CBB8DF6D72EFA0DBC3","rangeName":"3楼老田测试","remark":"","status":0,"statusName":"","stealStatus":0,"telphone":"18816284850","userChannelName":"","userChannelType":"","userId":"4cfe675cda224e308c5b5a77ecb49a4d"}
     * message :
     */

    private int code;
    private DataBean data;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataBean {
        /**
         * auditNickName :
         * auditStatus : 0
         * auditTime :
         * communityId : 78
         * communityName : 贝利集团
         * createTime : 2019-10-11 15:06:24
         * experience : 0
         * integral : 554
         * lat : 30.41201019
         * lng : 120.31374359
         * nickName : 琦琦
         * orderAuditLogId :
         * orderDetailList : []
         * orderDetails : [{"experience":0,"integral":0,"orderId":"7115","orderNum":"","sourceCount":"5.542","sourceId":"paper","sourceName":"纸张","status":0,"terminalId":"2f385907ebbe4bd7a390647c7e3a73d3","totalCarbon":0,"totalExperience":0,"totalIntegral":554,"unitDesc":"公斤"}]
         * orderId : 7115
         * orderNum : 6588318706672209920
         * rangeId : F591EAF8216145CBB8DF6D72EFA0DBC3
         * rangeName : 3楼老田测试
         * remark :
         * status : 0
         * statusName :
         * stealStatus : 0
         * telphone : 18816284850
         * userChannelName :
         * userChannelType :
         * userId : 4cfe675cda224e308c5b5a77ecb49a4d
         */

        private String auditNickName;
        private int auditStatus;
        private String auditTime;
        private String communityId;
        private String communityName;
        private String createTime;
        private int experience;
        private int integral;
        private String lat;
        private String lng;
        private String nickName;
        private String orderAuditLogId;
        private int orderId;
        private String orderNum;
        private String rangeId;
        private String rangeName;
        private String remark;
        private int status;
        private String statusName;
        private int stealStatus;
        private String telphone;
        private String userChannelName;
        private String userChannelType;
        private String userId;
        private List<?> orderDetailList;
        private List<OrderDetailsBean> orderDetails;

        public String getAuditNickName() {
            return auditNickName;
        }

        public void setAuditNickName(String auditNickName) {
            this.auditNickName = auditNickName;
        }

        public int getAuditStatus() {
            return auditStatus;
        }

        public void setAuditStatus(int auditStatus) {
            this.auditStatus = auditStatus;
        }

        public String getAuditTime() {
            return auditTime;
        }

        public void setAuditTime(String auditTime) {
            this.auditTime = auditTime;
        }

        public String getCommunityId() {
            return communityId;
        }

        public void setCommunityId(String communityId) {
            this.communityId = communityId;
        }

        public String getCommunityName() {
            return communityName;
        }

        public void setCommunityName(String communityName) {
            this.communityName = communityName;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getExperience() {
            return experience;
        }

        public void setExperience(int experience) {
            this.experience = experience;
        }

        public int getIntegral() {
            return integral;
        }

        public void setIntegral(int integral) {
            this.integral = integral;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getOrderAuditLogId() {
            return orderAuditLogId;
        }

        public void setOrderAuditLogId(String orderAuditLogId) {
            this.orderAuditLogId = orderAuditLogId;
        }

        public int getOrderId() {
            return orderId;
        }

        public void setOrderId(int orderId) {
            this.orderId = orderId;
        }

        public String getOrderNum() {
            return orderNum;
        }

        public void setOrderNum(String orderNum) {
            this.orderNum = orderNum;
        }

        public String getRangeId() {
            return rangeId;
        }

        public void setRangeId(String rangeId) {
            this.rangeId = rangeId;
        }

        public String getRangeName() {
            return rangeName;
        }

        public void setRangeName(String rangeName) {
            this.rangeName = rangeName;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getStatusName() {
            return statusName;
        }

        public void setStatusName(String statusName) {
            this.statusName = statusName;
        }

        public int getStealStatus() {
            return stealStatus;
        }

        public void setStealStatus(int stealStatus) {
            this.stealStatus = stealStatus;
        }

        public String getTelphone() {
            return telphone;
        }

        public void setTelphone(String telphone) {
            this.telphone = telphone;
        }

        public String getUserChannelName() {
            return userChannelName;
        }

        public void setUserChannelName(String userChannelName) {
            this.userChannelName = userChannelName;
        }

        public String getUserChannelType() {
            return userChannelType;
        }

        public void setUserChannelType(String userChannelType) {
            this.userChannelType = userChannelType;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public List<?> getOrderDetailList() {
            return orderDetailList;
        }

        public void setOrderDetailList(List<?> orderDetailList) {
            this.orderDetailList = orderDetailList;
        }

        public List<OrderDetailsBean> getOrderDetails() {
            return orderDetails;
        }

        public void setOrderDetails(List<OrderDetailsBean> orderDetails) {
            this.orderDetails = orderDetails;
        }

        public static class OrderDetailsBean {
            /**
             * experience : 0
             * integral : 0
             * orderId : 7115
             * orderNum :
             * sourceCount : 5.542
             * sourceId : paper
             * sourceName : 纸张
             * status : 0
             * terminalId : 2f385907ebbe4bd7a390647c7e3a73d3
             * totalCarbon : 0
             * totalExperience : 0
             * totalIntegral : 554
             * unitDesc : 公斤
             */

            private int experience;
            private int integral;
            private String orderId;
            private String orderNum;
            private String sourceCount;
            private String sourceId;
            private String sourceName;
            private int status;
            private String terminalId;
            private int totalCarbon;
            private int totalExperience;
            private int totalIntegral;
            private String unitDesc;

            public int getExperience() {
                return experience;
            }

            public void setExperience(int experience) {
                this.experience = experience;
            }

            public int getIntegral() {
                return integral;
            }

            public void setIntegral(int integral) {
                this.integral = integral;
            }

            public String getOrderId() {
                return orderId;
            }

            public void setOrderId(String orderId) {
                this.orderId = orderId;
            }

            public String getOrderNum() {
                return orderNum;
            }

            public void setOrderNum(String orderNum) {
                this.orderNum = orderNum;
            }

            public String getSourceCount() {
                return sourceCount;
            }

            public void setSourceCount(String sourceCount) {
                this.sourceCount = sourceCount;
            }

            public String getSourceId() {
                return sourceId;
            }

            public void setSourceId(String sourceId) {
                this.sourceId = sourceId;
            }

            public String getSourceName() {
                return sourceName;
            }

            public void setSourceName(String sourceName) {
                this.sourceName = sourceName;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getTerminalId() {
                return terminalId;
            }

            public void setTerminalId(String terminalId) {
                this.terminalId = terminalId;
            }

            public int getTotalCarbon() {
                return totalCarbon;
            }

            public void setTotalCarbon(int totalCarbon) {
                this.totalCarbon = totalCarbon;
            }

            public int getTotalExperience() {
                return totalExperience;
            }

            public void setTotalExperience(int totalExperience) {
                this.totalExperience = totalExperience;
            }

            public int getTotalIntegral() {
                return totalIntegral;
            }

            public void setTotalIntegral(int totalIntegral) {
                this.totalIntegral = totalIntegral;
            }

            public String getUnitDesc() {
                return unitDesc;
            }

            public void setUnitDesc(String unitDesc) {
                this.unitDesc = unitDesc;
            }
        }
    }
}
