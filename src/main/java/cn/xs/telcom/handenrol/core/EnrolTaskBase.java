package cn.xs.telcom.handenrol.core;


import cn.xs.telcom.handenrol.Utils.StringUtils;
import cn.xs.telcom.handenrol.bean.AudioBean;
import cn.xs.telcom.handenrol.config.Const;
import cn.xs.telcom.handenrol.service.IAudioService;
import cn.xs.telcom.handenrol.service.IDownlaodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author kenny_peng
 * @created 2019/9/23 16:46
 */

public abstract class EnrolTaskBase {

	private Logger logger = LoggerFactory.getLogger(EnrolTaskBase.class);
	private static final int MAX_AUDIO_COUNT = 1000;

	@Autowired
	protected IDownlaodService downlaodService;
	@Autowired
	private IAudioService audioService;

	private volatile boolean isStop = true;

	public boolean isStop() {
		return isStop;
	}

	private List<AudioBean> dataList = new ArrayList<>();

	private ExecutorService es = null;

	@Value("${max.thread.count}")
	private int MAX_THREAD_COUNT;// 最大线程数
	@Value("${application.id}")
	public int appId;
	@Value("${application.total}")
	public int appSum;
	@Value("${save.enroltouch.application.id}")
	protected int doSaveAppId;

	@Value("${max.retry.num:5}")
	public int retryNum;

	/**
	 * 开始执行注册任务
	 */
	public void start(String date) {
		isStop = false;
		new Thread(new Runnable() {
			@Override
			public void run() {
				doSave(date);
				doStart(date);
			}
		}).start();
	}

	protected abstract void doSave(String date);

	private void doStart(String date) {
		//开始注册
//		while (true) {
			if (isStop){
				logger.info("----------注册任务被关闭-------------");
				return;
			}
			int m = 0;
			do {
				try {
					if (dataList.size() > 0)
						dataList.clear();
					dataList.addAll(getAudioList(date));
					logger.info("设置appId为：" + appId + "的服务的处理日期为：" + date + "查询接续文件大小为：" + dataList.size());
					m = retryNum;
				} catch (Exception e) {
					m++;
					try {
						Thread.sleep(10000);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
					if (m == retryNum){
						logger.error("查询未注册音频接续失败", e);
					}
				}
			}while (m < retryNum);

			// 如果dataList为空，结束
			if (dataList == null || dataList.size() == 0) {
				isStop = true;
				logger.info("----------注册任务结束-------------");
				return;
			}
			logger.info("----------注册任务启动-------------");
			// 创建线程池
			es = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
			CountDownLatch cdl = new CountDownLatch(dataList.size());
			List<AudioBean> audioBeanList = new ArrayList<>();
			if (audioBeanList.size() > 0)
				audioBeanList.clear();
			for (int i = 0, max = dataList.size(); i < max; i++) {
				final AudioBean ab = new AudioBean(dataList.get(i));
				es.execute(new Runnable() {
					@Override
					public void run() {
						try {
							downlaodService.downloadAudio(ab);
						} catch (Exception e) {
							ab.setDownloadDate(new Date());
							ab.setDownloadError(StringUtils.getMaxLen("下载音频失败" + e.getMessage(),40));
							ab.setDownloadStatus(Const.FTP_DOWNLOAD_FAILED);
							logger.error("下载音频失败",e);
						}
						ab.setEnrolDate(new Date());
						try {
							if (ab.getDownloadStatus() == 2) {
								enrolAudio(ab);
								ab.setEnrolStatus(2);
							}
							logger.info("注册结束后的audioBean的下载状态：" + ab.getDownloadStatus());
						} catch (Exception e) {
							ab.setEnrolStatus(Const.VB_ENROL_STATUS_FAILED);
							ab.setEnrolError(StringUtils.getMaxLen("注册失败：" + e.getMessage(), 200));

							logger.error(ab.getAudioPath() + "注册失败", e);
						}
						logger.info(dataList.size() + "注册剩余大小：" + cdl.getCount());
						audioBeanList.add(ab);
						cdl.countDown();
					}
				});
			}
			try {
				//await()该线程进行等待，直到countDown减到0，然后逐个苏醒过来。wait()Causes the current thread to wait until another thread invokes the
				cdl.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// 更新经过注册流程的音频状态
			int count = 0;
			do {
				try {
					save(audioBeanList);
					count = retryNum;
				} catch (Exception e) {
					count++;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {
						logger.error("线程sleep异常", e);
					}
					if (count == retryNum){
						logger.error("更新经过注册流程的音频状态失败", e);
					}
				}
			}while (count < retryNum);
			dataList.clear();
//		}
	}

	public void stop() {
		isStop = true;
		if (es != null){
			es.shutdownNow();
			try {
				es.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		dataList.clear();
		logger.info("------------注册任务关闭成功-----------");
	}

	/**
	 * 读取行未下载的音频信息
	 * @return
	 */
	protected abstract List<AudioBean> getAudioList(String date) throws Exception;

	/**
	 * UA声纹注册
	 * 
	 * @param audioBean
	 */
	protected abstract void enrolAudio(AudioBean audioBean) throws Exception;

	/**
	 * 更新接续文件
	 * @param enrolledList
	 */
	protected abstract void save(List<AudioBean> enrolledList);

}
