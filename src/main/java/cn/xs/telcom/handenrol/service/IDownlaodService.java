package cn.xs.telcom.handenrol.service;


import cn.xs.telcom.handenrol.bean.AudioBean;

import java.util.List;

/**
 * @author kenny_peng
 * @created 2019/9/23 16:17
 */
public interface IDownlaodService {
    /**
     * 获取音频路径集合
     * @param date
     * @return
     */
    List<AudioBean> getAudioListByDate(String date) throws Exception;

    /**
     * 根据音频路径下载音频
     * @param audioBean
     * @return            返回音频实体
     */
    void downloadAudio(AudioBean audioBean) throws Exception;

//    /**
//     * 根据音频路径下载音频对应的随流json数据
//     * @param audioPath
//     * @return
//     */
//    AudioBean downloadJson(String audioPath) throws IOException;
}
