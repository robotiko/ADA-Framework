package com.desandroid.framework.ada.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define extended ObjectSet configuration.
 * @version 1.4.5
 * @author DesAndrOId
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ObjectSetConfiguration {

	/**
	 * Define if the ObjectSet does not create table into DataBase.
	 * @return
	 */
	public boolean virtual() default false;
}
