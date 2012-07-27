package com.desandroid.framework.ada;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.desandroid.framework.ada.annotations.CustomValidation;
import com.desandroid.framework.ada.annotations.Databinding;
import com.desandroid.framework.ada.annotations.RangeValidation;
import com.desandroid.framework.ada.annotations.RegularExpressionValidation;
import com.desandroid.framework.ada.annotations.RequiredFieldValidation;
import com.desandroid.framework.ada.annotations.TableField;

/**
 * Entity ObjectSet.
 * @version 1.4.5
 * @author DesAndrOId
 */
class ReflectionHelper {

	public static Method extractGetterMethod(Class<?> pObject, Field pField) {
		
		Method returnedValue = null;
		
		if (pField != null) {
			
			String getterMethodSuffix = "";
			
			TableField tableFieldAnnotation = pField.getAnnotation(TableField.class);
			if (tableFieldAnnotation != null) {
				if (tableFieldAnnotation.getterSuffix().trim() != "") {
					getterMethodSuffix = tableFieldAnnotation.getterSuffix(); 
				}
			} 

			if (getterMethodSuffix.equals("")) {
				Databinding dataBindAnnotation = pField.getAnnotation(Databinding.class);
				if (dataBindAnnotation != null) {
					if (!dataBindAnnotation.getterSuffix().trim().equals("")) {
						getterMethodSuffix = dataBindAnnotation.getterSuffix().trim();
					}
				} 
			}
			
			if (getterMethodSuffix.equals("")) {
				RequiredFieldValidation requiredFieldValidatorAnnotation = pField.getAnnotation(RequiredFieldValidation.class);
				if (requiredFieldValidatorAnnotation != null) {
					if (!requiredFieldValidatorAnnotation.getterSuffix().trim().equals("")) {
						getterMethodSuffix = requiredFieldValidatorAnnotation.getterSuffix().trim();
					}
				} 
			}
			
			if (getterMethodSuffix.equals("")) {
				RangeValidation rangeFieldValidatorAnnotation = pField.getAnnotation(RangeValidation.class);
				if (rangeFieldValidatorAnnotation != null) {
					if (!rangeFieldValidatorAnnotation.getterSuffix().trim().equals("")) {
						getterMethodSuffix = rangeFieldValidatorAnnotation.getterSuffix().trim();
					}
				} 
			}
			
			if (getterMethodSuffix.equals("")) {
				RegularExpressionValidation regularExpressionFieldValidatorAnnotation = pField.getAnnotation(RegularExpressionValidation.class);
				if (regularExpressionFieldValidatorAnnotation != null) {
					if (!regularExpressionFieldValidatorAnnotation.getterSuffix().trim().equals("")) {
						getterMethodSuffix = regularExpressionFieldValidatorAnnotation.getterSuffix().trim();
					}
				} 
			}
			
			
			if (getterMethodSuffix.equals("")) {
				CustomValidation customValidatorAnnotation = pField.getAnnotation(CustomValidation.class);
				if (customValidatorAnnotation != null) {
					if (!customValidatorAnnotation.getterSuffix().trim().equals("")) {
						getterMethodSuffix = customValidatorAnnotation.getterSuffix().trim();
					}
				} 
			}
			
			if (getterMethodSuffix.equals("")) {
				getterMethodSuffix = DataUtils.capitalize(pField.getName());
			}
			
			try {
				returnedValue = pObject.getMethod(String.format("get%s", getterMethodSuffix), (Class[])null);
			} catch (Exception e) {
				returnedValue = null;
			}
		}
		
		return returnedValue;
	}
	
	public static Method extractSetterMethod(Class<?> pObject, Field pField) {
		
		Method returnedValue = null;
		
		if (pField != null) {
			
			String setterMethodSuffix = "";
			
			TableField tableFieldAnnotation = pField.getAnnotation(TableField.class);
			if (tableFieldAnnotation != null) {
				if (tableFieldAnnotation.setterSuffix().trim() != "") {
					setterMethodSuffix = tableFieldAnnotation.setterSuffix(); 
				}
			} 

			if (setterMethodSuffix.equals("")) {
				Databinding dataBindAnnotation = pField.getAnnotation(Databinding.class);
				if (dataBindAnnotation != null) {
					if (!dataBindAnnotation.setterSuffix().trim().equals("")) {
						setterMethodSuffix = dataBindAnnotation.setterSuffix().trim();
					}
				} 
			}
			
			if (setterMethodSuffix.equals("")) {
				RequiredFieldValidation requiredFieldValidatorAnnotation = pField.getAnnotation(RequiredFieldValidation.class);
				if (requiredFieldValidatorAnnotation != null) {
					if (!requiredFieldValidatorAnnotation.setterSuffix().trim().equals("")) {
						setterMethodSuffix = requiredFieldValidatorAnnotation.setterSuffix().trim();
					}
				} 
			}
			
			if (setterMethodSuffix.equals("")) {
				RangeValidation rangeFieldValidatorAnnotation = pField.getAnnotation(RangeValidation.class);
				if (rangeFieldValidatorAnnotation != null) {
					if (!rangeFieldValidatorAnnotation.getterSuffix().trim().equals("")) {
						setterMethodSuffix = rangeFieldValidatorAnnotation.getterSuffix().trim();
					}
				} 
			}
			
			if (setterMethodSuffix.equals("")) {
				RegularExpressionValidation regularExpressionFieldValidatorAnnotation = pField.getAnnotation(RegularExpressionValidation.class);
				if (regularExpressionFieldValidatorAnnotation != null) {
					if (!regularExpressionFieldValidatorAnnotation.getterSuffix().trim().equals("")) {
						setterMethodSuffix = regularExpressionFieldValidatorAnnotation.getterSuffix().trim();
					}
				} 
			}
			
			if (setterMethodSuffix.equals("")) {
				CustomValidation customValidatorAnnotation = pField.getAnnotation(CustomValidation.class);
				if (customValidatorAnnotation != null) {
					if (!customValidatorAnnotation.setterSuffix().trim().equals("")) {
						setterMethodSuffix = customValidatorAnnotation.setterSuffix().trim();
					}
				} 
			}
			
			if (setterMethodSuffix.equals("")) {
				setterMethodSuffix = DataUtils.capitalize(pField.getName());
			}
			
			try {
				returnedValue = pObject.getMethod(String.format("set%s", setterMethodSuffix), pField.getType());
			} catch (Exception e) {
				returnedValue = null;
			}
		}
		
		return returnedValue;
	}
}
