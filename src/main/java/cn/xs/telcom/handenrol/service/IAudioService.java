package cn.xs.telcom.handenrol.service;


import cn.xs.telcom.handenrol.bean.AudioBean;

import java.util.List;

public interface IAudioService {

	void saveEnrolTouch(List<AudioBean> audioStringList, String date);

	/**
	 * 获取需要注册音频集合
	 * @param size      执行程序的服务总数
	 * @param appId     此服务的id号
	 * @return
	 */
	List<AudioBean> getAudioList(String date, int size, int appId);

	void updateByAudioBeanList(List<AudioBean> audioBeanList);

}
