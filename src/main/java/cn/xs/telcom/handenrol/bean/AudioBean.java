package cn.xs.telcom.handenrol.bean;

/**
 * 下载的音频信息
 * @author kenny_peng
 * @created 2019/9/23 16:18
 */
public class AudioBean extends EnrolTouch{

//    private String date;
//    private String audioPath;
//    private String callerid;
//    private String calledid;

    private String base64;

    public AudioBean(String audioPath){
        super(audioPath);
    }

    public AudioBean(){

    }

    public AudioBean(String callerid,String calledid, String audioPath, String createDate){
        super(callerid, calledid, audioPath,createDate);

    }

    public AudioBean(EnrolTouch enrolTouch){
        super(enrolTouch);
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

}
