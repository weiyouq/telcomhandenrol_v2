package cn.xs.telcom.handenrol.Utils;

import javax.xml.ws.http.HTTPException;
import java.io.*;
import java.net.*;

public class HttpUtils {

	private static String charset = "utf-8";
	private static String proxyHost = null;
	private static Integer proxyPort = null;

	/**
	 * Do POST request
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String url, String params) throws Exception {
		URL localURL = new URL(url);
		URLConnection connection = openConnection(localURL);
		HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
		httpURLConnection.setDoOutput(true);
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setRequestProperty("Accept-Charset", charset);
//		httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		httpURLConnection.setRequestProperty("Content-Type", "application/json");
		httpURLConnection.setRequestProperty("Content-Length", String.valueOf(params.length()));
		OutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		StringBuffer resultBuffer = new StringBuffer();
		String tempLine = null;
		try {
			outputStream = httpURLConnection.getOutputStream();
			outputStreamWriter = new OutputStreamWriter(outputStream);
			outputStreamWriter.write(params);
			outputStreamWriter.flush();
			//响应失败
			if (httpURLConnection.getResponseCode() >= 300) {
				throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
			}
			//接收响应流
			inputStream = httpURLConnection.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream);
			reader = new BufferedReader(inputStreamReader);
			while ((tempLine = reader.readLine()) != null) {
				resultBuffer.append(tempLine);
			}
		} finally {
			if (outputStreamWriter != null)
				outputStreamWriter.close();
			if (outputStream != null)
				outputStream.close();
			if (reader != null)
				reader.close();
			if (inputStreamReader != null)
				inputStreamReader.close();
			if (inputStream != null)
				inputStream.close();
		}
		return resultBuffer.toString();
	}

	/**
	 * HTTP GET请求
	 * @param requestUrl	请求地址
	 */
	public static String doGet(String requestUrl) throws Exception {
		HttpURLConnection connection = null;
		InputStream is = null;
		BufferedReader br = null;
		String result = null;
		try {
			/** 创建远程url连接对象 */
			URL url = new URL(requestUrl);
			/** 通过远程url对象打开一个连接，强制转换为HttpUrlConnection类型 */
			connection = (HttpURLConnection) url.openConnection();
			/** 设置连接方式：GET */
			connection.setRequestMethod("GET");
			/** 设置连接主机服务器超时时间：15000毫秒 */
			connection.setConnectTimeout(15000);
			/** 设置读取远程返回的数据时间：60000毫秒 */
			connection.setReadTimeout(60000);
			/** 设置通用的请求属性 */
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 设置传入参数的格式:请求参数应该是 name1=value1&name2=value2 的形式
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			/** 发送GET方式请求，使用connet方法建立和远程资源之间的实际连接即可 */
			connection.connect();

			/*-------------------------->*/
			/** 获取所有相应头字段 */
			/*Map<String, List<String>> map = connection.getHeaderFields();
			 *//** 遍历响应头字段 *//*
			for (String key : map.keySet()) {
				System.out.println(key + "---------->" + map.get(key));
			}*/
			/* <-------------------------- */

			int returnCode = connection.getResponseCode();
			/** 请求成功：返回码为200 */
			if (returnCode == 200) {
				/** 通过connection连接，获取输入流 */
				is = connection.getInputStream();
				/** 封装输入流is，并指定字符集 */
				br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				/** 存放数据 */
				StringBuffer sbf = new StringBuffer();
				String line = null;
				while ((line = br.readLine()) != null) {
					sbf.append(line);
					sbf.append("\r\n");
				}
				result = sbf.toString();
			}else {
				throw new HTTPException(returnCode);
			}
		} finally {
			/** 关闭资源 */
			try {
				if (null != br)
					br.close();
				if (null != is)
					is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			/** 关闭远程连接 */
			// 断开连接，最好写上，disconnect是在底层tcp socket链接空闲时才切断。如果正在被其他线程使用就不切断。
			// 固定多线程的话，如果不disconnect，链接会增多，直到收发不出信息。写上disconnect后正常一些
			connection.disconnect();
		}
		return result;
	}

	private static URLConnection openConnection(URL localURL) throws IOException {
		URLConnection connection;
		if (proxyHost != null && proxyPort != null) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
			connection = localURL.openConnection(proxy);
		} else {
			connection = localURL.openConnection();
		}
		return connection;
	}
	
	
}
