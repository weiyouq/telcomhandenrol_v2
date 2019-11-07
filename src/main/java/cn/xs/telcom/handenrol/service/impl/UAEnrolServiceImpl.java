package cn.xs.telcom.handenrol.service.impl;


import cn.xs.telcom.handenrol.Utils.StringUtils;
import cn.xs.telcom.handenrol.bean.AudioBean;
import cn.xs.telcom.handenrol.bean.User;
import cn.xs.telcom.handenrol.config.Const;
import cn.xs.telcom.handenrol.core.UAFunction;
import cn.xs.telcom.handenrol.dao.IBusinessDao;
import cn.xs.telcom.handenrol.dao.IEnrolTouchDao;
import cn.xs.telcom.handenrol.dao.IUserBusinessDao;
import cn.xs.telcom.handenrol.dao.IUserDao;
import cn.xs.telcom.handenrol.service.IUAEnrolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author kenny_peng
 * @created 2019/9/24 11:32
 */
@Service
public class UAEnrolServiceImpl implements IUAEnrolService {

	private static Integer MAX_URL_SUM = 0;
	private Logger logger = LoggerFactory.getLogger(UAEnrolServiceImpl.class);

	private String[] baseUrl;
	@Value("${ua.address}")
	public void setUaEnrolAddress(String address) {
		baseUrl = address.split(",");
		MAX_URL_SUM = baseUrl.length * 10000;
	}

	@Value("${snr.score:12.0}")
	private float snrScore;
	
	@Value("${ua.ti.threshold:2.0}")
	private float threshold;

	@Autowired
	private IUserDao userDao;
	@Autowired
	private IUserBusinessDao userBusinessDao;
	@Autowired
	private IBusinessDao businessDao;
	@Autowired
	private IEnrolTouchDao enrolTouchDao;

	private int urlSum = 0;
	private UAFunction uaFunction = new UAFunction();

	/**
	 * 均衡url
	 * @return
	 */
	private synchronized String getUseUrl() {
		if (urlSum > MAX_URL_SUM) {
			urlSum = 0;
		}
		return baseUrl[urlSum++ % baseUrl.length];
	}

