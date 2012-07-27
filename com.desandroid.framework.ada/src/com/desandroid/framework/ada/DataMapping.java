package com.desandroid.framework.ada;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

/**
 * Internal Entity fields DataMapping class.
 * @version 1.4.5
 * @author DesAndrOId
 */
class DataMapping {
	public Class<?> EntityManagedType = null;
	public String EntityFieldName = "";
	public Field EntityManagedField = null;
	public String DataBaseTableName = "";
	public String DataBaseFieldName = "";
	public int DataBaseDataType = Entity.DATATYPE_STRING;
	public int DataBaseLength = 20;
	public int DataBaseColumnIndex = 0;
	public boolean DataBaseAllowNulls = true;
	public boolean DataBaseIsPrimaryKey = false;
	public boolean Encrypted = false;
	public boolean Unique = false;
	public boolean ForeignKey = false;
	public boolean IsCollection = false;
	public boolean IsSpecialField = false;
	public CompressFormat BitmapCompression = Bitmap.CompressFormat.PNG;
	public Method getterMethod = null;
	public Method setterMethod = null;
	public Boolean virtual = false;
}
