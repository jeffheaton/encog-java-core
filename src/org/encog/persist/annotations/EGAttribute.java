package org.encog.persist.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * This annotation is used with Encog generic persistence. It allows a field to
 * be flagged as something that should be persisted as an XML attribute.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD })
public @interface EGAttribute {

}
