package com.epam.classloader;

import com.epam.classloader.loader.Loader;
import com.epam.entity.ClasspathResources;
import com.epam.exception.JclException;
import com.epam.util.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yayheniy_Lepkovich on 6/12/2017.
 */
@SuppressWarnings("unchecked")
public class JarClassLoader extends AbstractClassLoader {
    protected final Map<String, Class> classes;
    protected final ClasspathResources classpathResources;
    protected char classNameReplacementChar;
    private final Loader localLoader = new LocalLoader();

    public JarClassLoader() {
        classpathResources = new ClasspathResources();
        classes = Collections.synchronizedMap( new HashMap<String, Class>() );
        loaders.add( localLoader );
    }

    /**
     * Loads classes from different sources
     *
     * @param resources
     * @throws IOException
     */
    public JarClassLoader(Object[] resources) throws IOException {
        this();

        for( Object resource : resources ) {
            if( resource instanceof InputStream)
                add( (InputStream) resource );
            else if( resource instanceof URL)
                add( (URL) resource );
            else if( resource instanceof String )
                add( (String) resource );
            else
                throw new JclException( "Unknown Resource type" );
        }
    }

    /**
     * Loads local/remote resource
     *
     * @param resourceName
     * @throws IOException
     */
    public void add(String resourceName) throws IOException {
        classpathResources.loadResource( resourceName );
    }

    /**
     * Loads resource from InputStream
     *
     * @param jarStream
     * @throws IOException
     */
    public void add(InputStream jarStream) throws IOException {
        classpathResources.loadJar( jarStream );
    }

    /**
     * Loads local/remote resource
     *
     * @param url
     * @throws IOException
     */
    public void add(URL url) throws IOException {
        classpathResources.loadResource( url );
    }

    /**
     *
     * Reads the class bytes from different local and remote resources using
     * ClasspathResources
     *
     * @param className
     * @return class bytes
     */
    protected byte[] loadClassBytes(String className) {
        className = formatClassName( className );

        return classpathResources.getResource( className );
    }

    /**
     * @param className
     * @return String
     */
    protected String formatClassName(String className) {
        if( classNameReplacementChar == '\u0000' ) {
            // '/' is used to map the package to the path
            return className.replace( '.', '/' ) + ".class";
        } else {
            // Replace '.' with custom char, such as '_'
            return className.replace( '.', classNameReplacementChar ) + ".class";
        }
    }

    /**
     * Attempts to unload class, it only unloads the locally loaded classes by
     * JCL
     *
     * @param className
     */
//    public void unloadClass(String className) {
//        if( logger.isTraceEnabled() )
//            logger.trace( "Unloading class " + className );
//
//        if( classes.containsKey( className ) ) {
//            if( logger.isTraceEnabled() )
//                logger.trace( "Removing loaded class " + className );
//            classes.remove( className );
//            try {
//                classpathResources.unload( formatClassName( className ) );
//            } catch (ResourceNotFoundException e) {
//                throw new JclException( "Something is very wrong!!!"
//                        + "The locally loaded classes must be in synch with ClasspathResources", e );
//            }
//        } else {
//            try {
//                classpathResources.unload( formatClassName( className ) );
//            } catch (ResourceNotFoundException e) {
//                throw new JclException( "Class could not be unloaded "
//                        + "[Possible reason: Class belongs to the system]", e );
//            }
//        }
//    }

    /**
     * @param replacement
     */
    public void setClassNameReplacementChar(char replacement) {
        classNameReplacementChar = replacement;
    }

    /**
     * @return char
     */
    public char getClassNameReplacementChar() {
        return classNameReplacementChar;
    }

    /**
     * Local class loader
     *
     */
    public class LocalLoader extends Loader {

        public LocalLoader() {
            order = 1;
            enabled = Configuration.isLocalLoaderEnabled();
        }

        @Override
        public Class load(String className, boolean resolveIt) {
            Class result = null;
            byte[] classBytes;
            // if( logger.isTraceEnabled() )
            // logger.trace( "Loading class: " + className + ", " + resolveIt +
            // "" );

            result = classes.get( className );
            if( result != null ) {
                return result;
            }

            classBytes = loadClassBytes( className );
            if( classBytes == null ) {
                return null;
            }

            result = defineClass( className, classBytes, 0, classBytes.length );

            if( result == null ) {
                return null;
            }

            if( resolveIt )
                resolveClass( result );

            classes.put( className, result );
            return result;
        }

        @Override
        public InputStream loadResource(String name) {
            byte[] arr = classpathResources.getResource( name );
            if( arr != null ) {

                return new ByteArrayInputStream( arr );
            }

            return null;
        }
    }

    /**
     * @return Local JCL Loader
     */
    public Loader getLocalLoader() {
        return localLoader;
    }

    /**
     * Returns all JCL-loaded-classes as an immutable Map
     *
     * @return Map
     */
    public Map<String, Class> getLoadedClasses() {
        return Collections.unmodifiableMap( classes );
    }
}
