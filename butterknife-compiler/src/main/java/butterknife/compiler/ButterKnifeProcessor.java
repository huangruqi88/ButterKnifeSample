package butterknife.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * author:黄汝琪 on 2018/9/7.
 * email:huangruqi88@163.com
 */
@AutoService(Processor.class)
public class ButterKnifeProcessor extends AbstractProcessor{

    /**
     * 使用Filer你可以创建文件
     */
    private Filer filer;
    /**
     * 一个用来处理Element的工具类，源代码的每一个部分都是一个特定类型的Element，
     */
    private Elements elementUtils;
    /**
     * 一个用来处理TypeMirror的工具类；
     */
    private Types typeUtils;
    /**
     * 点击事件
     */
    private static final List<Class<? extends Annotation>> LISTENERS = Arrays.asList(
            OnCheckedChanged.class,
            OnClick.class
    );

    /**
     * AbstractProcessor 详细使用讲解
     * https://www.jianshu.com/p/07ef8ba80562
     */
    /**
     * 每一个注解处理器类都必须有一个空的构造函数。然而，这里有一个特殊的init()方法，它会被注解处理工具调用，
     * 并输入ProcessingEnviroment参数。ProcessingEnviroment提供很多有用的工具类Elements,Types和Filer。
     *
     * Elements详情{@link Foo}
     *
     *
     * @param roundEnvironment
     */

    @Override
    public synchronized void init(ProcessingEnvironment roundEnvironment) {
        super.init(roundEnvironment);

        elementUtils = roundEnvironment.getElementUtils();
        typeUtils = roundEnvironment.getTypeUtils();
        filer = roundEnvironment.getFiler();
//        roundEnvironment.
    }

    /**
     * 这相当于每个处理器的主函数main()。你在这里写你的扫描、评估和处理注解的代码，以及生成Java文件。
     * 输入参数RoundEnviroment，可以让你查询出包含特定注解的被注解元素。
     *
     * @param set
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);

        // 将获取到的bindview细分到每个class
        Map<Element, List<Element>> elementMap = new LinkedHashMap<>();

        for (Element element : elements) {
            // 返回activity
            Element enclosingElement = element.getEnclosingElement();

            List<Element> bindViewElements = elementMap.get(enclosingElement);
            if (bindViewElements == null) {
                bindViewElements = new ArrayList<>();
                elementMap.put(enclosingElement, bindViewElements);
            }
            bindViewElements.add(element);
        }

        // 生成代码
        for (Map.Entry<Element, List<Element>> entrySet : elementMap.entrySet()) {
            Element enclosingElement = entrySet.getKey();
            List<Element> bindViewElements = entrySet.getValue();

            // public final class xxxActivity_ViewBinding implements Unbinder
            // 获取activity的类名
            String activityClassNameStr = enclosingElement.getSimpleName().toString();
            System.out.println("------------->" + activityClassNameStr);
            ClassName activityClassName = ClassName.bestGuess(activityClassNameStr);
            ClassName unBinderClassName = ClassName.get("butterknife.compiler.butterknife", "Unbinder");
            //类的权限修饰符
            TypeSpec.Builder classBuilder =
                    TypeSpec.classBuilder(activityClassNameStr + "_ViewBinding")
                            .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                            .addSuperinterface(unBinderClassName)
                            // 添加属性 private MainActivity target;
                            .addField(activityClassName, "target", Modifier.PRIVATE);

            // unbind()
            ClassName callSuperClassName = ClassName.get("android.support.annotation", "CallSuper");
            MethodSpec.Builder unbindMethodBuilder = MethodSpec.methodBuilder("unbind")
                    .addAnnotation(Override.class)
                    .addAnnotation(callSuperClassName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            // 构造函数
            MethodSpec.Builder constructorMethodBuilder = MethodSpec.constructorBuilder()
                    .addParameter(activityClassName, "target")
                    .addModifiers(Modifier.PUBLIC)
                    // this.target = target
                    .addStatement("this.target = target");

            for (Element bindViewElement : bindViewElements) {
                // textview
                String fieldName = bindViewElement.getSimpleName().toString();
                // Utils
                ClassName utilsClassName = ClassName.get("butterknife.compiler.butterknife", "Utils");
                // R.id.textview
                int resourceId = bindViewElement.getAnnotation(BindView.class).value();
                // target.textview = Utils.findViewById(target, R.id.textview)
                constructorMethodBuilder.addStatement("target.$L = $T.findViewById(target, $L)", fieldName, utilsClassName, resourceId);
                // target.textview = null
                unbindMethodBuilder.addStatement("target.$L = null", fieldName);
            }


            classBuilder.addMethod(unbindMethodBuilder.build())
                    .addMethod(constructorMethodBuilder.build());

            // 获取包名
            String packageName = elementUtils.getPackageOf(enclosingElement).getQualifiedName().toString();

            try {
                JavaFile.builder(packageName, classBuilder.build())
                        .addFileComment("自己写的ButterKnife生成的代码，不要修改！！！")
                        .build().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;

    }

    /**
     *
     * 这里你必须指定，这个注解处理器是注册给哪个注解的。注意，它的返回值是一个字符串的集合，
     * 包含本处理器想要处理的注解类型的合法全称。换句话说，你在这里定义你的注解处理器注册到哪些注解上。
     *
     * 遍历注解，获取我们定义的注解类型
     *
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    /**
     * 用来指定你使用的Java版本。通常这里返回SourceVersion.latestSupported()。
     * 然而，如果你有足够的理由只支持Java 7的话，你也可以返回SourceVersion.RELEASE_7。我推荐你使用前者。
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return super.getSupportedSourceVersion();
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(BindView.class);
        annotations.add(BindViews.class);
        annotations.addAll(LISTENERS);

        return annotations;
    }

}
