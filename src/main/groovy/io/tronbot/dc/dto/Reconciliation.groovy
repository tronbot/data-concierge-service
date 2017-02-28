package io.tronbot.dc.dto

import org.springframework.http.ResponseEntity

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 14, 2017
 */
public class Reconciliation<T> {

	public enum Status{
		OK, NOT_FOUND
	}

	private final Status status
	private final T result

	private Reconciliation(Status status, T result){
		this.status = status
		this.result = result
	}
	
	public static ResponseEntity<Reconciliation<T>> resp(T result) {
		if(!result){
			return ResponseEntity.ok(new Reconciliation(Status.NOT_FOUND, null))
		}else{
			ResponseEntity.ok(new Reconciliation(Status.OK, result))
		}
	}

	public Status getStatus() {
		return status;
	}

	public T getResult() {
		return result;
	}
}
