package com.desandroid.framework.ada.exceptions;

/**
 * Framework generic exception.
 * @version 1.4.5
 * @author DesAndrOId
 */
@SuppressWarnings("serial")
public class AdaFrameworkException extends Exception { 
	
	private Exception _innerException;
	private void setInnerException(Exception pException) {
		this._innerException = pException;
	}
	public Exception getInnerException() {
		return this._innerException;
	}
	
	private String _message = "";
	public String getMessage() {
		return _message;
	}
	
	
	public void setMessage(String _message) {
		this._message = _message;
	}
	
	public AdaFrameworkException(Exception pInnerException) {
		if (pInnerException != null)
			setMessage(pInnerException.getMessage());
		
		setInnerException(pInnerException);
	}
	
	public AdaFrameworkException(String pMessage) {
		super(pMessage);
		
		setMessage(pMessage);
	}
	public AdaFrameworkException(String pMessage, Exception pInnerException) {
		super(pMessage, pInnerException);
		
		setMessage(pMessage);
		setInnerException(pInnerException);
	}
}
