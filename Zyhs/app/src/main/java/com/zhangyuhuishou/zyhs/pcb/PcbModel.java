package com.zhangyuhuishou.zyhs.pcb;

/**
 * 作者:created by author:tlh
 * 日期:2019/5/23 10:35
 * 邮箱:tianlihui2234@live.com
 * 描述:PCB model
 */
public class PcbModel {


    /**
     * code : 0
     * message :
     * data : {"machineVersionId":"9","version":"0.0.2","filePath":"https://zyhs-dev.oss-cn-hangzhou.aliyuncs.com/machine/2019-05/RTC-1558518153470.bin","fileUrl":"https://zyhs-dev.oss-cn-hangzhou.aliyuncs.com/machine/2019-05/RTC-1558518153470.bin","machineVersionType":"pcb","machineVersionTypeName":"pcb","rangeSource":"zyhs","rangeSourceName":"章鱼回收","remark":"","expectedTime":"","createTime":"2019-05-22 17:42:39"}
     */

    private int code;
    private String message;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * machineVersionId : 9
         * version : 0.0.2
         * filePath : https://zyhs-dev.oss-cn-hangzhou.aliyuncs.com/machine/2019-05/RTC-1558518153470.bin
         * fileUrl : https://zyhs-dev.oss-cn-hangzhou.aliyuncs.com/machine/2019-05/RTC-1558518153470.bin
         * machineVersionType : pcb
         * machineVersionTypeName : pcb
         * rangeSource : zyhs
         * rangeSourceName : 章鱼回收
         * remark :
         * expectedTime :
         * createTime : 2019-05-22 17:42:39
         */

        private String machineVersionId;
        private String version;
        private String filePath;
        private String fileUrl;
        private String machineVersionType;
        private String machineVersionTypeName;
        private String rangeSource;
        private String rangeSourceName;
        private String remark;
        private String expectedTime;
        private String createTime;

        public String getMachineVersionId() {
            return machineVersionId;
        }

        public void setMachineVersionId(String machineVersionId) {
            this.machineVersionId = machineVersionId;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getMachineVersionType() {
            return machineVersionType;
        }

        public void setMachineVersionType(String machineVersionType) {
            this.machineVersionType = machineVersionType;
        }

        public String getMachineVersionTypeName() {
            return machineVersionTypeName;
        }

        public void setMachineVersionTypeName(String machineVersionTypeName) {
            this.machineVersionTypeName = machineVersionTypeName;
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

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getExpectedTime() {
            return expectedTime;
        }

        public void setExpectedTime(String expectedTime) {
            this.expectedTime = expectedTime;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
}
