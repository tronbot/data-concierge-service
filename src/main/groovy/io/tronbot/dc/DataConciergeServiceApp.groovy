package io.tronbot.dc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.cloud.stream.messaging.Source
import org.springframework.integration.annotation.IntegrationComponentScan

@SpringBootApplication
@IntegrationComponentScan
@EnableFeignClients
@EnableDiscoveryClient
@EnableBinding([Sink.class, Source.class/*, DataConciergeSource.class, DataConciergeSink.class*/])
public class DataConciergeServiceApp {

	public static void main(String[] args) {
		SpringApplication.run(DataConciergeServiceApp.class, args)
	}
}