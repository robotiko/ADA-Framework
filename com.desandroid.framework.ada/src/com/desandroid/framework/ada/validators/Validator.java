package com.desandroid.framework.ada.validators;

import java.lang.reflect.Field;

import com.desandroid.framework.ada.Entity;

/**
 * Base class for all validations.
 * @version 1.4.5
 * @author DesAndrOId
 */
public class Validator {
	
	/**
	 * Execute the validation process.
	 * @return Validation process Result.
	 * @param pValue Value to validate. 
	 * @param pEntity Instance of Entity.
	 * @param pField Entity field.
	 * @param pAnnotation Instance of Entity Property Annotation.
	 * @return
	 */
	public Boolean Validate(Entity pEntity, Field pField, Object pAnnotation, Object pValue) {
		return null;
	}
}
