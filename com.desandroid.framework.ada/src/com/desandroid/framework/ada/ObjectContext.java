package com.desandroid.framework.ada;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.desandroid.framework.ada.annotations.ObjectSetConfiguration;
import com.desandroid.framework.ada.exceptions.AdaFrameworkException;
import com.desandroid.framework.ada.listeners.ObjectSetEventsListener;

/**
 * Object Context for the DataBase Entities management.
 * @version 1.4.5
 * @author DesAndrOId
 */
public class ObjectContext {
	private DataBaseHelper databaseHelper;
	private String databaseFileName = DataUtils.DEFAULT_DATABASE_NAME;
	private int databaseVersion = 1;
	private Context context;
	private String masterEncryptionKey = DataUtils.DEFAULT_MASTER_ENCRIPTION_KEY;
	private ObjectSetEventsListener objectSetEventsListener;
	
	private boolean useTransactions = true;
	private boolean generateTableIndexes = true;
	private boolean useInsertHelppers = false;
	
	/**
	 * Set the ObjectSet events listener.
	 */
	public void setObjectContextEventsListener(ObjectSetEventsListener pListener) {
		this.objectSetEventsListener = pListener;
	}
	
	/**
	 * Returns the global events listener.
	 */
	ObjectSetEventsListener getObjectContextEventsListener() {
		return this.objectSetEventsListener;
	}
	
	
	/**
	 * Get the encryption algorithm used by the framework.
	 */
	public String getEncryptionAlgorithm() {
		return EncryptionHelper.getEncriptionAlgorithm();
	}
	/**
	 * Set the encryption algorithm used into Database.
	 * @param pEncriptionAlgorithm
	 */
	public void setEncryptionAlgorithm(String pEncriptionAlgorithm) {
		EncryptionHelper.setEncriptionAlgorithm(pEncriptionAlgorithm);
	}
	
	/**
	 * Get the master encryption key.
	 * @return
	 */
	public String getMasterEncryptionKey() {
		return masterEncryptionKey;
	}
	/**
	 * Establish the master encryption key.
	 * @param masterEncriptionKey
	 */
	public void setMasterEncryptionKey(String masterEncriptionKey) {
		this.masterEncryptionKey = masterEncriptionKey;
	}
	
	/**
	 * Get if the Database process it's using transactions.
	 * @return If the DataBase process is transactional or not.
	 */
	public boolean isUseTransactions() {
		return useTransactions;
	}
	/**
	 * Define if the DataBase  operations uses transactions or not.
	 * @param useTransactions
	 */
	public void setUseTransactions(Boolean pUseTransactions) {
		Log.d(DataUtils.DEFAULT_LOGS_TAG, String.format("Use Transactions: %s", pUseTransactions.toString()));
		this.useTransactions = pUseTransactions;
	}
	
	/**
	 * Set if the model create table indexes for the foreign keys fields.
	 * @param pGenerateIndexes
	 */
	public void setUseTableIndexes(Boolean pGenerateIndexes) {
		Log.d(DataUtils.DEFAULT_LOGS_TAG, String.format("Use Table Indexes: %s", pGenerateIndexes.toString()));
		this.generateTableIndexes = pGenerateIndexes;
	}
	
	/**
	 * Get if the data model use table indexes for the foreign keys fields.
	 * @return
	 */
	public Boolean getUseTableIndexes() {
		return this.generateTableIndexes;
	}
	
	/**
	 * Define if the Insert Process uses InsertHelper interface.
	 * @param useTransactions
	 */
	public void setUseInsertHelpers(Boolean pUseInsertHelpers) {
		Log.d(DataUtils.DEFAULT_LOGS_TAG, String.format("Use Insert Helpers: %s", pUseInsertHelpers.toString()));
		this.useInsertHelppers = pUseInsertHelpers;
	}
	
	/**
	 * Get if the Insert Process uses InsertHelper interface.
	 */
	public Boolean getUseInsertHelpers() {
		return this.useInsertHelppers;
	}
	