	/**
	 * 1:1声纹离线注册
	 * 
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void enrol(AudioBean audioBean) throws Exception{
		audioBean.setEnrolDate(new Date());

		String base64 = audioBean.getBase64();
		String logName = "log-" + System.currentTimeMillis();// log日志名

		if (StringUtils.isNullOrEmpty(audioBean.getUserNo())) {
			// 该user已经被删除，结束注册
			audioBean.setEnrolStatus(Const.VB_ENROL_STATUS_SUCCESS);
			audioBean.setBase64(null);
			audioBean.setEnrolRecord(4);
			return;
		}
		/*
		 * 1、未注册，则未激活注册
		 * 
		 * 2、已激活注册，进行声纹验证，
		 * 
		 * 3、未注册，但有1条声纹模型，有2条声纹模型
		 * 
		 * 声纹状态：1、激活模型，2、未激活注册有一条模型1，3、未激活注册有两条模型2, 4、验证关注的激活模型，5、未注册
		 */
		boolean result = false;
		int userStatus = audioBean.getUserState();
		User user = new User(audioBean.getUserId(), audioBean.getUserNo(), 0, audioBean.getUsedUserId(), audioBean.getUserState());
		//"4"未注册
		if (userStatus == 4) {
			result = doEnrolSpeaker(logName, user.getVerifyId(), base64);
			if (result) {
				user.setUserState(1);
				audioBean.setEnrolRecord(1);
			}
		} else if (userStatus == 1) {//"1" 预注册
			result = doVerifySpeaker(logName, user.getVerifyId(), base64);
			if (result) {//验证通过成功，将该预注册设为激活的模型
				user.setUsedUserId(user.getVerifyId());
				user.setUserState(0);
				audioBean.setEnrolRecord(0);
			} else {//验证不通过，将该语音注册为第二条预注册声纹
				result = doEnrolSpeaker(logName, user.getVerifyId2(), base64);
				if (result) {
					user.setUserState(2);
					audioBean.setEnrolRecord(2);
				}
			}
		} else if (userStatus == 0) {//0 已激活
			result = doVerifySpeaker(logName, user.getVerifyId(), base64);
			if (!result) {//验证不通过，将模型设置为验证关注
				user.setUserState(3);
				audioBean.setEnrolRecord(3);
				result = true;
			}
		} else if (userStatus == 3) {//3 验证关注(为验证关注，则usedUserId不为空)
			result = doVerifySpeaker(logName, user.getVerifyId(), base64);
			if (result) {//验证通过，将验证关注设置为激活的模型
				user.setUserState(0);
				// user.setUsedUserId(user.getVerifyId());
				audioBean.setEnrolRecord(0);
			} else {//验证不通过，删除该验证关注模型
				// delete user,business_user
				userDao.deleteByPrimaryKey(user.getId());
				userBusinessDao.deleteByUserIdAndBuId(user.getId(), audioBean.getBuId());
				result = true;//设置结果为ture（删除用户成功，该条语音注册处理成功）
				audioBean.setEnrolRecord(4);
			}
		} else if (userStatus == 2) {//"2" 2条预注册
			String[] verifyIds = user.getVerifyId().split(",");
			result = doVerifySpeaker(logName, verifyIds[0], base64);//先跟第一条预注册模型比对
			if (result) {//成功将该条模型设为激活模型
				user.setUserState(0);
				user.setUsedUserId(verifyIds[0]);
				audioBean.setEnrolRecord(0);
			} else {//失败，跟第二条预注册模型比对
				result = doVerifySpeaker(logName, verifyIds[1], base64);
				if (result) {//验证通过，将该条模型设为激活模型
					user.setUserState(0);
					user.setUsedUserId(verifyIds[1]);
					audioBean.setEnrolRecord(0);
				}else {//跟两条预注册模型比对都不通过
					audioBean.setEnrolRecord(5);
					result = true;
				}
			}
		}
		if (result) {
			audioBean.setUserNo(user.getUserId());
			audioBean.setUsedUserId(user.getUsedUserId());
			audioBean.setUserState(user.getUserState());
			audioBean.setEnrolStatus(Const.VB_ENROL_STATUS_SUCCESS);
		} else {
			audioBean.setEnrolStatus(Const.VB_ENROL_STATUS_FAILED);
			audioBean.setEnrolError("SNR分数低于阈值");
		}
		audioBean.setBase64(null);
		// enrolTouchDao.updateAudioBean(audioBean);
	}

	/**
	 * 1:1根据userNo，base64进行注册
	 * 
	 * @param logName 日志名
	 * @param userNo  用户编号
	 * @param base64  音频对应base64
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "resource" })
	private boolean doEnrolSpeaker(String logName, String userNo, String base64) throws Exception{
		/*// 得到声纹注册payload
		String params = PayloadUtils.enrolOrVerifyPayload(logName, userNo, base64);
		// logger.info(params);

		// 测试，将所有的payload保存到本地
		// try {
		// BufferedWriter bw = new BufferedWriter(new
		// FileWriter("./temp/base64/enrol/" + userNo + ".txt"));
		// logger.info("保存enrol文件路径：" + "./temp/base64/enrol/" + userNo
		// +".txt");
		// bw.write(params);
		// bw.flush();
		// } catch (IOException e) {
		// logger.error("保存enrol文件到本地异常：", e);
		// }

		// 发送post请求,调用声纹引擎注册接口，得到声纹注册结果
		String enrolUrl = getUseUrl() + "enrolSpeaker";
		String enrolResult = HttpUtils.sendPost(enrolUrl, params);
		Map<String, Object> enrolResultMap = JSONObject.fromObject(enrolResult);
		// 返回正确结果
		if (enrolResultMap.containsKey("result") && !enrolResultMap.containsKey("errorData")) {

			// 解析verifyResult，得到信噪比值
			Object o = JSONObject.fromObject(enrolResultMap.get("result")).get("metaInformation");
			JSONArray jsonArray = JSONArray.fromObject(o);
			String resultSnrScore = null;
			for (int i = 0; i < jsonArray.size(); i++) {
				if (JSONObject.fromObject(jsonArray.getString(i)).get("key").equals("get-snr")) {
					resultSnrScore = (String) JSONObject.fromObject(
							JSONObject.fromObject(jsonArray.getString(i)).get("value")).get("value");
				}
			}

			// 如果信噪比分数低于阈值，返回错误
			if (Double.valueOf(resultSnrScore) < Double.valueOf(snrScore)) {
				logger.error("分数低于阈值");
				return RspUABase.error("分数低于阈值");
			} else {
				return RspUABase.success("注册成功");
			}
		} else {// 返回错误结果
			return RspUABase.error(enrolResult);
		}*/
		String enrolUrl = getUseUrl() + "enrolSpeaker";
		String enrolPayload = uaFunction.enrolPayload(logName, userNo, base64);
		logger.error("1:1enrolPayload" + enrolPayload);
		String enrolResult = uaFunction.enrol(enrolUrl, enrolPayload);
		return uaFunction.isEnrolPassed(enrolResult, snrScore);

	}

	/**
	 * 1:1根据userNo，用户传入音频的base64进行验证
	 * @param logName 日志名称
	 * @param userNo  用户编号
	 * @param base64  用户传入音频对应的base64
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "resource" })
	private boolean doVerifySpeaker(String logName, String userNo, String base64) throws Exception{

		/*// 得到声纹验证payload
		String verifyPayload = PayloadUtils.enrolOrVerifyPayload(logName, userNo, base64);
		// logger.info(verifyPayload);

		// 测试，将所有的payload保存到本地
		// try {
		// logger.info("保存verify文件路径：" + "./temp/base64/verify/" + visitConut +
		// ".txt");
		// BufferedWriter bw = new BufferedWriter(new
		// FileWriter("./temp/base64/verify/" + visitConut + ".txt"));
		// bw.write(verifyPayload);
		// bw.flush();
		//
		// } catch (IOException e) {
		// logger.error("保存verify文件到本地异常：", e);
		// }

		// 发送post请求,调用声纹引擎验证接口，得到声纹验证结果
		String verifyUrl = getUseUrl() + "verifySpeaker";
		String verifyResult = HttpUtils.sendPost(verifyUrl, verifyPayload);
		Map<String, Object> verifyResultMap = JSONObject.fromObject(verifyResult);

		// 返回正确结果
		if (verifyResultMap.containsKey("result")) {

			// 解析verifyResult，得到信噪比值
			Object o = JSONObject.fromObject(verifyResultMap.get("result")).get("metaInformation");
			JSONArray jsonArray = JSONArray.fromObject(o);
			String resultSnrScore = null;
			for (int i = 0; i < jsonArray.size(); i++) {
				if (JSONObject.fromObject(jsonArray.getString(i)).get("key").equals("get-snr")) {
					resultSnrScore = (String) JSONObject.fromObject(JSONObject.fromObject(jsonArray.getString(i)).get("value")).get("value");
				}
			}
			// 如果信噪比分数低于阈值，返回错误
			if (Double.valueOf(resultSnrScore) < Double.valueOf(snrScore)) {
				return RspUABase.error("分数低于阈值");
			} else {
				String score = (String) JSONObject.fromObject(verifyResultMap.get("result")).get("score");

				// 符合验证得分小于阈值返回false，否则返回true
				if (Float.valueOf(score) > threshold) {
					return RspUABase.success();
				} else {
					return RspUABase.error("error");
				}
			}
		} else {// 返回错误结果
			return RspUABase.error(verifyResult);
		}*/
		String verifyUrl = getUseUrl() + "verifySpeaker";
		String verifyPayload = uaFunction.verifyPayload(logName, userNo, base64);
		String verifyResult = uaFunction.verify(verifyUrl, verifyPayload);
		return uaFunction.isVerifyPassed(verifyResult, snrScore, threshold);

	}
}
