package com.desandroid.framework.ada;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Define the internal structure of validation.
 * @version 1.4.5
 * @author DesAndrOId
 */
final class Validation {

	public Object Annotation;
	
	/**
	 * Define the Entity Field.
	 */
	public Field EntityField;
	
	/**
	 * Define the error message;
	 */
	public String message;
	
	/**
	 * Define the Getter Method of the Entity.
	 */
	public Method getterMethod;
	
	/**
	 * Define the custom binder class type.
	 */
	public Class<?> Validator;
	
	
}
