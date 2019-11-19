package cn.xs.telcom.handenrol.core;


import cn.xs.telcom.handenrol.Utils.HttpUtils;
import cn.xs.telcom.handenrol.Utils.PayloadUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.rmi.runtime.Log;

import java.util.Map;

/**
 * @author kenny_peng
 * @created 2019/10/12 11:56
 */
public class UAFunction {

    public static String enrol(String enrolUrl, String params) throws Exception {
        return HttpUtils.doPost(enrolUrl, params);
    }

    public static String verify(String verifyUrl, String verifyPayload) throws Exception{
        return HttpUtils.doPost(verifyUrl, verifyPayload);
    }


    public static boolean isEnrolPassed(String enrolResult, float snrScore) throws Exception{
        Map<String, Object> enrolResultMap = JSONObject.fromObject(enrolResult);
        // 返回正确结果
        if (enrolResultMap.containsKey("result") && !enrolResultMap.containsKey("errorData")) {
            // 解析verifyResult，得到信噪比值
            Object o = JSONObject.fromObject(enrolResultMap.get("result")).get("metaInformation");
            JSONArray jsonArray = JSONArray.fromObject(o);
            String resultSnrScore = null;
            for (int i = 0; i < jsonArray.size(); i++) {
                if (JSONObject.fromObject(jsonArray.getString(i)).get("key").equals("get-snr")) {
                    resultSnrScore = (String) JSONObject.fromObject(JSONObject.fromObject(jsonArray.getString(i)).get("value")).get("value");
                }
            }
            // 如果信噪比分数低于阈值，返回错误
            if (Float.valueOf(resultSnrScore) < snrScore) {
                return false;
            } else {
                return true;
            }
        } else {// 返回错误结果
            throw new Exception(enrolResult);
        }
    }

    public static boolean isVerifyPassed(String verifyResult, float snrScore, float threshold) throws Exception {
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
                throw new Exception("SNR分数低于阈值");
            } else {
                String score = (String) JSONObject.fromObject(verifyResultMap.get("result")).get("score");

                // 符合验证得分小于阈值返回false，否则返回true
                if (Float.valueOf(score) > threshold) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {// 返回错误结果
            throw new Exception(verifyResult);
        }
    }

    /**
     * 注册payload
     * @param logName
     * @param userNo
     * @param base64
     * @return
     */
    public static String enrolPayload(String logName, String userNo, String base64){
        // 得到声纹注册payload
        return PayloadUtils.enrolOrVerifyPayload(logName, userNo, base64);
    }
    public static String enrolPayload(String logName, String userNo, String base64, String snrScore){
        // 得到声纹注册payload
        return PayloadUtils.uaEnrolPayload(logName, userNo, base64, snrScore);
    }

    /**
     * 验证payload
     * @param logName
     * @param userNo
     * @param base64
     * @return
     */
    public static String verifyPayload(String logName, String userNo, String base64){
        // 得到声纹验证payload
        return PayloadUtils.enrolOrVerifyPayload(logName, userNo, base64);
    }

    public static String doDelete(String deleteUrl, String deletePayload) throws Exception{
        String deleteResult = HttpUtils.doPost(deleteUrl, deletePayload);
        return deletePayload;
    }

    /**
     * 删除payload
     * @param useToEnrolNo
     * @return
     */
    public static String deletePayload(String useToEnrolNo){
        return PayloadUtils.vbDeletePayload(useToEnrolNo);
    }
}
