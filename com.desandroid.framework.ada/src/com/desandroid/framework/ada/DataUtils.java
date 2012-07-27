package com.desandroid.framework.ada;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Internal Framework Utils class.
 * @version 1.4.5
 * @author DesAndrOId
 */
final class DataUtils {
	protected  static final String DATABASE_BACKUP_NAME_PATTERN = "BACKUP_%s.bak";	
	protected  static final String DATABASE_INSERT_PATTERN = "INSERT INTO %s (%s) VALUES (%s)";
	protected  static final String DATABASE_UPDATE_PATTERN = "UPDATE %s SET %s WHERE %s";
	protected  static final String DATABASE_DELETE_PATTERN = "DELETE FROM %s WHERE %s";
	
	protected  static final boolean DATABASE_USE_INDEXES = true;
	
	protected  static final String DATABASE_ID_FIELD_NAME = "ID";
	protected  static final String DATABASE_ID_FIELD_WHERE = DATABASE_ID_FIELD_NAME + " = ?";
	protected  static final String DATABASE_FK_FIELD_PATTERN = "FK_%s_ID";
	protected  static final String DATABASE_INDEX_FIELD_PATTERN = "INDEX_%s_%s";

	protected  static final String DATABASE_TABLE_INDEX_PATTERN = "CREATE INDEX IF NOT EXISTS %s ON %s(%s);";
	protected  static final String DATABASE_TABLE_PATTERN = "CREATE TABLE IF NOT EXISTS %s (%s %s %s)";
	protected  static final String DATABASE_TABLE_UNIQUE_PATTERN = ", UNIQUE (%s) ON CONFLICT FAIL";
	protected  static final String DATABASE_TABLE_FOREIGN_KEY_PATTERN = ", FOREIGN KEY (%s) REFERENCES %s(ID)";
	
	protected  static final String DATABASE_FIELD_SEPARATOR = ", ";
	protected  static final String DATABASE_FIELD_VALUE = "?";
	protected  static final String DATABASE_LIMIT_PATTERN = " %d";
	protected  static final String DATABASE_LIMIT_OFFTSET_PATTERN = " %d,  %d";
	
	public static final String DEFAULT_DATABASE_NAME = "database.db";
	public static final String DEFAULT_MASTER_ENCRIPTION_KEY = "com.desandroid.framework.ada";
	public static final String DEFAULT_ENCRIPTION_ALGORITHM = "AES";
	public static final String DEFAULT_LOGS_TAG = "com.desandroid.framework.ada";
	
	public static final int DATABASE_ACTION_CREATE = 1;
	public static final int DATABASE_ACTION_UPDATE = 2;
	
	
	public static final int DATABASE_BOOLEAN_VALUE_FALSE = 0;
	public static final int DATABASE_BOOLEAN_VALUE_TRUE = 1;
	
	public static final String LOG_TAG = "com.desandroid.framework.ada";
	
	public static final String EXCEPTION_CAST_CONVERSION = "Invalid binding types conversion. Can not be convert %s to %s";
	
	
	static String capitalize(String pValue) {
        if (pValue.length() <= 1) return pValue;
        return pValue.substring(0, 1).toUpperCase() + pValue.substring(1);
    }
	
	static String calculateTimeDiference(Date pInitialDate, Date pFinishDate) {
		Long initialTime = pInitialDate.getTime();
		Long finishTime =pFinishDate.getTime();
		
		long totalTime = finishTime - initialTime;
		
		DateFormat df = new SimpleDateFormat("HH:mm:ss.S");
		String returnedValue = df.format(new Date(totalTime));
		
		return returnedValue;
	}
}
