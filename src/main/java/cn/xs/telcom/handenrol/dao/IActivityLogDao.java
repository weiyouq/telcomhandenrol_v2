package cn.xs.telcom.handenrol.dao;


import cn.xs.telcom.handenrol.bean.ActivityLog;

import java.util.List;

/**
 * @author kenny_peng
 * @created 2019/8/1 11:41
 */
public interface IActivityLogDao extends IBaseDao<Long, ActivityLog> {


    /**
     * 已经注册过的集合
     * @return
     */
    List<String> selectAlreadyEnroledList(String date);

    int insertByList(List<ActivityLog> list);

    int insertByUserNoAndBuNo(String name, String name1);

    List<ActivityLog> selectIfTodayEnrolled(Long id, String date);

    int saveAll(List<ActivityLog> activityLogList);
}
