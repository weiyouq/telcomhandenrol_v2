package cn.xs.telcom.handenrol.dao;


import cn.xs.telcom.handenrol.bean.AudioBean;
import cn.xs.telcom.handenrol.bean.EnrolTouch;

import java.util.List;

/**
 * @author kenny_peng
 * @created 2019/9/23 17:50
 */
public interface IEnrolTouchDao extends IBaseDao<Long, EnrolTouch> {

    int insertList(List<AudioBean> audioBeanList);
//    int insertAudioBeanList(@Param("list") List<AudioBean> list, @Param("date") String date);

    int updateByList(List<AudioBean> audioBeanList);

    int updateAudioBean(AudioBean bean);

    List<AudioBean> getAudioList(String queryDate, int size, int appId);

    List<EnrolTouch> getEnrolTouchByDate(String date);
}
