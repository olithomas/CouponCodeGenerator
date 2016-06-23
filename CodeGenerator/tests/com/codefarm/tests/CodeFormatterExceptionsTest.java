package com.codefarm.tests;

import org.junit.Test;

import com.codefarm.engine.*;

public class CodeFormatterExceptionsTest {

	@Test(expected=IllegalArgumentException.class)
	public void constructorShouldNotAllowTooLongCodeLength() {
		new CodeFormatter(CodeFormatter.MAX_CODE_LENGTH + 1);
	}
	@Test(expected=IllegalArgumentException.class)
	public void constructorShouldNotAllowTooShortCodeLength() {
		new CodeFormatter(CodeFormatter.MIN_CODE_LENGTH - 1);
	}
	@Test(expected=IllegalArgumentException.class)
	public void initExclusionsShouldNotAllowNonAlphaNumericExclusions() {
		CodeFormatter f = new CodeFormatter(CodeFormatter.MIN_CODE_LENGTH + 5);
		f.initExclusions(new char[] {'%', '1', 'f', '4'} );
	}
	@Test(expected=IllegalArgumentException.class)
	public void initExclusionsShouldNotAllowTooShortCharSetLength() {
		char[] excludes = new char[36 - CodeFormatter.MIN_CHAR_SET_LENGTH + 1];
		char exclude = '0';
		for (int i = 0; i < excludes.length; i++) {
			excludes[i] = exclude;
			exclude++;
			while (exclude > '9' && exclude < 'A') {
				exclude++;
			}
		}
		
		CodeFormatter f = new CodeFormatter(CodeFormatter.MIN_CODE_LENGTH + 5);
		f.initExclusions(excludes);
	}
}
