package com.desandroid.framework.ada;

import com.desandroid.framework.ada.exceptions.AdaFrameworkException;
import android.app.Activity;
import android.util.Log;

/**
 * The class encapsulate the exceptions management.
 * @version 1.4.5
 * @author DesAndrOId
 */
class ExceptionsHelper {
	/**
	 * Manage the framework exceptions.
	 * @param pException
	 * @throws Exception
	 */
	public static void manageException(final ObjectSet<?> pObjcetSet, final Exception pException) throws AdaFrameworkException {
		manageException(pException.toString());
		
		if (pObjcetSet != null) {
			if (pObjcetSet.getObjectSetEventsListener() != null) {
				if (pObjcetSet.getContext() != null) {
					if (pObjcetSet.getContext().getContext() != null) {
						if (pObjcetSet.getContext().getContext() instanceof Activity) {
							((Activity)pObjcetSet.getContext().getContext()).runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									pObjcetSet.getObjectSetEventsListener().OnError(pObjcetSet, pException);
								}
							});
						}
					}
				}
			}
		}
		

		throw new AdaFrameworkException(pException);
	}
	
	/**
	 * Manage the framework exceptions.
	 * @param pException
	 * @throws Exception
	 */
	public static void manageException(String pException) {
		Log.e(DataUtils.DEFAULT_LOGS_TAG, pException.toString());
	}
	
	/**
	 * Manage the framework exceptions.
	 * @param pException
	 * @throws Exception
	 */
	public static void manageException(Exception pException) throws AdaFrameworkException {
		manageException(pException.getMessage());
		throw new AdaFrameworkException(pException);
	}
}
