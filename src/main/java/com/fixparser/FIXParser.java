package com.fixparser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fixparser.FIXDictionary.TagType;

/**
 * @author Ram
 *
 */
public class FIXParser {
	public static final char FIX_DELIMITER = '|';
	public static final char KEY_VALUE_SEPARATOR = '=';
	
	private FIXDictionary dictionary;
	
	private int cursor;
	
	public FIXParser(FIXDictionary dictionary) {
		this.dictionary = dictionary;
	}
	
	public FIXMessage parseFIXMessage(String message) throws InvalidMessageException {
		if (cursor > 0) throw new IllegalStateException("The cursor is not at the beginning for this parser! Current location is: " + cursor);
		
		if (StringUtils.isBlank(message)) return null;
		
		FIXMessage fixMessage = new FIXMessage();
		Field<?> preReadField = null;
		while (true) {
			// reached the end of the FIX message
			if (preReadField == null && cursor >= message.length()) break;
			
			Field<?> field = null;
			if (preReadField != null) {
				field = preReadField;
				preReadField = null;
			} else {
				field = parseField(message);
			}
			
			// check what type of tag it is
			TagType tagType = dictionary.getTagType(field.getTag());
			if (tagType == TagType.SINGLE) {
				fixMessage.addField(field);
			} else if (tagType == TagType.GROUP) {
				// parse as groups
				ParseGroupOutput output = parseGroup(field, message);
				preReadField = output.preReadField;
				fixMessage.addGroups(output.groups);
			} else {
				throw new InvalidMessageException("Invalid Tag type found at position: " + cursor);
			}
		}
		
		return fixMessage;
	}
	
	private Field<?> parseField(String message) throws InvalidMessageException {

		int equalsIndex = message.indexOf(KEY_VALUE_SEPARATOR, cursor);
		
		// reached the end of string
		if (equalsIndex == -1) return null;
		
		String tagStr = message.substring(cursor, equalsIndex);
		Integer tag = Converters.getInt(tagStr);
		if (tag == null) {
			throw new InvalidMessageException("Error parsing FIX Message at cursor position : " + cursor);
		}
		cursor = cursor + tagStr.length() + 1;

		int delimiterIndex = message.indexOf(FIX_DELIMITER, cursor);
		String valueStr = null;
		if (delimiterIndex == -1) {
			// reached the end of the string
			valueStr = message.substring(cursor);
		} else {
			valueStr = message.substring(cursor, delimiterIndex);
		}
		cursor = cursor + valueStr.length() + 1;
		Field<?> field = dictionary.getField(tag, valueStr);
		if (field == null) {
			throw new InvalidMessageException("Error creating a field at cursor: " + cursor + ", tag: " + tag + ", value: " + valueStr);
		}
		if (field.getData() == null) {
			throw new InvalidMessageException("Error retrieving the value for tag: " + tag + ", with value: " + valueStr + ", at cursor: " + cursor);
		}
		return field;
	}
	
	private static class ParseGroupOutput {
		List<Group> groups;
		Field<?> preReadField;
	}
	
	private ParseGroupOutput parseGroup(Field<?> groupIndicatorField, String message) throws InvalidMessageException {
		ParseGroupOutput output = new ParseGroupOutput();
		int noOfRepGroups = Field.getIntData(groupIndicatorField);
		GroupInfo groupInfo = dictionary.getGroupInfo(groupIndicatorField.getTag());
		List<Group> groups = new ArrayList<>(noOfRepGroups);
		
		Field<?> preDetectedFirstField = null;
		for (int i=0;i<noOfRepGroups;i++) {
			Group group = new Group(groupInfo);
			
			if (preDetectedFirstField == null) {
				Field<?> firstField = parseField(message);
				if (groupInfo.getFirstTag() != firstField.getTag()) {
					throw new InvalidMessageException("Invalid first tag: " + firstField.getTag() + " in the group: " + groupIndicatorField.getTag() + ", at cursor: " + cursor);
				}
				group.setField(firstField);
			} else {
				group.setField(preDetectedFirstField);
			}
			
			Field<?> field = null;
			while (true) {
				field = parseField(message);
				// is it the start of the next group?
				if (field.getTag() == groupInfo.getFirstTag()) {
					if (i < (noOfRepGroups-1)) {
						// start of the repeating group
						preDetectedFirstField = field;
						break;
					} else {
						throw new InvalidMessageException("Invalid Position of the first tag: " + field.getTag() + " of the group: " + groupIndicatorField.getTag() + ", at cursor: " + cursor);
					}
				}
				
				if (group.containsField(field.getTag())) {
					// its a duplicate
					throw new InvalidMessageException("Duplicate tag: " + field.getTag() + " in the group: " + groupIndicatorField.getTag() + ", at cursor: " + cursor);
				}

				if (!groupInfo.containsTag(field.getTag())) {
					// end of the group
					output.preReadField = field;
					break;
				}
				
				// all clear now
				group.setField(field);
			}
			groups.add(group);
			
			if (i < (noOfRepGroups-1) && output.preReadField != null) {
				// didn't read the required number of repeating groups before a tag that doesn't belong to this group is seen
				throw new InvalidMessageException("Invalid number of repeating groups in the group: " + groupIndicatorField.getTag() + ", expected: " + noOfRepGroups + ", only seen: " + (i+1) + ", at cursor: " + cursor);
			}
			if (output.preReadField != null) {
				// reached end of all groups
				break;
			}
		}
		
		output.groups = groups;
		return output;
	}
}
