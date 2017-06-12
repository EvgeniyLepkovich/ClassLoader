package com.epam;

import com.epam.classloader.AbstractClassLoader;
import com.epam.classloader.JarClassLoader;
import com.epam.classloader.loader.Loader;
import com.epam.util.JclObjectFactory;
import com.epam.util.JclUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Yayheniy_Lepkovich on 6/12/2017.
 */
public class AppConfig {
    private static final String JAR = "classpath: ClassLoader-1.0-SNAPSHOT.jar";
    private static final String CLASS = "com.epam.util.JclUtil";

    public static void main(String[] args) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JarClassLoader classLoader = new JarClassLoader();
        classLoader.add(JAR);
        Object object = JclObjectFactory.getInstance().create(classLoader, CLASS);
        System.out.println(object == null);
    }
}
