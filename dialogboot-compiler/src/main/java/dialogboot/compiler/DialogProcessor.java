package dialogboot.compiler;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
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

import com.masum.annotation.InjectDialog;
import com.masum.annotation.InjectView;

import utils.CompilerUtils;

import static utils.CompilerUtils.PACKAGE_NAME;


@AutoService(Processor.class)
public class DialogProcessor extends AbstractProcessor {
    private static String ACTIVITY_PARAMETER_NAME = "activity";
    private static String VIEW_PARAMETER_NAME = "view";
    private Messager messager;
    private Filer filer;
    private Elements elements;
    private HashMap<Element, String> elementsWithPackageName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        elements = processingEnvironment.getElementUtils();
        elementsWithPackageName = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        try {

            TypeSpec.Builder dialogBootClass = TypeSpec
                    .classBuilder(CompilerUtils.CLASS_NAME)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            dialogInjectorMethodBuilder(roundEnvironment, dialogBootClass);

            viewInjectorMethodBuilder(roundEnvironment, dialogBootClass);

            JavaFile.builder(PACKAGE_NAME, dialogBootClass.build()).build().writeTo(filer);


        } catch (Exception e) {

        }

        return true;
    }

    private void dialogInjectorMethodBuilder(RoundEnvironment roundEnvironment, TypeSpec.Builder dialogBootClass) {

        for(Element element : roundEnvironment.getElementsAnnotatedWith(InjectDialog.class)) {
            if (element.getKind() != ElementKind.FIELD) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Annotation didn't apply on the field");
            }

            elementsWithPackageName.put(element, elements.getPackageOf(element).getQualifiedName().toString());

        }


        HashMap<Element, ArrayList<Element>> enclosedViewElementClassList = new HashMap<>();

        for (Map.Entry<Element, String> entry: elementsWithPackageName.entrySet()) {
            addToEnclosedClassList(enclosedViewElementClassList, entry);

        }

        for(Map.Entry<Element, ArrayList<Element>> elementArrayListEntry:  enclosedViewElementClassList.entrySet()) {
            buildDialogInjectMethod(dialogBootClass, elementArrayListEntry);

        }
    }

    private void viewInjectorMethodBuilder(RoundEnvironment roundEnvironment, TypeSpec.Builder dialogBootClass) {
        elementsWithPackageName.clear();

        for(Element element : roundEnvironment.getElementsAnnotatedWith(InjectView.class)) {
            if (element.getKind() != ElementKind.FIELD) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Annotation didn't apply on the field");
            }

            elementsWithPackageName.put(element, elements.getPackageOf(element).getQualifiedName().toString());

        }


        HashMap<Element, ArrayList<Element>> enclosedViewElementClassList = new HashMap<>();

        for (Map.Entry<Element, String> entry: elementsWithPackageName.entrySet()) {
            addToEnclosedClassList(enclosedViewElementClassList, entry);
        }

        for(Map.Entry<Element, ArrayList<Element>> elementArrayListEntry:  enclosedViewElementClassList.entrySet()) {
            buildViewInjectMethod(dialogBootClass, elementArrayListEntry);
        }
    }

    private void buildViewInjectMethod(TypeSpec.Builder dialogBootClass, Map.Entry<Element, ArrayList<Element>> elementArrayListEntry) {
        MethodSpec.Builder viewInjectorMethod;
        viewInjectorMethod = MethodSpec.constructorBuilder();
        viewInjectorMethod.addModifiers(Modifier.PUBLIC);

        viewInjectorMethod.addParameter(TypeName.get(elementArrayListEntry.getKey().asType()), ACTIVITY_PARAMETER_NAME);
        viewInjectorMethod.addParameter(CompilerUtils.classView, VIEW_PARAMETER_NAME);

        for (int i = 0; i < elementArrayListEntry.getValue().size(); i++) {
            int layout = elementArrayListEntry.getValue().get(i).getAnnotation(InjectView.class).layout();
            viewInjectorMethod.addStatement(ACTIVITY_PARAMETER_NAME + "." + elementArrayListEntry.getValue().get(i)
                            + " = $L", getLayout(ACTIVITY_PARAMETER_NAME, layout));

        }

        dialogBootClass.addMethod(viewInjectorMethod.build());
    }

    private void buildDialogInjectMethod(TypeSpec.Builder dialogBootClass, Map.Entry<Element, ArrayList<Element>> elementArrayListEntry) {
        MethodSpec.Builder methodBuilder;
        methodBuilder = MethodSpec.constructorBuilder();
        methodBuilder.addModifiers(Modifier.PUBLIC);
        methodBuilder.addParameter(TypeName.get(elementArrayListEntry.getKey().asType()), ACTIVITY_PARAMETER_NAME);


        for (int i = 0; i < elementArrayListEntry.getValue().size(); i++) {

            String annotatedDialogVariableName = elementArrayListEntry.getValue().get(i) + "";

            String message = elementArrayListEntry.getValue().get(i).getAnnotation(InjectDialog.class).getMessage();

            boolean isCancelable = elementArrayListEntry.getValue().get(i).getAnnotation(InjectDialog.class).isCancelable();

            int layout = elementArrayListEntry.getValue().get(i).getAnnotation(InjectDialog.class).layout();
            int fullScreen = elementArrayListEntry.getValue().get(i).getAnnotation(InjectDialog.class).fullScreen();

            methodBuilder.addStatement(ACTIVITY_PARAMETER_NAME + "." + annotatedDialogVariableName + " = $L", createNewAlertDialog(ACTIVITY_PARAMETER_NAME));

            String inflater = getLayout(ACTIVITY_PARAMETER_NAME, layout);

            methodBuilder.addStatement(ACTIVITY_PARAMETER_NAME + "." + annotatedDialogVariableName + ".setMessage ($S)", message);
            methodBuilder.addStatement(ACTIVITY_PARAMETER_NAME + "." + annotatedDialogVariableName + ".setView ($L)", inflater);

            if (!isCancelable) {
                methodBuilder.addStatement(ACTIVITY_PARAMETER_NAME + "." + annotatedDialogVariableName + ".setCancelable ($L)", isCancelable);
            }

        }

        dialogBootClass.addMethod(methodBuilder.build());


    }

    private String getLayout(String parameterName, int layout) {
        return parameterName + "." + "getLayoutInflater()" + ".inflate(" + layout + ", null)";
    }

    private String createNewAlertDialog(String activityName) {
        return "new " + CompilerUtils.classIntent + "." + "Builder(" + activityName + ").create()";
    }

    private void addToEnclosedClassList(HashMap<Element, ArrayList<Element>> enclosedClassList, Map.Entry<Element, String> entry) {
        Element enclosedClass = entry.getKey().getEnclosingElement();

        if (enclosedClassList.containsKey(enclosedClass)) {
            enclosedClassList.get(enclosedClass).add(entry.getKey());
        } else {
            ArrayList<Element> elements = new ArrayList<Element>();
            elements.add(entry.getKey());
            enclosedClassList.put(enclosedClass, elements);
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(InjectDialog.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
