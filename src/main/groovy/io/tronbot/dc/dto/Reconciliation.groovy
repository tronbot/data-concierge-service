package io.tronbot.dc.dto

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */

public class Reconciliation<T>{
	T entity
	Date refreshDate
	public Reconciliation(T entity, Date refreshDate){
		this.entity = entity
		this.refreshDate = refreshDate
	}
	public Reconciliation(T entity){
		this.entity = entity
		this.refreshDate = new Date()
	}
}

