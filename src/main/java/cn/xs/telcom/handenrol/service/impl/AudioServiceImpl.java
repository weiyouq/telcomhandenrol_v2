package cn.xs.telcom.handenrol.service.impl;


import cn.xs.telcom.handenrol.bean.*;
import cn.xs.telcom.handenrol.dao.IBusinessDao;
import cn.xs.telcom.handenrol.dao.IEnrolTouchDao;
import cn.xs.telcom.handenrol.dao.IUserBusinessDao;
import cn.xs.telcom.handenrol.dao.IUserDao;
import cn.xs.telcom.handenrol.service.IAudioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 从数据库中获取未处理过的语音接续文件路径
 * @author kenny_peng
 * @created 2019/9/24 17:25
 */
@Service
public class AudioServiceImpl implements IAudioService {

    private Logger logger = LoggerFactory.getLogger(AudioServiceImpl.class);
    @Autowired
    private IEnrolTouchDao enrolTouchDao;
    @Autowired
    private IUserDao userDao;
    @Autowired
    private IBusinessDao businessDao;
    @Autowired
    private IUserBusinessDao userBusinessDao;
    private final int INSERT_SIZE = 1000;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveEnrolTouch(List<AudioBean> audioBeanList, String date){
        List<EnrolTouch> dataSaved = enrolTouchDao.getEnrolTouchByDate(date);
        if (dataSaved.size() == audioBeanList.size()){
            logger.info("保存接续文件已经保存过，不再保存");
            return;
        }

        //1、批量保存
        List<User> users = new ArrayList<>();
        List<Business> businesses = new ArrayList<>();
        List<UserBusiness> userBusinesses = new ArrayList<>();
        List<AudioBean> audioBeans = new ArrayList<>();
        int i = audioBeanList.size();
        int count = 0;
        while (i > 0){
            if (count < INSERT_SIZE){
                AudioBean bean = audioBeanList.get(i-1);
                String callerid = bean.getUserNo();//手机号
                String calledid = bean.getBuNo();//租户号
                String audioPath = bean.getAudioPath();
                audioBeans.add(bean);
                users.add(new User(callerid, 0));
                businesses.add(new Business(calledid));
                userBusinesses.add(new UserBusiness(callerid, calledid, audioPath));
                i--;
                count++;
            }

            if (count >= INSERT_SIZE || i == 0){
                userDao.insertList(users);
                enrolTouchDao.insertList(audioBeans);
                businessDao.insertList(businesses);
                userBusinessDao.insertList(userBusinesses);
                count = 0;
            }
        }
        logger.info("保存"+ audioBeanList.size()+"个接续文件成功");
    }

    @Override
    public List<AudioBean> getAudioList(String date, int appSum, int appId){
        //查询未处理过的语音接续文件对象
        List<AudioBean> audioBeanList = enrolTouchDao.getAudioList(date, appSum, appId);
        return audioBeanList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByAudioBeanList(List<AudioBean> audioBeanList) {
        int updateEnrolTouchResult = enrolTouchDao.updateByList(audioBeanList);
        int updateUserResult = userDao.updateByList(audioBeanList);
        logger.info("更新接续文件状态条数：" + updateEnrolTouchResult + "更新用户注册状态条数：" + updateUserResult);
    }
}