	/** 
	 * Get the application context.
	 */
	public Context getContext() {
		return this.context;
	}
	
	/**
	 * Set the object context.
	 * @param pContext
	 */
	public void setContext(Context pContext) {
		this.context = pContext;
	}
	/**
	 * Get the Database file name.
	 */
	public String getDatabaseFileName() {
		return databaseFileName;
	}
	/**
	 * Set the Database file name.
	 * @param pDatabaseFileName
	 */
	private void setDatabaseFileName(String pDatabaseName) {
		this.databaseFileName = pDatabaseName;
	}
	
	/**
	 * Get the Database schema version.
	 */
	public int getDatabaseVersion() {
		return databaseVersion;
	}

	/** 
	 * @return DataBase Helper instance.
	 */
	/*
	private DataBaseHelper getHelper() {
		return this.databaseHelper;
	}
	*/
	
	/**
	 * Default constructor of the class.
	 * @param pDatabaseName
	 */
	public ObjectContext(Context pContext) {  
		setContext(pContext);
		
		this.databaseVersion = getCodeVersion();
		this.databaseHelper = new DataBaseHelper(this);
	}
	
	/**
	 * Secondary constructor of the class.
	 * @param pDatabaseName
	 * @throws ArgumentRequiredException 
	 */
	public ObjectContext(Context pContext, String pDatabaseName) {
		setContext(pContext);
		
		if (pDatabaseName != null) {
			if (pDatabaseName.trim() != "") {
				setDatabaseFileName(pDatabaseName);
				
				this.databaseVersion = getCodeVersion();
				this.databaseHelper = new DataBaseHelper(this);		
			} 
		}
	}	
	
	/**
	 * Secondary constructor of the class.
	 * @param pContext
	 * @param pDatabaseName
	 * @param pDatabaseVersion
	 */
	public ObjectContext(Context pContext, String pDatabaseName, int pDatabaseVersion) {
		setContext(pContext);
		
		if (pDatabaseName != null) {
			if (pDatabaseName.trim() != "") {
				setDatabaseFileName(pDatabaseName);
				
				this.databaseVersion = pDatabaseVersion;
				this.databaseHelper = new DataBaseHelper(this);
				
			} 
		}
	}
	
	/**
	 * Delete physical file of the database from to device.
	 * @return True if success.
	 */
	public Boolean deleteDatabase() {
		Boolean returnedValue = true;
		
		try {
			
			returnedValue = this.context.getApplicationContext().getDatabasePath(this.databaseFileName).delete();
			
		} catch (Exception e) {
			Log.e(DataUtils.DEFAULT_LOGS_TAG, e.getMessage());
			returnedValue = false;
		}
		
		return returnedValue;
	}
	
	/**
	 * Copy DataBase Backup file to default application folder..
	 * @return True if success.
	 */
	public Boolean backup() {
		File applicationDatabasePath = this.context.getApplicationContext().getDatabasePath(this.databaseFileName);
		File destinationPath = new File(applicationDatabasePath.getParent(), "");
		
		return backup(destinationPath);
	}
	
	/**
	 * Copy DataBase Backup file to Specified folder..
	 * @return True if success.
	 */
	public Boolean backup(String pDestinationFolder) {
		File destinationPath = new File(pDestinationFolder, "");
		
		return backup(destinationPath);
	}
	
