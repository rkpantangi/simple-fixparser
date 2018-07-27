package com.fixparser;

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
	

}
