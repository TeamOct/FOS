package fos.processors;

import arc.audio.Sound;
import arc.files.Fi;
import arc.struct.Seq;
import arc.util.Structs;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import fos.annotations.AnnotationProcessor;
import fos.annotations.FOSAnnotations;
import fos.util.FOSProcessor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.util.Set;

@AnnotationProcessor
@SupportedOptions({"ProjectName", "ProjectRes", "ModPackage", "FileTree"}) // TODO remove this anno?
@FOSAnnotations.SupportedAnnotationTypes(FOSAnnotations.CreateSoundHost.class)
public class SoundProc extends FOSProcessor {

    @Override
    public void process(RoundEnvironment env) throws Exception {

    }

    Seq<Fi> temp = new Seq<>();
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        super.process(annotations, roundEnv);

        String[] resources = arguments.get("ProjectRes").split(";");
        String packagee = arguments.get("ModPackage");
        String fileTree = arguments.get("FileTree");
        String n = arguments.get("ProjectName");
        String name = n.substring(0, 1).toUpperCase() + n.substring(1);

        roundEnv.getElementsAnnotatedWith(FOSAnnotations.CreateSoundHost.class).forEach(element -> {
            FOSAnnotations.CreateSoundHost annotation = element.getAnnotation(FOSAnnotations.CreateSoundHost.class);

            String[] extensions = annotation.extensions();
            String[] paths = annotation.paths();
            String typeName = annotation.className() + name;
            int depth = annotation.depth();

            Seq<String> fields = new Seq<>();
            Seq<String> fieldsPaths = new Seq<>();

            for (int i = 0; i < resources.length; i++) {
                for (int j = 0; j < paths.length; j++) {
                    int finalI = i;
                    temp.clear();
                    scanFiles(depth, new Fi(resources[i] + "/" + paths[j]), temp).filter(file ->
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

    Seq<Fi> scanFiles(int depth, Fi file, Seq<Fi> out) {
        return scanFiles(depth, file, out, 0);
    }

    Seq<Fi> scanFiles(int depth, Fi file, Seq<Fi> out, int counter) {
        for (Fi fi : file.list()) {
            out.add(fi);
            if (counter >= 0 && counter < depth)
                scanFiles(depth, fi, out, counter+1);
        }
        return out;
    }
}
