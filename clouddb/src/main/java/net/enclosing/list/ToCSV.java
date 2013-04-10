package net.enclosing.list;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ToCSV {
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.err.println("comming here");
		new ToCSV((args!=null&&args.length>0)?args[0]:null);
	}
	public ToCSV(String path){
		try {

			List list = new List(List.DEFAULT_DRIVECSVPATH);
			if(path!=null){
				list = new List(path);
			}
			final File cloudDir = new File(list.getDrivecsvPath());
//			File workingDir = new File("/Users/toukubo/Documents/clouddb/work");
//			FileUtils.copyDirectory(cloudDir, workingDir);
			File[] xlsFiles = cloudDir.listFiles(getFilter());

			for (File xlsFile : xlsFiles) {
				File csvFile = new File(xlsFile.getAbsolutePath().replaceAll(".xls", ".csv"));
				System.err.println(csvFile.getAbsolutePath());
				toCSV(csvFile,xlsFile);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void toCSV(File csvFile, File xlsFile) {
		
		try
		{
			FileInputStream fi = new  FileInputStream(xlsFile);;

			XSSFWorkbook w = new XSSFWorkbook(fi);

			OutputStream os = (OutputStream)new FileOutputStream(csvFile);
			String encoding = "UTF8";
			OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
			BufferedWriter bw = new BufferedWriter(osw);

			XSSFSheet s = w.getSheetAt(0);
			System.err.println(s.getSheetName());

			XSSFRow row = null;

			for (int i = 0 ; i < s.getLastRowNum()+1 ; i++)
			{
				row = s.getRow(i);
				if (row.getLastCellNum() > 0)
				{
					XSSFCell cell = row.getCell(0);
					bw.write(cell2String(cell));
					for (int j = 1; j < row.getLastCellNum(); j++) {
						cell = row.getCell(j);
						bw.write(',');
						bw.write(cell2String(cell));
					}
				}
				bw.newLine();
				System.err.println();
			}
			bw.flush();
			bw.close();
			//			fi.close();
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
	}
	
	public static String cell2String(XSSFCell cell) {
		if(cell==null) return "";
		
			switch (cell.getCellType()) {
			case XSSFCell.CELL_TYPE_BLANK:
				return "";
		
			case XSSFCell.CELL_TYPE_BOOLEAN:
		     return new Boolean(cell.getBooleanCellValue()).toString();
		 
		   case XSSFCell.CELL_TYPE_ERROR:
		     return null;
		 
		   case XSSFCell.CELL_TYPE_FORMULA:
		     return "";
		 
		   case XSSFCell.CELL_TYPE_NUMERIC:
		 
		     if (DateUtil.isCellDateFormatted(cell)) {
		    	 
		       return dateFormat.format(cell.getDateCellValue());
		     } else {
		       double val = cell.getNumericCellValue();
		 
		       if (val == Math.ceil(val)) {
		         return new Integer((int) val).toString();
		       } else {
		         return new Double(val).toString();
		       }
		     }
		 
		   case XSSFCell.CELL_TYPE_STRING:
		     return cell.getStringCellValue();
		 
		   default:
		     return null;
		   }
		 }

	public static FileFilter getFilter() {  
		return new FileFilter() {  
			public boolean accept(File file) {  
				return file.isFile() && file.getName().endsWith("xls");  
			}  
		};  
	}
}
