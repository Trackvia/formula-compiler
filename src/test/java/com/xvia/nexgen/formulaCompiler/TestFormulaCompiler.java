package com.xvia.nexgen.formulaCompiler;

import java.io.IOException;

import org.junit.Test;

public class TestFormulaCompiler {

	
	@Test
	public void test() throws IOException{
		FormulaCompiler compiler = new FormulaCompiler("in.py", "out.py");
		compiler.compile();
	}
}
