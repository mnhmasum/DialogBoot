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


@AutoService(Processor.class)
public class DialogProcessor extends AbstractProcessor {

    private static final ClassName classIntent = ClassName.get("android.app", "AlertDialog");
    private static final ClassName classView = ClassName.get("android.view", "View");
    private static final ClassName classActivity = ClassName.get("android.app", "Activity");

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
                    .classBuilder("DialogBoot")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addField(classIntent, "al",  Modifier.PUBLIC, Modifier.STATIC);

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


                MethodSpec.Builder b = MethodSpec.constructorBuilder();
                b.addModifiers(Modifier.PUBLIC);
                b.addParameter(TypeName.get(entry.getKey().getEnclosingElement().asType()), "activity");

                /*b.addStatement("activity." + entry.getKey() + " = $L", "new " + classIntent
                        + ".Builder(" + "" + "activity)");*/
                String dialogBuilder = entry.getKey() + "";

                b.addStatement("activity." + dialogBuilder + " = $L", "new " + classIntent
                        + ".Builder(" + "" + "activity).create()");

                String inflater = "activity." + "getLayoutInflater()" + ".inflate(" + layout + ", null)";


                b.addStatement("activity." + dialogBuilder + ".setMessage ($S)", val + elements.getTypeElement("java.lang.String").asType());
                b.addStatement("activity." + dialogBuilder + ".setView ($L)", inflater);

                if (!isCancelable) {
                    b.addStatement("activity." + dialogBuilder + ".setCancelable ($L)", isCancelable);
                }

                //b.addStatement("activity." + entry.getKey() + " = $L", entry.getKey() + "Builder" + ".create()");

                MethodSpec methodSpec1 = b.build();
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

            String m = "";
            MethodSpec.Builder b1 = null;
            for (Map.Entry<Element, String> entry: elementsWithPackageName.entrySet()) {

                int layout = entry.getKey().getAnnotation(InjectView.class).layout();

                if (!m.equals(entry.getKey().getEnclosingElement().toString())) {
                    b1 = MethodSpec.constructorBuilder();
                    b1.addModifiers(Modifier.PUBLIC);
                    b1.addParameter(TypeName.get(entry.getKey().getEnclosingElement().asType()), "activity");
                    b1.addParameter(classView, "view");
                }

                Element q = entry.getKey().getEnclosingElement();

                if (h.containsKey(q)) {
                    h.get(q).add(entry.getKey());
                } else {
                    ArrayList<Element> e = new ArrayList<Element>();
                    e.add(entry.getKey());
                    h.put(q, e);
                }


                b1.addStatement("activity." + entry.getKey() + " = activity.getLayoutInflater().inflate($L, $L)", layout, null );
                b1.addStatement("String x = $S", elements.getPackageOf(entry.getKey()).getQualifiedName().toString() + entry.getKey().getEnclosingElement());

                //b.addStatement("activity." + entry.getKey() + " = $L", entry.getKey() + "Builder" + ".create()");

                m = entry.getKey().getEnclosingElement().toString();

            }

            for(Map.Entry<Element, ArrayList<Element>> j:  h.entrySet()) {

                b1 = MethodSpec.constructorBuilder();
                b1.addModifiers(Modifier.PUBLIC);

                b1.addParameter(TypeName.get(j.getKey().asType()), "activity");
                b1.addParameter(classView, "view");

                for (int i = 0; i < j.getValue().size(); i++) {
                    int layout = j.getValue().get(i).getAnnotation(InjectView.class).layout();
                    b1.addStatement("activity." + j.getValue().get(i) + " = activity.getLayoutInflater().inflate($L, $L)",
                             layout, null);
                }

                navigatorClass1.addMethod(b1.build());

            }

            JavaFile.builder("com.masum.dialogboot", navigatorClass1.build()).build().writeTo(filer);


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
