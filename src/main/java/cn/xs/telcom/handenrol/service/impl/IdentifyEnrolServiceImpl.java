package cn.xs.telcom.handenrol.service.impl;

import cn.xs.telcom.handenrol.bean.ActivityLog;
import cn.xs.telcom.handenrol.bean.AudioBean;
import cn.xs.telcom.handenrol.bean.Business;
import cn.xs.telcom.handenrol.bean.User;
import cn.xs.telcom.handenrol.core.IdentifyFunction;
import cn.xs.telcom.handenrol.dao.IActivityLogDao;
import cn.xs.telcom.handenrol.dao.IBusinessDao;
import cn.xs.telcom.handenrol.dao.IUserDao;
import cn.xs.telcom.handenrol.service.IIdentifyEnrolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kenny_peng
 * @created 2019/10/25 14:07
 */
@Service
public class IdentifyEnrolServiceImpl implements IIdentifyEnrolService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IActivityLogDao activityLogDao;
    @Autowired
    private IBusinessDao businessDao;
    @Autowired
    private IUserDao userDao;

    private int urlSum = 0;
    private static Integer MAX_URL_SUM = 0;
    private String[] baseUrl;
    @Value("${identity.address}")
    public void setUaEnrolAddress(String address) {
        baseUrl = address.split(",");
        MAX_URL_SUM = baseUrl.length * 10000;
    }
    @Value("${snr.score:12.0}")
    private float snrScore;
    /**
     * 均衡url
     * @return
     */
    private synchronized String getUseUrl() {
        if (urlSum > MAX_URL_SUM) {
            urlSum = 0;
        }
        return baseUrl[urlSum++ % baseUrl.length];
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String enrol(AudioBean audioBean) throws Exception{
        String base64 =audioBean.getBase64();
        String url = getUseUrl()+"enrolSpeaker";
        return IdentifyFunction.doEnrol(url, audioBean.getUserNo(), base64);
    }

    @Override
    public void saveByAudioBeanList(List<AudioBean> enrolledList) {
        List<User> userList = new ArrayList<>();
        List<Business> businessList = new ArrayList<>();
        List<ActivityLog> activityLogList = new ArrayList<>();

        for (AudioBean audioBean : enrolledList){
            if (audioBean.getDownloadStatus() == 2 && audioBean.getEnrolStatus() == 2){
                userList.add(new User(audioBean.getUserNo(), 1));
                if (audioBean.getBuNo() !=null && audioBean.getBuNo() != ""){
                    businessList.add(new Business(audioBean.getBuNo()));
                }
            }
            logger.info("传入日期为：" + audioBean.getDownloadDate() + "--" + audioBean.getEnrolDate());
            activityLogList.add(new ActivityLog(audioBean));
        }
        activityLogDao.saveAll(activityLogList);
        if (userList.size() > 0)
            userDao.insertList(userList);
        if (businessList.size() > 0)
            businessDao.insertList(businessList);
    }
}
