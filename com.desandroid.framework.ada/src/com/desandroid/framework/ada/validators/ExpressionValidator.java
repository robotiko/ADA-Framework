package com.desandroid.framework.ada.validators;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.desandroid.framework.ada.Entity;
import com.desandroid.framework.ada.annotations.RegularExpressionValidation;

/**
 * This class implement the logic of the Regular Expression Validations. 
 * @version 1.4.5
 * @author DesAndrOId
 */
public class ExpressionValidator extends Validator {

	@Override
	public Boolean Validate(Entity pEntity, Field pField, Object pAnnotation, Object pValue) {
		Boolean returnedValue = true;
		
		if (pValue != null) {
			if (pAnnotation != null) {
				if (pAnnotation instanceof RegularExpressionValidation) {
					String regularExpression = ((RegularExpressionValidation)pAnnotation).expression();
					
					if (regularExpression != null && !regularExpression.trim().equals("")) {
						
						Pattern pattern = Pattern.compile(regularExpression);
						if (pattern != null) {
							String value = "";
							if (pValue instanceof String) {
								value = (String)pValue;
							} else {
								value = pValue.toString();
							}
							
							Matcher matcher = pattern.matcher(value);
							if (!matcher.matches()) {
								returnedValue = false;
							}
						}
					}
				}
			}
		}
		
		return returnedValue;
	}
}
