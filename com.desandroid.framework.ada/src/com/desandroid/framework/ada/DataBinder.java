package com.desandroid.framework.ada;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import com.desandroid.framework.ada.exceptions.AdaFrameworkException;
import android.R.bool;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ToggleButton;

/**
 * Class dedicated to binding data between the Entities and the User Interface.
 * Extend of this class to make your custom DataBinders.
 * @version 1.4.5
 * @author DesAndrOId
 */
public class DataBinder {
	/**
	 * Define Data Binding direction between Entity and UI.
	 * 	   Entity --> UI Controls Layer
	 */
	public static final int BINDING_ENTITY_TO_UI = 1;
	
	/**
	 * Define Data Binding direction between UI and Entity.
	 * 	   UI Controls Layer --> Entity
	 */
	public static final int BINDING_UI_TO_ENTITY = 2;
	
	/**
	 * Parse and Casting object value.
	 * @param pBinding
	 * @param pValue
	 * @return
	 */
	private Object parseValue(DataBinding pBinding, Object pValue) {
		Object returnedValue = null;
		
		if(pValue != null) {
			final Class<?> entityFielType = pBinding.EntityField.getType();
			
			if (entityFielType == String.class) {
				if (pValue.getClass() == String.class) {
					returnedValue = (String)pValue;
				} else {
					returnedValue = pValue.toString();
				}
			} else if (entityFielType == Integer.class || entityFielType == int.class) {
				if (pValue.getClass() == String.class) {
					if (!((String)pValue).trim().equals("")) {
						returnedValue = Integer.parseInt((String)pValue);
					}
				} else {
					if (!((String)pValue).trim().equals("")) {
						returnedValue = Integer.parseInt(pValue.toString());
					}
				}
			} else if (entityFielType == Long.class || entityFielType == long.class) {
				if (pValue.getClass() == String.class) {
					if (!((String)pValue).trim().equals("")) {
						returnedValue = Long.parseLong((String)pValue);
					}
				} else {
					if (!((String)pValue).trim().equals("")) {
						returnedValue = Long.parseLong(pValue.toString());
					}
				}
			} else if (entityFielType == Double.class || entityFielType == double.class) {
				if (pValue.getClass() == String.class) {
					if (!((String)pValue).trim().equals("")) {
						returnedValue = Double.parseDouble((String)pValue);
					}
				} else {
					if (!((String)pValue).trim().equals("")) {
						returnedValue = Double.parseDouble(pValue.toString());
					}
				}
			}  else if (entityFielType == Float.class || entityFielType == float.class) {
				if (pValue.getClass() == String.class) {
					if (!((String)pValue).trim().equals("")) {
						returnedValue = Float.parseFloat((String)pValue);
					}
				} else {
					if (!((String)pValue).trim().equals("")) {
						returnedValue = Float.parseFloat(pValue.toString());
					}
				}
			}  else if (entityFielType == Short.class || entityFielType == short.class) {
				if (pValue.getClass() == String.class) {
					if (!((String)pValue).trim().equals("")) {
						returnedValue = Short.parseShort((String)pValue);
					}
				} else {
					if (!((String)pValue).trim().equals("")) {
						returnedValue = Short.parseShort(pValue.toString());
					}
				}
			}  else if (entityFielType == Boolean.class || entityFielType == boolean.class || entityFielType == bool.class) {
				if (pValue.getClass() == String.class) {
					if (!((String)pValue).trim().equals("")) {
						returnedValue = Boolean.parseBoolean((String)pValue);
					}
				} else {
					if (pValue.toString().trim() != "") {
						returnedValue = Boolean.parseBoolean(pValue.toString());
					}
				}
			}  else if (entityFielType == Date.class) {
				if (pValue.getClass() == Date.class) {
					returnedValue = pValue;
				} else {
					if (pValue.getClass() == String.class) {
						if (!((String)pValue).trim().equals("")) {
							returnedValue = Date.parse((String)pValue);
						}
					} else {
						if (!((String)pValue).trim().equals("")) {
							returnedValue = Date.parse(pValue.toString());
						}
					}
				}
			} else {
				returnedValue = pValue;
			}
		}
		
		return returnedValue;
	}
	
	/**
	 * Get Entity property value.
	 * @param pBinding
	 * @param pEntity
	 * @param pView
	 * @param pDirection
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException 
	 */
	private Object getBindingValue(DataBinding pBinding, Entity pEntity, View pView, int pDirection) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Object returnedValue = null;
		
		if (pView != null) {
			if (pEntity != null) {
				if (pDirection == BINDING_ENTITY_TO_UI) {
					if (pBinding.getterMethod != null) {
						returnedValue = pBinding.getterMethod.invoke(pEntity, (Object[])null);
					} else {
						returnedValue = pBinding.EntityField.get(pEntity);
					}
				} else if (pDirection == BINDING_UI_TO_ENTITY) {
					if (pView instanceof CheckBox) {
						returnedValue = ((CheckBox)pView).isChecked();
					} else if (pView instanceof SeekBar) {
						returnedValue = ((SeekBar)pView).getProgress();
					} else if (pView instanceof ProgressBar) {
						returnedValue = ((ProgressBar)pView).getProgress();
					} else if (pView instanceof ImageView) {
						returnedValue = ((ImageView)pView).getDrawingCache();
					} else if (pView instanceof RatingBar) {
						returnedValue = ((RatingBar)pView).getRating();
					} else if (pView instanceof RadioButton) {
						returnedValue = ((RadioButton)pView).isChecked();
					} else if (pView instanceof ToggleButton) {
						returnedValue = ((ToggleButton)pView).isChecked();
					} else if (pView instanceof Button) {
						returnedValue = ((Button)pView).getText().toString();
					} else if (pView instanceof TextView) {
						returnedValue = ((TextView)pView).getText().toString();
					} else if (pView instanceof EditText) {
						returnedValue = ((EditText)pView).getText().toString();
					}
				}
			}
		}
		
