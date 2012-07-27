package com.desandroid.framework.ada.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation to define information of the DataBase table.
 * @version 1.4.5
 * @author DesAndrOId
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	/**
	 * Name of the DataBase Table.
	 * @return String
	 * @author DesAndrOId
	 */
	public String name() default "";
	
	/**
	 * Define if the table creation process generate indexes.
	 * @return
	 */
	public boolean useIndexes() default true;
}
