package io.tronbot.dc

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.netflix.feign.EnableFeignClients
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.integration.annotation.MessageEndpoint
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.stereotype.Component

import groovy.util.logging.Log4j

@SpringBootApplication
@EnableBinding(Sink.class)
@EnableDiscoveryClient
@EnableFeignClients
class DataConciergeApp {

	static void main(String[] args) {
		SpringApplication.run(DataConciergeApp, args)
	}
}

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




@Entity
class Reservation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
		[
			'Josh',
			'Juergen',
			'Andrew',
			'Bridget',
			'Onsi',
			'Phil',
			'Stephane',
			'Cornelia'
		]
		.forEach{x -> reservationRepository.save(new Reservation(x))}
		reservationRepository.findAll().forEach{ x -> System.out.&println }
	}
}