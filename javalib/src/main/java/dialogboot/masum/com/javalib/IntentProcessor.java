package dialogboot.masum.com.javalib;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import dialogboot.masum.com.annotation.NewIntent;


@AutoService(Processor.class)
public class IntentProcessor extends AbstractProcessor {
    private static final String TARGET_STATEMENT_FORMAT = "target.%1$s = %2$s";
    private static final String METHOD_PREFIX = "start";
    private static final ClassName classIntent = ClassName.get("android.content", "Intent");
    private static final ClassName classContext = ClassName.get("android.content", "Context");

    private Filer filer;
    private Messager messager;
    private Elements elements;
    private Map<Element, String> activitiesWithPackage;
    private Map<String, String> activitiesWithPackage1;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        elements = processingEnvironment.getElementUtils();
        activitiesWithPackage = new HashMap<>();
        activitiesWithPackage1 = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        try {
            /**
             * 1- Find all annotated element
             */
            for (Element element : roundEnvironment.getElementsAnnotatedWith(NewIntent.class)) {

                if (element.getKind() != ElementKind.CLASS) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Can be applied to class.");
                    return true;
                }

                TypeElement typeElement = (TypeElement) element;
                activitiesWithPackage1.put(
                        typeElement.getSimpleName().toString(),
                        elements.getPackageOf(typeElement).getQualifiedName().toString());

                messager.printMessage(Diagnostic.Kind.NOTE, "Type:=== " + typeElement.getSimpleName());
            }


            /**
             * 2- Generate a class
             */
            TypeSpec.Builder navigatorClass = TypeSpec
                    .classBuilder("Navigator")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            for (Map.Entry<String, String> element : activitiesWithPackage1.entrySet()) {
                String activityName = element.getKey();
                String packageName = element.getValue();
                ClassName activityClass = ClassName.get(packageName, activityName);
                String x = "new " + classIntent.toString() +"(context, " + activityClass + ".class)";
                MethodSpec intentMethod = MethodSpec
                        .methodBuilder(METHOD_PREFIX + activityName)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(classIntent)
                        .addParameter(classContext, "context")
                        .addStatement("context.startActivity($L)", x)
                        .addStatement("return new $T($L, $L)", classIntent, "context", activityClass + ".class")
                        .build();
                navigatorClass.addMethod(intentMethod);
            }

            /**
             * 3- Write generated class to a file
             */
            JavaFile.builder("com.annotationsample", navigatorClass.build()).build().writeTo(filer);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(NewIntent.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}