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

            TypeSpec.Builder navigatorClass1 = TypeSpec
                    .classBuilder(CompilerUtils.CLASS_NAME)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addField(CompilerUtils.classIntent, "al",  Modifier.PUBLIC, Modifier.STATIC);

            for(Element element : roundEnvironment.getElementsAnnotatedWith(InjectDialog.class)) {
                if (element.getKind() != ElementKind.FIELD) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Annotation didn't apply on the field");
                }

                elementsWithPackageName.put(element, elements.getPackageOf(element).getQualifiedName().toString());

            }

            for (Map.Entry<Element, String> entry: elementsWithPackageName.entrySet()) {

                String val = entry.getKey().getAnnotation(InjectDialog.class).getMessage();
                boolean isCancelable = entry.getKey().getAnnotation(InjectDialog.class).isCancelable();
                int layout = entry.getKey().getAnnotation(InjectDialog.class).layout();
                int fullScreen = entry.getKey().getAnnotation(InjectDialog.class).fullScreen();


                MethodSpec.Builder methodBuilder = MethodSpec.constructorBuilder();
                methodBuilder.addModifiers(Modifier.PUBLIC);
                methodBuilder.addParameter(TypeName.get(entry.getKey().getEnclosingElement().asType()), "activity");

                String dialogBuilder = entry.getKey() + "";

                methodBuilder.addStatement("activity" + "." + dialogBuilder + " = $L", "new " + CompilerUtils.classIntent
                        + "." + "Builder(" + "activity).create()");

                String inflater = "activity" + "." + "getLayoutInflater()" + ".inflate(" + layout + ", null)";


                methodBuilder.addStatement("activity" + "." + dialogBuilder + ".setMessage ($S)", val + elements.getTypeElement("java.lang.String").asType());
                methodBuilder.addStatement("activity" + "." + dialogBuilder + ".setView ($L)", inflater);

                if (!isCancelable) {
                    methodBuilder.addStatement("activity" + "." + dialogBuilder + ".setCancelable ($L)", isCancelable);
                }

                MethodSpec methodSpec1 = methodBuilder.build();
                navigatorClass1.addMethod(methodSpec1);


            }

            elementsWithPackageName.clear();

            for(Element element : roundEnvironment.getElementsAnnotatedWith(InjectView.class)) {
                if (element.getKind() != ElementKind.FIELD) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Annotation didn't apply on the field");
                }

                elementsWithPackageName.put(element, elements.getPackageOf(element).getQualifiedName().toString());

            }

            HashMap<Element, ArrayList<Element>> h = new HashMap<>();

            String elementClassName = "";

            MethodSpec.Builder viewInjectorMethod = null;

            for (Map.Entry<Element, String> entry: elementsWithPackageName.entrySet()) {

                int layout = entry.getKey().getAnnotation(InjectView.class).layout();

                if (!elementClassName.equals(entry.getKey().getEnclosingElement().toString())) {
                    viewInjectorMethod = MethodSpec.constructorBuilder();
                    viewInjectorMethod.addModifiers(Modifier.PUBLIC);
                    viewInjectorMethod.addParameter(TypeName.get(entry.getKey().getEnclosingElement().asType()), "activity");
                    viewInjectorMethod.addParameter(CompilerUtils.classView, "view");
                }

                Element q = entry.getKey().getEnclosingElement();

                if (h.containsKey(q)) {
                    h.get(q).add(entry.getKey());
                } else {
                    ArrayList<Element> e = new ArrayList<Element>();
                    e.add(entry.getKey());
                    h.put(q, e);
                }


                viewInjectorMethod.addStatement("activity." + entry.getKey() + " = activity.getLayoutInflater().inflate($L, $L)", layout, null );
                viewInjectorMethod.addStatement("String x = $S", elements.getPackageOf(entry.getKey()).getQualifiedName().toString() + entry.getKey().getEnclosingElement());

                elementClassName = entry.getKey().getEnclosingElement().toString();

            }

            for(Map.Entry<Element, ArrayList<Element>> j:  h.entrySet()) {

                viewInjectorMethod = MethodSpec.constructorBuilder();
                viewInjectorMethod.addModifiers(Modifier.PUBLIC);

                viewInjectorMethod.addParameter(TypeName.get(j.getKey().asType()), "activity");
                viewInjectorMethod.addParameter(CompilerUtils.classView, "view");

                for (int i = 0; i < j.getValue().size(); i++) {
                    int layout = j.getValue().get(i).getAnnotation(InjectView.class).layout();
                    viewInjectorMethod.addStatement("activity." + j.getValue().get(i) + " = activity.getLayoutInflater().inflate($L, $L)",
                             layout, null);
                }

                navigatorClass1.addMethod(viewInjectorMethod.build());

            }

            JavaFile.builder(PACKAGE_NAME, navigatorClass1.build()).build().writeTo(filer);


        } catch (Exception e) {

        }

        return true;
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
