package com.example.demo;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class DemoApplication  {
	//環境変数　反映確認用
	@Autowired
	private  Environment environment;
	@PostConstruct
	public void init(){
		System.out.println("main/ MYSQLPASSWORD:"+environment.getProperty("MYSQLPASSWORD")  );
		System.out.println("main/ apiKey:"+environment.getProperty("googleMapsApiKey")  );
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
