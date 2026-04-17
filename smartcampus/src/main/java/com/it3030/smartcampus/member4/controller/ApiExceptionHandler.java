package com.it3030.smartcampus.member4.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.it3030.smartcampus.member2.exception.BookingConflictException;
import com.it3030.smartcampus.member4.dto.MessageResponse;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(BookingConflictException.class)
	public ResponseEntity<MessageResponse> handleBookingConflict(BookingConflictException exception) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse(exception.getMessage()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<MessageResponse> handleIllegalArgument(IllegalArgumentException exception) {
		String message = exception.getMessage() == null || exception.getMessage().isBlank()
				? "Invalid request"
				: exception.getMessage();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(message));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<MessageResponse> handleValidation(MethodArgumentNotValidException exception) {
		Optional<FieldError> firstFieldError = exception.getBindingResult().getFieldErrors().stream().findFirst();
		String message = firstFieldError
				.map(error -> error.getField() + " " + error.getDefaultMessage())
				.orElse("Validation failed");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(message));
	}
}
