package cn.xs.telcom.handenrol.core;


import cn.xs.telcom.handenrol.bean.AudioBean;
import cn.xs.telcom.handenrol.service.IAudioService;
import cn.xs.telcom.handenrol.service.IUAEnrolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UAEnrolTask extends EnrolTaskBase {

	private Logger logger = LoggerFactory.getLogger(UAEnrolTask.class);
	@Autowired
	private IAudioService audioService;
	@Autowired
	private IUAEnrolService uAEnrolService;



	@Override
	protected void doSave(String date) {
		if (appId == doSaveAppId){//设置只有一个应用服务执行save接续文件操作
			//1、获取音频路径、手机号和租户号
			List<AudioBean> audioBeanList = null;
			if(audioBeanList != null && audioBeanList.size() > 0){
				audioBeanList.clear();
			}
			int sum = 0;
			do {
				try {
					audioBeanList = downlaodService.getAudioListByDate(date);
					logger.info("获取当天ftp音频文件路径个数为：" + audioBeanList.size());
					sum = retryNum;
				} catch (Exception e) {
					sum++;
					logger.info("获取当天ftp音频文件路径异常,重试次数" + sum);
					try {
						Thread.sleep(10000);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
					if (sum == retryNum){
						logger.error("获取当天ftp音频文件路径异常", e);
						/**
						 * 添加警告机制
						 */
						return;
					}
				}
			}while (sum < retryNum);

			//2、保存当天ftp接续数据，出现异常时，睡眠10s重试，重试超过retryNum结束
			int n = 0;
			do {
				try {
					if (audioBeanList.size() > 0){
						audioService.saveEnrolTouch(audioBeanList, date);
					}
					n = retryNum;
				} catch (Exception e) {
					n++;
					logger.info("测试catch异常后执行次数：" + n);
					try {
						Thread.sleep(10000);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
					if (n == retryNum){
						logger.error("保存当天ftp接续数据失败", e);
					}
				}
			}while (n < retryNum);
		}
	}

	@Override
	protected List<AudioBean> getAudioList(String date) throws Exception{
		return audioService.getAudioList(date, appSum, appId);
	}

	@Override
	protected void enrolAudio(AudioBean audioBean) throws Exception {
		uAEnrolService.enrol(audioBean);
	}

	@Override
	protected void save(List<AudioBean> enrolledList) {
		audioService.updateByAudioBeanList(enrolledList);

	}
}
