package utils;

import com.squareup.javapoet.ClassName;

/**
 * Created by mac on 4/8/18.
 */

public class CompilerUtils {
    public static final String PACKAGE_NAME = "com.masum.dialogboot";
    public static final String CLASS_NAME = "DialogBoot";
    public static final ClassName classIntent = ClassName.get("android.app", "AlertDialog");
    public static final ClassName classView = ClassName.get("android.view", "View");
}
