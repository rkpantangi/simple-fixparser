package com.fixparser;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;

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

	public Field<?> getField(int tag) {
		return fields.get(tag);
	}

	public Set<Integer> getGroupMemberTags() {
		return Collections.unmodifiableSet(fields.keySet());
	}
	
	@Override
	public String toString() {
		return "Group [groupIdentifier - " + groupInfo.getGroupIdentifier() + ", fields=" + fields.values() + "]";
	}

}
