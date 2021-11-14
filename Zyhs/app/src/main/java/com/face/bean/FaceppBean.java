package com.face.bean;

import java.util.List;

/**
 * 面部识别结果的bean
 *
 * @author chaochaowu
 */
public class FaceppBean {

    /**
     * faces : [{"attributes":{"age":{"value":30},"beauty":{"female_score":59.436,"male_score":63.947},"blur":{"blurness":{"threshold":50,"value":0.439},"gaussianblur":{"threshold":50,"value":0.439},"motionblur":{"threshold":50,"value":0.439}},"eyestatus":{"left_eye_status":{"dark_glasses":0,"no_glass_eye_close":0,"no_glass_eye_open":0,"normal_glass_eye_close":0.056,"normal_glass_eye_open":99.944,"occlusion":0},"right_eye_status":{"dark_glasses":0,"no_glass_eye_close":0,"no_glass_eye_open":0,"normal_glass_eye_close":0,"normal_glass_eye_open":100,"occlusion":0}},"facequality":{"threshold":70.1,"value":91.8},"gender":{"value":"Male"},"glass":{"value":"Normal"},"headpose":{"pitch_angle":5.2117567,"roll_angle":0.5129598,"yaw_angle":-3.2204554},"mouthstatus":{"close":100,"open":0,"other_occlusion":0,"surgical_mask_or_respirator":0},"smile":{"threshold":50,"value":8.033}},"face_rectangle":{"height":125,"left":1,"top":3,"width":125},"face_token":"7e24dff3f4e8f414e748a234a874eb64"}]
     * image_id : KfCTH96TbgD2CRx70HsHoA==
     * request_id : 1542764879,c53db24d-2838-4c6a-adee-db79b710a3f4
     * time_used : 338，
     */

    private String image_id;
    private String request_id;
    private String time_used;
    private List<FacesBean> faces;

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getTime_used() {
        return time_used;
    }

    public void setTime_used(String time_used) {
        this.time_used = time_used;
    }

    public List<FacesBean> getFaces() {
        return faces;
    }

    public void setFaces(List<FacesBean> faces) {
        this.faces = faces;
    }

    public static class FacesBean {
        /**
         * attributes : {"age":{"value":30},"beauty":{"female_score":59.436,"male_score":63.947},"blur":{"blurness":{"threshold":50,"value":0.439},"gaussianblur":{"threshold":50,"value":0.439},"motionblur":{"threshold":50,"value":0.439}},"eyestatus":{"left_eye_status":{"dark_glasses":0,"no_glass_eye_close":0,"no_glass_eye_open":0,"normal_glass_eye_close":0.056,"normal_glass_eye_open":99.944,"occlusion":0},"right_eye_status":{"dark_glasses":0,"no_glass_eye_close":0,"no_glass_eye_open":0,"normal_glass_eye_close":0,"normal_glass_eye_open":100,"occlusion":0}},"facequality":{"threshold":70.1,"value":91.8},"gender":{"value":"Male"},"glass":{"value":"Normal"},"headpose":{"pitch_angle":5.2117567,"roll_angle":0.5129598,"yaw_angle":-3.2204554},"mouthstatus":{"close":100,"open":0,"other_occlusion":0,"surgical_mask_or_respirator":0},"smile":{"threshold":50,"value":8.033}}
         * face_rectangle : {"height":125,"left":1,"top":3,"width":125}
         * face_token : 7e24dff3f4e8f414e748a234a874eb64
         */

        private AttributesBean attributes;
        private FaceRectangleBean face_rectangle;
        private String face_token;

        public AttributesBean getAttributes() {
            return attributes;
        }

        public void setAttributes(AttributesBean attributes) {
            this.attributes = attributes;
        }

        public FaceRectangleBean getFace_rectangle() {
            return face_rectangle;
        }

        public void setFace_rectangle(FaceRectangleBean face_rectangle) {
            this.face_rectangle = face_rectangle;
        }

        public String getFace_token() {
            return face_token;
        }

        public void setFace_token(String face_token) {
            this.face_token = face_token;
        }

