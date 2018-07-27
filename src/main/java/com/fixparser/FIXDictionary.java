package com.fixparser;

import java.util.HashMap;
import java.util.Map;

public class FIXDictionary {
	public static enum TagType {
		UNKNOWN, SINGLE, GROUP
	}
	
	private Map<Integer, FieldDataType> validFields = new HashMap<>();
	private Map<Integer, GroupInfo> validGroups = new HashMap<>();
	
	public FIXDictionary(Map<Integer, FieldDataType> validFields, Map<Integer, GroupInfo> validGroups) {
		if (validFields != null) {
			this.validFields = validFields;
		}
		if (validGroups != null) {
			this.validGroups = validGroups;
		}
	}
	
	/**
	 * @param tag
	 * @return
	 */
	public TagType getTagType(Integer tag) {
		if (validGroups.containsKey(tag)) return TagType.GROUP;
		if (validFields.containsKey(tag)) return TagType.SINGLE;
		return TagType.UNKNOWN;
	}
	
	public FieldDataType getFieldDataType(int tag) {
		return validFields.get(tag);
	}

	public Field<?> getField(int tag, String value) {
		FieldDataType ft = getFieldDataType(tag);
		Field<?> field = null;
		if (FieldDataType.INTEGER == ft) {
			field = IntegerField.fromString(tag, value);
		} else if (FieldDataType.STRING == ft) {
			field = StringField.fromString(tag, value);
		}
		return field;
	}
	
	public GroupInfo getGroupInfo(int groupIndicator) {
		return validGroups.get(groupIndicator);
	}
}
