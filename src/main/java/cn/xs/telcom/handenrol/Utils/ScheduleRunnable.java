package cn.xs.telcom.handenrol.Utils;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 执行定时任务
 * @author kenny_peng
 *
 */
public class ScheduleRunnable implements Runnable {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private Object target;
	private Method method;
	private String params;

	ScheduleRunnable(String beanName, String methodName, String params)
			throws NoSuchMethodException, SecurityException {
		this.target = SpringContextUtils.getBean(beanName);
		this.params = params;

		//isNotBlank：判断某字符串是否不为空且长度不为0且不由空白符(whitespace)构成，
		if (StringUtils.isNotBlank(params)) {
			this.method = target.getClass().getDeclaredMethod(methodName, String.class);
		} else {
			this.method = target.getClass().getDeclaredMethod(methodName);
		}
	}

	@Override
	public void run() {
		try {
			ReflectionUtils.makeAccessible(method);
			if (StringUtils.isNotBlank(params)) {
				method.invoke(target, params);
			} else {
				method.invoke(target);
			}
		} catch (Exception e) {
			log.error("执行定时任务失败",e);
		}
	}
}
