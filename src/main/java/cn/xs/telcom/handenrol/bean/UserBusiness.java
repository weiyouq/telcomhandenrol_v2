package cn.xs.telcom.handenrol.bean;

public class UserBusiness {
    private Long id;

    private Long userId;

    private Integer buId;

    private Long enrolTouchId;

    private String userNo;
    private String buNo;
    private String audioPath;

    public UserBusiness() {
    }

    public UserBusiness(String userNo, String buNo, String audioPath) {
        this.userNo = userNo;
        this.buNo = buNo;
        this.audioPath = audioPath;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getBuNo() {
        return buNo;
    }

    public void setBuNo(String buNo) {
        this.buNo = buNo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getBuId() {
        return buId;
    }

    public void setBuId(Integer buId) {
        this.buId = buId;
    }

    public Long getEnrolTouchId() {
        return enrolTouchId;
    }

    public void setEnrolTouchId(Long enrolTouchId) {
        this.enrolTouchId = enrolTouchId;
    }
}