package cn.xs.telcom.handenrol.dao;


import cn.xs.telcom.handenrol.bean.UserBusiness;

import java.util.List;

/**
 * @author kenny_peng
 * @created 2019/8/2 15:22
 */
public interface IUserBusinessDao extends IBaseDao<Long, UserBusiness> {
    UserBusiness selectUserByUserIdAndBuId(Long userID, Integer buID);

    int deleteByUserIdAndBuId(Long userID, Integer buID);

    int insertList(List<UserBusiness> userBusinesses);
}
