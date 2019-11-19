package cn.xs.telcom.handenrol.service.impl;


import cn.xs.telcom.handenrol.Utils.*;
import cn.xs.telcom.handenrol.bean.AudioBean;
import cn.xs.telcom.handenrol.config.Const;
import cn.xs.telcom.handenrol.service.IDownlaodService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 获取当天所有音频存储ftp路径与下载单个音频实现类
 * @author kenny_peng
 * @created 2019/9/23 16:30
 */
@Configuration
public class DownloadServiceImpl implements IDownlaodService {

    private Logger logger = LoggerFactory.getLogger(DownloadServiceImpl.class);

    @Value("${ua.voice.save.location}")
    private String voiceSaveLocation;//1vs1音频保存位置

    @Value("${sox.install.path}")
    private String soxPath;
    @Value("${sox.trim.starttime}")
    private String trimStartTime;
    @Value("${sox.trim.duration}")
    private String trimDuration;

    @Value("${max.thread.count}")
    private int MAX_THREAD_COUNT;// 最大线程数
    private ExecutorService es = null;



    private List<AudioBean> resultAudioBeanList = new CopyOnWriteArrayList<>();
    private List<String> repetitionRecord = new CopyOnWriteArrayList<>();

    public List<AudioBean> getResultAudioBeanList() {
        return resultAudioBeanList;
    }

    public synchronized void addResultAudioBeanList(AudioBean audioBean) {
        this.resultAudioBeanList.add(audioBean);
    }

    public List<String> getRepetitionRecord() {
        return repetitionRecord;
    }

    public synchronized void addRepetitionRecord(String s) {
        this.repetitionRecord.add(s);
    }

