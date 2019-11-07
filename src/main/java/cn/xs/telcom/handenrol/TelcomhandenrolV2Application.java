package cn.xs.telcom.handenrol;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.xs.telcom.handenrol.dao")
public class TelcomhandenrolV2Application {

	public static void main(String[] args) {
		SpringApplication.run(TelcomhandenrolV2Application.class, args);
	}

}