	/**
	 * Copy DataBase Backup file to Specified folder.
	 * @return True if success.
	 */
	public Boolean backup(File pDestinationFolder) {
		Boolean returnedValue = true;
		
		try {
			
			//Generate the backup file name.
			String newFileName = String.format(DataUtils.DATABASE_BACKUP_NAME_PATTERN, this.context.getApplicationContext().getDatabasePath(this.databaseFileName).getName());
			
			File currentDatabaseFile = this.context.getApplicationContext().getDatabasePath(this.databaseFileName).getAbsoluteFile();
			File backupFile = new File(pDestinationFolder, newFileName);
			
			if (currentDatabaseFile.exists()) {
				if (backupFile.exists()) {
					backupFile.delete();
				}
	
	            FileInputStream in = new FileInputStream(currentDatabaseFile);
	            FileOutputStream out = new FileOutputStream(backupFile);
	            
	            byte[] buf = new byte[1024];
	            int i = 0;
	            while ((i = in.read(buf)) != -1) {
	                out.write(buf, 0, i);
	            }
	            in.close();
	            out.close();
		        
				returnedValue = true;
			} else {
				Log.d(DataUtils.DEFAULT_LOGS_TAG, "DataBase file does not exist.");
				returnedValue = false;
			}
			
		} catch (Exception e) {
			Log.e(DataUtils.DEFAULT_LOGS_TAG, e.getMessage());
			returnedValue = false;
		}
		
		return returnedValue;
	}
	
	
	/**
	 * Restore DataBase Backup file from default application folder.
	 * @return True if success.
	 */
	public Boolean restore() {
		File applicationDatabasePath = this.context.getApplicationContext().getDatabasePath(this.databaseFileName);
		File sourcePath = new File(applicationDatabasePath.getParent(), "");
		
		return restore(sourcePath);
	}
	
	/**
	 * Restore DataBase Backup file from Specified folder..
	 * @return True if success.
	 */
	public Boolean restore(String pSourceFolder) {
		File sourcePath = new File(pSourceFolder, "");
		
		return restore(sourcePath);
	}
	
	/**
	 * Restore DataBase Backup file from Specified folder.
	 * @return True if success.
	 */
	public Boolean restore(File pSourceFolder) {
		Boolean returnedValue = true;
		
		try {
			
			//Generate the backup file name.
			String backupFileName = String.format(DataUtils.DATABASE_BACKUP_NAME_PATTERN, this.context.getApplicationContext().getDatabasePath(this.databaseFileName).getName());
			
			File backupFile = new File(pSourceFolder, backupFileName);
			File currentDatabaseFile = this.context.getApplicationContext().getDatabasePath(this.databaseFileName).getAbsoluteFile();
			
			
			if (backupFile.exists()) {
				if (currentDatabaseFile.exists()) {
					currentDatabaseFile.delete();
				}
	
	            FileInputStream in = new FileInputStream(backupFile);
	            FileOutputStream out = new FileOutputStream(currentDatabaseFile);
	            
	            byte[] buf = new byte[1024];
	            int i = 0;
	            while ((i = in.read(buf)) != -1) {
	                out.write(buf, 0, i);
	            }
	            in.close();
	            out.close();
		        
				returnedValue = true;
			} else {
				returnedValue = false;
				Log.d(DataUtils.DEFAULT_LOGS_TAG, "BackUp file does not exist.");
			}
			
		} catch (Exception e) {
			Log.e(DataUtils.DEFAULT_LOGS_TAG, e.getMessage());
			returnedValue = false;
		}
		
		return returnedValue;
	}
	
	/***
	 * On Create Database method.
	 * @param pDataBase
	 * @throws AdaFrameworkException
	 */
	protected final void onCreateDataBase(SQLiteDatabase pDataBase) throws AdaFrameworkException {
		this.onCreate(pDataBase);
	}
	
	/***
	 * On Update Database Method.
	 * @param pDataBase
	 * @param pOldVersion
	 * @param pNewVersion
	 * @throws Exception
	 */
	protected final void onUpdateDataBase(SQLiteDatabase pDataBase, int pOldVersion, int pNewVersion) throws AdaFrameworkException {
		this.onUpdate(pDataBase, pOldVersion, pNewVersion);
	}

	
	/**
	 * Use this method to populate database. 
	 */
	protected void onPopulate(SQLiteDatabase pDatabase) { }
	
	/**
	 * Method called before database creation.
	 * @param pDataBase
	 */
	protected void onPreCreate(SQLiteDatabase pDataBase) { }
	
