package com.drukido.vrun.database.annotations;

import com.drukido.vrun.database.DBObject;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Ido on 10/4/2015.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EntityArraySetter {
    String fkColumnName();
    Class<? extends DBObject> entityClass();
}
