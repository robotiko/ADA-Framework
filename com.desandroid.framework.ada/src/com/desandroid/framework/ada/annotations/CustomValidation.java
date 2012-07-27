package com.desandroid.framework.ada.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define a Custom Validator.
 * @version 1.4.5
 * @author DesAndrOId
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomValidation {

	/**
	 * Define the error message when the validation is not ok. 
	 * @return
	 */
	public String message() default "* Required Field.";
	
	/**
	 * Define the error message resource string when the validation is not ok.
	 * @return
	 */
	public int messageResourceId() default 0;
	
	/**
	 * Define the custom property getter suffix, Example: getXXXX() => getMySufix()
	 * @return
	 */
	public String getterSuffix() default "";
	
	/**
	 * Define the custom property setter suffix, Example: setXXXX(Object pVar) => setMySufix(Object pVar)
	 * @return
	 */
	public String setterSuffix() default "";
	
	/**
	 * Define the custom DataBinder Class.
	 * @return
	 */
	public Class<?> validator();
}
