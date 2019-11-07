package cn.xs.telcom.handenrol.dao;


import cn.xs.telcom.handenrol.bean.AudioBean;
import cn.xs.telcom.handenrol.bean.User;

import java.util.List;

/**
 * @author kenny_peng
 * @created 2019/8/2 15:13
 */
public interface IUserDao extends IBaseDao<Long, User> {

    User selectUserByType(String userId, int enrolType);

    int insertList(List<User> users);

    int updateByList(List<AudioBean> audioBeanList);

}
