package com.xvia.nexgen.formulaCompiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class FormulaCompiler {
	
	
	private File sourceFile;
	private File destinationFile;
	private Map<String, String> macroMap = new HashMap<String, String>();
	private boolean isInHeader = false;
	private boolean isHeaderProcessed = false;
	private boolean isInBody = false;
	private String currentMacroName = null;
	private StringBuilder macroStringBuilder;
	private PrintWriter fileWriter;
	
	
	
	public static void main(String[] args){
		if (args.length == 0 || args[0].equals("-h") || args[0].equals("help")){
			print("Usage: fcompile <source file> <destination file>");
			return;
		} else if (args.length < 2){
			print("Too few parameters");
			print("Usage: fcompile <source file> <destination file>");
		}
		
		FormulaCompiler compiler = new FormulaCompiler(args[0], args[1]);
		try{
			compiler.compile();
		} catch(IOException e){
			print(e.toString());
		}
	}
	
	
	public FormulaCompiler(String sourcePath, String destinationPath){
		sourceFile = new File(sourcePath);
		destinationFile = new File(destinationPath);
		
		print("Input file: " + sourceFile.getAbsolutePath());
		print("output file: " + destinationFile.getAbsolutePath());
	}
	
	/**
	 * Kicks off the work flow
	 * @throws IOException 
	 */
	public void compile() throws IOException{
		//open input file
		try (BufferedReader br = new BufferedReader(new FileReader(sourceFile))) {
		    String line;
		    int lineCount = 0;
		    while ((line = br.readLine()) != null) {
		    	lineCount++;
		    	processLine(line, lineCount);
		    }
		    
		    if(fileWriter != null){
		    	fileWriter.flush();
		    	fileWriter.close();
		    }
		}
		
	}
	
	/**
	 * handle the lines
	 * @param line
	 * @param lineCount
	 * @throws FileNotFoundException 
	 */
	private void processLine(String line, int lineCount) throws FileNotFoundException{
		//what kind of line is it?
		////////////////////////////////////////////////////////////
		/////Start of the header
		if(line.trim().toLowerCase().equals(Constants.HEADER)){
			if(isInHeader){
				throw new RuntimeException("Can't have more than one header on line: " + lineCount);
			}
			isInHeader = true;
			return;
			////////////////////////////////////////////////////////////	
			/////Comment character
		} else if(line.trim().startsWith(Constants.COMMENT_CHARACTER)){
			//no-op
			return;
			////////////////////////////////////////////////////////////
			/////Defining a macro
		} else if(isInHeader && line.trim().startsWith(Constants.MACRO_DEF_KEYWORD)){
			endCurrentMacro();
			String macroName = line.trim().substring(Constants.MACRO_DEF_KEYWORD.length() - 1).trim();
			if(macroName.contains(" ") || 
					macroName.contains("$") ||
					macroName.contains("\t") ||
					macroName.contains("#") ||
					macroName.contains("@") ||
					macroName.contains(".") ||
					macroName.contains("-")){
				throw new RuntimeException("Illegal character in Macro name on line: " + lineCount);
			}
			currentMacroName = macroName;
			macroStringBuilder = new StringBuilder();
			return;
			////////////////////////////////////////////////////////////
			/////Body of a macro	
		} else if(isInHeader && currentMacroName != null && (line.startsWith(Constants.TAB_CHARACTER) || line.startsWith(Constants.SOFT_TAB_CHARACTER))){
			macroStringBuilder.append(line + "\n");
			return;
			////////////////////////////////////////////////////////////
			/////Start of the body	
		} else if(line.trim().toLowerCase().equals(Constants.BODY)){
			isInHeader = false;
			isInBody = true;
			isHeaderProcessed = true;
			endCurrentMacro();
			FileOutputStream outputStream = new FileOutputStream(destinationFile);
			fileWriter = new PrintWriter(outputStream);
			////////////////////////////////////////////////////////////
			/////white space at the end of a macro	
		} else if(isInHeader && line.trim().length() == 0){
			endCurrentMacro();
			////////////////////////////////////////////////////////////
			/////Crud in the header error out
		} else if(isInHeader && line.trim().length() > 0){
			throw new RuntimeException("unknown input on line: " + lineCount);
			////////////////////////////////////////////////////////////
			/////Handle the body
		} else if(isInBody){
			parseLineInBody(line, lineCount);
		}
		
	}
	
	
	/**
	 * Parses the lines in the body
	 * @param line
	 */
	private void parseLineInBody(String line, int lineCount){
		while(line.contains(Constants.MACRO_PREFIX)){
			String macroName;
			int macroPrefixIndex = line.indexOf(Constants.MACRO_PREFIX);
			macroName = line.substring( macroPrefixIndex + Constants.MACRO_PREFIX.length());
			macroName = macroName.trim();
			//is there anything left?
			if(macroName.indexOf(" ") != -1){
				int indexOfSpace = macroName.indexOf(" ");
				macroName = macroName.substring(0, indexOfSpace);
			} 
			
			if(!macroMap.containsKey(macroName)){
				throw new RuntimeException("unknown macro: \"" + macroName + "\" on line: " + lineCount);
			}
			
			String macroBody = macroMap.get(macroName);
			String macroNamePlusPrefix = Constants.MACRO_PREFIX + macroName;
			line = line.replace(macroNamePlusPrefix, macroBody);
		}
		
		fileWriter.println(line);
	}
	
	/**
	 * Handle the end of a macro
	 */
	private void endCurrentMacro(){
		if(currentMacroName != null && macroStringBuilder != null){
			macroMap.put(currentMacroName, macroStringBuilder.toString());
		}
		currentMacroName = null;
		macroStringBuilder = null;
	}
	
	
	public static void print(String str){
		System.out.println(str);
	}

}
