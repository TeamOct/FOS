package fos.files;

import arc.files.Fi;
import arc.files.ZipFi;
import arc.util.Log;

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

        Log.info("Initialising internal file tree from " + owner.toString());
        String classPath = owner.getResource("").getFile();
        Log.info(classPath);
        classPath = classPath.substring(classPath.indexOf(":")+2);
        Log.info(classPath);
        String jarPath = classPath.substring(0, classPath.indexOf("!"));
        Log.info(jarPath);

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
