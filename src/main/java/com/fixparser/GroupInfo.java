package com.fixparser;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class GroupInfo {

	private int groupIdentifier;
	private int firstTag;
	private Set<Integer> tags = new HashSet<>();
	
	public GroupInfo(int groupIdentifier, int firstTag, Set<Integer> tags) {
		super();
		//TODO Add validation
		this.groupIdentifier = groupIdentifier;
		this.firstTag = firstTag;
		Objects.requireNonNull(tags);
		tags.forEach(tag -> {
			if (tag != null && tag >= 0) {
				this.tags.add(tag);				
			}
		});
	}
	
	public boolean addTag(int tag) {
		if (tag <= 0) throw new IllegalArgumentException("Tag cannot be <= 0");
		return tags.add(tag);
	}
	
	public boolean removeTag(int tag) {
		return tags.remove(tag);
	}

	public int getGroupIdentifier() {
		return groupIdentifier;
	}

	public int getFirstTag() {
		return firstTag;
	}
	
	public boolean containsTag(int tag) {
		return tags.contains(tag);
	}

}
