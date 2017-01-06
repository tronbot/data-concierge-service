package io.tronbot

import java.util.stream.Stream

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.integration.annotation.MessageEndpoint
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

import groovy.util.logging.Log4j

@EnableBinding(Sink.class)
@EnableDiscoveryClient
@SpringBootApplication
class ReservationServiceApplication {

	static void main(String[] args) {
		SpringApplication.run(ReservationServiceApplication, args)
	}
}

//interface ReservationChannels{
//	@Input
//	SubscribableChannel input()
//}

@MessageEndpoint
class ReservationProcessor{
	private final ReservationRepository reservationRepository

	public ReservationProcessor(ReservationRepository reservationRepository) {
		this.reservationRepository = reservationRepository
	}

	@ServiceActivator(inputChannel='input')
	public void onNewReservation(String reservationName){
		this.reservationRepository.save(new Reservation(reservationName))
	}
}

@RestController
@RefreshScope
class GreetingRestController{
	private final String value

	@Autowired
	public GreetingRestController(@Value('${message}') String value) {
		this.value=value
	}

	@GetMapping('/message')
	public String greet(){
		return this.value
	}
}

@Entity
class Reservation {
	@Id
	@GeneratedValue
	private Long id

	private String reservationName


	public Reservation() {
	}

	public Reservation(String reservationName) {
		this.reservationName = reservationName
	}

	public Long getId() {
		return id
	}

	public String getReservationName() {
		return reservationName
	}
}

@RepositoryRestResource
interface ReservationRepository extends JpaRepository<Reservation, Long>{
}

@Component
@Log4j
class SampleDataCLR implements CommandLineRunner{
	private final ReservationRepository reservationRepository

	@Autowired
	public SampleDataCLR(ReservationRepository reservationRepository) {
		this.reservationRepository = reservationRepository
	}

	@Override
	public void run(String... args) throws Exception {
		Stream.of('Josh', 'Juergen','Andrew','Bridget','Onsi','Phil', 'Stephane', 'Cornelia')
				.forEach{x -> reservationRepository.save(new Reservation(x))}
		reservationRepository.findAll().forEach{ x -> System.out.&println }
	}
}