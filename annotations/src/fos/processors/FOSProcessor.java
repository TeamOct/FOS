package fos.processors;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.struct.StringMap;
import arc.util.Log;
import arc.util.Strings;
import fos.annotations.FOSAnnotations;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.security.KeyException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class FOSProcessor extends AbstractProcessor {
    public Types types;
    public Elements elements;

    public StringMap arguments = new StringMap(){
        @Override
        public boolean containsKey(String key) {
            if (super.containsKey(key)) {
                return true;
            } else {
                throw new IllegalArgumentException(Strings.format("Key @ not found.", key));
            }
        }
    };

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        types = processingEnv.getTypeUtils();
        elements = processingEnv.getElementUtils();

        processingEnv.getOptions().forEach((k, v) -> arguments.put(k, v));
        roundEnv.getElementsAnnotatedWith(FOSAnnotations.Settings.class).forEach(e -> {
            arguments.putAll((Object[]) e.getAnnotation(FOSAnnotations.Settings.class).map());
        });

        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        FOSAnnotations.SupportedAnnotationTypes sat = getClass().getAnnotation(FOSAnnotations.SupportedAnnotationTypes.class);
        if (sat == null) {
            return new HashSet<>();
        } else {
            return Arrays.stream(sat.value()).map(Class::getCanonicalName).collect(Collectors.toSet());
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }
}
