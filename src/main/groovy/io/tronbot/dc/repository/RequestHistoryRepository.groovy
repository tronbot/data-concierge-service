package io.tronbot.dc.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

import io.tronbot.dc.domain.RequestHistory


/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
@Repository
interface RequestHistoryRepository extends JpaRepository<RequestHistory, String>{
}
