package dialogboot.compiler;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
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
import static utils.CompilerUtils.classIntent;


@AutoService(Processor.class)
public class DialogProcessor extends AbstractProcessor {
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

            for(Element element : roundEnvironment.getElementsAnnotatedWith(InjectDialog.class)) {
                if (element.getKind() != ElementKind.FIELD) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Annotation didn't apply on the field");
                }

                elementsWithPackageName.put(element, elements.getPackageOf(element).getQualifiedName().toString());

            }

            dialogInjectorMethodBuilder(dialogBootClass);

            viewInjectorMethodBuilder(roundEnvironment, dialogBootClass);

            JavaFile.builder(PACKAGE_NAME, dialogBootClass.build()).build().writeTo(filer);


        } catch (Exception e) {

        }

        return true;
    }

    private void dialogInjectorMethodBuilder(TypeSpec.Builder dialogBootClass) {
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

        viewInjectorMethod.addParameter(TypeName.get(elementArrayListEntry.getKey().asType()), "activity");
        viewInjectorMethod.addParameter(CompilerUtils.classView, "view");

        for (int i = 0; i < elementArrayListEntry.getValue().size(); i++) {
            int layout = elementArrayListEntry.getValue().get(i).getAnnotation(InjectView.class).layout();
            viewInjectorMethod.addStatement("activity." + elementArrayListEntry.getValue().get(i)
                            + " = activity.getLayoutInflater().inflate($L, $L)", layout, null);
        }

        dialogBootClass.addMethod(viewInjectorMethod.build());
    }

    private void buildDialogInjectMethod(TypeSpec.Builder dialogBootClass, Map.Entry<Element, ArrayList<Element>> elementArrayListEntry) {
        MethodSpec.Builder methodBuilder;
        methodBuilder = MethodSpec.constructorBuilder();
        methodBuilder.addModifiers(Modifier.PUBLIC);
        methodBuilder.addParameter(TypeName.get(elementArrayListEntry.getKey().asType()), "activity");


        for (int i = 0; i < elementArrayListEntry.getValue().size(); i++) {

            String dialogBuilder = elementArrayListEntry.getValue().get(i) + "";

            String val = elementArrayListEntry.getValue().get(i).getAnnotation(InjectDialog.class).getMessage();

            boolean isCancelable = elementArrayListEntry.getValue().get(i).getAnnotation(InjectDialog.class).isCancelable();

            int layout = elementArrayListEntry.getValue().get(i).getAnnotation(InjectDialog.class).layout();
            int fullScreen = elementArrayListEntry.getValue().get(i).getAnnotation(InjectDialog.class).fullScreen();

            methodBuilder.addStatement("activity" + "." + dialogBuilder + " = $L", "new " + CompilerUtils.classIntent
                    + "." + "Builder(" + "activity).create()");

            String inflater = "activity" + "." + "getLayoutInflater()" + ".inflate(" + layout + ", null)";


            methodBuilder.addStatement("activity" + "." + dialogBuilder + ".setMessage ($S)", val + elements.getTypeElement("java.lang.String").asType());
            methodBuilder.addStatement("activity" + "." + dialogBuilder + ".setView ($L)", inflater);

            if (!isCancelable) {
                methodBuilder.addStatement("activity" + "." + dialogBuilder + ".setCancelable ($L)", isCancelable);
            }

        }

        dialogBootClass.addMethod(methodBuilder.build());


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
