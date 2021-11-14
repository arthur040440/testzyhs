package com.zhangyuhuishou.zyhs.model;

import java.util.List;

public class ClassificationModel {


    /**
     * code : 0
     * data : [{"carbonEmission":0,"experience":0,"integral":2,"maxDayCount":0,"metering":1,"resourceType":"glassBottle","resourceTypeName":"玻璃瓶","securityValue":0,"threshold":20,"unitDesc":"个"},{"carbonEmission":1,"experience":0,"integral":5,"maxDayCount":0,"metering":1,"resourceType":"plasticBottle","resourceTypeName":"塑料瓶","securityValue":0,"threshold":20,"unitDesc":"个"},{"carbonEmission":1,"experience":0,"integral":6,"maxDayCount":0,"metering":1,"resourceType":"cans","resourceTypeName":"易拉罐","securityValue":0,"threshold":20,"unitDesc":"个"},{"carbonEmission":22500,"experience":0,"integral":100,"maxDayCount":0,"metering":1,"resourceType":"paper","resourceTypeName":"纸张","securityValue":-300,"threshold":20000,"unitDesc":"公斤"},{"carbonEmission":0,"experience":0,"integral":40,"maxDayCount":0,"metering":1,"resourceType":"spin","resourceTypeName":"纺织品","securityValue":-300,"threshold":20000,"unitDesc":"公斤"},{"carbonEmission":20,"experience":0,"integral":70,"maxDayCount":0,"metering":1,"resourceType":"plastic","resourceTypeName":"塑料制品","securityValue":-300,"threshold":20000,"unitDesc":"公斤"},{"carbonEmission":0,"experience":0,"integral":60,"maxDayCount":0,"metering":1,"resourceType":"metal","resourceTypeName":"金属","securityValue":-300,"threshold":20000,"unitDesc":"公斤"},{"carbonEmission":0,"experience":0,"integral":2,"maxDayCount":5,"metering":1,"resourceType":"harmful","resourceTypeName":"有害","securityValue":0,"threshold":0,"unitDesc":"次"}]
     * message :
     */

    private int code;
    private String message;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * carbonEmission : 0
         * experience : 0
         * integral : 2
         * maxDayCount : 0
         * metering : 1
         * resourceType : glassBottle
         * resourceTypeName : 玻璃瓶
         * securityValue : 0
         * threshold : 20
         * unitDesc : 个
         */

        private int carbonEmission;
        private int experience;
        private int integral;
        private int maxDayCount;
        private int metering;
        private String resourceType;
        private String resourceTypeName;
        private int securityValue;
        private int threshold;
        private String unitDesc;

        public int getCarbonEmission() {
            return carbonEmission;
        }

        public void setCarbonEmission(int carbonEmission) {
            this.carbonEmission = carbonEmission;
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

        public int getMaxDayCount() {
            return maxDayCount;
        }

        public void setMaxDayCount(int maxDayCount) {
            this.maxDayCount = maxDayCount;
        }

        public int getMetering() {
            return metering;
        }

        public void setMetering(int metering) {
            this.metering = metering;
        }

        public String getResourceType() {
            return resourceType;
        }

        public void setResourceType(String resourceType) {
            this.resourceType = resourceType;
        }

        public String getResourceTypeName() {
            return resourceTypeName;
        }

        public void setResourceTypeName(String resourceTypeName) {
            this.resourceTypeName = resourceTypeName;
        }

        public int getSecurityValue() {
            return securityValue;
        }

        public void setSecurityValue(int securityValue) {
            this.securityValue = securityValue;
        }

        public int getThreshold() {
            return threshold;
        }

        public void setThreshold(int threshold) {
            this.threshold = threshold;
        }

        public String getUnitDesc() {
            return unitDesc;
        }

        public void setUnitDesc(String unitDesc) {
            this.unitDesc = unitDesc;
        }
    }
}


