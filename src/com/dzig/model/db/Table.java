package com.dzig.model.db;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

public class Table {
	private final String tableName;
	private final List<Pair<String, Type>> columns;
	private final List<Pair<String, String>> indexes;
	
	private Table(String tableName, List<Pair<String, Type>> columns, List<Pair<String, String>> indexes) {
		this.tableName = tableName;
		this.columns = columns;
		this.indexes = indexes;
	}
	
	public static Table newInstance(Class<?> clazz) {
		return new Table (parseTableName(clazz), parseTableColumns(clazz), parseTableIndexes(clazz)); 
	}
	
	
	public void create (SQLiteDatabase db){
		ArrayList<String> sqlCommands = new ArrayList<String>();
		sqlCommands.add(getCreateTableCommand());
		
		for (Pair<String, String> pair : indexes) {
			sqlCommands.add(getCreateIndexCommand(pair.first, pair.second));
		}
		
		// TODO print
		
		for (String command : sqlCommands) {
			db.execSQL(command);
		}
	}
	
	public void drop (SQLiteDatabase db){
		ArrayList<String> sqlCommands = new ArrayList<String>();
		
		for (Pair<String, String> pair : indexes) {
			sqlCommands.add(getDropIndexCommand(pair.first));
		}
		sqlCommands.add(getDropTableCommand());
		
		// TODO print
		
		for (String command : sqlCommands) {
			db.execSQL(command);
		}
	}
	
	public String getTableName(){
		return tableName;
	}
	
	
	// ======================= PARSING ======================================
	private static String parseTableName(Class<?> clazz){
		ATable aTable = clazz.getAnnotation(ATable.class);
		if (aTable == null) throw new IllegalArgumentException(clazz+" does not provide @ATable annotation");
		return aTable.name();
	}
	
	private static List<Pair<String, Type>> parseTableColumns(Class<?> clazz) {
		List<Pair<String, Type>> result = new ArrayList<Pair<String,Type>>(); 
		for (Field field : clazz.getFields()) {
			AColumn aColumn = field.getAnnotation(AColumn.class);
			if (aColumn != null) {
				result.add(new Pair<String, Type>(getFieldValue(field), aColumn.type()));
			}
		}
		return result;
	}

	private static List<Pair<String, String>> parseTableIndexes(Class<?> clazz) {
		List<Pair<String, String>> result = new ArrayList<Pair<String,String>>();  
		for (Field field : clazz.getFields()) {
			AIndex aIndex = field.getAnnotation(AIndex.class);
			if (aIndex != null) {
				String indexName = getFieldValue(field);
				String columns = aIndex.columns();
				checkIndex(clazz, indexName, columns);
				result.add(new Pair<String, String>(indexName, columns));
			}
		}
		return result;
	}

	private static String getFieldValue (Field field){
		if (!Modifier.isStatic(field.getModifiers())) {
			throw new IllegalArgumentException(field+ " should be declared as static");
		}
		
		if (!String.class.equals(field.getType())){
			throw new IllegalArgumentException(field+ " should be declared as String");
		}
		
		try {
			return (String)field.get(null);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	
	private static Field findFieldWithValue(Class<?> clazz, String value){
		for (Field field : clazz.getFields()) {
			if (Modifier.isStatic(field.getModifiers()) && String.class.equals(field.getType())) {
				
				try {
					if (value.equals((String)field.get(null))) {
						return field;
					}
				} catch (Exception e) {
					//ignore;
				}
			}
		}
		return null;
	}
	
	private static void checkIndex(Class<?> clazz, String indexName, String columns) {
		for (String columnName : columns.split(",")) {
			Field field = findFieldWithValue(clazz, columnName);
			if (field == null) {
				throw new IllegalArgumentException("Index "+indexName+" error: " + columnName+ " column can't be found");
			} else if (!field.isAnnotationPresent(AColumn.class)){
				throw new IllegalArgumentException("Index "+indexName+" error: " + columnName+ " column found, but contains no @AColumn annotation");
			}
		}
	}
	
	private String getCreateTableCommand(){
		StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(tableName).append("(");
		for (int i = 0; i < columns.size(); i++) {
			if (i > 0){
				builder.append(", ");
			}
			Pair<String, Type> pair = columns.get(i);
			builder.append(pair.first).append(" ").append(pair.second);
		}
		builder.append(")");
		return builder.toString();
	}
	
	private String getDropTableCommand(){
		return new StringBuilder("DROP TABLE IF EXISTS ").append(tableName).toString();
	}
	
	private String getDropIndexCommand(String indexName){
		return new StringBuilder("DROP INDEX IF EXISTS ").append(indexName).toString();
	}
	
	private String getCreateIndexCommand(String indexName, String columns){
		return new StringBuilder("CREATE INDEX IF NOT EXISTS ").append(indexName).append(" ON ").append(tableName)
				.append("(").append(columns).append(")").toString();
	}
	
//	private static String getCreateTableCommand(Class<?> clazz){
//		String tableName = getTableName(clazz);
//		List<Pair<String, Type>> columns = getTableColumns(clazz);
//		StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(tableName).append("(");
//		
//		for (int i = 0; i < columns.size(); i++) {
//			if (i > 0){
//				builder.append(", ");
//			}
//			Pair<String, Type> pair = columns.get(i);
//			builder.append(pair.first).append(" ").append(pair.second);
//		}
//		builder.append(")");
//		return builder.toString();
//	}
//	
//	private static String getDropTableCommand(Class<?> clazz){
//		return new StringBuilder("DROP TABLE IF EXISTS ").append(getTableName(clazz)).toString();
//	}
//	
//	private static String getDropIndexCommand(String indexName){
//		return new StringBuilder("DROP INDEX IF EXISTS ").append(indexName).toString();
//	}
//	
//	private static String getCreateIndexCommand(String tableName, String indexName, String columns){
//		return new StringBuilder("CREATE INDEX IF NOT EXISTS ").append(indexName).append(" ON ").append(tableName)
//				.append("(").append(columns).append(")").toString();
//	}
}
