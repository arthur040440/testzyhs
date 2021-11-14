package com.face.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// 搜索到的脸部数据
public class FaceSearchInfo {


    /**
     * request_id : 1542764880,67dee906-bbc2-45dc-b55a-f3111238913f
     * results : [{"confidence":83.294,"face_token":"a04662902e516db50867e49feb4a338e","user_id":"7897A113E7A143E2B0FDF99E9A1B8363"}]
     * thresholds : {"1e-3":62.327,"1e-4":69.101,"1e-5":73.975}
     * time_used : 474
     */

    private String request_id;
    private ThresholdsBean thresholds;
    private int time_used;
    private List<ResultsBean> results;

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public ThresholdsBean getThresholds() {
        return thresholds;
    }

    public void setThresholds(ThresholdsBean thresholds) {
        this.thresholds = thresholds;
    }

    public int getTime_used() {
        return time_used;
    }

    public void setTime_used(int time_used) {
        this.time_used = time_used;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ThresholdsBean {
        /**
         * 1e-3 : 62.327
         * 1e-4 : 69.101
         * 1e-5 : 73.975
         */

        @SerializedName("1e-3")
        private double _$1e3;
        @SerializedName("1e-4")
        private double _$1e4;
        @SerializedName("1e-5")
        private double _$1e5;

        public double get_$1e3() {
            return _$1e3;
        }

        public void set_$1e3(double _$1e3) {
            this._$1e3 = _$1e3;
        }

        public double get_$1e4() {
            return _$1e4;
        }

        public void set_$1e4(double _$1e4) {
            this._$1e4 = _$1e4;
        }

        public double get_$1e5() {
            return _$1e5;
        }

        public void set_$1e5(double _$1e5) {
            this._$1e5 = _$1e5;
        }
    }

    public static class ResultsBean {
        /**
         * confidence : 83.294
         * face_token : a04662902e516db50867e49feb4a338e
         * user_id : 7897A113E7A143E2B0FDF99E9A1B8363
         */

        private double confidence;
        private String face_token;
        private String user_id;

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }

        public String getFace_token() {
            return face_token;
        }

        public void setFace_token(String face_token) {
            this.face_token = face_token;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    }
}
