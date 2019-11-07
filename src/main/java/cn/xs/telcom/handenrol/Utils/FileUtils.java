package cn.xs.telcom.handenrol.Utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 保存base64到文件，保存位置为：指定路径+当前日期+文件名(用户id.txt)
 * @author kenny_peng
 * @created 2019/7/25 11:04
 */
public class FileUtils {

    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);


    public static String formateDate(String formate){
        SimpleDateFormat sdf = new SimpleDateFormat(formate);
        String date = sdf.format(new Date());
        return date;
    }

    /**
     * 返回执行日期的指定格式
     * @param formate
     * @param date
     * @return
     */
    public static String formateDate(String formate, Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(formate);
        String dateString = sdf.format(date);
        return dateString;
    }

    public static Date formateDate(String formate, String date){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(formate);
            Date dateTime = sdf.parse(date);
            return dateTime;
        } catch (ParseException e) {
           logger.error("转换日期异常", e);
           return null;
        }
    }

    /**
     * 传入时间的前几天或后几天
     * @param date 传入日期date
     * @param index 为正表示当前时间加天数，为负表示当前时间减天数
     * @return String
     */
    public static Date addSomeDay(Date date, int index){
        Date dateTime = null;
        try {
            Calendar begin=Calendar.getInstance();
            begin.setTime(date);
            begin.add(Calendar.DAY_OF_MONTH,index);
            dateTime = begin.getTime();
        } catch (Exception e) {
            logger.error("转换日期错误", e);
        }
        return dateTime;
    }
    /**
     * 遍历路径path下的所有文件
     * @param file
     */
    public static List<File> getAllFiles(File file){
        List<File> listFiles = new ArrayList<>();
        if (file.exists()) {
            //判断文件是否是文件夹，如果是，开始递归
            if (!file.isFile()) {
                File f[] = file.listFiles();
                for (File file2 : f) {
                    List<File> allFiles = getAllFiles(file2);
                    listFiles.addAll(allFiles);
                }
            }else {
                listFiles.add(file);
            }
        }
        return listFiles;
    }

    @SuppressWarnings("unused")
	private void stringToFile(String s){
        FileWriter fw = null;
        File f = new File("f:\\cms\\.txt");
        try {
            if(!f.exists()){
                f.createNewFile();
            }
            fw = new FileWriter(f);
            BufferedWriter out = new BufferedWriter(fw);
            out.write(s, 0, s.length()-1);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getFilePathByPath(String path){
        List<String> filePath = new ArrayList<>();
        File file = new File(path);
        if (file.exists()) {
            //判断文件是否是文件夹，如果是，开始递归
            if (!file.isFile()) {
                File f[] = file.listFiles();
                for (File file2 : f) {
                    List<String> filePathList = getFilePathByPath(file2.getPath());
                    filePath.addAll(filePathList);
                }
            }else {
                filePath.add(file.getPath());
                logger.info("========="+file.getPath());
            }
        }else {
            logger.error("不存在该路径" + path);
            return null;
        }
        logger.info("路径：" + path + "下文件大小为：" + filePath.size());
        return filePath;
    }


}