        public static class AttributesBean {
            /**
             * age : {"value":30}
             * beauty : {"female_score":59.436,"male_score":63.947}
             * blur : {"blurness":{"threshold":50,"value":0.439},"gaussianblur":{"threshold":50,"value":0.439},"motionblur":{"threshold":50,"value":0.439}}
             * eyestatus : {"left_eye_status":{"dark_glasses":0,"no_glass_eye_close":0,"no_glass_eye_open":0,"normal_glass_eye_close":0.056,"normal_glass_eye_open":99.944,"occlusion":0},"right_eye_status":{"dark_glasses":0,"no_glass_eye_close":0,"no_glass_eye_open":0,"normal_glass_eye_close":0,"normal_glass_eye_open":100,"occlusion":0}}
             * facequality : {"threshold":70.1,"value":91.8}
             * gender : {"value":"Male"}
             * glass : {"value":"Normal"}
             * headpose : {"pitch_angle":5.2117567,"roll_angle":0.5129598,"yaw_angle":-3.2204554}
             * mouthstatus : {"close":100,"open":0,"other_occlusion":0,"surgical_mask_or_respirator":0}
             * smile : {"threshold":50,"value":8.033}
             */

            private AgeBean age;
            private BeautyBean beauty;
            private BlurBean blur;
            private EyestatusBean eyestatus;
            private FacequalityBean facequality;
            private GenderBean gender;
            private GlassBean glass;
            private HeadposeBean headpose;
            private MouthstatusBean mouthstatus;
            private SmileBean smile;

            public AgeBean getAge() {
                return age;
            }

            public void setAge(AgeBean age) {
                this.age = age;
            }

            public BeautyBean getBeauty() {
                return beauty;
            }

            public void setBeauty(BeautyBean beauty) {
                this.beauty = beauty;
            }

            public BlurBean getBlur() {
                return blur;
            }

            public void setBlur(BlurBean blur) {
                this.blur = blur;
            }

            public EyestatusBean getEyestatus() {
                return eyestatus;
            }

            public void setEyestatus(EyestatusBean eyestatus) {
                this.eyestatus = eyestatus;
            }

            public FacequalityBean getFacequality() {
                return facequality;
            }

            public void setFacequality(FacequalityBean facequality) {
                this.facequality = facequality;
            }

            public GenderBean getGender() {
                return gender;
            }

            public void setGender(GenderBean gender) {
                this.gender = gender;
            }

            public GlassBean getGlass() {
                return glass;
            }

            public void setGlass(GlassBean glass) {
                this.glass = glass;
            }

            public HeadposeBean getHeadpose() {
                return headpose;
            }

            public void setHeadpose(HeadposeBean headpose) {
                this.headpose = headpose;
            }

            public MouthstatusBean getMouthstatus() {
                return mouthstatus;
            }

            public void setMouthstatus(MouthstatusBean mouthstatus) {
                this.mouthstatus = mouthstatus;
            }

            public SmileBean getSmile() {
                return smile;
            }

            public void setSmile(SmileBean smile) {
                this.smile = smile;
            }

            public static class AgeBean {
                /**
                 * value : 30
                 */

                private int value;

                public int getValue() {
                    return value;
                }

                public void setValue(int value) {
                    this.value = value;
                }
            }

            public static class BeautyBean {
                /**
                 * female_score : 59.436
                 * male_score : 63.947
                 */

                private double female_score;
                private double male_score;

                public double getFemale_score() {
                    return female_score;
                }

                public void setFemale_score(double female_score) {
                    this.female_score = female_score;
                }

                public double getMale_score() {
                    return male_score;
                }

                public void setMale_score(double male_score) {
                    this.male_score = male_score;
                }
            }

            public static class BlurBean {
                /**
                 * blurness : {"threshold":50,"value":0.439}
                 * gaussianblur : {"threshold":50,"value":0.439}
                 * motionblur : {"threshold":50,"value":0.439}
                 */

                private BlurnessBean blurness;
                private GaussianblurBean gaussianblur;
                private MotionblurBean motionblur;

                public BlurnessBean getBlurness() {
                    return blurness;
                }

                public void setBlurness(BlurnessBean blurness) {
                    this.blurness = blurness;
                }

                public GaussianblurBean getGaussianblur() {
                    return gaussianblur;
                }

                public void setGaussianblur(GaussianblurBean gaussianblur) {
                    this.gaussianblur = gaussianblur;
                }

                public MotionblurBean getMotionblur() {
                    return motionblur;
                }

                public void setMotionblur(MotionblurBean motionblur) {
                    this.motionblur = motionblur;
                }

                public static class BlurnessBean {
                    /**
                     * threshold : 50
                     * value : 0.439
                     */

                    private int threshold;
                    private double value;

                    public int getThreshold() {
                        return threshold;
                    }

