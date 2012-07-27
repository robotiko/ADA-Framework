package com.desandroid.framework.ada;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * DataBinding Structure.
 * @version 1.4.5
 * @author DesAndrOId
 */
public class DataBinding {
	/**
	 * Define the Entity Field.
	 */
	public Field EntityField;
	
	/**
	 * Define the Getter Method of the Entity.
	 */
	public Method getterMethod;
	/**
	 * Define the Setter Method of the Entity.
	 */
	public Method setterMethod;
	/**
	 * Define the View Id into the UI layer.
	 */
	public int ViewId;
	
	/**
	 * Define the custom binder class type.
	 */
	public Class<?> Binder;
	
	/**
	 * Define the custom data parser class type.
	 */
	public Class<?> Parser;
	
	/**
	 * Default constructor of the class.
	 */
	public DataBinding() { }
	
	/**
	 * Secondary constructor of the class.
	 * @param pEntityField
	 * @param pViewId
	 */
	public DataBinding(Field pEntityField, int pViewId) {
		this.EntityField = pEntityField;
		this.ViewId = pViewId;
	}
	/**
	 * Secondary constructor of the class.
	 * @param pEntityField
	 * @param pViewId
	 * @param pGetterMethod
	 */
	public DataBinding(Field pEntityField, int pViewId, Method pGetterMethod) {
		this.EntityField = pEntityField;
		this.ViewId = pViewId;
		this.getterMethod = pGetterMethod;
	}
}
