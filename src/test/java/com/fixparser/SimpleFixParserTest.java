package com.fixparser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SimpleFixParserTest {
	private static final Logger logger = Logger.getLogger(SimpleFixParserTest.class);
	
	private FIXParser fixParser;
	
	GroupInfo gi269;
	GroupInfo gi123;
	
	@Rule
	public TestRule watcher = new TestWatcher() {
	   protected void starting(Description description) {
	      System.out.println("Starting test: " + description.getMethodName() + " -----------------------------");
	   }
	};
		  
	@Before
	public void setup() {
		Map<Integer, FieldDataType> validFields = new HashMap<>();
		validFields.put(8, FieldDataType.INTEGER);
		validFields.put(9, FieldDataType.INTEGER);
		validFields.put(55, FieldDataType.STRING);
		validFields.put(40, FieldDataType.STRING);
		validFields.put(269, FieldDataType.INTEGER);
		validFields.put(277, FieldDataType.INTEGER);
		validFields.put(231, FieldDataType.INTEGER);
		validFields.put(283, FieldDataType.INTEGER);
		validFields.put(456, FieldDataType.INTEGER);
		validFields.put(44, FieldDataType.INTEGER);
		validFields.put(123, FieldDataType.INTEGER);
		validFields.put(786, FieldDataType.STRING);
		validFields.put(398, FieldDataType.STRING);
		validFields.put(567, FieldDataType.INTEGER);
		validFields.put(496, FieldDataType.STRING);
		
		Map<Integer, GroupInfo> validGroups = new HashMap<>();
		gi269 = new GroupInfo(269, 277, new HashSet<>(Arrays.asList(283, 456, 231)));
		validGroups.put(269, gi269);
		gi123 = new GroupInfo(123, 786, new HashSet<>(Arrays.asList(398, 567, 496)));
		validGroups.put(123, gi123);
		FIXDictionary dictionary = new FIXDictionary(validFields, validGroups);

		fixParser = new FIXParser(dictionary);
	}
	
	@Test
	public void a_testNoGroups() throws InvalidMessageException { 
		FIXMessage message = fixParser.parseFIXMessage("8=345|9=12|55=IBM|40=P");
		logger.info(message);

		assertNotNull("FIX Message cannot be null", message);
		Collection<Integer> groups = message.getGroups();
		assertNotNull("groups cannot be null", groups);
		assertTrue("There cannot be any repeating groups in this message", groups.isEmpty());
	}

	@Test
	public void b_testNoGroupsDuplicateTags() throws InvalidMessageException { 
		FIXMessage message = fixParser.parseFIXMessage("8=345|9=12|8=2|55=IBM|40=P");
		logger.info(message);

		assertNotNull("FIX Message cannot be null", message);
		Collection<Integer> groups = message.getGroups();
		assertNotNull("groups cannot be null", groups);
		assertTrue("There cannot be any repeating groups in this message", groups.isEmpty());
	}

	@Test
	public void c_testOneRepeatingGroupOneInstance() throws InvalidMessageException { 
		FIXMessage message = fixParser.parseFIXMessage("8=345|9=12|55=IBM|40=P|269=1|277=12|283=5|456=7|44=12");
		logger.info(message);
		
		assertNotNull("FIX Message cannot be null", message);

		Collection<Integer> groups = message.getGroups();
		assertThat(groups, contains(269));

		List<Group> igroups = message.getGroup(269);
		assertThat(igroups, hasSize(1));
		
		Group g269_1 = igroups.get(0);
		assertNotNull(g269_1);
		
		assertThat(g269_1.getGroupMemberTags(), contains(277, 283, 456));
		assertEquals(new IntegerField(277, 12), g269_1.getField(277));
		assertEquals(new IntegerField(283, 5), g269_1.getField(283));
		assertEquals(new IntegerField(456, 7), g269_1.getField(456));
	}

	@Test
	public void d_testOneRepeatingGroupTwoInstances() throws InvalidMessageException { 
		FIXMessage message = fixParser.parseFIXMessage("8=345|9=12|55=IBM|40=P|269=2|277=12|283=5|456=7|277=1|231=56|456=7|44=12");
		logger.info(message);
		
		assertNotNull("FIX Message cannot be null", message);

		Collection<Integer> groups = message.getGroups();
		assertThat(groups, contains(269));

		List<Group> igroups = message.getGroup(269);
		assertThat(igroups, hasSize(2));
		
		Group g269_1 = igroups.get(0);
		assertNotNull(g269_1);
		
		assertThat(g269_1.getGroupMemberTags(), contains(277, 283, 456));
		assertEquals(new IntegerField(277, 12), g269_1.getField(277));
		assertEquals(new IntegerField(283, 5), g269_1.getField(283));
		assertEquals(new IntegerField(456, 7), g269_1.getField(456));

		Group g269_2 = igroups.get(1);
		assertNotNull(g269_2);

		assertThat(g269_2.getGroupMemberTags(), contains(277, 231, 456));
		assertEquals(new IntegerField(277, 1), g269_2.getField(277));
		assertEquals(new IntegerField(231, 56), g269_2.getField(231));
		assertEquals(new IntegerField(456, 7), g269_2.getField(456));
	}

	@Test
	public void e_testTwoRepeatingGroupsOneInstances() throws InvalidMessageException { 
		FIXMessage message = fixParser.parseFIXMessage("8=345|9=12|55=IBM|40=P|269=1|277=12|283=5|456=7|123=1|786=23|398=34|567=11|496=ABC|44=12");
		logger.info(message);
		
		assertNotNull("FIX Message cannot be null", message);

		Collection<Integer> groups = message.getGroups();
		assertThat(groups, contains(269, 123));

		List<Group> igroups_1 = message.getGroup(269);
		assertThat(igroups_1, hasSize(1));
		
		Group g269_1 = igroups_1.get(0);
		assertNotNull(g269_1);
		
		assertThat(g269_1.getGroupMemberTags(), contains(277, 283, 456));
		assertEquals(new IntegerField(277, 12), g269_1.getField(277));
		assertEquals(new IntegerField(283, 5), g269_1.getField(283));
		assertEquals(new IntegerField(456, 7), g269_1.getField(456));

		List<Group> igroups_2 = message.getGroup(123);
		assertThat(igroups_2, hasSize(1));

		Group g123_1 = igroups_2.get(0);
		assertNotNull(g123_1);

		assertThat(g123_1.getGroupMemberTags(), contains(786, 398, 567, 496));
		assertEquals(new StringField(786, "23"), g123_1.getField(786));
		assertEquals(new StringField(398, "34"), g123_1.getField(398));
		assertEquals(new IntegerField(567, 11), g123_1.getField(567));
		assertEquals(new StringField(496, "ABC"), g123_1.getField(496));
	}

	
	@Test
	public void f_testTwoRepeatingGroupsDifferentNumberOfInstances() throws InvalidMessageException { 
		FIXMessage message = fixParser.parseFIXMessage("8=345|9=12|55=IBM|40=P|269=2|277=12|283=5|456=7|277=1|231=56|456=7|123=2|786=9|398=ABC|786=QAS|567=12|496=SDF|398=12|44=12");
		logger.info(message);
	}

	@Test
	public void g_testTwoRepeatingGroupsMoreNumberOfInstances() throws InvalidMessageException { 
		FIXMessage message = fixParser.parseFIXMessage(
				"8=345|9=12|55=IBM|40=P|269=2|277=12|283=5|456=7|277=1|231=56|456=7|123=3|786=9|398=ABC|786=QAS|567=12|496=SDF|398=12|786=QAF|567=52|496=SDX|44=12");
		logger.info(message);
	}

	@Test
	public void h_testGroupTagsOutOfOrder1() throws InvalidMessageException { 
		FIXMessage message = fixParser.parseFIXMessage(
				"8=345|9=12|55=IBM|40=P|269=1|277=12|283=5|456=7|123=1|786=23|398=34|567=11|44=12|496=ABC");
		logger.info(message);
	}

	@Test
	public void i_testGroupTagsOutOfOrder2() throws InvalidMessageException { 
		FIXMessage message = fixParser.parseFIXMessage(
				"8=345|9=12|55=IBM|40=P|269=1|277=12|456=7|283=5|123=1|786=23|398=34|567=11|44=12|496=ABC");
		logger.info(message);
	}

	@Test(expected=InvalidMessageException.class)
	public void j_testFirstGroupTagIsOutOfOrder1() throws InvalidMessageException {
		try {
			fixParser.parseFIXMessage(
					"8=345|9=12|55=IBM|40=P|269=1|456=7|283=5|277=12|123=1|786=23|398=34|567=11|44=12");
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			throw ex;
		}
	}

	@Test(expected=InvalidMessageException.class)
	public void k_testIncorrectNumberOfRepeatingGroups1() throws InvalidMessageException {
		try {
			fixParser.parseFIXMessage(
					"8=345|9=12|55=IBM|40=P|269=2|277=12|456=7|283=5|123=1|786=23|398=34|567=11|44=12");
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			throw ex;
		}
	}

	@Test(expected=InvalidMessageException.class)
	public void l_testIncorrectNumberOfRepeatingGroups2() throws InvalidMessageException {
		try {
			fixParser.parseFIXMessage(
					"8=345|9=12|55=IBM|40=P|269=1|277=12|456=7|283=5|123=3|786=23|398=34|567=11|44=12");
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			throw ex;
		}
	}

	@Test(expected=InvalidMessageException.class)
	public void m_testInvalidDataTypeOfATag() throws InvalidMessageException {
		try {
			fixParser.parseFIXMessage(
					"8=345|9=12|55=IBM|40=P|269=1|277=12|283=5|456=ABC|44=12");
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			throw ex;
		}
	}

}
