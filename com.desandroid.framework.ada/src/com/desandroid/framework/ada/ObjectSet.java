package com.desandroid.framework.ada;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.desandroid.framework.ada.annotations.Table;
import com.desandroid.framework.ada.annotations.TableField;
import com.desandroid.framework.ada.exceptions.AdaFrameworkException;
import com.desandroid.framework.ada.listeners.ObjectSetEventsListener;

/**
 * Entity ObjectSet.
 * @version 1.4.5
 * @author DesAndrOId
 */
@SuppressWarnings("serial")
public final class ObjectSet<T extends Entity> extends ArrayList<T> implements List<T> {	
	private ObjectSet<Entity> ownerEntityType = null;
	private Class<?> managedType;
	private ObjectContext dataContext;
	private List<DataMapping> dataMappings = new ArrayList<DataMapping>();
	private String dataBaseTableName = "";
	private String[] dataBaseTableFields = null;
	private String dataBaseUniqueTableFields = "";
	private ArrayAdapter<T> dataAdapter;
	private boolean deleteOnCascade = true;
	private Dictionary<Class<?>, ObjectSet<Entity>> inheritedObjectSets = new Hashtable<Class<?>, ObjectSet<Entity>>();
	private ObjectSetEventsListener objectSetEventsListener;
	private boolean dataBaseUseIndexes = DataUtils.DATABASE_USE_INDEXES;
	
	ObjectContext getContext() {
		return this.dataContext;
	}
	
	ObjectSet<Entity> getOwnerEntityType() {
		return this.ownerEntityType;
	}
	
	List<DataMapping> getDataMappings() {
		return this.dataMappings;
	}
	
	String[] getDataBaseTableFields() {
		return this.dataBaseTableFields;
	}
	
	/***
	 * Set the ObjectSet events listener.
	 * @param pListener
	 */
	public void setObjectSetEventsListener(ObjectSetEventsListener pListener) {
		this.objectSetEventsListener = pListener;
	}
	
	/**
	 * Get the ObjectSetEventsListener instance.
	 * @return
	 */
	public ObjectSetEventsListener getObjectSetEventsListener() {
		return this.objectSetEventsListener;
	}
	
	/**
	 * @return If the delete commands use cascade method.
	 */
	public boolean isDeleteOnCascade() {
		return deleteOnCascade;
	}

	/**
	 * Set if the delete actions use cascade method.
	 * @param deleteOnCascade
	 */
	public void setDeleteOnCascade(boolean deleteOnCascade) {
		this.deleteOnCascade = deleteOnCascade;
	}
	
	/**
	 * @return True if the managed entity contains other entities.
	 */
	protected final boolean ContainInheritedEntities() {
		boolean returnedValue = false;
		
		if (inheritedObjectSets != null) {
			if (inheritedObjectSets.size() > 0) {
				returnedValue = true;
			}
		}
		
		return returnedValue;
	}
	
	/**
	 * @return The list with the inherited fields.
	 */
	protected final Enumeration<ObjectSet<Entity>> getInheritedObjectSets () {
		return this.inheritedObjectSets.elements();
	}
	
	/**
	 * Set the Data Adapter to notify DataSet Changes.
	 * @param pDataAdapter
	 */
	public void setAdapter(ArrayAdapter<T> pDataAdapter) {
		this.dataAdapter = pDataAdapter;
		
		initializeDataAdapter();
	}
	
	/**
	 * @return Class object of managed type for the Object Set.
	 */
	public Class<?> getManagedType() {
		return managedType;
	}
	
	/**
	 * @return DataBase table name.
	 */
	public String getDataBaseTableName() {
		return this.dataBaseTableName;
	}
	
	public List<String> getDataBaseTableIndexes() {
		return this.generateDataBaseTableIndexesScript(this.dataMappings);
	}
	/**
	 * @return DataBase table creation Script.
	 */
	public String getDataBaseTableScript() {
		return generateDataBaseTableScript(this.dataMappings);
	}
	
	/**
	 * Gets the database field name associated with the entity property.
	 * @param pFieldName Entity property name. 
	 * @return Database table field name.
	 * @throws AdaFrameworkException
	 */
	public String getDataTableFieldName(String pFieldName) throws AdaFrameworkException {
		String returnedValue = null;
		
		try {
		
			if (this.dataMappings != null) {
				if (this.dataMappings.size() > 0) {
					for(DataMapping mapping : this.dataMappings) {
						if (mapping.EntityFieldName.equals(pFieldName)) {
							returnedValue = mapping.DataBaseFieldName;
							break;
						}
					}
				}
			}
			
		} catch (Exception e) {
			ExceptionsHelper.manageException(this, e);
		}
		
		return returnedValue;
	}
	
	/**
	 * Gets the database field name associated with the entity property.
	 * @param pField Entity property.
	 * @return Database table field name.
	 * @throws AdaFrameworkException
	 */
	public String getDataTableFieldName(Field pField) throws AdaFrameworkException {
		String returnedValue = null;
		
		try {
		
			if (pField != null) {
				TableField tableFieldAnnotation = pField.getAnnotation(TableField.class);
				
				if (tableFieldAnnotation != null) {
					returnedValue = tableFieldAnnotation.name();
				}
			}
			
		} catch (Exception e) {
			ExceptionsHelper.manageException(this, e);
		}
		
		return returnedValue;
	}
	
	/**
	 * Principal constructor of the class.
	 * @throws AdaFrameworkException 
	 */
	public ObjectSet(Class<T> pManagedType, ObjectContext pContext) throws AdaFrameworkException { 
		try{
			
			this.ownerEntityType = null;
			this.dataContext = pContext;
			this.managedType = pManagedType;
			
			this.dataMappings = loadDataMappings(this.managedType); //Always First;
			this.dataBaseTableFields = loadDataBaseTableFields();
			
			if (pContext.getObjectContextEventsListener() != null) {
				setObjectSetEventsListener(pContext.getObjectContextEventsListener());
			}
			
		} catch (Exception e) {
			ExceptionsHelper.manageException(this, e);
		} 
	}
	
	/**
	 * Constructor of the class.
	 * @throws AdaFrameworkException 
	 */
	protected ObjectSet(ObjectSet<Entity> pOwnerEntityType, Class<T> pManagedType, ObjectContext pContext) throws AdaFrameworkException { 
		try{
			
			this.ownerEntityType = pOwnerEntityType;
			this.dataContext = pContext;
			this.managedType = pManagedType;
			
			this.dataMappings = loadDataMappings(this.managedType); //Always First;
			this.dataBaseTableFields = loadDataBaseTableFields();
			
		} catch (Exception e) {
			ExceptionsHelper.manageException(this, e);
		}
	}
	 
	/**
	 * Constructor of the class.
	 * @throws AdaFrameworkException 
	 */
	protected ObjectSet(ObjectSet<Entity> pOwnerEntityType, Class<T> pManagedType, ObjectContext pContext, List<DataMapping> pDataMapping, String[] pDataBaseTableFields, String pTableName, Dictionary<Class<?>, ObjectSet<Entity>> pInheritedObjectSets, Boolean pUseIndexes) throws AdaFrameworkException {
		try{
			
			this.ownerEntityType = pOwnerEntityType;
			this.dataContext = pContext;
			this.managedType = pManagedType;
			
			this.dataBaseUseIndexes = pUseIndexes;
			this.dataMappings = pDataMapping;
			this.dataBaseTableName = pTableName;
			this.dataBaseTableFields = pDataBaseTableFields;
			this.inheritedObjectSets = pInheritedObjectSets;
			
		} catch (Exception e) {
			ExceptionsHelper.manageException(this, e);
		}
	}
	
	
	/**
	 * Get all elements in the Entity Storage.
	 * @throws Exception 
	 */
	public void fill() throws AdaFrameworkException {
		fillList(false, null, null, null, null, null, null, null, null);
	}
	
	/**
	 * Get N elements from the Entity Storage.
	 * @param pLimit Maximum number of elements.
	 * @throws Exception 
	 */
	public void fill(Integer pLimit) throws AdaFrameworkException {
		fillList(false, null, null, null, null, null, null, pLimit, null);
	}
	/**
	 * Get N element from Y position from the Entity Storage.
	 * @param pOffset Start position.
	 * @param pLimit Maximum number of elements.
	 * @throws Exception
	 */
	public void fill(Integer pOffset, Integer pLimit) throws AdaFrameworkException {
		fillList(false, null, null, null, null, null, pOffset, pLimit, null);
	}
	
