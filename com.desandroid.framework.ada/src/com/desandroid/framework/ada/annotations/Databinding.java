package com.desandroid.framework.ada.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.desandroid.framework.ada.DataBinder;
import com.desandroid.framework.ada.DataParser;

/**
 * Annotation to define data binding between the Entity object and the UI View control.
 * @version 1.4.5
 * @author DesAndrOId
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Databinding {
	/**
	 * Define the View control ID linked to the Entity property.
	 * @return
	 */
	public int ViewId();
	
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
	public Class<?> binder() default DataBinder.class;
	
	
	/**
	 * Define the custom dapa parser class.
	 * @return
	 */
	public Class<?> parser()  default DataParser.class;
}
