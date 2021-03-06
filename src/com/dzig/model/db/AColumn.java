package com.dzig.model.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.FIELD)
public @interface AColumn {
	Type type();
	Constraint[] constraint() default Constraint.NONE;
	OnConflict[] onConflict() default OnConflict.NONE;
	
}
