package io.tronbot.dc.domain

import javax.persistence.OneToMany

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Mar 22, 2017
 */
class Question {
	@JsonIgnore
	Long id
	String asker
	String subject
	String hint
	@OneToMany
	List<Answer> answers
}

