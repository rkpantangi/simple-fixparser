package com.fixparser;

public class IntegerField extends Field<Integer> {

	public IntegerField(int tag, Integer data) {
		super(tag, data);
	}

	public static IntegerField fromString(int tag, String val) {
		Integer intVal = Converters.getInt(val);
		return new IntegerField(tag, intVal);
	}
	
	public FieldDataType getFieldDataType() {
		return FieldDataType.INTEGER;
	}

}
