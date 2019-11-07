package cn.xs.telcom.handenrol.core;

import cn.xs.telcom.handenrol.bean.AudioBean;
import cn.xs.telcom.handenrol.service.IIdentifyEnrolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author kenny_peng
 * @created 2019/10/25 13:47
 */
@Component
public class IdentifyEnrolTask extends EnrolTaskBase{

    @Autowired
    private IIdentifyEnrolService identifyEnrolService;

    @Override
    protected void doSave(String date) {

    }

    @Override
    protected List<AudioBean> getAudioList(String date) throws Exception {
        return downlaodService.getAudioListByDate(date);
    }

    @Override
    protected void enrolAudio(AudioBean audioBean) throws Exception {
        identifyEnrolService.enrol(audioBean);
    }

    @Override
    protected void save(List<AudioBean> enrolledList) {
        identifyEnrolService.saveByAudioBeanList(enrolledList);
    }
}
