package com.smarteinc.utility;

import java.util.Map;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Reporter;

public class ExcelUtility {
	public static XSSFWorkbook openSpreadSheet(String fileName)
	{
		try{
			File f=new File(fileName);
			if(f.isFile() && f.canRead()) {
				FileInputStream fip = new FileInputStream(f); 
				XSSFWorkbook excelWorkbook=new XSSFWorkbook(f);
				return excelWorkbook;
			}
		}
		catch(Exception e)
		{	
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	public synchronized static void saveChangesToAnother(String filePath,XSSFWorkbook wrkBook) throws Exception{
		FileOutputStream fos=new FileOutputStream(new File(filePath));
		wrkBook.write(fos);
		wrkBook.getPackage().revert();
		fos.flush();
		fos.close();
		Reporter.log( "Successfuly saved file to : "+filePath,true);
	}
	
	public synchronized static Object[][] getFullDataSetAsProperties(XSSFSheet sheet)
	{
		List<List<String>> dataSetA=new ArrayList<List<String>>();
		int lastRow=sheet.getLastRowNum();
		int firstRow=sheet.getFirstRowNum();
		int indexCounter=0;
		Properties[][] prop=new Properties[lastRow][1];
		for(int rowCounter=firstRow+1; rowCounter<=lastRow;rowCounter++)
		{
			dataSetA.add(getRowData(sheet,rowCounter));
			prop[indexCounter][0] = getRowDataAsProperty(sheet, rowCounter);
			indexCounter++;
		}
		return prop;
	}
	
	public synchronized static List<Properties[]>  getDataSetAsProperty(XSSFSheet sheet,Map<String,String> filters){
		List<Properties[]> dataSetProperties=new ArrayList<Properties[]>(); 
		int lastRow=sheet.getLastRowNum();
		int firstRow=sheet.getFirstRowNum();
		/*if(filters.isEmpty()){
			return getFullDataSetAsProperties(sheet);
		}*/
		for(int rowCounter=firstRow+1; rowCounter<=lastRow;rowCounter++)
		{
			boolean isFiltered=true;
			for(String key:filters.keySet()){
				String identifierColumn=key;
				String identifierValue = filters.get(key);
				String cellValue=getCellData(sheet,rowCounter,identifierColumn);
				if(cellValue.trim().equalsIgnoreCase(identifierValue))
				{
					isFiltered=true;
				}else{
					isFiltered=false;
					break;
				}
			}
			if(isFiltered){
				dataSetProperties.add(new Properties[]{
						getRowDataAsProperty(sheet, rowCounter)
					});
			}
		}
		return dataSetProperties;
	}
	
	public synchronized static void saveChanges(String filePath,XSSFWorkbook wrkBook) throws Exception{
		FileOutputStream fos=new FileOutputStream(new File(filePath));
		wrkBook.write(fos);
		wrkBook.close();
		fos.flush();
		fos.close();
	}
	
	public synchronized static void saveChanges(String filePath) throws Exception{
		
		XSSFWorkbook wbk = openSpreadSheet(filePath);
		FileOutputStream fos=new FileOutputStream(new File(filePath));
		
		wbk.write(fos);
		wbk.close();
		fos.flush();
		fos.close();
	}
	
	public synchronized static void removeAllRowsExceptColumn(XSSFSheet sheet){
		int firstRow = sheet.getFirstRowNum();
		firstRow=firstRow+1;
		int lastRow=sheet.getLastRowNum();
		for(int i=firstRow;i<=lastRow;i++){
			sheet.removeRow(sheet.getRow(i));
		}
	}
	
	
	public synchronized static void appendRowToSheet(XSSFSheet sheet, List<String> cellValues){
		int endRowNum=sheet.getLastRowNum();
		System.out.println("Last row number : "+endRowNum);
		int nextRowNum=endRowNum+1;
		Row existingRow=sheet.getRow(0);
		Row newRow=sheet.createRow(nextRowNum);
		int firstCellNum=existingRow.getFirstCellNum();
		System.out.println("Starting cell Number:"+firstCellNum);
		for(String value:cellValues){
			Cell c=newRow.createCell(firstCellNum++);
			c.setCellValue(value);
		}	
	}
	
	/**
	 * A overloaded method to create instance of {@link XSSFWorkbook} and return the {@link XSSFSheet} of the given sheet name.
	 * 
	 * @param fileName
	 * @param sheetName
	 * @return {@link XSSFSheet} 
	 */
	public static XSSFSheet openSpreadSheet(String fileName,String sheetName)
	{
		try{
		return openSpreadSheet(fileName).getSheet(sheetName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static XSSFSheet openSheetUsingWk(XSSFWorkbook wk, String sheetName){
		
		 return wk.getSheet(sheetName);
	}
	
	/**
	 * returns the single cell data of the given row and column name from the given sheet.
	 * @param excelSheet : an instance of {@link XSSFSheet}
	 * @param rowNum : row number of the row, generally row num starts with 0 index.
	 * @param columnName : a column name of the sheet of which cell data is to be retrieved
	 * @return {@link String}
	 */
	public static String getCellData(XSSFSheet excelSheet,int rowNum,String columnName)
	{
		if(excelSheet!=null){
			int columnNumber=getColumnNumber(excelSheet, columnName);
			return getCellData(excelSheet,rowNum,columnNumber);
		}
		return null;
	}
	
	public static String getCellData(XSSFSheet excelSheet,int rowNum,int headerRow,String columnName)
	{
		if(excelSheet!=null){
			int columnNumber=getColumnNumber(excelSheet, columnName,headerRow);
			return getCellData(excelSheet,rowNum,columnNumber);
		}
		return null;
	}
	

	public static String getCellData(XSSFSheet excelSheet,int rowNum,int columnNumber)
	{
		if(excelSheet!=null){
		    DataFormatter formatter = new DataFormatter();
		    return formatter.formatCellValue(excelSheet.getRow(rowNum).getCell(columnNumber));
		}
		return null;
	}
	

	
	public synchronized static void updateOrCreateCellData(XSSFSheet excelSheet,int rowNum,int headerRow,String columnName,String updatedCellData)
	{
		if(excelSheet!=null){
			int columnNumber=getColumnNumber(excelSheet, columnName,headerRow);
			Row row=excelSheet.getRow(rowNum);
			if(row!=null){
				Cell cell=row.getCell(columnNumber);
				if(cell!=null){
					cell.setCellValue(updatedCellData);
				}else{
					row.createCell(columnNumber).setCellValue(updatedCellData);
				}
			}else{
				row=excelSheet.createRow(rowNum);
				row.createCell(columnNumber).setCellValue(updatedCellData);
			}
		}
	}
	
	public synchronized static void updateCellData(XSSFSheet excelSheet,int rowNum,String columnName,String updatedCellData)
	{
		if(excelSheet!=null){
			int columnNumber=getColumnNumber(excelSheet, columnName);
			Row row=excelSheet.getRow(rowNum);
			if(row == null){
				Reporter.log( "Row was Null");
				row = excelSheet.createRow(rowNum);
			}
			if(row.getCell(columnNumber) == null){
				Reporter.log( "Creating cell");
				row.createCell(columnNumber);
			}
			updateCellData(excelSheet, rowNum, columnNumber, updatedCellData);
		}
	}
	
	public synchronized static void updateCellData(XSSFSheet excelSheet,int rowNum,int columnNumber,String updatedCellData){
		
		Row row=excelSheet.getRow(rowNum);
		if(row!=null){
			Cell cell=row.getCell(columnNumber);
			if(cell!=null){
				cell.setCellValue(updatedCellData);
			}else{
				row.createCell(columnNumber).setCellValue(updatedCellData);
			}
		}else{
			row=excelSheet.createRow(rowNum);
			row.createCell(columnNumber).setCellValue(updatedCellData);
		}
		
		excelSheet.getRow(rowNum).getCell(columnNumber).setCellValue(updatedCellData);
		Reporter.log( "Updated "+updatedCellData+" Successfully in"
				+ " Col No : "+columnNumber+" and Row No : "+rowNum);
	}
	
	

	
	private static int getColumnNumber(XSSFSheet sheet,String columnName, int rowNum){
		
		Row row=sheet.getRow(rowNum);
		for(int columnIndex=0; columnIndex<row.getPhysicalNumberOfCells();columnIndex++)
		{			
			if(getCellData(sheet,row.getRowNum(),columnIndex).trim().equalsIgnoreCase(columnName.trim()))
			{
				return columnIndex;
			}
		}
		return -1;
	}
	
	public static int getColumnNumber(XSSFSheet sheet,String columnName)
	{	
		return getColumnNumber(sheet,columnName, sheet.getFirstRowNum());
//		Row row=sheet.getRow(sheet.getFirstRowNum());
//		for(int columnIndex=0; columnIndex<row.getPhysicalNumberOfCells();columnIndex++)
//		{			
//			if(getCellData(sheet,row.getRowNum(),columnIndex).trim().equalsIgnoreCase(columnName))
//			{
//				return columnIndex;
//			}
//		}
//		return -1;
	}
	

	/**
	 * returns the full data set of the provided sheet.
	 * @param sheet : an instance of {@link XSSFSheet} where the test data is located
	 * @return {@link Object[][]}
	 */
	public synchronized static Object[][] getFullDataSet(XSSFSheet sheet)
	{
		List<List<String>> dataSetA=new ArrayList<List<String>>();
		int lastRow=sheet.getLastRowNum();
		int firstRow=sheet.getFirstRowNum();
		for(int rowCounter=firstRow+1; rowCounter<=lastRow;rowCounter++)
		{
			dataSetA.add(getRowData(sheet,rowCounter));
		}
		return dataSetA.stream().map(List::toArray).toArray(Object[][]::new);
	}
	
	
	/**
	 * returns the data set of the provided sheet. calls {@link #getFullDataSet(XSSFSheet)} if the identifier column and value is blank
	 * @param sheet : an instance of {@link XSSFSheet} where the test data is located
	 * @param identifierColumn : an identifier column name used as a look up key 
	 * @param identifierValue : a look up value used to fetch the data set of those rows matching with the value.
	 * @return {@link Object[][]}
	 */
	public synchronized static Object[][] getDataSet(XSSFSheet sheet ,String identifierColumn,String identifierValue)
	{
		if(identifierColumn.equals("") && identifierValue.equals(""))
		{
			return getFullDataSet(sheet);
		}
		List<List<String>> dataSet=new ArrayList<List<String>>();
		int lastRow=sheet.getLastRowNum();
		int firstRow=sheet.getFirstRowNum();
		for(int rowCounter=firstRow+1; rowCounter<=lastRow;rowCounter++)
		{
			String cellValue=getCellData(sheet,rowCounter,identifierColumn);
			if(cellValue.trim().equalsIgnoreCase(identifierValue))
			{
				dataSet.add(getRowData(sheet,rowCounter));
			}
		}
		return dataSet.stream().map(List::toArray).toArray(Object[][]::new);
	}
	
	/**
	 * returns the provided row's data as a list of Strings.
	 * @param sheet : an instance of {@link XSSFSheet}
	 * @param rowNum : row number of the row, generally row num starts with 0 index.
	 * @return {@link List<String>}
	 */
	public static List<String> getRowData(XSSFSheet sheet, int rowNum)
	{
		XSSFRow row=sheet.getRow(rowNum);
		Iterator<Cell> ci=row.cellIterator();
		List<String> rowData=new ArrayList<String>();
		while(ci.hasNext())
		{
			Cell c=ci.next();
			rowData.add(getCellData(sheet, rowNum,c.getColumnIndex()));
		}
		return rowData;
	}
	
	/**
	 * returns the provided row's data as a list of Strings.
	 * @param sheet : an instance of {@link XSSFSheet}
	 * @param rowNum : row number of the row, generally row num starts with 0 index.
	 * @return {@link List<String>}
	 */
	public static Properties getRowDataAsProperty(XSSFSheet sheet, int rowNum)
	{
		XSSFRow row=sheet.getRow(rowNum);
		Iterator<Cell> ci=row.cellIterator();
		Properties rowMap=new Properties(); 
		while(ci.hasNext())
		{
			Cell c=ci.next();
			int columnIndex=c.getColumnIndex();
			String columnName=getCellData(sheet, 0, columnIndex);
			String data=getCellData(sheet, rowNum,columnIndex);
			rowMap.put(columnName,data);
		}
		return rowMap;
	}
	
	/***
	 * This method updates values in the merged cells of the provided sheet
	 * @param sheet - {@link XSSFSheet} object of the sheet in which data is to be written
	 * @param mergedRegion - merged region in the sheet. Eg : "A8:A9"
	 * @param updatedValue - value to be updated
	 */
	public static void updateMergedCells(XSSFSheet sheet,String mergedRegion,String updatedValue) {
		
		for (CellRangeAddress range : sheet.getMergedRegions()) {
			if(range.formatAsString().equals(mergedRegion)){
				int rowNum = range.getFirstRow(); 
				int colIndex = range.getFirstColumn(); 
				updateCellData(sheet, rowNum, colIndex, updatedValue);
			}
			
		}
	}
	
	/**
	 * returns the full data set of the provided sheet.
	 * @param sheet : an instance of {@link XSSFSheet} where the test data is located
	 * @return {@link Object[][]}
	 */
	public synchronized static Map<String, String> getFullDataSetAsMap(XSSFSheet sheet)
	{
		Map<String, String> values=new HashMap<String,String>();
		int lastRow=sheet.getLastRowNum();
		int firstRow=sheet.getFirstRowNum();
		for(int rowCounter=firstRow+1; rowCounter<=lastRow;rowCounter++)
		{
			values.put(getCellData(sheet, rowCounter, 0), getCellData(sheet, rowCounter, 1));
			
		}
		return values;
	}
	

}
