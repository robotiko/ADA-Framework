package com.desandroid.framework.ada.validators;

import java.lang.reflect.Field;

import com.desandroid.framework.ada.Entity;


/**
 * This class implement the logic to validate the required fields validations. 
 * @version 1.4.5
 * @author DesAndrOId
 */
public final class RequiredValidator extends Validator {

	@Override
	public Boolean Validate(Entity pEntity, Field pField, Object pAnnotation, Object pValue) {
		Boolean returnedValue = true;
		
		
		if (pValue == null) {
			returnedValue = false;
		} else {
			if (pValue instanceof String) {
				if (((String)pValue).trim().equals("")) {
					returnedValue = false;
				} 
			}
		}
		
		return returnedValue;
	}
}
