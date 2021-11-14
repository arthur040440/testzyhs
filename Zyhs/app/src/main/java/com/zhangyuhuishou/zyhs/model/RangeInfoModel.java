package com.zhangyuhuishou.zyhs.model;

import java.util.List;

/**
 * author:tlh
 * email:tianlihui2234@lie.com
 * time:2019/1/31 10:40
 * description:range info
 */
public class RangeInfoModel {


    /**
     * code : 0
     * data : {"active":"Y","activeName":"有效","activeTime":"2019-06-17 18:01:07"," adCode":"330110001","communityAddress":"临平南大街255号","communityId":"78","communityName":"贝利集团","createTime":"2018-10-19 14:54:43","lastConnectTime":"2019-06-17 15:34:35","lat":30.41201019,"lng":120.31374359,"modifyTime":"","pcbVersion":"5","rangeId":"F591EAF8216145CBB8DF6D72EFA0DBC3","rangeName":"3楼老田测试","rangeNum":"330106-78-1","rangeSource":"zyhs","rangeSourceName":"章鱼回收","status":"1","statusName":"已激活","terminalCount":8,"terminalList":[],"version":""}
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
         * active : Y
         * activeName : 有效
         * activeTime : 2019-06-17 18:01:07
         *  adCode : 330110001
         * communityAddress : 临平南大街255号
         * communityId : 78
         * communityName : 贝利集团
         * createTime : 2018-10-19 14:54:43
         * lastConnectTime : 2019-06-17 15:34:35
         * lat : 30.41201019
         * lng : 120.31374359
         * modifyTime :
         * pcbVersion : 5
         * rangeId : F591EAF8216145CBB8DF6D72EFA0DBC3
         * rangeName : 3楼老田测试
         * rangeNum : 330106-78-1
         * rangeSource : zyhs
         * rangeSourceName : 章鱼回收
         * status : 1
         * statusName : 已激活
         * terminalCount : 8
         * terminalList : []
         * version :
         */

        private String active;
        private String activeName;
        private String activeTime;
        private String adCode;
        private String communityAddress;
        private String communityId;
        private String communityName;
        private String createTime;
        private String lastConnectTime;
        private double lat;
        private double lng;
        private String modifyTime;
        private String pcbVersion;
        private String rangeId;
        private String rangeName;
        private String rangeNum;
        private String rangeSource;
        private String rangeSourceName;
        private String status;
        private String statusName;
        private int terminalCount;
        private String version;
        private List<?> terminalList;

        public String getActive() {
            return active;
        }

        public void setActive(String active) {
            this.active = active;
        }

        public String getActiveName() {
            return activeName;
        }

        public void setActiveName(String activeName) {
            this.activeName = activeName;
        }

        public String getActiveTime() {
            return activeTime;
        }

        public void setActiveTime(String activeTime) {
            this.activeTime = activeTime;
        }

        public String getAdCode() {
            return adCode;
        }

        public void setAdCode(String adCode) {
            this.adCode = adCode;
        }

        public String getCommunityAddress() {
            return communityAddress;
        }

        public void setCommunityAddress(String communityAddress) {
            this.communityAddress = communityAddress;
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

        public String getLastConnectTime() {
            return lastConnectTime;
        }

        public void setLastConnectTime(String lastConnectTime) {
            this.lastConnectTime = lastConnectTime;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public String getModifyTime() {
            return modifyTime;
        }

        public void setModifyTime(String modifyTime) {
            this.modifyTime = modifyTime;
        }

        public String getPcbVersion() {
            return pcbVersion;
        }

        public void setPcbVersion(String pcbVersion) {
            this.pcbVersion = pcbVersion;
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

        public String getRangeNum() {
            return rangeNum;
        }

        public void setRangeNum(String rangeNum) {
            this.rangeNum = rangeNum;
        }

        public String getRangeSource() {
            return rangeSource;
        }

        public void setRangeSource(String rangeSource) {
            this.rangeSource = rangeSource;
        }

        public String getRangeSourceName() {
            return rangeSourceName;
        }

        public void setRangeSourceName(String rangeSourceName) {
            this.rangeSourceName = rangeSourceName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatusName() {
            return statusName;
        }

        public void setStatusName(String statusName) {
            this.statusName = statusName;
        }

        public int getTerminalCount() {
            return terminalCount;
        }

        public void setTerminalCount(int terminalCount) {
            this.terminalCount = terminalCount;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public List<?> getTerminalList() {
            return terminalList;
        }

        public void setTerminalList(List<?> terminalList) {
            this.terminalList = terminalList;
        }
    }
}
