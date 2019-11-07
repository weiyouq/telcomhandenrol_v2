package cn.xs.telcom.handenrol.bean;

import cn.xs.telcom.handenrol.config.Const;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class ActivityLog {
    private Long id;

    private Long userId;

    private int buId;

    private String voiceLocation;

    private Integer category;

    private Date createDate;

    private String result;

    private String userNo;
    private String buNo;

    public ActivityLog(AudioBean audioBean) {
        this.voiceLocation = audioBean.getAudioPath();
        if (audioBean.getDownloadStatus() != 2){
            this.category = Const.IDENTIFY_ENROL_FAILED;
            this.createDate = audioBean.getDownloadDate();
            this.result = audioBean.getDownloadError();
        }else {
            this.createDate = audioBean.getEnrolDate();
            if (audioBean.getEnrolStatus() != 2){
                this.category = Const.IDENTIFY_ENROL_FAILED;
                this.result = audioBean.getEnrolError();
            }else {
                this.category = Const.IDENTIFY_ENROL;
                this.result = "";
                this.userNo = audioBean.getUserNo();
                this.buNo = audioBean.getBuNo();
            }
        }
        LoggerFactory.getLogger(ActivityLog.class).info("voiceLocation:"+voiceLocation+"userNo:" + userNo + "buNo:" +buNo+"result:"+result+"category:"+category+"createDate:"+createDate);
    }

    public ActivityLog(Long userId, Integer buId, String voiceLocation, Integer category, Date createDate, String result) {

        this.userId = userId;
        if (buId != null){
            this.buId = buId;
        }

        this.voiceLocation = voiceLocation;
        this.category = category;
        this.createDate = createDate;
        this.result = result;
    }

    public ActivityLog() {
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

    public int getBuId() {
        return buId;
    }

    public void setBuId(int buId) {
        this.buId = buId;
    }

    public String getVoiceLocation() {
        return voiceLocation;
    }

    public void setVoiceLocation(String voiceLocation) {
        this.voiceLocation = voiceLocation == null ? null : voiceLocation.trim();
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result == null ? null : result.trim();
    }
}