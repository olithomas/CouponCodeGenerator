package com.codefarm.entities;

import java.math.BigInteger;

import com.codefarm.engine.*;

public class ObjectIdGenerator extends CodeGenerator {

	public ObjectIdGenerator() {
		super(new CodeFormatter(9, new char[] 
				{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 
				'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'}), BigInteger.ONE);
	}
	
	public int nextId() {
		Code code = super.next();
		return code.getIntegerVal().intValue();
	}

}
