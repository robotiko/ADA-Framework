package com.desandroid.framework.ada;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Internal class dedicated to Database Schema management and Database connections management.
 * @version 1.4.5
 * @author DesAndrOId
 *
 */
class DataBaseHelper extends SQLiteOpenHelper {
	private ObjectContext context;
	private void setContext(ObjectContext pContext) {
		this.context = pContext;
	}
	private ObjectContext getContext() {
		return  this.context;
	}
	
	/**
	 * Principal constructor of the class.
	 * @param pContext
	 */
	public DataBaseHelper(ObjectContext pContext) {
		super(pContext.getContext(), pContext.getDatabaseFileName(), null, pContext.getDatabaseVersion());
		setContext(pContext);
	}

	@Override
	public void onCreate(SQLiteDatabase pDataBase) {
		
		try {
			
			getContext().onPreCreate(pDataBase);
			getContext().onCreateDataBase(pDataBase);
			getContext().onPostCreate(pDataBase);
			
			getContext().onPopulate(pDataBase);
			
		} catch (Exception e) {
			ExceptionsHelper.manageException(e.toString());
		} finally {
			
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase pDataBase, int pOldVersion, int pNewVersion) {
		
		
		try {
			
			getContext().onPreUpdate(pDataBase, pOldVersion, pNewVersion);
			getContext().onUpdateDataBase(pDataBase, pOldVersion, pNewVersion);
			getContext().onPostUpdate(pDataBase, pOldVersion, pNewVersion);
			
			getContext().onPopulate(pDataBase);
			
		} catch (Exception e) {
			ExceptionsHelper.manageException(e.toString());
		} finally {
		}
	}
}
