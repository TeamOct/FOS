package fos.files;

import arc.files.Fi;
import arc.files.ZipFi;

/**
 * Use for JAR internal navigation
 * @author nekit508
 **/
public class InternalFileTree {
    public Class<?> anchorClass;

    public ZipFi root;

    /**
     * @param owner navigation anchor
     **/
    public InternalFileTree(Class<?> owner) {
        anchorClass = owner;

        String classPath = owner.getResource("").getFile();
        classPath = classPath.substring(classPath.indexOf(":")+2);
        String jarPath = classPath.substring(0, classPath.indexOf("!"));

        root = new ZipFi(new Fi(jarPath));
    }

    public Fi child(String childPath) {
        Fi out = root;
        for (String s : childPath.split("/")) {
            out = out.child(s);
        }
        return out;
    }
}
