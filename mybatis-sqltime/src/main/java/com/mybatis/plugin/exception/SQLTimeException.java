package com.mybatis.plugin.exception;

public class SQLTimeException extends RuntimeException{
	  private static final long serialVersionUID = 1L;

	    public SQLTimeException(String message) {
	        super(message);
	    }

	    public SQLTimeException(Throwable throwable) {
	        super(throwable);
	    }

	    public SQLTimeException(String message, Throwable throwable) {
	        super(message, throwable);
	    }
}
