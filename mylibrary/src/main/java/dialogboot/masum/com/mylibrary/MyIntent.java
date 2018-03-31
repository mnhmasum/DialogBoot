package dialogboot.masum.com.mylibrary;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Created by mac on 3/31/18.
 */

@Retention(CLASS)
@Target(value = FIELD)
public @interface MyIntent {
}
