package com.fixparser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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

import com.fixparser.FIXDictionary;
import com.fixparser.FIXMessage;
import com.fixparser.FIXParser;
import com.fixparser.FieldDataType;
import com.fixparser.GroupInfo;
import com.fixparser.InvalidMessageException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SimpleFixParserTest {
	private static final Logger logger = Logger.getLogger(SimpleFixParserTest.class);
	
	private FIXParser fixParser;
	
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
		validGroups.put(269, new GroupInfo(269, 277, new HashSet<>(Arrays.asList(283, 456, 231))));
		validGroups.put(123, new GroupInfo(123, 786, new HashSet<>(Arrays.asList(398, 567, 496))));
		FIXDictionary dictionary = new FIXDictionary(validFields, validGroups);

		fixParser = new FIXParser(dictionary);
	}
	
	@Test
	public void a_testNoGroups() throws InvalidMessageException { 
		FIXMessage message = fixParser.parseFIXMessage("8=345|9=12|55=IBM|40=P");
		logger.info(message);
	}

	@Test
	public void b_testNoGroupsDuplicateTags() throws InvalidMessageException { 
		FIXMessage message = fixParser.parseFIXMessage("8=345|9=12|8=2|55=IBM|40=P");
		logger.info(message);
	}

	@Test
	public void c_testOneRepeatingGroupOneInstance() throws InvalidMessageException { 
		FIXMessage message = fixParser.parseFIXMessage("8=345|9=12|55=IBM|40=P|269=1|277=12|283=5|456=7|44=12");
		logger.info(message);
	}

	@Test
	public void d_testOneRepeatingGroupTwoInstances() throws InvalidMessageException { 
		FIXMessage message = fixParser.parseFIXMessage("8=345|9=12|55=IBM|40=P|269=2|277=12|283=5|456=7|277=1|231=56|456=7|44=12");
		logger.info(message);
	}

	@Test
	public void d_testTwoRepeatingGroupsOneInstances() throws InvalidMessageException { 
		FIXMessage message = fixParser.parseFIXMessage("8=345|9=12|55=IBM|40=P|269=1|277=12|283=5|456=7|123=1|786=23|398=34|567=11|496=ABC|44=12");
		logger.info(message);
	}

	@Test
	public void e_testTwoRepeatingGroupsDifferentNumberOfInstances() throws InvalidMessageException { 
		FIXMessage message = fixParser.parseFIXMessage("8=345|9=12|55=IBM|40=P|269=2|277=12|283=5|456=7|277=1|231=56|456=7|123=2|786=9|398=ABC|786=QAS|567=12|496=SDF|398=12|44=12");
		logger.info(message);
	}

	@Test
	public void f_testTwoRepeatingGroupsMoreNumberOfInstances() throws InvalidMessageException { 
		FIXMessage message = fixParser.parseFIXMessage(
				"8=345|9=12|55=IBM|40=P|269=2|277=12|283=5|456=7|277=1|231=56|456=7|123=3|786=9|398=ABC|786=QAS|567=12|496=SDF|398=12|786=QAF|567=52|496=SDX|44=12");
		logger.info(message);
	}

	@Test
	public void g_testGroupTagsOutOfOrder1() throws InvalidMessageException { 
		FIXMessage message = fixParser.parseFIXMessage(
				"8=345|9=12|55=IBM|40=P|269=1|277=12|283=5|456=7|123=1|786=23|398=34|567=11|44=12|496=ABC");
		logger.info(message);
	}

	@Test
	public void h_testGroupTagsOutOfOrder2() throws InvalidMessageException { 
		FIXMessage message = fixParser.parseFIXMessage(
				"8=345|9=12|55=IBM|40=P|269=1|277=12|456=7|283=5|123=1|786=23|398=34|567=11|44=12|496=ABC");
		logger.info(message);
	}

	@Test(expected=InvalidMessageException.class)
	public void i_testFirstGroupTagIsOutOfOrder1() throws InvalidMessageException {
		try {
			fixParser.parseFIXMessage(
					"8=345|9=12|55=IBM|40=P|269=1|456=7|283=5|277=12|123=1|786=23|398=34|567=11|44=12");
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			throw ex;
		}
	}

	@Test(expected=InvalidMessageException.class)
	public void j_testIncorrectNumberOfRepeatingGroups1() throws InvalidMessageException {
		try {
			fixParser.parseFIXMessage(
					"8=345|9=12|55=IBM|40=P|269=2|277=12|456=7|283=5|123=1|786=23|398=34|567=11|44=12");
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			throw ex;
		}
	}

	@Test(expected=InvalidMessageException.class)
	public void h_testIncorrectNumberOfRepeatingGroups2() throws InvalidMessageException {
		try {
			fixParser.parseFIXMessage(
					"8=345|9=12|55=IBM|40=P|269=1|277=12|456=7|283=5|123=3|786=23|398=34|567=11|44=12");
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			throw ex;
		}
	}

	@Test(expected=InvalidMessageException.class)
	public void i_testInvalidDataTypeOfATag() throws InvalidMessageException {
		try {
			fixParser.parseFIXMessage(
					"8=345|9=12|55=IBM|40=P|269=1|277=12|283=5|456=ABC|44=12");
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			throw ex;
		}
	}

}
