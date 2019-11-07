package cn.xs.telcom.handenrol.service;

import cn.xs.telcom.handenrol.bean.AudioBean;

import java.util.List;

/**
 * @author kenny_peng
 * @created 2019/10/25 14:06
 */
public interface IIdentifyEnrolService {

    String enrol(AudioBean audioBean) throws Exception;

    void saveByAudioBeanList(List<AudioBean> enrolledList);
}
