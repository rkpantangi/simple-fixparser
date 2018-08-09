package com.fixparser;

/**
 * @author Ram
 *
 * @param <T>
 * 
 * Abstract Field type. I created just IntegerField/StringField for demonstration. It can be extended for various data types.
 */
public abstract class Field<T> {
	
	private int tag;
	private T data;

	public Field(int tag, T data) {
		super();
		this.tag = tag;
		this.data = data;
	}

	public int getTag() {
		return tag;
	}

	public T getData() {
		return data;
	}
	
	public abstract FieldDataType getFieldDataType(); 
	
	public static Integer getIntData(Field<?> field) {
		if (FieldDataType.INTEGER == field.getFieldDataType()) {
			return (Integer)field.getData();
		}
		return null;
	}

	public static String getStringData(Field<?> field) {
		if (FieldDataType.STRING == field.getFieldDataType()) {
			return (String)field.getData();
		}
		return null;
	}

	@Override
	public String toString() {
		return tag + " - " + data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + tag;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Field other = (Field) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (tag != other.tag)
			return false;
		return true;
	}
	
	

}
