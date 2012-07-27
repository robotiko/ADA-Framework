package com.desandroid.framework.ada.validators;

import java.lang.reflect.Field;
import com.desandroid.framework.ada.Entity;
import com.desandroid.framework.ada.annotations.RangeValidation;

/**
 * This class implement the logic of the Range Validations. 
 * @version 1.4.5
 * @author DesAndrOId
 */
public class RangeValidator extends Validator {

	@Override
	public Boolean Validate(Entity pEntity, Field pField, Object pAnnotation, Object pValue) {
		Boolean returnedValue = true;
		
		
		if (pValue != null) {
			if (pAnnotation != null) {
				if (pAnnotation instanceof RangeValidation) {
					if (isNumeric(pValue)) {
						int minValue = ((RangeValidation)pAnnotation).minValue();
						int maxValue = ((RangeValidation)pAnnotation).maxValue();
						int value = Integer.parseInt(pValue.toString());
						
						if (value >= minValue) {
							if (value > maxValue) {
								returnedValue = false;
							}
						} else {
							returnedValue = false;
						}
					}
				}
			}
		}
		
		return returnedValue;
	}
	
	private Boolean isNumeric(Object pValue) {
		try {
			Integer.parseInt(pValue.toString());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
