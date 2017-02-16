package io.tronbot.dc;

import org.apache.commons.lang3.builder.ToStringBuilder
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.Output
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.cloud.stream.messaging.Source
import org.springframework.http.ResponseEntity
import org.springframework.integration.annotation.Gateway
import org.springframework.integration.annotation.IntegrationComponentScan
import org.springframework.integration.annotation.MessageEndpoint
import org.springframework.integration.annotation.MessagingGateway
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.messaging.MessageChannel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import groovy.util.logging.Log4j
import io.tronbot.dc.dao.RequestHistoryRepository
import io.tronbot.dc.domain.Business
import io.tronbot.dc.dto.Reconciliation

@SpringBootApplication
@IntegrationComponentScan
@EnableFeignClients
@EnableDiscoveryClient
@EnableBinding([Source.class,Sink.class,DataConciergeSource.class, DataConciergeSink.class])
public class DataConciergeServiceApp {

	public static void main(String[] args) {
		SpringApplication.run(DataConciergeServiceApp.class, args)
	}
}

@RestController
@RequestMapping('/kafka')
@RefreshScope
@Log4j
class KafkaController {

	private final Emitter emitter

	KafkaController(Emitter emitter){
		this.emitter = emitter
	}

	@GetMapping('/message')
	public ResponseEntity<Reconciliation> message(@RequestParam('q') String msg){
		Business business = new Business()
		business.setName(msg);
		emitter.saveBusiness(business)
		return Reconciliation.ok("Processing : ${msg}")
	}
}

interface DataConciergeSource{
	String SAVE_BUSINESS = 'SAVE_BUSINESS_OUTPUT'
	String SAVE_REQHISTORY= 'SAVE_REQHISTORY_OUTPUT'

	@Output(DataConciergeSource.SAVE_BUSINESS)
	MessageChannel saveBusiness()

	@Output(DataConciergeSource.SAVE_REQHISTORY)
	MessageChannel saveRequestHistory()
}

interface DataConciergeSink{
	String ON_SAVE_BUSINESS = 'SAVE_BUSINESS_INPUT'
	String ON_SAVE_REQHISTORY= 'SAVE_REQHISTORY_INPUT'

	@Input(DataConciergeSink.ON_SAVE_BUSINESS)
	MessageChannel onSaveBusiness()

	@Input(DataConciergeSink.ON_SAVE_REQHISTORY)
	MessageChannel onSaveRequestHistory()
}


@MessagingGateway
interface Emitter{
	@Gateway(requestChannel = Source.OUTPUT)
	void saveBusiness(Business business)
}


@MessageEndpoint
@Log4j
class Receiver  {

	private final RequestHistoryRepository requestHistoryRepository

	public Receiver(RequestHistoryRepository requestHistoryRepository) {
		this.requestHistoryRepository = requestHistoryRepository
	}
	@ServiceActivator(inputChannel=Sink.INPUT)
	public void onSaveBusiness(Business business){
		log.debug ToStringBuilder.reflectionToString(business)
	}
}