                    public void setThreshold(int threshold) {
                        this.threshold = threshold;
                    }

                    public double getValue() {
                        return value;
                    }

                    public void setValue(double value) {
                        this.value = value;
                    }
                }

                public static class GaussianblurBean {
                    /**
                     * threshold : 50
                     * value : 0.439
                     */

                    private int threshold;
                    private double value;

                    public int getThreshold() {
                        return threshold;
                    }

                    public void setThreshold(int threshold) {
                        this.threshold = threshold;
                    }

                    public double getValue() {
                        return value;
                    }

                    public void setValue(double value) {
                        this.value = value;
                    }
                }

                public static class MotionblurBean {
                    /**
                     * threshold : 50
                     * value : 0.439
                     */

                    private int threshold;
                    private double value;

                    public int getThreshold() {
                        return threshold;
                    }

                    public void setThreshold(int threshold) {
                        this.threshold = threshold;
                    }

                    public double getValue() {
                        return value;
                    }

                    public void setValue(double value) {
                        this.value = value;
                    }
                }
            }

            public static class EyestatusBean {
                /**
                 * left_eye_status : {"dark_glasses":0,"no_glass_eye_close":0,"no_glass_eye_open":0,"normal_glass_eye_close":0.056,"normal_glass_eye_open":99.944,"occlusion":0}
                 * right_eye_status : {"dark_glasses":0,"no_glass_eye_close":0,"no_glass_eye_open":0,"normal_glass_eye_close":0,"normal_glass_eye_open":100,"occlusion":0}
                 */

                private LeftEyeStatusBean left_eye_status;
                private RightEyeStatusBean right_eye_status;

                public LeftEyeStatusBean getLeft_eye_status() {
                    return left_eye_status;
                }

                public void setLeft_eye_status(LeftEyeStatusBean left_eye_status) {
                    this.left_eye_status = left_eye_status;
                }

                public RightEyeStatusBean getRight_eye_status() {
                    return right_eye_status;
                }

                public void setRight_eye_status(RightEyeStatusBean right_eye_status) {
                    this.right_eye_status = right_eye_status;
                }

                public static class LeftEyeStatusBean {
                    /**
                     * dark_glasses : 0
                     * no_glass_eye_close : 0
                     * no_glass_eye_open : 0
                     * normal_glass_eye_close : 0.056
                     * normal_glass_eye_open : 99.944
                     * occlusion : 0
                     */

                    private double dark_glasses;
                    private double no_glass_eye_close;
                    private double no_glass_eye_open;
                    private double normal_glass_eye_close;
                    private double normal_glass_eye_open;
                    private double occlusion;

                    public double getDark_glasses() {
                        return dark_glasses;
                    }

                    public void setDark_glasses(int dark_glasses) {
                        this.dark_glasses = dark_glasses;
                    }

                    public double getNo_glass_eye_close() {
                        return no_glass_eye_close;
                    }

                    public void setNo_glass_eye_close(int no_glass_eye_close) {
                        this.no_glass_eye_close = no_glass_eye_close;
                    }

                    public double getNo_glass_eye_open() {
                        return no_glass_eye_open;
                    }

                    public void setNo_glass_eye_open(int no_glass_eye_open) {
                        this.no_glass_eye_open = no_glass_eye_open;
                    }

                    public double getNormal_glass_eye_close() {
                        return normal_glass_eye_close;
                    }

                    public void setNormal_glass_eye_close(double normal_glass_eye_close) {
                        this.normal_glass_eye_close = normal_glass_eye_close;
                    }

                    public double getNormal_glass_eye_open() {
                        return normal_glass_eye_open;
                    }

                    public void setNormal_glass_eye_open(double normal_glass_eye_open) {
                        this.normal_glass_eye_open = normal_glass_eye_open;
                    }

                    public double getOcclusion() {
                        return occlusion;
                    }

                    public void setOcclusion(int occlusion) {
                        this.occlusion = occlusion;
                    }
                }

                public static class RightEyeStatusBean {
                    /**
                     * dark_glasses : 0
                     * no_glass_eye_close : 0
                     * no_glass_eye_open : 0
                     * normal_glass_eye_close : 0
                     * normal_glass_eye_open : 100
                     * occlusion : 0
                     */

                    private double dark_glasses;
                    private double no_glass_eye_close;
                    private double no_glass_eye_open;
                    private double normal_glass_eye_close;
                    private double normal_glass_eye_open;
                    private double occlusion;

