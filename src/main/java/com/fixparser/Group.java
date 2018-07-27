package com.fixparser;

import java.util.LinkedHashMap;

public class Group {
	private GroupInfo groupInfo;
	
	private LinkedHashMap<Integer, Field<?>> fields = new LinkedHashMap<>();

	public Group(GroupInfo groupInfo) {
		super();
		this.groupInfo = groupInfo;
	}

	public GroupInfo getGroupInfo() {
		return groupInfo;
	}
	
	public void setField(Field<?> field) {
		fields.put(field.getTag(), field);
	}
	
	public boolean containsField(int tag) {
		return fields.containsKey(tag);
	}

	@Override
	public String toString() {
		return "Group [groupIdentifier - " + groupInfo.getGroupIdentifier() + ", fields=" + fields.values() + "]";
	}

}
