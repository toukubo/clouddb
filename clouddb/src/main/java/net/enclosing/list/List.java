package net.enclosing.list;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class List {
	public List(String googldDriveListDirPath){
		drivecsvPath = googldDriveListDirPath;
	}
	public List(){
	}




	public String getDrivecsvPath() {
		return drivecsvPath;
	}
	public void setDrivecsvPath(String drivecsvPath) {
		this.drivecsvPath = drivecsvPath;
	}



	public static String DEFAULT_DRIVECSVPATH = "/opt/list/";

	public String drivecsvPath = DEFAULT_DRIVECSVPATH;
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static void main(String[] args) {
		List list  = new List();
		java.util.List<String[]> rows = list.list("Custom");
		for (String[] strings : rows) {
			for (String string : strings) {
				System.err.print(string);
				System.err.print(",");
			}
			System.err.println();
		}
		System.err.println("was list by csv");

		java.util.List<Object[]> objects = list.list(Custom.class);
		for (Object object : objects) {
			Custom custom = (Custom) object;
			System.err.print(custom.getName());
			System.err.print(",");
			System.err.print(custom.getKit());
			System.err.print(",");
			System.err.print(custom.getBufferable());
			System.err.print(",");
			System.err.print(custom.getProject());
			System.err.print(",");
			System.err.println();
		}

	}
	public java.util.List list(Class clazz){
		String listname = clazz.getSimpleName();
		final java.util.List<String[]> rows = list(listname);
		final String[] attrsInCloudDB = rows.get(0);
		rows.remove(0);
		java.util.List objects = new ArrayList();
		for (String[] row : rows){
			Object object = fillModel(clazz, row, attrsInCloudDB);
			objects.add(object);
		}
		return objects;
	}
	private Object fillModel(Class clazz,String[] row,String[] attrs) {
		try {
			Object model = clazz.newInstance();
			Method methods[] = clazz.getMethods();
			for(int j=0; j<methods.length; j++) {
				if(methods[j].getName().startsWith(("set"))){
					String caseinsensitveAttrname = methods[j].getName().replaceFirst("set","");
					Class attrclass = methods[j].getParameterTypes()[0];
					String valueString = getValue(caseinsensitveAttrname,row,attrs);

					if(valueString!=null){
						if(attrclass.equals(int.class)) {
							methods[j].invoke(model,Integer.parseInt(valueString) );
						}else if(attrclass.equals(int.class)) {
							methods[j].invoke(model, Float.parseFloat(valueString));;
						}else if(attrclass.equals(java.util.Date.class)) {
							try {
								methods[j].invoke(model, DATE_FORMAT.parse(valueString));
							} catch (Exception e) {
								e.printStackTrace();
								methods[j].invoke(model, new Date());

							}
						}else if(attrclass.equals(boolean.class)) {
							methods[j].invoke(model, new Boolean(valueString.equals("true")));
						}else if(attrclass.equals(String.class)){
							methods[j].invoke(model, valueString);
						}else if (attrclass.equals(byte[].class)) {
						}else if (attrclass.equals(java.lang.Integer.class)) {
							methods[j].invoke(model, Integer.valueOf(valueString));
						}else if(!attrclass.equals(java.util.Collection.class)){

						}
					}

				}
			}	
			return model;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();    
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	private static String getValue(String caseinsensitveAttrname,String[] row, String[] attrs) {
		for (int i = 0; i < attrs.length; i++) {
			String attr = attrs[i];
			if(attr.equalsIgnoreCase(caseinsensitveAttrname)){
				return row[i];
			}
		}
		return null;
	}
	public java.util.List<String[]> list(String listname){
		try {
			String thepath = drivecsvPath+"/"+listname+".csv";
			CSVReader csvReader = new CSVReader(new FileReader(thepath));;
			return csvReader.readAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public void writeList(java.util.List objects, Class clazz) {
		try {

			String listname = clazz.getSimpleName();
			final java.util.List<String[]> rowsForHeader = list(listname);
			final String[] attrsInCloudDB = rowsForHeader.get(0);

			CSVWriter writer = new CSVWriter(new FileWriter(this.drivecsvPath+"/"+clazz.getSimpleName()+".csv"));
			java.util.List<String[]> rows = new ArrayList<String[]>();
			rows.add(attrsInCloudDB);
			for (Object object : objects) {
				String[] row = toRow(object,attrsInCloudDB);
				rows.add(row);
//				writer.writeNext(row);
			}
			writer.writeAll(rows);
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}


	}
	private String[] toRow(Object object, String[] attrsInCloudDB) {
		try {
			Class clazz = object.getClass();;

			java.util.List<String> attrValues = new ArrayList<String>();
			Method methods[] = clazz.getMethods();
			for(int j=0; j<methods.length; j++) {
				if(methods[j].getName().startsWith(("get"))){
					String caseinsensitveAttrname = methods[j].getName().replaceFirst("get","");
					//					Class attrclass = methods[j].getParameterTypes()[0];
					for (int i = 0; i < attrsInCloudDB.length; i++) {
						String attr = attrsInCloudDB[i];
						if(attr.equalsIgnoreCase(caseinsensitveAttrname)){
							String valueString = getValue(caseinsensitveAttrname,object,attrsInCloudDB,methods[j]);
							attrValues.add(valueString);
						}
					}
				}

			}

			String row[] = new String[attrValues.size()];
			attrValues.toArray(row);

			return	row;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private String getValue(String caseinsensitveAttrname, Object object,
			String[] attrs, Method method) {
		try {

			if(caseinsensitveAttrname.endsWith("Date")){
				Object dateObject = method.invoke(object);
				return DATE_FORMAT.format(dateObject);
			}
			return method.invoke(object).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}
}











