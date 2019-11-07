package cn.xs.telcom.handenrol.bean;

import java.util.Date;

public class EnrolTouch {
    private Long id;

    private String audioPath;

    private String createDate;

    private Integer downloadStatus;

    private Date downloadDate;

    private String downloadError;

    private Integer enrolStatus;

    private Date enrolDate;

    private String enrolError;

    /**
     * 0注册激活，1预注册，2预注册为第二条模型，3设置为验证关注，
     * 4验证失败删除模型,5跟两条预注册模型比对都不通过，6当天注册过不在注册
     */
    private int enrolRecord;


    private long userId;
    private String userNo;
    private String usedUserId;
    private int userState;
    private int buId;
    private String buNo;


    public EnrolTouch(String audioPath) {
        this.audioPath = audioPath;
    }

    public EnrolTouch() {
    }

    public EnrolTouch(EnrolTouch enrolTouch) {
        this.id = enrolTouch.getId();
        this.audioPath = enrolTouch.getAudioPath();
        this.createDate = enrolTouch.getCreateDate();
        this.downloadStatus = enrolTouch.getDownloadStatus();
        this.downloadDate = enrolTouch.getDownloadDate();
        this.downloadError = enrolTouch.getDownloadError();
        this.enrolStatus = enrolTouch.getEnrolStatus();
        this.enrolDate = enrolTouch.getEnrolDate();
        this.enrolError = enrolTouch.getEnrolError();
        this.userId = enrolTouch.getUserId();
        this.userNo = enrolTouch.getUserNo();
        this.usedUserId = enrolTouch.getUsedUserId();
        this.userState = enrolTouch.getUserState();
        this.buId = enrolTouch.getBuId();
        this.buNo = enrolTouch.getBuNo();
    }

    public EnrolTouch(String audioPath, String createDate, Integer downloadStatus, Date downloadDate, String downloadError){
        this.audioPath = audioPath;
        this.createDate = createDate;
        this.downloadStatus = downloadStatus;
        this.downloadDate = downloadDate;
        this.downloadError = downloadError;
    }

    public EnrolTouch(String userNo, String buNo, String audioPath, String createDate) {
        this.userNo = userNo;
        this.buNo = buNo;
        this.audioPath = audioPath;
        this.createDate = createDate;
    }

    public int getEnrolRecord() {
        return enrolRecord;
    }

    public void setEnrolRecord(int enrolRecord) {
        this.enrolRecord = enrolRecord;
    }

    public String getUsedUserId() {
        return usedUserId;
    }

    public void setUsedUserId(String usedUserId) {
        this.usedUserId = usedUserId;
    }

    public int getUserState() {
        return userState;
    }

    public void setUserState(int userState) {
        this.userState = userState;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getBuId() {
        return buId;
    }

    public void setBuId(int buId) {
        this.buId = buId;
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

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath == null ? null : audioPath.trim();
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate == null ? null : createDate.trim();
    }

    public Integer getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(Integer downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public Date getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(Date downloadDate) {
        this.downloadDate = downloadDate;
    }

    public String getDownloadError() {
        return downloadError;
    }

    public void setDownloadError(String downloadError) {
        this.downloadError = downloadError == null ? null : downloadError.trim();
    }

    public Integer getEnrolStatus() {
        return enrolStatus;
    }

    public void setEnrolStatus(Integer enrolStatus) {
        this.enrolStatus = enrolStatus;
    }

    public Date getEnrolDate() {
        return enrolDate;
    }

    public void setEnrolDate(Date enrolDate) {
        this.enrolDate = enrolDate;
    }

    public String getEnrolError() {
        return enrolError;
    }

    public void setEnrolError(String enrolError) {
        this.enrolError = enrolError == null ? null : enrolError.trim();
    }
}