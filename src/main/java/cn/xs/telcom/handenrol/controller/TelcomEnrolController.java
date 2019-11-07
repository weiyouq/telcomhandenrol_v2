package cn.xs.telcom.handenrol.controller;


import cn.xs.telcom.handenrol.Utils.FileUtils;
import cn.xs.telcom.handenrol.Utils.SoxUtils;
import cn.xs.telcom.handenrol.core.IdentifyEnrolTask;
import cn.xs.telcom.handenrol.core.UAEnrolTask;
import cn.xs.telcom.handenrol.core.UAFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 * 每天定时执行"saveEnrolTouch"，保存需要注册数据
 * @author kenny_peng
 * @created 2019/7/25 10:09
 */
@Controller
public class TelcomEnrolController {

    private Logger logger = LoggerFactory.getLogger(TelcomEnrolController.class);
    @Autowired
    private UAEnrolTask uaEnrolTask;
    @Autowired
    private IdentifyEnrolTask identifyEnrolTask;
    @Value("${ua.enrol.day}")
    private int enrolDay;


    /**
     * 1对1注册接口
     *
     * @return
     */
    @RequestMapping("/stdBiometricLite/enrolSpeaker")
    @ResponseBody
    public void doUaEnrolSpeaker() {

        String date = FileUtils.formateDate("yyyyMMdd", FileUtils.addSomeDay(new Date(), enrolDay));
        //注册是否停止，停止了就开启
        if (!uaEnrolTask.isStop()) {
            uaEnrolTask.stop();
        }
        uaEnrolTask.start(date);
    }

    /**
     * 1对1注册接口
     *
     * @return
     */
    @RequestMapping("/biometric/enrolSpeaker")
    @ResponseBody
    public void doIdentifyEnrolSpeaker() {

        String date = FileUtils.formateDate("yyyyMMdd", FileUtils.addSomeDay(new Date(), enrolDay));
        //注册是否停止，停止了就开启
        if (!identifyEnrolTask.isStop()) {
            identifyEnrolTask.stop();
        }
        identifyEnrolTask.start(date);
    }

//    /**
//     * 1对多注册接口
//     * @param path      选择的音频路径
//     * @return
//     */
//    @PostMapping("/biometric/enrolSpeaker")
//    @ResponseBody
//    public String identifyService(@RequestBody String path, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        return iEnrolService.identifyService(path);
//    }

    @Value("${sox.install.path}")
    private String soxPath;
    @Value("${sox.command}")
    private String soxCommand;
    @RequestMapping("/testhello")
    @ResponseBody
    public String test(String audioPath){
        List<String> filePathByPath = FileUtils.getFilePathByPath(audioPath);
        for (String path : filePathByPath){
            try {
                String s = soxCommand.replaceAll("inpath", path);
                String replacedCommand = s.replaceAll("outpath", path.substring(path.lastIndexOf("/")+1, path.lastIndexOf(".")));
                SoxUtils.doSox(soxPath, replacedCommand);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "success";
    }



}
