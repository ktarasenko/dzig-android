package com.dzig.model.db;

public enum Type {
	PRIMARY_KEY("INTEGER PRIMARY KEY AUTOINCREMENT"), INTEGER("INTEGER"), REAL("REAL"), TEXT("TEXT");

	final String sqlStr;
	private Type(String sqlStr){
		this.sqlStr = sqlStr;
	}
}
