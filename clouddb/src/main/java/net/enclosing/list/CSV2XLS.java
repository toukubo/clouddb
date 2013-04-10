package net.enclosing.list;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

public class CSV2XLS {
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
    private HSSFWorkbook workBook;
    private HSSFSheet sheet;
    private HSSFCellStyle style;
    private static final short FONT_SIZE = 11;

    private boolean test = false;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CSV2XLS(args.length>0?args[0]:null);
	}
	public CSV2XLS(String path){
		try {
			List list = new List(List.DEFAULT_DRIVECSVPATH + (test?"test/":""));
			if(path!=null){
				list = new List(path);
			}

			final File cloudDir = new File(list.getDrivecsvPath());
			File[] csvFiles = cloudDir.listFiles(getFilter());

			for (File csvFile : csvFiles) {
				File xlsFile = new File(csvFile.getAbsolutePath().replaceAll(".csv", ".xls"));
				System.err.println(csvFile.getAbsolutePath());
				toXls(csvFile,xlsFile, list);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void toXls(File csvFile, File xlsFile, List list) {
		
		try
		{	
			java.util.List<String[]> rows = list.list(csvFile.getName().replaceAll(".csv", ""));
	        	String[] header = rows.get(0);
	        	rows.remove(0);
	        	
	            String[][] data = new String[rows.size()][];
	            createFile(header, (String[][])rows.toArray(data), xlsFile.getAbsolutePath());

		}
		catch (Exception e)
		{
			System.err.println(e);
		}
	}
	
    public void createFile(String[] header, String[][] data, String xlsFile) throws IOException {
        workBook = new HSSFWorkbook();
        sheet = workBook.createSheet("シート名"); // シート名を指定する
        style = createCellStyle();
        writeHeader(header);
        write(data);
        OutputStream out = new FileOutputStream(xlsFile); // ファイル名を指定する
        workBook.write(out);
        out.close();
    }

    private void writeHeader(String[] header) throws IOException {
        HSSFRow row = sheet.createRow(0);
        for (short i = 0; i < header.length; i++) {
            HSSFCell cell = row.createCell(i);
//            cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            cell.setCellValue(header[i]);
            cell.setCellStyle(style);
        }
        sheet.createFreezePane(0, 1, 0, 1);
    }

    private void write(String[][] data) throws IOException {
        short rowCount = 1;
        for (short i = 0; i < data.length; i++) {
            HSSFRow row = sheet.createRow(rowCount++);
            for (short j = 0; j < data.length; j++) {
                HSSFCell cell = row.createCell(j);
//                cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                cell.setCellValue(data[i][j]);
                cell.setCellStyle(style);
            }
        }
    }

    private HSSFCellStyle createCellStyle() {
        HSSFFont font = workBook.createFont();
        font.setFontHeightInPoints(FONT_SIZE);
        font.setFontName("ＭＳ Ｐゴシック");
        HSSFCellStyle style = workBook.createCellStyle();
        style.setFont(font);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setRightBorderColor(HSSFColor.BLACK.index);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setTopBorderColor(HSSFColor.BLACK.index);
        return style;
    }


	

	public static FileFilter getFilter() {  
		return new FileFilter() {  
			public boolean accept(File file) {  
				return file.isFile() && file.getName().endsWith("csv");  
			}  
		};  
	}
}
