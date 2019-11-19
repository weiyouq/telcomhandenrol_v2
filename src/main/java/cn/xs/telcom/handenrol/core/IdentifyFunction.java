package cn.xs.telcom.handenrol.core;

import cn.xs.telcom.handenrol.Utils.HttpUtils;
import cn.xs.telcom.handenrol.Utils.PayloadUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author kenny_peng
 * @created 2019/10/25 13:14
 */
public class IdentifyFunction {

    private static Logger logger = LoggerFactory.getLogger(IdentifyFunction.class);

    public static String doEnrol(String enrolUrl, String callerid, String base64) throws Exception{
        String enrolPayload = enrolPayload(callerid, base64);
        String enrolResult = HttpUtils.doPost(enrolUrl, enrolPayload);
        Integer retCode = (Integer) JSONObject.fromObject(enrolResult).get("retCode");
        if (retCode == 0){
            return enrolResult;
        }else {
            throw new Exception("注册失败：" + enrolResult);
        }
    }


    /**
     * snr值检测
     * @param logName
     * @param userNo
     * @param base64
     * @return
     */
    public static boolean isPassSNR(String logName, String userNo, String base64, String snrScore, String uaEnrolUrl) throws Exception{
        //得到声纹注册payload
        String useToEnrolNo = userNo+"_ispasssnr";
        String enrolPayload = new UAFunction().enrolPayload(logName,useToEnrolNo,base64,String.valueOf(snrScore));
        //发送post请求,调用声纹引擎注册接口，得到声纹注册结果
        String enrolResult = HttpUtils.doPost(uaEnrolUrl, enrolPayload);
        Map<String,Object> enrolResultMap = JSONObject.fromObject(enrolResult);

        //返回正确结果
        if (enrolResultMap.containsKey("result") && !enrolResultMap.containsKey("errorData")){

            //调用1vs1删除模型
            UAFunction.doDelete(uaEnrolUrl, UAFunction.deletePayload(useToEnrolNo));
            //解析verifyResult，得到信噪比值
            Object o = JSONObject.fromObject(enrolResultMap.get("result")).get("metaInformation");
            JSONArray jsonArray = JSONArray.fromObject(o);
            String resultSnrScore = null;
            for (int i = 0; i<jsonArray.size(); i++){
                if (JSONObject.fromObject(jsonArray.getString(i)).get("key").equals("get-snr")){
                    resultSnrScore = (String) JSONObject.fromObject(JSONObject.fromObject(jsonArray.getString(i)).get("value")).get("value");
                }
            }

            //如果信噪比分数低于阈值，返回错误
            if (Double.valueOf(resultSnrScore) < Double.valueOf(snrScore)){
                logger.info("snr分数过低，snr=" + resultSnrScore);
                return false;
            }else {
                logger.info("snr校验通过：" + enrolResult);
                return true;
            }
            /*String getVerifyScore = (String) JSONObject.fromObject(verifyResultMap.get("result")).get("score");
            //符合验证得分小于阈值返回false，否则返回true
            if (Double.valueOf(getVerifyScore) > Double.valueOf(verifyScore)){
                return ResponseTemplate.ok(true);
            }else {
                return ResponseTemplate.ok(false);
            }*/

        }else {//返回错误结果
            throw new Exception(enrolResult);
        }
    }



    public static String enrolPayload(String callerid, String enrolBase64){
        return PayloadUtils.identifyPayload(callerid, enrolBase64);
    }

}
