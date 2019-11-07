package cn.xs.telcom.handenrol.service;


import cn.xs.telcom.handenrol.bean.AudioBean;

/**
 * @author kenny_peng
 * @created 2019/9/24 11:33
 */
public interface IUAEnrolService {

    /**
     * 1对1声纹注册
     * @return
     */
    void enrol(AudioBean audioBean) throws Exception;

}