		if (pBinding.Parser != null) {
			Object dataParser = pBinding.Parser.newInstance();
			if (dataParser != null) {
				if (dataParser instanceof DataParser) {
					returnedValue = ((DataParser)dataParser).parseValue(returnedValue, pDirection);
				}
			}
		}
		
		return returnedValue;
	}
	
	/**
	 * Set Entity property value.	
	 * @param pBinding
	 * @param pEntity
	 * @param pView
	 * @param pDirection
	 * @param pValue
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws AdaFrameworkException
	 */
	private void setBindingValue(DataBinding pBinding, Entity pEntity, View pView, int pDirection, Object pValue) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, AdaFrameworkException {
		if (pView != null) {
			if (pEntity != null) {
				if (pDirection == BINDING_UI_TO_ENTITY) {
					
					try {
						if (pBinding.setterMethod != null) {
							pBinding.setterMethod.invoke(pEntity, parseValue(pBinding, pValue));
						} else {
							pBinding.EntityField.set(pEntity, parseValue(pBinding, pValue));
						}
					} catch (Exception e) {
						throw new AdaFrameworkException(e);
					}
					
				} else if (pDirection == BINDING_ENTITY_TO_UI) {
					if (pView instanceof CheckBox) {
						if (pValue != null) {
							if (pValue instanceof Boolean) {
								((CheckBox)pView).setChecked((Boolean)pValue);
							} else {
								throw new AdaFrameworkException(String.format(DataUtils.EXCEPTION_CAST_CONVERSION, Boolean.class.getName(), pBinding.EntityField.getType().getName()));
							}
						}
					} else if (pView instanceof SeekBar) {
						if (pValue != null) {
							if (pValue instanceof Integer) {
								((SeekBar)pView).setProgress((Integer)pValue);
							} else {
								throw new AdaFrameworkException(String.format(DataUtils.EXCEPTION_CAST_CONVERSION, Integer.class.getName(), pBinding.EntityField.getType().getName()));
							}
						}
					} else if (pView instanceof ProgressBar) {
						if (pValue != null) {
							if (pValue instanceof Integer) {
								((ProgressBar)pView).setProgress((Integer)pValue);
							} else {
								throw new AdaFrameworkException(String.format(DataUtils.EXCEPTION_CAST_CONVERSION, Integer.class.getName(), pBinding.EntityField.getType().getName()));
							}
						}
					} else if (pView instanceof RatingBar) {
						if (pValue != null) {
							if (pValue instanceof Float) {
								((RatingBar)pView).setRating((Float)pValue);
							} else {
								throw new AdaFrameworkException(String.format(DataUtils.EXCEPTION_CAST_CONVERSION, Float.class.getName(), pBinding.EntityField.getType().getName()));
							}
						}
					} else if (pView instanceof ImageView) {
						if (pValue != null) {
							if (pValue instanceof Bitmap) {
								((ImageView)pView).setImageBitmap((Bitmap)pValue);
							} else {
								throw new AdaFrameworkException(String.format(DataUtils.EXCEPTION_CAST_CONVERSION, Bitmap.class.getName(), pBinding.EntityField.getType().getName()));
							}
						} else {
							((ImageView)pView).setImageBitmap(null);
						}
					} else if (pView instanceof RadioButton) {
						if (pValue != null) {
							if (pValue instanceof Boolean) {
								((RadioButton)pView).setChecked((Boolean)pValue);
							} else {
								throw new AdaFrameworkException(String.format(DataUtils.EXCEPTION_CAST_CONVERSION, Boolean.class.getName(), pBinding.EntityField.getType().getName()));
							}
						}
					} else if (pView instanceof ToggleButton) {
						if (pValue != null) {
							if (pValue instanceof Boolean) {
								((ToggleButton)pView).setChecked((Boolean)pValue);
							} else {
								throw new AdaFrameworkException(String.format(DataUtils.EXCEPTION_CAST_CONVERSION, Boolean.class.getName(), pBinding.EntityField.getType().getName()));
							}
						}
					} else if (pView instanceof Button) {
						if (pValue instanceof String) {
							((EditText)pView).setText((String)pValue);
						} else {
							if (pValue != null) {
								((EditText)pView).setText(pValue.toString());
							} else {
								((EditText)pView).setText("");
							}
						}
					} else if (pView instanceof TextView) {
						if (pValue instanceof String) {
							((TextView)pView).setText((String)pValue);
						} else {
							if (pValue != null) {
								((TextView)pView).setText(pValue.toString());
							} else {
								((TextView)pView).setText("");
							}
						}
					} else if (pView instanceof EditText) {
						if (pValue instanceof String) {
							((EditText)pView).setText((String)pValue);
						} else {
							if (pValue != null) {
								((EditText)pView).setText(pValue.toString());
							} else {
								((EditText)pView).setText("");
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Execute the Data Binding logic.
	 * @param pView
	 */
	public void bind(DataBinding pBinding, Entity pEntity, View pView, int pDirection) throws AdaFrameworkException {
		if (pDirection == BINDING_ENTITY_TO_UI || pDirection == BINDING_UI_TO_ENTITY) {
			
			try {
				
				if (pView != null) {
					Object value = getBindingValue(pBinding, pEntity, pView, pDirection);
					setBindingValue(pBinding, pEntity, pView, pDirection, value);
				}
				
			} catch (Exception e) {
				throw new AdaFrameworkException(e);
			}
				
		} else {
			throw new AdaFrameworkException("Invalid databinding direction, please use BINDING_ENTITY_TO_UI or BINDING_UI_TO_ENTITY.");
		}		
	}
}
