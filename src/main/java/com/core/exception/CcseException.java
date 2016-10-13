package com.core.exception;

public class CcseException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public CcseException(){};
	public CcseException(String message) {
        super(message);
    }
}