                    public double getDark_glasses() {
                        return dark_glasses;
                    }

                    public void setDark_glasses(int dark_glasses) {
                        this.dark_glasses = dark_glasses;
                    }

                    public double getNo_glass_eye_close() {
                        return no_glass_eye_close;
                    }

                    public void setNo_glass_eye_close(int no_glass_eye_close) {
                        this.no_glass_eye_close = no_glass_eye_close;
                    }

                    public double getNo_glass_eye_open() {
                        return no_glass_eye_open;
                    }

                    public void setNo_glass_eye_open(int no_glass_eye_open) {
                        this.no_glass_eye_open = no_glass_eye_open;
                    }

                    public double getNormal_glass_eye_close() {
                        return normal_glass_eye_close;
                    }

                    public void setNormal_glass_eye_close(int normal_glass_eye_close) {
                        this.normal_glass_eye_close = normal_glass_eye_close;
                    }

                    public double getNormal_glass_eye_open() {
                        return normal_glass_eye_open;
                    }

                    public void setNormal_glass_eye_open(int normal_glass_eye_open) {
                        this.normal_glass_eye_open = normal_glass_eye_open;
                    }

                    public double getOcclusion() {
                        return occlusion;
                    }

                    public void setOcclusion(int occlusion) {
                        this.occlusion = occlusion;
                    }
                }
            }

            public static class FacequalityBean {
                /**
                 * threshold : 70.1
                 * value : 91.8
                 */

                private double threshold;
                private double value;

                public double getThreshold() {
                    return threshold;
                }

                public void setThreshold(double threshold) {
                    this.threshold = threshold;
                }

                public double getValue() {
                    return value;
                }

                public void setValue(double value) {
                    this.value = value;
                }
            }

            public static class GenderBean {
                /**
                 * value : Male
                 */

                private String value;

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }

            public static class GlassBean {
                /**
                 * value : Normal
                 */

                private String value;

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }

            public static class HeadposeBean {
                /**
                 * pitch_angle : 5.2117567
                 * roll_angle : 0.5129598
                 * yaw_angle : -3.2204554
                 */

                private double pitch_angle;
                private double roll_angle;
                private double yaw_angle;

                public double getPitch_angle() {
                    return pitch_angle;
                }

                public void setPitch_angle(double pitch_angle) {
                    this.pitch_angle = pitch_angle;
                }

                public double getRoll_angle() {
                    return roll_angle;
                }

                public void setRoll_angle(double roll_angle) {
                    this.roll_angle = roll_angle;
                }

                public double getYaw_angle() {
                    return yaw_angle;
                }

                public void setYaw_angle(double yaw_angle) {
                    this.yaw_angle = yaw_angle;
                }
            }

            public static class MouthstatusBean {
                /**
                 * close : 100
                 * open : 0
                 * other_occlusion : 0
                 * surgical_mask_or_respirator : 0
                 */

                private double close;
                private double open;
                private double other_occlusion;
                private double surgical_mask_or_respirator;

                public double getClose() {
                    return close;
                }

                public void setClose(int close) {
                    this.close = close;
                }

                public double getOpen() {
                    return open;
                }

                public void setOpen(int open) {
                    this.open = open;
                }

                public double getOther_occlusion() {
                    return other_occlusion;
                }

                public void setOther_occlusion(int other_occlusion) {
                    this.other_occlusion = other_occlusion;
                }

                public double getSurgical_mask_or_respirator() {
                    return surgical_mask_or_respirator;
                }

                public void setSurgical_mask_or_respirator(int surgical_mask_or_respirator) {
                    this.surgical_mask_or_respirator = surgical_mask_or_respirator;
                }
            }

            public static class SmileBean {
                /**
                 * threshold : 50
                 * value : 8.033
                 */

                private int threshold;
                private double value;

                public int getThreshold() {
                    return threshold;
                }

                public void setThreshold(int threshold) {
                    this.threshold = threshold;
                }

                public double getValue() {
                    return value;
                }

                public void setValue(double value) {
                    this.value = value;
                }
            }
        }

        public static class FaceRectangleBean {
            /**
             * height : 125
             * left : 1
             * top : 3
             * width : 125
             */

            private int height;
            private int left;
            private int top;
            private int width;

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public int getLeft() {
                return left;
            }

            public void setLeft(int left) {
                this.left = left;
            }

            public int getTop() {
                return top;
            }

            public void setTop(int top) {
                this.top = top;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }
        }
    }
}
