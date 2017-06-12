package com.epam.util;

import com.epam.classloader.JarClassLoader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Yayheniy_Lepkovich on 6/12/2017.
 */
@SuppressWarnings("unchecked")
public class JclObjectFactory {
    private static JclObjectFactory jclObjectFactory = new JclObjectFactory();

    /**
     * private constructor
     */
    private JclObjectFactory() {
    }

    /**
     * Returns the instance of the singleton factory
     *
     * @return JclObjectFactory
     */
    public static JclObjectFactory getInstance() {
        return jclObjectFactory;
    }

    /**
     * Creates the object of the specified class from the specified class loader
     * by invoking the default constructor
     *
     * @param jcl
     * @param className
     * @return Object
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public Object create(JarClassLoader jcl, String className) throws IllegalArgumentException, SecurityException,
            IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        return create( jcl, className, null );
    }

    /**
     * Creates the object of the specified class from the specified class loader
     * by invoking the right arguments-constructor
     *
     * @param jcl
     * @param className
     * @param args
     * @return Object
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public Object create(JarClassLoader jcl, String className, Object[] args) throws IOException,
            ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
            SecurityException, InvocationTargetException, NoSuchMethodException {
        if( args == null || args.length == 0 )
            return jcl.loadClass( className ).newInstance();

        Class[] types = new Class[args.length];

        for( int i = 0; i < args.length; i++ )
            types[i] = args[i].getClass();

        return jcl.loadClass( className ).getConstructor( types ).newInstance( args );
    }

    /**
     * Creates the object of the specified class from the specified class loader
     * by invoking the right static factory method
     *
     * @param jcl
     * @param className
     * @param methodName
     * @param args
     * @return Object
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public Object create(JarClassLoader jcl, String className, String methodName, Object[] args) throws IOException,
            ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
            SecurityException, InvocationTargetException, NoSuchMethodException {
        if( args == null || args.length == 0 )
            return jcl.loadClass( className ).getMethod( methodName ).invoke( null );

        Class[] types = new Class[args.length];

        for( int i = 0; i < args.length; i++ )
            types[i] = args[i].getClass();

        return jcl.loadClass( className ).getMethod( methodName, types ).invoke( null, args );
    }
}