	/**
	 * Auto DataBase Schema generator. 
	 * @param pDataBase
	 * @throws Exception 
	 */
	protected void onCreate(SQLiteDatabase pDataBase) throws AdaFrameworkException {
		generateDataBase(pDataBase, DataUtils.DATABASE_ACTION_CREATE);
	}
	
	/**
	 * Method called after database creation.
	 * @param pDataBase
	 */
	protected void onPostCreate(SQLiteDatabase pDataBase) { }
	
	/**
	 * Method called before database update.
	 * @param pDataBase
	 */
	protected void onPreUpdate(SQLiteDatabase pDataBase, int pOldVersion, int pNewVersion) { }
	
	/**
	 * Auto DataBase Schema generator.
	 * @param pDataBase
	 * @param pOldVersion
	 * @param pNewVersion
	 * @throws AdaFrameworkException 
	 */
	protected void onUpdate(SQLiteDatabase pDataBase, int pOldVersion, int pNewVersion) throws AdaFrameworkException {
		generateDataBase(pDataBase, DataUtils.DATABASE_ACTION_UPDATE);
	}
	
	/**
	 * Method called after database update.
	 * @param pDataBase
	 */
	protected void onPostUpdate(SQLiteDatabase pDataBase, int pOldVersion, int pNewVersion) { }
	
