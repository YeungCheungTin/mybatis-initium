package org.mybatis.generator.internal.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * This class holds methods useful for constructing custom classloaders.
 * 
 * @author Jeff Butler
 * 
 */
public class ClassloaderUtility {

    /**
     * Utility Class - No Instances
     */
    private ClassloaderUtility() {
    }

    public static ClassLoader getCustomClassloader(List<String> entries) {
        List<URL> urls = new ArrayList<>();
        File file;

        if (entries != null) {
            for (String classPathEntry : entries) {
                file = new File(classPathEntry);
                if (!file.exists()) {
                    throw new RuntimeException(getString("RuntimeError.9", classPathEntry)); 
                }

                try {
                    urls.add(file.toURI().toURL());
                } catch (MalformedURLException e) {
                    // this shouldn't happen, but just in case...
                    throw new RuntimeException(getString("RuntimeError.9", classPathEntry)); 
                }
            }
        }

        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        return new URLClassLoader(urls.toArray(new URL[0]), parent);
    }
}
