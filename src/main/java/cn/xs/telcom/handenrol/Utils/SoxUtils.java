package cn.xs.telcom.handenrol.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author kenny_peng
 * @created 2019/9/29 14:59
 */
public class SoxUtils {

    private static Logger logger = LoggerFactory.getLogger(SoxUtils.class);
    /**
     * 去静音和截取长度
     * @param soxPath
     * @param inputPath     处理音频的位置
     * @param outputPath    处理后音频保存位置
     * @param startTime     截去长度
     * @param duration      截取截去后的长度
     * @throws Exception
     */
    public static void doSoxProcess(String soxPath, String inputPath, String outputPath, String startTime, String duration) throws Exception {
        logger.info("sox命令：" + soxPath +" -t raw -c 1 -e signed-integer -b 16 -r 8000 "+ inputPath +" "+ outputPath +" silence 1 0.1 1% -1 0.1 1% trim "+ startTime +" "+ duration +" ");
        Process pr = Runtime.getRuntime().exec(soxPath +" -t raw -c 1 -e signed-integer -b 16 -r 8000 "+ inputPath +" "+ outputPath +" silence 1 0.1 1% -1 0.1 1% trim "+ startTime +" "+ duration +" ");
        BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        pr.waitFor();
        input.close();
    }


    public static void doSox(String soxPath, String command) throws Exception{
        logger.info(soxPath + command);
        Process pr = Runtime.getRuntime().exec(soxPath + command);
        BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        pr.waitFor();
        input.close();

    }
}