    /**
     * 获取当天所有音频存储ftp路径集合
     * @param date  日期
     * @return
     */
    @Override
    public List<AudioBean> getAudioListByDate(String date) throws Exception {
        logger.info("开始清空集合");
        if (resultAudioBeanList.size() > 0)
            resultAudioBeanList.clear();
        logger.info("清空集合完成一个");
        if (repetitionRecord.size() > 0)
            repetitionRecord.clear();
        logger.info("结束清空集合");
//        ftpUtils = ;
        String filePath = voiceSaveLocation  + date + "/";//File.separator;
        List<File> fileNameList = FileUtils.getAllFiles(new File(filePath));
        logger.info("获取当天注册音频总大小为：" + fileNameList.size());

        // 创建线程池
        es = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
        CountDownLatch cdl = new CountDownLatch(fileNameList.size());
        for (File file : fileNameList){

            es.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        String fileName = file.getName();
                        String substring = fileName.substring(0, fileName.indexOf("."));
                        String[] userArray = getUserArray(substring);
                        String callerid = userArray[0];//手机号
                        String calledid = null;//用户呼叫的手机号，即呼叫的租户号码
                        if (userArray.length >= 2)
                            calledid = userArray[1];
                        AudioBean audioBean = new AudioBean(callerid, calledid, file.getPath(), date);
                        if (getRepetitionRecord().contains(callerid)){//包含，该callerid已经有一条记录
                            audioBean.setEnrolDate(new Date());
                            audioBean.setEnrolStatus(Const.VB_ENROL_STATUS_SUCCESS);
                            audioBean.setEnrolError("当天已经注册过，不在进行注册");
                            audioBean.setEnrolRecord(6);
                            addResultAudioBeanList(audioBean);
                        }else {
                            addRepetitionRecord(callerid);
                            addResultAudioBeanList(audioBean);
                        }
                    } catch (Exception e) {
                        AudioBean audioBean = new AudioBean();
                        audioBean.setDownloadDate(new Date());
                        audioBean.setDownloadError(StringUtils.getMaxLen("下载json文件错误" + e.getMessage(), 40));
                        addResultAudioBeanList(audioBean);
                        logger.info("下载json出错", e);
                    }
                    cdl.countDown();
                }
            });
        }
        cdl.await();
        return getResultAudioBeanList();
    }

    @Override
    public void downloadAudio(AudioBean audioBean) throws Exception {
        InputStream downloadAudio = null;
        try {
            String audioPath = audioBean.getAudioPath();
//        FTPUtils ftp2 = new FTPUtils();
//        ftp2.connect(ftpHostname, ftpPort, ftpUserName,ftpPwd);
//            downloadAudio = ftpUtils.downloadAudio(audioPath);
            String base64 = soxPreprocessingAudio(new FileInputStream(new File(audioPath)), audioPath);
            audioBean.setDownloadDate(new Date());
            audioBean.setDownloadStatus(Const.FTP_DOWNLOAD_SUCCESS);
            audioBean.setBase64(base64);
//        ftp2.disconnect();
        } finally {
            if (downloadAudio != null){
                downloadAudio.close();
            }
        }
    }

    /**
     * 加入sox预处理音频
     * @param inputStream
     * @param pathAndFileName 文件路径和文件名
     * @return
     */
    private String soxPreprocessingAudio(InputStream inputStream, String pathAndFileName) throws Exception{
//		logger.info("当前的路径为：" + System.getProperty("user.dir"));//user.dir指定了当前的路径
        OutputStream outStream = null;
        try {
            String replaceAll = pathAndFileName.replaceAll("/", "_");
            //临时存放音频文件名、目录
            String path = System.getProperty("user.dir") + "/temp/";
            String pcmPath = path + replaceAll;
            ;
            String wavPath = path + replaceAll + ".wav";
            logger.info("待处理音频文件路径为：" + pcmPath);
            File pcmFile = new File(pcmPath);
            //先判断文件是否存在
            if (!pcmFile.exists()){
                //不存在先创建目录
                File dir = new File(pcmFile.getParent());
                if(!dir.exists()){
                    boolean mkdirs = dir.mkdirs();
                    if (!mkdirs){
                        throw new Exception("创建保存文件夹失败");
                    }
                }
//                boolean newFile = pcmFile.createNewFile();
//                if (!newFile){
//                    throw new Exception("创建保存文件失败");
//                }
            }
            outStream = new FileOutputStream(pcmFile);
            byte[] buff = new byte[1024];
            int rc = 0;
            while ((rc = inputStream.read(buff, 0, 1024)) > 0) {
                outStream.write(buff, 0, rc);
            }
            SoxUtils.doSoxProcess(soxPath, pcmPath, wavPath, trimStartTime, trimDuration);
            File audioFile = new File(wavPath);
            logger.info(audioFile.exists()+"处理后文件路径为：" + wavPath);
            if (!audioFile.exists() || audioFile.length() <= 10240){
                throw new Exception("音频长度太短，或者格式错误");
            }
            String base64 = AudioUtils.audioToBase64(wavPath);
            if (pcmFile.delete() && audioFile.delete()){
                logger.info("删除成功" + pcmPath);
            }else {
                logger.error("删除失败" + pcmPath);
            }
            return base64;
        } finally {
            if (inputStream != null){
                inputStream.close();
            }
            if (outStream != null){
                outStream.close();
            }
        }
    }

    /**
     * 解析userId，得到用户编号，租户号
     * @param fileName
     * @return
     */
    private String[] getUserArray(String fileName){
        String[] names = fileName.split("_");
        if (names.length == 3){
            String userNo = names[0];
            String buNo = names[1];
            String blackOrWrite = names[2];
            String[] resultName = new String[]{userNo, buNo, blackOrWrite};
            return resultName;
        }else if (names.length ==2){
            String userNo = names[0];
            String buNo = names[1];
            String[] resultName = new String[]{userNo, buNo, null};
            return  resultName;
        } else if (names.length ==1){
            String userNo = names[0];
            String[] resultName = new String[]{userNo, null, null};
            return  resultName;
        } else {
            return null;
        }
    }
}
