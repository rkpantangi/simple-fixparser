package com.fixparser;

public class StringField extends Field<String> {

	public StringField(int tag, String data) {
		super(tag, data);
	}

	public static StringField fromString(int tag, String val) {
		return new StringField(tag, val);
	}
	
	public FieldDataType getFieldDataType() {
		return FieldDataType.STRING;
	}
}
