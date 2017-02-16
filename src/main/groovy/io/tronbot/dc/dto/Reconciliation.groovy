package io.tronbot.dc.dto

import org.springframework.http.ResponseEntity

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 14, 2017
 */
public class Reconciliation<T> {

	public enum Status{
		ACCURATE_MATCH, CONFIDENT_MATCH, POSSIBLE_MATCHING, NOT_FOUND
	}

	private final Status status
	private final T result

	private Reconciliation(Status status, T result){
		this.status = status
		this.result = result
	}
	/**
	 * Matching found with original input 
	 * @param result
	 * @return
	 */
	public static ResponseEntity<Reconciliation<T>> accurate(T result) {
		return ResponseEntity.ok(new Reconciliation(Status.ACCURATE_MATCH, result))
	}
	/**
	 * Matching found with generic type + address
	 * @param result
	 * @return
	 */
	public static ResponseEntity<Reconciliation<T>> confident(T result) {
		return ResponseEntity.ok(new Reconciliation(Status.CONFIDENT_MATCH, result))
	}
	/**
	 * Matching found with address only
	 * @param result
	 * @return
	 */
	public static ResponseEntity<Reconciliation<T>> possible(T result) {
		return ResponseEntity.ok(new Reconciliation(Status.POSSIBLE_MATCHING, result))
	}


	/**
	 * No Matching Found
	 * @param result
	 * @return
	 */
	public static ResponseEntity<Reconciliation<?>> notFound() {
		return ResponseEntity.ok(new Reconciliation(Status.NOT_FOUND, null))
	}

	public Status getStatus() {
		return status;
	}

	public T getResult() {
		return result;
	}
}
