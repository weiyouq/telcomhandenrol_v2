package cn.xs.telcom.handenrol.dao;


import cn.xs.telcom.handenrol.bean.Business;

import java.util.List;

/**
 * @author kenny_peng
 * @created 2019/8/2 15:32
 */
public interface IBusinessDao extends IBaseDao<Integer, Business> {
    Business selectByBuNo(String buNo);

    int insertList(List<Business> businesses);
}
