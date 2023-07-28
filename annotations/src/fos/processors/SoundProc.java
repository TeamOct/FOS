package fos.processors;

import arc.audio.Sound;
import arc.files.Fi;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Structs;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import fos.annotations.CreateSoundHost;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({"ProjectName", "ProjectRes", "ModPackage", "FileTree"})
@SupportedAnnotationTypes("fos.annotations.CreateSoundHost")
public class SoundProc extends AbstractProcessor {
    private Types types;
    private Elements elements;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        types = processingEnv.getTypeUtils();
        elements = processingEnv.getElementUtils();

        String[] resources = processingEnv.getOptions().get("ProjectRes").split(";");
        String n = processingEnv.getOptions().get("ProjectName");
        String name = n.substring(0, 1).toUpperCase() + n.substring(1);
        String packagee = processingEnv.getOptions().get("ModPackage");
        String fileTree = processingEnv.getOptions().get("FileTree");

        roundEnv.getElementsAnnotatedWith(CreateSoundHost.class).forEach(element -> {
            CreateSoundHost annotation = element.getAnnotation(CreateSoundHost.class);

            String[] extensions = annotation.extensions();
            String[] paths = annotation.paths();
            String typeName = annotation.className() + name;

            Seq<String> fields = new Seq<>();
            Seq<String> fieldsPaths = new Seq<>();

            for (int i = 0; i < resources.length; i++) {
                for (int j = 0; j < paths.length; j++) {
                    int finalI = i;
                    new Fi(resources[i] + "/" + paths[j]).findAll(file ->
                            Structs.contains(extensions, file.extension())).each(file -> {
                        String fieldName = file.nameWithoutExtension();
                        while (fields.contains(fieldName)) {
                            fieldName += "_";
                        }
                        fields.add(fieldName);
                        fieldsPaths.add(file.absolutePath().substring(resources[finalI].length()));
                    });
                }
            }

            TypeSpec.Builder type = TypeSpec.classBuilder(typeName).addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            for (int i = 0; i < fields.size; i++) {
                type.addField(FieldSpec.builder(Sound.class, fields.get(i), Modifier.PUBLIC, Modifier.STATIC)
                        .initializer(CodeBlock.builder().add("arc.Core.audio.newSound($1L.child(\"$2L\"))",
                                fileTree, fieldsPaths.get(i)).build()).build());
            }

            try {
                JavaFile.builder(packagee + ".audio", type.build()).indent("    ").build().writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return false;
    }
}
