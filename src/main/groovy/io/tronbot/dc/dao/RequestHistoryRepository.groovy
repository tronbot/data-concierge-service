package io.tronbot.dc.dao

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

import io.tronbot.dc.domain.RequestHistory


/**
 * @author <a href='mailto:juanyong.zhang@gmail.com'>Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
interface RequestHistoryRepository extends JpaRepository<RequestHistory, Long>{
	@Query('SELECT req.request FROM RequestHistory req')
	Set<String> findAllRequests()

	//	@Query('SELECT req FROM RequestHistory req WHERE req.request = :request ORDER BY req.timestamp DESC')
	//	RequestHistory findOneByRequest(@Param('request') final String request)
	List<RequestHistory> findByRequest(final String request)

	void deleteByRequest(String request)
}
