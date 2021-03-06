package com.xvia.nexgen.formulaCompiler;

import java.util.ArrayList;
import java.util.List;

public class Constants {

	public static final String HEADER = "**header**";
	
	public static final String BODY = "**body**";
	
	public static final String MACRO_PREFIX = "$$";
	public static final String MACRO_DEF_KEYWORD = "macro: ";
	public static final String TAB_CHARACTER = "\t";
	public static final String SOFT_TAB_CHARACTER = "  ";
	
	public static final String COMMENT_CHARACTER = "#";
	
	public static final List<String> ILLEGAL_CHARACTERS = new ArrayList(){{
		add("#");
		add("$");
		add("*");
		add("&");
		add("-");
		add(",");
		add(".");
		add("!");
		add(" ");
		add("\t");
		add("`");
		add("~");
		add("=");
		add(")");
		add("(");
		add("[");
		add("]");
		add("}");
		add("{");
		
	}};
}