	/**
	 * Get all elements from the Entity Storage ordered by the Sort Expression.
	 * @param pOrderBy Sort Expression.
	 * @throws Exception 
	 */
	public void fill(String pOrderBy) throws AdaFrameworkException {
		fillList(false, null, null, pOrderBy, null, null, null, null, null);
	}
	
	/**
	 * Get N elements from the Entity Storage ordered by the Sort Expression.
	 * @param pOrderBy Sort Expression.
	 * @param pLimit Maximum number of elements.
	 * @throws Exception 
	 */
	public void fill(String pOrderBy, Integer pLimit) throws AdaFrameworkException {
		fillList(false, null, null, pOrderBy, null, null, null, pLimit, null);
	}
	
	/**
	 * Get N elements with the Y offset from the Entity Storage ordered by the Sort Expression.
	 * @param pOrderBy Sort Expression.
	 * @param pOffset Start position.
	 * @param pLimit Maximum number of elements.
	 * @throws Exception 
	 */
	public void fill(String pOrderBy, Integer pOffset, Integer pLimit) throws AdaFrameworkException {
		fillList(false, null, null, pOrderBy, null, null, pOffset, pLimit, null);
	}
	
	/**
	 * Get all elements from the Entity Storage ordered by the Sort Expression, and filter with the Where arguments values.
	 * @param pWherePattern Where clause pattern.
	 * @param pWhereValues  Where clause values.
	 * @param pOrderBy Sort Expression.
	 * @throws Exception 
	 */
	public void fill(String pWherePattern, String[] pWhereValues, String pOrderBy) throws AdaFrameworkException {
		fillList(false, pWherePattern, pWhereValues, pOrderBy, null, null, null, null, null);
	}
	
	public void fill(SQLiteDatabase pDatabase, String pWherePattern, String[] pWhereValues, String pOrderBy) throws AdaFrameworkException {
		fillList(pDatabase, false, pWherePattern, pWhereValues, pOrderBy, null, null, null, null, null);
	}
	
	/**
	 * Get N elements from the Entity Storage ordered by the Sort Expression, and filter with the Where arguments values.
	 * @param pWherePattern Where clause pattern.
	 * @param pWhereValues  Where clause values.
	 * @param pOrderBy Sort Expression.
	 * @param pLimit Maximum number of Entities.
	 * @throws Exception 
	 */
	void fill(SQLiteDatabase pDatabase, String pWherePattern, String[] pWhereValues, String pOrderBy, Integer pLimit) throws AdaFrameworkException {
		fillList(pDatabase, false, pWherePattern, pWhereValues, pOrderBy, null, null, null, pLimit, null);
	}
	
	public void fill(String pWherePattern, String[] pWhereValues, String pOrderBy, Integer pLimit) throws AdaFrameworkException {
		fillList(false, pWherePattern, pWhereValues, pOrderBy, null, null, null, pLimit, null);
	}
	
	/**
	 * Get N elements  with Y offset from the Entity Storage ordered by the Sort Expression, and filter with the Where arguments values.
	 * @param pWherePattern Where clause pattern.
	 * @param pWhereValues  Where clause values.
	 * @param pOrderBy Sort Expression.
	 * @param pOffset Start position.
	 * @param pLimit Maximum number of Entities.
	 * @throws Exception 
	 */
	public void fill(String pWherePattern, String[] pWhereValues, String pOrderBy, Integer pOffset, Integer pLimit) throws AdaFrameworkException {
		fillList(false, pWherePattern, pWhereValues, pOrderBy, null, null, pOffset, pLimit, null);
	}
	
