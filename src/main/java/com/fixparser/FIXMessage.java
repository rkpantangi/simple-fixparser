package com.fixparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FIXMessage {

	Map<Integer, Field<?>> fields = new LinkedHashMap<>();
	Map<Integer, List<Group>> groups = new LinkedHashMap<>();
	
	public Field<?> getField(int tag) {
		return fields.get(tag);
	}
	
	public List<Group> getGroup(int groupIdentifier) {
		return Collections.unmodifiableList(groups.get(groupIdentifier));
	}
	
	public void addField(Field<?> field) {
		fields.put(field.getTag(), field);
	}

	public void addGroup(Group group) {
		List<Group> repGroups = groups.getOrDefault(group.getGroupInfo().getGroupIdentifier(), new ArrayList<>());
		repGroups.add(group);
		groups.put(group.getGroupInfo().getGroupIdentifier(), repGroups);
	}
	
	public void addGroups(List<Group> groups) {
		groups.forEach(x -> {
			if (x != null) {
				addGroup(x);
			}
		});
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("FIXMessage \n[fields=");

		for (Entry<Integer, Field<?>> entry : fields.entrySet()) {
			s.append("\n\t").append(entry.getValue());
		}

		s.append("\n]\n[groups=\n");
		
		for (Entry<Integer, List<Group>> group : groups.entrySet()) { 
			for (Group g : group.getValue()) {
				s.append("\t" + g + "\n");				
			}
		}
		s.append("\n]");
		return s.toString();
	}

}
