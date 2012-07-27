package com.desandroid.framework.ada.listeners;

import com.desandroid.framework.ada.ObjectSet;

/***
 * Define the events fired by the ObjectSets.
 * @version 1.4.5
 * @author DesAndrOId
 */
public interface ObjectSetEventsListener {
	/**
	 * Occurs when the Fill process of ObjectSet finalize.
	 */
	void OnFillComplete(ObjectSet<?> pObjsetSet);
	
	/**
	 * Occurs when the Save process of ObjectSet finalize.
	 * @param pObjsetSet
	 */
	void OnSaveComplete(ObjectSet<?> pObjsetSet);
	
	/**
	 * Occurs when framework process fail.
	 * @param pObjcetSet
	 * @param pException
	 */
	void OnError(ObjectSet<?> pObjcetSet, Exception pException);
	
	/**
	 * Occurs during the save process.
	 * @param pObjsetSet
	 * @param pActualPosition
	 * @param pTotalNumOfPositions
	 */
	void OnSaveProgress(ObjectSet<?> pObjsetSet, int pActualPosition, int pTotalNumOfPositions);
}
