package com.masum.annotation;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InjectDialog {
    boolean isTypeConfirmation() default false;
    boolean isCancelable();
    String getMessage();
    int layout() default 0;
    int fullScreen() default 0;

}