	/***
	 * This method generate Database model schema. 
	 * @param pDataBase
	 * @param pAction
	 * @throws Exception
	 */
	private void generateDataBase(SQLiteDatabase pDataBase, int pAction) throws AdaFrameworkException {
		Date initOfProcess = new Date();
		
		try{
			
			//Get all declared fields in the Object
			Field[] declaredFields = this.getClass().getDeclaredFields();
			List<String> tableIndexScript;
			if (declaredFields != null) {
				if (declaredFields.length > 0) {
					for(Field declaredField : declaredFields) {
						Object  objectSet = null;
						boolean virtualObjectSet = false;
						
						if (declaredField.getModifiers() == Modifier.PRIVATE) {
							Method getterMethod = null;
							try {
								getterMethod = this.getClass().getMethod(String.format("get%s", DataUtils.capitalize(declaredField.getName())), (Class[])null);
							} catch (Exception e) {
								getterMethod = null;
							}
							if (getterMethod == null) {
								try {
									getterMethod = this.getClass().getMethod(String.format("get%s", declaredField.getName()), (Class[])null);
								} catch (Exception e) {
									getterMethod = null;
								}
							}
							
							if(getterMethod == null) {
								throw new AdaFrameworkException(String.format("The field '%s' of the ObjectContext is not accesible. Please change the field modifier or create a getter method.", declaredField.getName()));
							} else {
								objectSet = getterMethod.invoke(this, (Object[])null);
							}
						} else {
							objectSet = declaredField.get(this);
						}
						
						
						try {
							ObjectSetConfiguration objectSetConfiguration = declaredField.getAnnotation(ObjectSetConfiguration.class);
							if (objectSetConfiguration != null) {
								virtualObjectSet = objectSetConfiguration.virtual();
							}
						} catch (Exception e) { }
						
						//Check if the ObjectSet is virtual or not.
						if (!virtualObjectSet) {
							//Check if the field is a ObjectSet instance.
							if (objectSet instanceof ObjectSet) {
								String tableName = ((ObjectSet<?>)objectSet).getDataBaseTableName();
								String tableScript = ((ObjectSet<?>)objectSet).getDataBaseTableScript();
								
								generateTable(pDataBase, tableName, tableScript, pAction);
								
								if (generateTableIndexes) {
									tableIndexScript = ((ObjectSet<?>)objectSet).getDataBaseTableIndexes();
									if (tableIndexScript != null) {
										if (tableIndexScript.size() > 0) {
											for(String indexScript : tableIndexScript) {
												generateIndex(pDataBase, indexScript);
											}
										}
									}
								}
								
								if (((ObjectSet<?>)objectSet).ContainInheritedEntities()) {
									Enumeration<ObjectSet<Entity>> inheritedFields = ((ObjectSet<?>)objectSet).getInheritedObjectSets();
									
									while(inheritedFields.hasMoreElements()) {
										ObjectSet<Entity> inheritedObjectSet = inheritedFields.nextElement();
										
										tableName = inheritedObjectSet.getDataBaseTableName();
										tableScript = inheritedObjectSet.getDataBaseTableScript();
										
										generateTable(pDataBase, tableName, tableScript, pAction);
										
										if (generateTableIndexes) {
											tableIndexScript = inheritedObjectSet.getDataBaseTableIndexes();
											if (tableIndexScript != null) {
												if (tableIndexScript.size() > 0) {
													for(String indexScript : tableIndexScript) {
														generateIndex(pDataBase, indexScript);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		
		} catch (Exception e) {
			ExceptionsHelper.manageException(null, e);
		}
		
		Date endOfProcess = new Date();
		String totalTime = DataUtils.calculateTimeDiference(initOfProcess, endOfProcess);
		Log.d(DataUtils.DEFAULT_LOGS_TAG, String.format("TOTAL Time to generate entities Data Model: %s.", totalTime));
	}
	
	/***
	 * This generate a table into Database.
	 * @param pDataBase
	 * @param pTableName
	 * @param pTableScript
	 * @param pAction
	 * @throws AdaFrameworkException
	 */
	private void generateTable(SQLiteDatabase pDataBase, String pTableName, String pTableScript, int pAction) throws AdaFrameworkException {
		Boolean createTable = true;
		
		try{
			
			if (createTable) {
				Log.d(DataUtils.DEFAULT_LOGS_TAG, pTableScript);
				pDataBase.execSQL(pTableScript);
			}
			
		} catch (Exception e) {
			ExceptionsHelper.manageException(null, e);
		}
	}

	/***
	 * This method generate a Index into Database.
	 * @param pDataBase
	 * @param pTableIndexScript
	 * @throws AdaFrameworkException
	 */
	private void generateIndex(SQLiteDatabase pDataBase, String pTableIndexScript) throws AdaFrameworkException {
		Boolean createTable = true;
		
		try{
			
			if (createTable) {
				Log.d(DataUtils.DEFAULT_LOGS_TAG, pTableIndexScript);
				pDataBase.execSQL(pTableIndexScript);
			}
			
		} catch (Exception e) {
			ExceptionsHelper.manageException(null, e);
		}
	}
	
	/**
	 * This method obtain a readable Database connection.
	 * @return Readable DataBase instance.
	 */
	protected final SQLiteDatabase getReadableDatabase() {
		Log.d(DataUtils.DEFAULT_LOGS_TAG, "Getting Readable Database.");
		return this.databaseHelper.getReadableDatabase();
	}

	/**
	 * This method obtain a writable Database connection.
	 * @return Writable DataBase instance.
	 */
	protected final SQLiteDatabase getWritableDatabase() {
		Log.d(DataUtils.DEFAULT_LOGS_TAG, "Getting Writable Database.");
		return this.databaseHelper.getWritableDatabase();
	}
	
	/***
	 * This method execute SELECT Query.
	 * @param pDatabase
	 * @param pDistinct
	 * @param pTable
	 * @param pColumns
	 * @param pSelection
	 * @param pSelectionArgs
	 * @param pGroupBy
	 * @param pHaving
	 * @param pOrderBy
	 * @param pLimit
	 * @return
	 */
	protected Cursor executeQuery(SQLiteDatabase pDatabase, Boolean pDistinct, String pTable, String[] pColumns, String pSelection, String[] pSelectionArgs, String pGroupBy, String pHaving, String pOrderBy, String pLimit) {
		return pDatabase.query(pDistinct, pTable, pColumns, pSelection, pSelectionArgs, pGroupBy, pHaving, pOrderBy, pLimit);
	}	

	/**
	 * This method execute INSERT Query.
	 * @param pDatabase
	 * @param pTable
	 * @param pNullColumnHack
	 * @param pValues
	 * @return
	 */
	protected  Long executeInsert(SQLiteDatabase pDatabase, String pTable, String pNullColumnHack, ContentValues pValues) {
		Log.d(DataUtils.DEFAULT_LOGS_TAG, String.format("INSERT INTO TABLE %s", pTable));
		return pDatabase.insert(pTable, pNullColumnHack, pValues);
	}
	
	/**
	 * This method execute UPDATE Query.
	 * @param pDatabase
	 * @param pTable
	 * @param pValues
	 * @param pWhereClause
	 * @param pWhereArgs
	 * @return
	 */
	protected Integer executeUpdate(SQLiteDatabase pDatabase, String pTable, ContentValues pValues, String pWhereClause, String[] pWhereArgs) {
		Log.d(DataUtils.DEFAULT_LOGS_TAG, String.format("UPDATE ON TABLE %s", pTable));
		return pDatabase.update(pTable, pValues, pWhereClause, pWhereArgs);
	}
	
	/**
	 * This method execute DELETE Query.
	 * @param pDatabase
	 * @param pTable
	 * @param pWhereClause
	 * @param pWhereArgs
	 * @return
	 */
	protected Integer executeDelete(SQLiteDatabase pDatabase, String pTable, String pWhereClause, String[] pWhereArgs) {
		Log.d(DataUtils.DEFAULT_LOGS_TAG, String.format("DELETE FROM TABLE %s", pTable));
		return pDatabase.delete(pTable, pWhereClause, pWhereArgs);
	}
	
	/**
	 * This method return application source code version.
	 * @return Application source code version.
	 */
	private int getCodeVersion() {
		int returnedValue = this.databaseVersion;
		
		try {
			returnedValue = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionCode;
		} catch (Exception e) {
			returnedValue = this.databaseVersion;
		}
		
		return returnedValue;
	}
	
	/**
	 * Parse entity property value to save into DataBase.
	 * @param pValue
	 * @param pMappnig
	 * @return
	 * @throws AdaFrameworkException 
	 */
	String prepareObjectToDatabase(Object pValue, DataMapping pMapping) throws AdaFrameworkException {
		String returnedValue = "NULL";
		
		if (pValue != null) {
			if (pValue instanceof Date) {
				returnedValue = DateToString((Date)pValue);
				
				if (pMapping.Encrypted) {
					returnedValue = EncryptionHelper.encrypt(getMasterEncryptionKey(), returnedValue);
				}
			} else {
				if (pValue instanceof Boolean) {
					if ((Boolean)pValue) {
						returnedValue = "1";
					} else {
						returnedValue = "0";
					}
				} else {
					if (pMapping.DataBaseDataType == Entity.DATATYPE_ENTITY_REFERENCE) {
						if(pValue instanceof Entity) {
							returnedValue = ((Entity)pValue).ID.toString();
						}
					} else {
						returnedValue = pValue.toString();
						
						if (pMapping.DataBaseLength > 0) {
							if (returnedValue.length() > pMapping.DataBaseLength) {
								returnedValue = returnedValue.substring(0, pMapping.DataBaseLength);
							}
						}
						
						if (pMapping.Encrypted) {
							returnedValue = EncryptionHelper.encrypt(getMasterEncryptionKey(), returnedValue);
						}
					}
				}
			}
			
			
		}
		
		return returnedValue;
	}
	
	/**
	 * Parse DataBase Date to Java Date.
	 * @param date
	 * @return
	 */
	Date StringToDate(String date) {
		Date returnedValue = new Date();
		
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			returnedValue = dateFormat.parse(date);
			
		} catch (Exception e) {
			returnedValue = new Date();
		}
		
		return returnedValue;
	}
	
	/**
	 * Parse Java Date to DataBase Date.
	 * @param date
	 * @return
	 */
	String DateToString(Date date) {
		String returedValue = "NULL";

		if (date != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			returedValue = dateFormat.format(date);
			
			if (returedValue.trim().equals("")) {
				returedValue = "NULL";
			}
		}
		
		return returedValue;
	}
}
