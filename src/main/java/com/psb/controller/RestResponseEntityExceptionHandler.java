package com.psb.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.psb.exception.SpotifyClientException;
import com.psb.exception.SpotifyClientUnauthorizedException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

//	@ExceptionHandler(value = { SpotifyClientUnauthorizedException.class })
//	protected ResponseEntity<Object> handleWebClientConflict(SpotifyClientUnauthorizedException ex,
//			WebRequest request) {
//		String bodyOfResponse = "Error calling spotify api. " + ex.getMessage();
//		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
//	}
//
//	@ExceptionHandler(value = { SpotifyClientException.class })
//	protected ResponseEntity<Object> handleSpotifyException(SpotifyClientException ex, WebRequest request) {
//		String bodyOfResponse = "Error calling spotify api. " + ex.getMessage();
//		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE, request);
//	}

	@ExceptionHandler(value = { SpotifyClientUnauthorizedException.class })
	public ResponseEntity<Object> handleUnauthorizedException(SpotifyClientUnauthorizedException e) {
		ExceptionResponse res = new ExceptionResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
		return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(value = { SpotifyClientException.class })
	public ResponseEntity<Object> handleClientException(SpotifyClientException e) {
		ExceptionResponse res = new ExceptionResponse(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
		return new ResponseEntity<>(res, HttpStatus.SERVICE_UNAVAILABLE);
	}
}