	/**
	 * Parse and log Query sentence.
	 * @param pDistinct
	 * @param pTableName
	 * @param pFields
	 * @param pWherePattern
	 * @param pWhereValues
	 * @param pOrderBy
	 * @param pGroupBy
	 * @param pHaving
	 * @param pLimit
	 */
	private void logQuery(String pTotalTime, Boolean pDistinct, String pTableName, String[] pFields, String pWherePattern, String[] pWhereValues, String pOrderBy, String pGroupBy, String pHaving, String pLimit) {
		try {
			boolean debugable = (0 != (getContext().getContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
			
			if (debugable) {
				String sQuery = "SELECT";
				if (pDistinct) {
					sQuery += " DISTINCT";
				}
				
				String sQueryFields = "";
				for(String sField : pFields) {
					if (sQueryFields != "") {
						sQueryFields += ", ";
					}
					sQueryFields += sField;
				}
				sQuery += " " + sQueryFields;
				sQuery += " FROM " + pTableName;
				
				if (pWherePattern != null) {
					if (pWherePattern != "") {
						sQuery += " WHERE " + pWherePattern;
					}
				}
				if (pWhereValues != null) {
					if (pWhereValues.length > 0){
						sQuery = String.format(sQuery.replace("?", "%s"), (Object)pWhereValues);
					}
				}
				
				if (pOrderBy != null) {
					sQuery += " ORDER BY " + pOrderBy;
				}
				if (pLimit != null) {
					sQuery += " LIMIT " + pLimit;
				}
				
				Log.d(DataUtils.DEFAULT_LOGS_TAG, pTotalTime + ": " + sQuery);
			}
		} catch (Exception e) {
			ExceptionsHelper.manageException(e.toString());
		}
	}
	
	/**
	 * Populate internal Entities List.
	 * @param pDistinct
	 * @param pWherePattern
	 * @param pWhereValues
	 * @param pOrderBy
	 * @param pGroupBy
	 * @param pHaving
	 * @param pOffset
	 * @param pLimit
	 * @param pOwnerID
	 * @throws AdaFrameworkException
	 */
	void fillList(Boolean pDistinct, String pWherePattern, String[] pWhereValues, String pOrderBy, String pGroupBy, String pHaving, Integer pOffset, Integer pLimit, Integer pOwnerID) throws AdaFrameworkException {
		fillList(null, pDistinct, pWherePattern, pWhereValues, pOrderBy, pGroupBy, pHaving, pOffset, pLimit, pOwnerID);
	}
	
	@SuppressWarnings("unchecked")
	void fillList(SQLiteDatabase pDataBase, Boolean pDistinct, String pWherePattern, String[] pWhereValues, String pOrderBy, String pGroupBy, String pHaving, Integer pOffset, Integer pLimit, Integer pOwnerID) throws AdaFrameworkException {
		Date initOfProcess = new Date();
		Boolean manageDatabase = false;
		SQLiteDatabase database = pDataBase;
		Cursor entitiesCursor = null;
		
		try {
			
			String whereFormat = pWherePattern;
			String whereValues[] = pWhereValues;
			String orderBy = pOrderBy;
			String groupBy = pGroupBy;
			String having = pHaving;
			String limit = null;
			boolean distinct = false;
			if (pDistinct != null) {
				distinct = pDistinct;
			}
			
			if ((pLimit != null) && (pOffset != null)) {
				limit = String.format(DataUtils.DATABASE_LIMIT_OFFTSET_PATTERN, pOffset, pLimit);
			} else if (pLimit != null) {
				limit = String.format(DataUtils.DATABASE_LIMIT_PATTERN, pLimit);
			}
			if (orderBy == null) {
				orderBy = DataUtils.DATABASE_ID_FIELD_NAME + " ASC";
			}
			
			if (database == null) {
				database = this.dataContext.getReadableDatabase();
				manageDatabase = true;
			}
			
			if (database != null) {			
				this.clear();		
				
				entitiesCursor = getContext().executeQuery(database, distinct, this.dataBaseTableName, this.dataBaseTableFields, whereFormat, whereValues, groupBy, having, orderBy, limit);
				
				if (entitiesCursor != null) {
					entitiesCursor.moveToLast();
					entitiesCursor.moveToFirst();
					
					int numberOfElements = entitiesCursor.getCount();
					if (numberOfElements > 0) {
						do{
							Entity entity = generateNewEntity(database, entitiesCursor);
							
							if (entity != null) {
								this.add((T)entity);
							}
						} while(entitiesCursor.moveToNext());
					}
				}
			}
			
			
			Date endOfProcess = new Date();
			String totalTime = DataUtils.calculateTimeDiference(initOfProcess, endOfProcess);
			
			logQuery(totalTime, pDistinct, this.dataBaseTableName, this.dataBaseTableFields,  whereFormat, whereValues, orderBy, groupBy, having, limit);
			
		} catch (Exception e) {
			ExceptionsHelper.manageException(this, e);
		} finally {
			if (entitiesCursor != null) {
				entitiesCursor.close();
				entitiesCursor = null;
			}
			
			if (manageDatabase) {
				if (database != null) {
					if (database.isOpen()) {
						database.close();
					}
				}
			}
			
			database = null;
			
			if (this.objectSetEventsListener != null) {
				if (isContextActivity()) {
					((Activity)dataContext.getContext()).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							objectSetEventsListener.OnFillComplete(ObjectSet.this);
						}
					});
				}
			}
		}
	}
	
	
	/****************************************/
	/*		SEARCH METHODS 					*/
	/****************************************/
	
	/**
	 * Get a list of entities unlinked of owner ObjectSet.
	 * @param pDistinct
	 * @param pWherePattern
	 * @param pWhereValues
	 * @param pOrderBy
	 * @param pGroupBy
	 * @param pHaving
	 * @param pOffset
	 * @param pLimit
	 * @return List of entities.
	 * @throws AdaFrameworkException
	 */
	public List<T> search(Boolean pDistinct, String pWherePattern, String[] pWhereValues, String pOrderBy, String pGroupBy, String pHaving, Integer pOffset, Integer pLimit) throws AdaFrameworkException {
		return serachList(null, pDistinct, this.dataBaseTableFields, pWherePattern, pWhereValues, pOrderBy, pGroupBy, pHaving, pOffset, pLimit);
	}
	
	/**
	 * Get a list of entities unlinked of owner ObjectSet.
	 * @param pDistinct
	 * @param pFields
	 * @param pWherePattern
	 * @param pWhereValues
	 * @param pOrderBy
	 * @param pGroupBy
	 * @param pHaving
	 * @param pOffset
	 * @param pLimit
	 * @return List of entities.
	 * @throws AdaFrameworkException
	 */
	public List<T> search(Boolean pDistinct, String[] pFields, String pWherePattern, String[] pWhereValues, String pOrderBy, String pGroupBy, String pHaving, Integer pOffset, Integer pLimit) throws AdaFrameworkException {
		return serachList(null, pDistinct, pFields, pWherePattern, pWhereValues, pOrderBy, pGroupBy, pHaving, pOffset, pLimit);
	}
	
	@SuppressWarnings("unchecked")
	List<T> serachList(SQLiteDatabase pDataBase, Boolean pDistinct, String[] pFields, String pWherePattern, String[] pWhereValues, String pOrderBy, String pGroupBy, String pHaving, Integer pOffset, Integer pLimit) throws AdaFrameworkException {
		List<T> returnedValue = null;
		Date initOfProcess = new Date();
		Boolean manageDatabase = false;
		SQLiteDatabase database = pDataBase;
		Cursor entitiesCursor = null;
		
		try {
					
			String whereFormat = pWherePattern;
			String whereValues[] = pWhereValues;
			String orderBy = pOrderBy;
			String groupBy = pGroupBy;
			String having = pHaving;
			String limit = null;
			boolean distinct = false;
			if (pDistinct != null) {
				distinct = pDistinct;
			}
			
			if ((pLimit != null) && (pOffset != null)) {
				limit = String.format(DataUtils.DATABASE_LIMIT_OFFTSET_PATTERN, pOffset, pLimit);
			} else if (pLimit != null) {
				limit = String.format(DataUtils.DATABASE_LIMIT_PATTERN, pLimit);
			}
			if (orderBy == null) {
				orderBy = DataUtils.DATABASE_ID_FIELD_NAME + " ASC";
			}
			
			if (database == null) {
				database = this.dataContext.getReadableDatabase();
				manageDatabase = true;
			}
			
			if (database != null) {				
				entitiesCursor = getContext().executeQuery(database, distinct, this.dataBaseTableName, pFields, whereFormat, whereValues, groupBy, having, orderBy, limit);
				
				if (entitiesCursor != null) {
					entitiesCursor.moveToLast();
					entitiesCursor.moveToFirst();
					
					int numberOfElements = entitiesCursor.getCount();
					if (numberOfElements > 0) {
						returnedValue = new ArrayList<T>();
						
						do{
							Entity entity = generateNewEntity(database, entitiesCursor);
							
							if (entity != null) {
								returnedValue.add((T)entity);
							}
						} while(entitiesCursor.moveToNext());
					}
				}
			}
			
			
			Date endOfProcess = new Date();
			String totalTime = DataUtils.calculateTimeDiference(initOfProcess, endOfProcess);
			
			logQuery(totalTime, pDistinct, this.dataBaseTableName, this.dataBaseTableFields,  whereFormat, whereValues, orderBy, groupBy, having, limit);
			
		} catch (Exception e) {
			ExceptionsHelper.manageException(this, e);
		} finally {
			if (entitiesCursor != null) {
				entitiesCursor.close();
				entitiesCursor = null;
			}
			
			if (manageDatabase) {
				if (database != null) {
					if (database.isOpen()) {
						database.close();
					}
				}
			}
			
			database = null;
		}
		
		return returnedValue;
	}
	
	
	/**
	 * Get element by ID from the Entities Storage.
	 * @param pId ID to find.
	 * @return Entity.
	 * @throws Exception
	 */
	public T getElementByID(Long pId) throws AdaFrameworkException {
		return getElementByID(null, pId);
	}
	
	@SuppressWarnings("unchecked")
	public T getElementByID(SQLiteDatabase pDatabase, Long pId) throws AdaFrameworkException {
		T returnedValue = null;
		SQLiteDatabase database = pDatabase;
		Cursor entitiesCursor = null;
		Boolean manageDatabase = false;
		
		try {
						
			String whereFormat = DataUtils.DATABASE_ID_FIELD_NAME + "=?";
			String[] whereValues = new String[] { Long.toString(pId)  };

			if (database == null) {
				manageDatabase = true;
				database = this.dataContext.getReadableDatabase();
			}
			
			
			if (database != null) {
				//entitiesCursor = getContext().query(database, this.dataBaseTableName, this.dataBaseTableFields, whereFormat, whereValues, null, null, null);
				entitiesCursor = getContext().executeQuery(database, false, this.getDataBaseTableName(), this.dataBaseTableFields, whereFormat, whereValues, null, null, null, null);
				
				if (entitiesCursor != null) {
					entitiesCursor.moveToLast();
					entitiesCursor.moveToFirst();
					
					if (entitiesCursor.getCount() > 0) {
						returnedValue = (T)generateNewEntity(database, entitiesCursor);
					}
				}
			}
			
		} catch (Exception e) {
			ExceptionsHelper.manageException(this, e);
		} finally {
			if (entitiesCursor != null) {
				entitiesCursor.close();
				entitiesCursor = null;
			}
			
			if (manageDatabase) {
				if (database != null) {
					if (database.isOpen()) {
						database.close();
					}
				}
				database = null;
			}
		}
		
		return returnedValue;
	}
	
	/**
	 * Save the Entities collection into the Entities Storage.
	 * @throws AdaFrameworkException
	 */
	public void save() throws AdaFrameworkException {
		SQLiteDatabase database = null;
		
		Date initOfProcess = new Date();
		
		try {
		
			database = this.dataContext.getWritableDatabase();
			if (database != null) {
				if (this.dataContext.isUseTransactions()) {
					//Init the DataBase transaction.
					database.beginTransaction();
				}
			
				save(database, null);
				
				if (this.dataContext.isUseTransactions()) {
					//Make commit into active transaction.
					database.setTransactionSuccessful();
				}
			}
			
		} catch (Exception e) {
			ExceptionsHelper.manageException(this, e);
		} finally {
			if (database != null) {
				if (this.dataContext.isUseTransactions()) {
					if (database.inTransaction()) {
						database.endTransaction();
					}
				}
				if (database.isOpen()) {
					database.close();
				}
				database = null;
			}
			
			if (isContextActivity()) {
				((Activity)dataContext.getContext()).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						notifyDataSetChanged();
						
						if (objectSetEventsListener != null) {
							objectSetEventsListener.OnSaveComplete(ObjectSet.this);
						}
					}
				});
			}
		}
		
		Date endOfProcess = new Date();
		String totalTime = DataUtils.calculateTimeDiference(initOfProcess, endOfProcess);
		
		Log.d(DataUtils.DEFAULT_LOGS_TAG, String.format("TOTAL Time to execute Save '%s' command: %s.", this.managedType.getSimpleName(), totalTime));
	}
	
	/**
	 * Save the Entities collection into the Entities Storage.
	 * @throws AdaFrameworkException
	 */
	public void save(SQLiteDatabase pDataBase) throws AdaFrameworkException {
		
		try{
			save(pDataBase, null);
		} catch (Exception e) {
			ExceptionsHelper.manageException(this, e);
		}
		
		if (isContextActivity()) {
			((Activity)dataContext.getContext()).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					notifyDataSetChanged();
					
					if (objectSetEventsListener != null) {
						objectSetEventsListener.OnSaveComplete(ObjectSet.this);
					}
				}
			});
		}
	}
	
	/**
	 * Save Entities into DataBase.
	 * @throws AdaFrameworkException 
	 */
	void save(SQLiteDatabase pDataBase, Long pOwnerID) throws AdaFrameworkException {
		SQLiteDatabase database = pDataBase;
		int index = 0;
		
		if (this.size() > 0) {
			if (database != null) {
				for(; index < this.size(); index++) {
					Entity entity = this.get(index);
					
					switch(entity.getStatus()) {
						case Entity.STATUS_NEW:
							saveNewEntity(database, entity, pOwnerID);
							saveInheritedEntities(pDataBase, entity);
							break;
						case Entity.STATUS_UPDATED:
							saveUpdatedEntity(database, entity, pOwnerID);
							saveInheritedEntities(pDataBase, entity);
							break;
						case Entity.STATUS_DELETED:
							saveDeletedEntity(database, entity, pOwnerID);
							if (index > 0){
								index--;
							}
							break;
					}
					
					if (this.objectSetEventsListener != null) {
						if (isContextActivity()) { 
							final int currentIndex = index;
							
							((Activity)dataContext.getContext()).runOnUiThread(new Runnable() {
								@Override
								public void run() {
									objectSetEventsListener.OnSaveProgress(ObjectSet.this, currentIndex, ObjectSet.this.size());
								}
							});
						}
					}
				}
				
				if (this.size() > 0) {
					if (index == this.size()){
						Entity entity = this.get(index - 1);
						switch(entity.getStatus()) {
							case Entity.STATUS_NEW:
								saveNewEntity(database, entity, pOwnerID);
								saveInheritedEntities(pDataBase, entity);
								break;
							case Entity.STATUS_UPDATED:
								saveUpdatedEntity(database, entity, pOwnerID);
								saveInheritedEntities(pDataBase, entity);
								break;
							case Entity.STATUS_DELETED:
								saveDeletedEntity(database, entity, pOwnerID);
								break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Save the passed Entity into Entities Storage.
	 * @param pEntity Entity object to be saved.
	 * @throws AdaFrameworkException 
	 */
	public void save(T pEntity) throws AdaFrameworkException {
		SQLiteDatabase database = null;
		
		Date initOfProcess = new Date();
		
		try {
		
			database = this.dataContext.getWritableDatabase();
			if (database != null) {
				if (this.dataContext.isUseTransactions()) {
					//Init the DataBase transaction.
					database.beginTransaction();
				}
			
				save(database, pEntity, null);
				
				if (this.dataContext.isUseTransactions()) {
					//Make commit into active transaction.
					database.setTransactionSuccessful();
				}
			}
			
		} catch (Exception e) {
			ExceptionsHelper.manageException(this, e);
		} finally {
			if (database != null) {
				if (this.dataContext.isUseTransactions()) {
					if (database.inTransaction()) {
						database.endTransaction();
					}
				}
				if (database.isOpen()) {
					database.close();
				}
				database = null;
			}
			
			if (isContextActivity()) {
				((Activity)dataContext.getContext()).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						notifyDataSetChanged();
						
						if (objectSetEventsListener != null) {
							objectSetEventsListener.OnSaveComplete(ObjectSet.this);
						}
					}
				});
			}
		}
		
		Date endOfProcess = new Date();
		String totalTime = DataUtils.calculateTimeDiference(initOfProcess, endOfProcess);
		Log.d(DataUtils.DEFAULT_LOGS_TAG, String.format("TOTAL Time to execute Save '%s' command: %s.", this.managedType.getSimpleName(), totalTime));
	}
	
	/**
	 * Save Entities into DataBase.
	 * @throws Exception 
	 */
	void save(SQLiteDatabase pDataBase, T pEntity, Long pOwnerID) throws AdaFrameworkException {
		SQLiteDatabase database = pDataBase;
		
		if (database != null) {
			switch(pEntity.getStatus()) {
				case Entity.STATUS_NEW:
					saveNewEntity(database, pEntity, pOwnerID);
					saveInheritedEntities(pDataBase, pEntity);
					break;
				case Entity.STATUS_UPDATED:
					saveUpdatedEntity(database, pEntity, pOwnerID);
					saveInheritedEntities(pDataBase, pEntity);
					break;
				case Entity.STATUS_DELETED:
					saveDeletedEntity(database, pEntity, pOwnerID);
					break;
			}
		}
	}
	
	/**
	 * Initialize DataSet DataAdapter.
	 */
	private void initializeDataAdapter() {
		if (this.dataAdapter != null) {
			if (isContextActivity()) {
				((Activity)this.dataContext.getContext()).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dataAdapter.clear();
						
						if (size() > 0) {
							for (T entity : ObjectSet.this) {
								dataAdapter.add(entity);
							}
							notifyDataSetChanged();
						}					
					}
				});
			}
		}
	}
	
	/**
	 * Get the InheritedObjectSet instance.
	 * @param pType
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ObjectSet<Entity> getInheritedObjectSet(Class<?> pType) throws AdaFrameworkException {
		ObjectSet<Entity> inheritedObjectSet = null;
		ObjectSet<Entity> commonInheritedObjectSet = inheritedObjectSets.get(pType);

		if (commonInheritedObjectSet != null){
			inheritedObjectSet = new ObjectSet<Entity>(commonInheritedObjectSet.getOwnerEntityType(), (Class)commonInheritedObjectSet.getManagedType(), commonInheritedObjectSet.getContext(), commonInheritedObjectSet.getDataMappings(), commonInheritedObjectSet.getDataBaseTableFields(), commonInheritedObjectSet.getDataBaseTableName(), commonInheritedObjectSet.inheritedObjectSets, commonInheritedObjectSet.dataBaseUseIndexes);
		}
		
		return inheritedObjectSet;
	}
	
	/**
	 * Load the DataBase fields mapping.
	 * @throws Exception 
	 */
	private List<DataMapping> loadDataMappings(Class<?> pManagedType) throws AdaFrameworkException {
		List<DataMapping> returnedValue = new ArrayList<DataMapping>();
		
		try {
			Class<?> managedType = pManagedType;
			
			if (managedType != Entity.class) {
				while((managedType != Entity.class || managedType == Object.class)) {
					if (managedType != null) {
						//Get the DataBase table name from the Entity class metadata.
						Table tableAnnotation = (Table)managedType.getAnnotation(Table.class);
						if (tableAnnotation != null) {
							if (this.dataBaseTableName == "") {
								this.dataBaseTableName = tableAnnotation.name();
							}
							
							this.dataBaseUseIndexes = tableAnnotation.useIndexes();
						}
	
						//Get all declared fields in the managed Object.
						Field[] declaredFields = managedType.getDeclaredFields();
						if (declaredFields != null) {
							extractDataMappings(declaredFields, returnedValue, false);
						}
					}
					
					managedType = managedType.getSuperclass();
					if (managedType == null) {
						break;
					}
				}
				
				if (managedType != null) {
					if (managedType == Entity.class) {
						if (this.ownerEntityType != null) {
							Field[] ownerFieldId = new Field[] { Entity.class.getDeclaredField("ID") };
							extractDataMappings(ownerFieldId, returnedValue, true);
						}
	
						//Get all declared fields in the managed Object.
						Field[] declaredFields = managedType.getDeclaredFields();
						if (declaredFields != null) {
							extractDataMappings(declaredFields, returnedValue, false);
						}
					}
				}
			}
			
			//If Table Name not found assume the name of the ObjectSet managed Type.
			if (this.dataBaseTableName == "") {
				this.dataBaseTableName = this.getManagedType().getSimpleName();
			}
			
		} catch (Exception e) {
			ExceptionsHelper.manageException(this, e);
		}
		return returnedValue;
	}
	
	/**
	 * Extract Data Mappings from the Class Fields and it's added to the data mappings list.
	 * @param pDeclaredFields
	 * @param pMappingsList
	 * @throws AdaFrameworkException
	 */
	@SuppressWarnings("unchecked")
	private void extractDataMappings(Field[] pDeclaredFields, List<DataMapping> pMappingsList, Boolean pForeignKeys) throws AdaFrameworkException {
		Field[] declaredFields = pDeclaredFields;
		
		try {
			if (declaredFields != null) {
				if (declaredFields.length > 0) {
					//Look at all elements of the list of Fields
					for(Field declaredField : declaredFields) {
						//Get TableField Annotation object from the Class field definition.
						TableField tableFieldAnnotation = declaredField.getAnnotation(TableField.class);
						
						if (tableFieldAnnotation != null) {
							DataMapping dataMapping = new DataMapping();
							dataMapping.ForeignKey = pForeignKeys;
							dataMapping.EntityManagedType = declaredField.getType();
							dataMapping.EntityManagedField = declaredField;
							dataMapping.EntityFieldName = declaredField.getName();
							dataMapping.DataBaseTableName = this.dataBaseTableName;
							dataMapping.DataBaseFieldName = tableFieldAnnotation.name();
							dataMapping.DataBaseLength = tableFieldAnnotation.maxLength();
							dataMapping.DataBaseAllowNulls = !tableFieldAnnotation.required();
							dataMapping.DataBaseDataType = tableFieldAnnotation.datatype();
							dataMapping.DataBaseIsPrimaryKey = tableFieldAnnotation.isPrimaryKey();
							dataMapping.Encrypted = tableFieldAnnotation.encripted();
							dataMapping.Unique = tableFieldAnnotation.unique();
							dataMapping.BitmapCompression = tableFieldAnnotation.BitmapCompression();
							dataMapping.virtual = tableFieldAnnotation.virtual();
							
							//Getter And Setter Management
							dataMapping.setterMethod = ReflectionHelper.extractSetterMethod(this.managedType, dataMapping.EntityManagedField);
							dataMapping.getterMethod = ReflectionHelper.extractGetterMethod(this.managedType, dataMapping.EntityManagedField);
							//End of Getter And Setter Management
							
							//Check if the Field is a List or Collection Type.
							if (isCollection(dataMapping.EntityManagedType)) {
								dataMapping.IsCollection = true;
								
								//Get the generic type managed by the List.
								ParameterizedType listGenericType = (ParameterizedType)declaredField.getGenericType();
								Class<?> listGenericClass = (Class<?>)listGenericType.getActualTypeArguments()[0];
								if (listGenericClass != null) {
									dataMapping.EntityManagedType = listGenericClass;
								} else {
									dataMapping.EntityManagedType = Entity.class;
								}
							}
							
							//Check if the field is a ForeignKey field.
							if(dataMapping.ForeignKey) {
								//Format the special name of the Database table filed. 
								dataMapping.DataBaseFieldName = String.format(DataUtils.DATABASE_FK_FIELD_PATTERN, this.ownerEntityType.dataBaseTableName);
								dataMapping.DataBaseIsPrimaryKey = false;
								dataMapping.Unique = false;	
								dataMapping.IsSpecialField = true;
							}
							
							switch(dataMapping.DataBaseDataType) {
								case Entity.DATATYPE_ENTITY:						
									//Generate new ObjectSet for the inherited Entities Types.
									generateNewInheritedObjectSet((ObjectSet<Entity>)this, dataMapping.EntityManagedType);
									break;
									
								case Entity.DATATYPE_ENTITY_REFERENCE:
									//Generate new ObjectSet for the inherited Entities Types.
									generateNewInheritedObjectSet(null, dataMapping.EntityManagedType);
									
									//By default set the field name same as managed type class name.
									String tableName = dataMapping.EntityManagedType.getSimpleName();
									
									//Get Table Annotation from the ManagedType information.
									Table tableAnnotation = (Table)dataMapping.EntityManagedType.getAnnotation(Table.class);
									if (tableAnnotation != null) {
										if (tableAnnotation.name().trim() != "") {
											tableName = tableAnnotation.name();
										}
									}
									
									//Format the special name of the Database table filed. 
									if (dataMapping.DataBaseFieldName.equals("")) {
										dataMapping.DataBaseFieldName = String.format(DataUtils.DATABASE_FK_FIELD_PATTERN, tableName);
									}
									dataMapping.DataBaseIsPrimaryKey = false;
									dataMapping.Unique = false;
									dataMapping.IsSpecialField = true;
									break;
							}
							
							//If the DataBaseFieldName is empty, set the name same as Class field name.
							if (dataMapping.DataBaseFieldName == "") {
								dataMapping.DataBaseFieldName = dataMapping.EntityFieldName;
							}
							
							if (dataMapping.DataBaseFieldName == DataUtils.DATABASE_ID_FIELD_NAME) {
								dataMapping.IsSpecialField = true;
							}
							
							//Add the mapping to the data mappings collection.
							if (dataMapping.IsSpecialField) {
								pMappingsList.add(0, dataMapping);
							} else {
								pMappingsList.add(dataMapping);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionsHelper.manageException(this, e);
		}
	}
	
	/**
	 * Check if the type is a Collection. 
	 * @param pType Class to validate.
	 * @return True if is a Collection
	 */
	private Boolean isCollection(Class<?> pType) {
		Boolean returnedValue = false;
		
		if (pType == List.class) {
			returnedValue = true;
		}
		
		return returnedValue;
	}
	
	/**
	 * Generate new ObjectSet instance for any the Inherited Entities.
	 * @param managedType
	 * @throws AdaFrameworkException
	 */
	@SuppressWarnings("unchecked")
	private void generateNewInheritedObjectSet(ObjectSet<Entity> pOwnerSet, Class<?> pManagedType) throws AdaFrameworkException {
		//Generate new ObjectSet instance.
		@SuppressWarnings("rawtypes")
		ObjectSet<Entity> inheritedReferenceEntityObjectSet = new ObjectSet<Entity>(pOwnerSet, (Class)pManagedType, getContext());
		inheritedReferenceEntityObjectSet.setDeleteOnCascade(this.isDeleteOnCascade());
		
		if (inheritedReferenceEntityObjectSet != null) {
			//Add the ObjectSet to the InheritedObjectSet internal list.
			inheritedObjectSets.put(pManagedType, inheritedReferenceEntityObjectSet);
		}
	}
	
	/**
	 * Load the DataBase fields mapping.
	 */
	private String[] loadDataBaseTableFields() {
		List<String> returnedValue = new ArrayList<String>();
		
		for(DataMapping mapping : this.dataMappings) {
			if (!mapping.virtual) {
				switch (mapping.DataBaseDataType) {
					case Entity.DATATYPE_BOOLEAN:
					case Entity.DATATYPE_DATE:
					case Entity.DATATYPE_INTEGER:
					case Entity.DATATYPE_LONG:
					case Entity.DATATYPE_DOUBLE:
					case Entity.DATATYPE_REAL:
					case Entity.DATATYPE_TEXT:
					case Entity.DATATYPE_STRING:
					case Entity.DATATYPE_BLOB:
					case Entity.DATATYPE_ENTITY_REFERENCE:
						if (mapping.Unique) {
							if (dataBaseUniqueTableFields != "") {
								dataBaseUniqueTableFields += ", ";
							}
							dataBaseUniqueTableFields += mapping.DataBaseFieldName;
						}
						
						returnedValue.add(mapping.DataBaseFieldName);
						break;
				}
			}
		}
		
		return returnedValue.toArray(new String[returnedValue.size()]);
	}
	
	/**
	 * Generate the DataBase table script.
	 * @return Script with the table creation.
	 */
	private String generateDataBaseTableScript(List<DataMapping> pDataMappings) {
		String returnedValue = "";
		String tableName = "";
		String tableFieldsScript = "";
		String tableUniqueFieldsScript = "";
		String tableForeignKeyScript = "";
		
		for(DataMapping mapping : pDataMappings) {
			if (mapping.virtual) {
				Log.d(DataUtils.LOG_TAG, String.format("The field '%s' has been omitted its virtual condition.", mapping.EntityFieldName));
			} else {
				if ((mapping.DataBaseDataType != Entity.DATATYPE_ENTITY)) {
					
					tableName = mapping.DataBaseTableName;
					String dataFieldName = mapping.DataBaseFieldName;
					String dataTypeScript = "";
					String dataPrimaryKeyScript = "";
					String dataAllowNullsScript = "";
					
					switch(mapping.DataBaseDataType) {
						case Entity.DATATYPE_BOOLEAN:
						case Entity.DATATYPE_INTEGER:
						case Entity.DATATYPE_LONG:
						case Entity.DATATYPE_DOUBLE:
						case Entity.DATATYPE_ENTITY_REFERENCE:
							dataTypeScript = "INT";
							break;
						case Entity.DATATYPE_DATE:
						case Entity.DATATYPE_TEXT:
						case Entity.DATATYPE_STRING:
							dataTypeScript = "TEXT";
							break;
						case Entity.DATATYPE_REAL:
							dataTypeScript = "REAL";
							break;
						case Entity.DATATYPE_BLOB:
							dataTypeScript = "BLOB";
							break;
					}
					
					if (tableFieldsScript != "") {
						tableFieldsScript += ", ";
					}
					if (mapping.DataBaseIsPrimaryKey) {
						dataPrimaryKeyScript = "PRIMARY KEY ";	
					}
					if (!mapping.DataBaseAllowNulls) {
						dataAllowNullsScript = "NOT NULL ";
					}
					
					if (mapping.DataBaseFieldName == DataUtils.DATABASE_ID_FIELD_NAME) {
						dataTypeScript = "INTEGER";
						dataPrimaryKeyScript = "PRIMARY KEY AUTOINCREMENT";	
						dataAllowNullsScript = "NOT NULL";
					}
					
					//Format String
					tableFieldsScript += String.format("%s %s %s %s", 
							dataFieldName,
							dataTypeScript,
							dataPrimaryKeyScript,
							dataAllowNullsScript);
					
					//Clean up the excess space.
					tableFieldsScript = tableFieldsScript.replace("  ", " ");
					
					if (mapping.ForeignKey) {	
						if (this.ownerEntityType != null) {
							tableForeignKeyScript += String.format(DataUtils.DATABASE_TABLE_FOREIGN_KEY_PATTERN, mapping.DataBaseFieldName, this.ownerEntityType.dataBaseTableName);
						}
					} else {
						if (mapping.DataBaseDataType == Entity.DATATYPE_ENTITY_REFERENCE) {
							try {
								ObjectSet<Entity> ownerSet = getInheritedObjectSet(mapping.EntityManagedType);
								
								if (ownerSet != null) {
									tableForeignKeyScript += String.format(DataUtils.DATABASE_TABLE_FOREIGN_KEY_PATTERN, mapping.DataBaseFieldName, ownerSet.getDataBaseTableName());
								}
							} catch (Exception e) {
							}
						}
					}
				}
			}
		}
		
		if (dataBaseUniqueTableFields != "") {
			tableUniqueFieldsScript = String.format(DataUtils.DATABASE_TABLE_UNIQUE_PATTERN, dataBaseUniqueTableFields);
		}
		
		returnedValue = String.format(DataUtils.DATABASE_TABLE_PATTERN, tableName, tableFieldsScript, tableForeignKeyScript, tableUniqueFieldsScript);
		
		return returnedValue.replace("  ", " ");
	}
	
	private List<String> generateDataBaseTableIndexesScript(List<DataMapping> pDataMappings) {
		List<String> returnedValue = new ArrayList<String>();
		String tableName = "";
		String dataFieldName = "";
		
		if (this.dataBaseUseIndexes) {
			for(DataMapping mapping : pDataMappings) {
				if ((mapping.DataBaseDataType != Entity.DATATYPE_ENTITY)) {
					tableName = mapping.DataBaseTableName;
					dataFieldName = mapping.DataBaseFieldName;
					
					if (mapping.ForeignKey) {	
						if (mapping.virtual) {
							Log.d(DataUtils.LOG_TAG, String.format("The field '%s' has been omitted its virtual condition.", mapping.EntityFieldName));
						} else {
							String indexName = 	String.format(DataUtils.DATABASE_INDEX_FIELD_PATTERN, tableName, dataFieldName);
							String createIndexScript = String.format(DataUtils.DATABASE_TABLE_INDEX_PATTERN, indexName, tableName, dataFieldName);
							
							returnedValue.add(createIndexScript);
						}
					}
				}
			}
		}

		return returnedValue;
	}
	/**
	 * Generate and Fill Entity instance.
	 * @param pCursor
	 * @return
	 * @throws AdaFrameworkException
	 */
	private Entity generateNewEntity(SQLiteDatabase database, Cursor pCursor) throws AdaFrameworkException {
		Entity entity = null;
		try {
		
			entity = (Entity) this.managedType.newInstance();
			for (DataMapping mapping : this.dataMappings) {
				if ((mapping.DataBaseDataType != Entity.DATATYPE_ENTITY) &&
					(!mapping.ForeignKey)) {
					
					int columIndex = pCursor.getColumnIndex(mapping.DataBaseFieldName);
					
					if (columIndex >= 0) {
						Object value = null;
						
						switch (mapping.DataBaseDataType) {
							case Entity.DATATYPE_BOOLEAN:
								value = pCursor.getInt(columIndex);
								if ((Integer)value == DataUtils.DATABASE_BOOLEAN_VALUE_TRUE) {
									value = true;
								} else {
									value = false;
								}
								break;
							case Entity.DATATYPE_TEXT:
							case Entity.DATATYPE_STRING:
								value = pCursor.getString(columIndex);
								if (mapping.Encrypted){
									value = EncryptionHelper.decrypt(this.dataContext.getMasterEncryptionKey(), (String)value);
								}
								break;
							case Entity.DATATYPE_DATE:
								value = pCursor.getString(columIndex);
								if (mapping.Encrypted){
									value = EncryptionHelper.decrypt(this.dataContext.getMasterEncryptionKey(), (String)value);
								}
								value = getContext().StringToDate((String)value);
								break;
							case Entity.DATATYPE_REAL:
								value = pCursor.getFloat(columIndex);
								break;
							case Entity.DATATYPE_INTEGER:
								value = pCursor.getInt(columIndex);
								break;
							case Entity.DATATYPE_LONG:
								value = pCursor.getLong(columIndex);
								break;
							case Entity.DATATYPE_DOUBLE:
								value = pCursor.getDouble(columIndex);
								break;
							case Entity.DATATYPE_BLOB:
								value = pCursor.getBlob(columIndex);
								
								if (value != null) {
									if (mapping.EntityManagedType == Bitmap.class) {
										value = BitmapFactory.decodeByteArray((byte[])value, 0, ((byte[])value).length);
									}
								}
								break;
							case Entity.DATATYPE_ENTITY_REFERENCE:
								Long foreignID = pCursor.getLong(columIndex);
								
								if (foreignID != null) {
									ObjectSet<Entity> inheritedObjectSet = getInheritedObjectSet(mapping.EntityManagedType);
									if (inheritedObjectSet != null) {
										value = inheritedObjectSet.getElementByID(database, foreignID);
									}
								}
								break;
						}
						
						if (value != null) {
							if (mapping.DataBaseFieldName == DataUtils.DATABASE_ID_FIELD_NAME) {
								entity.ID = (Long)value;
							} else {
								//mapping.EntityManagedField.set(entity, value);
								setEntityPropertyValue(entity, value, mapping);
							}	
						}
					}
				}
			}
			
			if (entity != null) {
				if (ContainInheritedEntities()) {
					String wherePattern = String.format(DataUtils.DATABASE_FK_FIELD_PATTERN, this.getDataBaseTableName()) + " = ?";
					String[] whereValue = new String[] { Long.toString(entity.ID) };
					
					
					for (DataMapping mapping : this.dataMappings) {
						switch(mapping.DataBaseDataType) {
							case Entity.DATATYPE_ENTITY:
								//Get the InheritedObjectSet responsible for managing the type.
								//Field entityField = mapping.EntityManagedField;
								ObjectSet<Entity> inheritedObjectSet = getInheritedObjectSet(mapping.EntityManagedType);
								
								if (inheritedObjectSet != null) {
									if (!mapping.IsCollection) {
										//If the managed field is not a List, set the first item of the inherited ObjectSet.
										inheritedObjectSet.fill(database, wherePattern, whereValue, DataUtils.DATABASE_ID_FIELD_NAME + " ASC", 1);
										if(inheritedObjectSet.size() > 0) {
											//entityField.set(entity, inheritedObjectSet.get(0));
											setEntityPropertyValue(entity, inheritedObjectSet.get(0), mapping);
										}
									} else {
										//If the managed field is a List, set the all items of the inherited ObjectSet.
										inheritedObjectSet.fill(database, wherePattern, whereValue, DataUtils.DATABASE_ID_FIELD_NAME + " ASC");
										//entityField.set(entity, (List<Entity>)inheritedObjectSet);
										setEntityPropertyValue(entity, inheritedObjectSet, mapping);
									}
								}
								
								break;
						}
					}
				}
			}
			
			entity.setStatus(Entity.STATUS_NOTHING);
			
		} catch (Exception e) {
			ExceptionsHelper.manageException(this, e);
		}
	
		
		return entity;
	}

	
	
	/**
	 * Save inherited entities into DataBase.
	 * @param pDataBase
	 * @param pEntity
	 * @throws AdaFrameworkException
	 */
	@SuppressWarnings("unchecked")
	private void saveInheritedEntities(SQLiteDatabase pDataBase, Entity pEntity) throws AdaFrameworkException {
		try {
			if (ContainInheritedEntities()) {
				for (DataMapping mapping : this.dataMappings) {
					switch(mapping.DataBaseDataType) {
						case Entity.DATATYPE_ENTITY:
							//Recovery the Entity ObjectSet Controller.
							ObjectSet<Entity> inheritedObjectSet = getInheritedObjectSet(mapping.EntityManagedType);
							
							if (inheritedObjectSet != null) {
								if (!mapping.IsCollection) {
									
									Entity inheritedEntity = null;
									if (mapping.getterMethod != null) {
										inheritedEntity = (Entity)mapping.getterMethod.invoke(pEntity, (Object[])null);
									} else {
										inheritedEntity = (Entity)pEntity.getClass().getDeclaredField(mapping.EntityFieldName).get(pEntity);
									}
								
									if (inheritedEntity != null) {
										inheritedObjectSet.save(pDataBase, inheritedEntity, pEntity.ID);
									}
								} else {
									List<Entity> inheritedEntities = null;
									if (mapping.getterMethod != null) {
										inheritedEntities = (List<Entity>)mapping.getterMethod.invoke(pEntity, (Object[])null);
									} else {
										inheritedEntities = (List<Entity>)pEntity.getClass().getDeclaredField(mapping.EntityFieldName).get(pEntity);
									}
									
									if (inheritedEntities != null) {
										if (inheritedEntities.size() > 0) {
											for(Entity inheritedEntity : inheritedEntities) {
												inheritedObjectSet.save(pDataBase, inheritedEntity, pEntity.ID);
											}
										}
									}
								}
							}
	
							break;
					}
				}
			}
		} catch (Exception e) {
			ExceptionsHelper.manageException(this, e);
		}
	}
	
	/**
	 * Delete Inherited elements from the DataBase.
	 * @param pDataBase
	 * @param pEntity
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void deleteInheritedEntities(SQLiteDatabase pDataBase, Entity pEntity) throws AdaFrameworkException {
		try {
			for (DataMapping mapping : this.dataMappings) {
				switch(mapping.DataBaseDataType) {
					case Entity.DATATYPE_ENTITY:
						//Create new ObjectSet the Entity type ObjectSet Controller.
						ObjectSet<Entity> inheritedObjectSet = getInheritedObjectSet(mapping.EntityManagedType);
						
						if (inheritedObjectSet != null) {
							if (!mapping.IsCollection) {
								Entity inheritedEntity = (Entity)pEntity.getClass().getDeclaredField(mapping.EntityFieldName).get(pEntity);
							
								inheritedEntity.setStatus(Entity.STATUS_DELETED);
								inheritedObjectSet.save(pDataBase, inheritedEntity, pEntity.ID);
							} else {
								//List<Entity> inheritedEntities = (List<Entity>)mapping.EntityManagedField.get(pEntity);
								List<Entity> inheritedEntities = (List<Entity>)getEntityPropertyValue(pEntity, mapping);
								
								if (inheritedEntities != null) {
									if (inheritedEntities.size() > 0) {
										for(Entity inheritedEntity : inheritedEntities) {
											inheritedEntity.setStatus(Entity.STATUS_DELETED);
											inheritedObjectSet.add(inheritedEntity);
										}
										inheritedObjectSet.save(pDataBase, pEntity.ID);
									}
								}
							}
						}
						break;
				}
			}
		} catch (Exception e) {
			ExceptionsHelper.manageException(this, e);
		}
	}
	
	/**
	 * Get the value of property into the Entity.
	 * @param pEntity
	 * @param pMapping
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private Object getEntityPropertyValue(Entity pEntity, DataMapping pMapping) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Object returnedValue = null;
		
		if (pMapping.getterMethod != null) {
			returnedValue = pMapping.getterMethod.invoke(pEntity, (Object[])null);
		} else {
			returnedValue = pMapping.EntityManagedField.get(pEntity);
		}
		
		return returnedValue;
	}
	
	/**
	 * Set the value of property into the Entity.
	 * @param pEntity
	 * @param pValue
	 * @param pMapping
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private void setEntityPropertyValue(Entity pEntity, Object pValue, DataMapping pMapping) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (pMapping.setterMethod != null) {
			pMapping.setterMethod.invoke(pEntity, pValue);
		} else {
			pMapping.EntityManagedField.set(pEntity, pValue);
		}
	}
	
	/**
	 * This method generate DataBase Actions content values. Used for INSERT and UPDATES. 
	 * @return
	 * @throws AdaFrameworkException 
	 */
	private ContentValues generateContentValues(Entity pEntity, Long pOwnerID) throws AdaFrameworkException {
		ContentValues values = new ContentValues();
		
		try {
			for (DataMapping mapping : this.dataMappings) {
				if (mapping.virtual) {
					Log.d(DataUtils.LOG_TAG, String.format("The field '%s' has been omitted its virtual condition.", mapping.EntityFieldName));
				} else {
					//Skip Entities and Entities Collection because this objects don't allow storage in the object table.
					if (mapping.DataBaseDataType != Entity.DATATYPE_ENTITY) {
						if (mapping.DataBaseFieldName != DataUtils.DATABASE_ID_FIELD_NAME) {
							Object propertyValue = getEntityPropertyValue(pEntity, mapping);
							
							if (mapping.ForeignKey) {
								if (pOwnerID != null) {
									values.put(mapping.DataBaseFieldName, pOwnerID);
								} else {
									values.putNull(mapping.DataBaseFieldName);
								}
							} else {
								if (propertyValue != null) {
									switch (mapping.DataBaseDataType) {
										case Entity.DATATYPE_INTEGER:
											values.put(mapping.DataBaseFieldName, (Integer)propertyValue);
											break;
										case Entity.DATATYPE_LONG:
											values.put(mapping.DataBaseFieldName, (Long)propertyValue);
											break;
										case Entity.DATATYPE_DOUBLE:
											values.put(mapping.DataBaseFieldName, (Double)propertyValue);
											break;
										case Entity.DATATYPE_BOOLEAN:
											if ((Boolean)propertyValue) {
												values.put(mapping.DataBaseFieldName, DataUtils.DATABASE_BOOLEAN_VALUE_TRUE);
											} else {
												values.put(mapping.DataBaseFieldName, DataUtils.DATABASE_BOOLEAN_VALUE_FALSE);
											}
											break;
										case Entity.DATATYPE_REAL:
											values.put(mapping.DataBaseFieldName, (Float)propertyValue);
											break;
										case Entity.DATATYPE_TEXT:
										case Entity.DATATYPE_STRING:
										case Entity.DATATYPE_DATE:
											values.put(mapping.DataBaseFieldName, getContext().prepareObjectToDatabase(propertyValue, mapping));
											break;
										case Entity.DATATYPE_BLOB:
											if (propertyValue instanceof Bitmap) {
												byte[] byteArrayValue = extractBitmatBytes((Bitmap)propertyValue, mapping.BitmapCompression);
												
												if (byteArrayValue != null){
													values.put(mapping.DataBaseFieldName, byteArrayValue);
												} else {
													values.putNull(mapping.DataBaseFieldName);
												}
											} else {
												if (propertyValue instanceof byte[]) {
													values.put(mapping.DataBaseFieldName, (byte[])propertyValue);
												} else {
													values.putNull(mapping.DataBaseFieldName);
												}
											}
											break;
										case Entity.DATATYPE_ENTITY_REFERENCE:
											propertyValue = ((Entity)propertyValue).getID();
											values.put(mapping.DataBaseFieldName, (Long)propertyValue);
											break;
									}
								} else {
									values.putNull(mapping.DataBaseFieldName);
								}
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			ExceptionsHelper.manageException(this, e);
		}
		
		return values;
	}
	
	/**
	 * This method compress and stract bytes from Bitmap.
	 * @param pBitmap
	 * @return
	 * @throws IOException 
	 */
	private byte[] extractBitmatBytes(Bitmap pBitmap, CompressFormat pFormat) {
		byte[] returnedValue = null;
		
		try{
			
			if (pBitmap != null){
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				pBitmap.compress(pFormat, 100, out);
		        
		        returnedValue = out.toByteArray();
		        
		        out.close();
		        out = null;
			}
			
		} catch (Exception e) {
			returnedValue = null;
		}
		
		return returnedValue;
	}
	
	/**
	 * Save new entity into DataBase.
	 * @param pDatabase
	 * @param pEntity
	 * @throws AdaFrameworkException 
	 */
	private void saveNewEntity(SQLiteDatabase pDatabase, Entity pEntity, Long pOwnerID) throws AdaFrameworkException {
		SQLiteDatabase database = pDatabase;
		
		try {
			
			if (database != null) {
				if (pEntity != null) {
					if (pEntity.getID() == null) {
						
							ContentValues insertValues = generateContentValues(pEntity, pOwnerID);
							Long entityID = getContext().executeInsert(database, this.dataBaseTableName, null, insertValues);
							
							pEntity.setID(entityID);
							pEntity.setStatus(Entity.STATUS_NOTHING);
						
					} else {
						saveUpdatedEntity(pDatabase, pEntity, pOwnerID);
					}
				}
			}
			
		} catch (Exception e) {
			ExceptionsHelper.manageException(this, e);
		}
	}
	
	/**
	 * Update entity information into DataBase.
	 * @param pDatabase
	 * @param pEntity
	 * @throws AdaFrameworkException 
	 */
	private void saveUpdatedEntity(SQLiteDatabase pDatabase, Entity pEntity, Long pOwnerID) throws AdaFrameworkException {
		SQLiteDatabase database = pDatabase;
		
		try {
			
			if (database != null) {
				if (pEntity != null) {
					if (pEntity.getID() != null) {

						ContentValues updateValues = generateContentValues(pEntity, pOwnerID);						
						getContext().executeUpdate(database, this.dataBaseTableName, updateValues, DataUtils.DATABASE_ID_FIELD_WHERE, new String[] { Long.toString(pEntity.getID()) });
						
						pEntity.setStatus(Entity.STATUS_NOTHING);
						
					} else {
						saveNewEntity(database, pEntity, pOwnerID);
					}
				}
			}
			
		} catch (Exception e) {
			ExceptionsHelper.manageException(this, e);
		}
	}
	

	
	/**
	 * Delete entity from DataBase.
	 * @param pDatabase
	 * @param pEntity
	 * @throws AdaFrameworkException 
	 */
	private void saveDeletedEntity(SQLiteDatabase pDatabase, final Entity pEntity, Long pOwnerID) throws AdaFrameworkException {
		SQLiteDatabase database = pDatabase;
		
		try {
			if (database != null) {
				if (pEntity != null) {
					if (pEntity.getID() != null) {	
						if (isDeleteOnCascade()) {
							if (ContainInheritedEntities()) {
								deleteInheritedEntities(pDatabase, pEntity);
							}
						}
						
						//Delete entity from Database.
						getContext().executeDelete(database, this.dataBaseTableName, DataUtils.DATABASE_ID_FIELD_WHERE, new String[] { Long.toString(pEntity.getID()) });
						
						super.remove(pEntity);

						if (dataAdapter != null) {
							if (isContextActivity()) {
								((Activity)this.dataContext.getContext()).runOnUiThread(new Runnable() {
									@SuppressWarnings("unchecked")
									@Override
									public void run() {
										int position = dataAdapter.getPosition((T)pEntity);
										
										if (position >= 0){
											dataAdapter.remove((T)pEntity);
										}					
									}
								});
							}
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionsHelper.manageException(this, e);
		}
	}
		
	
	/**
	 * Execute notifyDataSetChanged method of the ObjectSet ArrayAdapter.
	 */
	private void notifyDataSetChanged() {
		try {
			
			if (this.dataAdapter != null) {
				dataAdapter.notifyDataSetChanged();					
			}
			
		} catch (Exception e) {
		}
	}
	
	/**
	 * Add a new Entity into the ObjectSet.
	 */
	public boolean add(final T object) {
		boolean returnedValue = super.add(object);
		
		if (this.dataAdapter != null) {
			if (isContextActivity()) {
				((Activity)this.dataContext.getContext()).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dataAdapter.add(object);
						
						notifyDataSetChanged();
					}
				});
			}
		}
		
		return returnedValue;
	}
	
	/**
	 * Add a new Entity into the ObjectSet into specific position.
	 */
	public void add(final int location, final T object) {
		super.add(location, object);
		
		if (this.dataAdapter != null) {
			if (isContextActivity()) {
				((Activity)this.dataContext.getContext()).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dataAdapter.insert(object, location);
						
						notifyDataSetChanged();
					}
				});
			}
		}
	}
	
	@Override
	public boolean addAll(Collection<? extends T> collection) {
		boolean returnedValue = super.addAll(collection);
		
		if (this.dataAdapter != null) {
			for(T entity : collection) {
				this.add(entity);
			}

			if (isContextActivity()) {
				((Activity)this.dataContext.getContext()).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						notifyDataSetChanged();
					}
				});
			}
		}
		
		return returnedValue;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends T> collection) {
		boolean returnedValue = super.addAll(index, collection);
	
		if (this.dataAdapter != null) {
			int location = index;
			for(T entity : collection) {
				this.add(location, entity);
				location++;
			}
			
			if (isContextActivity()) {
				((Activity)this.dataContext.getContext()).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						notifyDataSetChanged();
					}
				});
			}
		}
		
		return returnedValue;
	}

	@Override
	public T remove(final int index) {
		this.get(index).setStatus(Entity.STATUS_DELETED);
		
		if (this.dataAdapter != null) {
			if (isContextActivity()) {
				((Activity)this.dataContext.getContext()).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dataAdapter.remove(get(index));
						
						notifyDataSetChanged();
					}
				});
			}
		}
		
		return this.get(index);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean remove(final Object object) {
		((Entity)object).setStatus(Entity.STATUS_DELETED);
		
		if (this.dataAdapter != null) {
			if (isContextActivity()) {
				((Activity)this.dataContext.getContext()).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dataAdapter.remove((T)object);
						
						notifyDataSetChanged();
					}
				});
			}
		}

		return true;
	}
	
	@Override
	public void clear() {
		super.clear();
		
		if (this.dataAdapter != null) {
			if (isContextActivity()) {
				((Activity)this.dataContext.getContext()).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dataAdapter.clear();
						
						notifyDataSetChanged();
					}
				});
			}
		}
	}
	
	private Boolean isContextActivity() {
		Boolean returnedValue = false;
		
		try {
			
			if (this.dataContext.getContext() != null) {
				if (this.dataContext.getContext() instanceof Activity) {
					returnedValue = true;
				}
			}
			
		} catch (Exception e) {
			returnedValue = false;
		}
		
		return returnedValue;
	}
}
