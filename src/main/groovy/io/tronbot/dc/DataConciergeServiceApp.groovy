package io.tronbot.dc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Processor

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableBinding(Processor.class)
public class DataConciergeServiceApp {

	public static void main(String[] args) {
		SpringApplication.run(DataConciergeServiceApp.class, args);
	}
}
