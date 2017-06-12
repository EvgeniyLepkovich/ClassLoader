package com.epam.classloader;

import com.epam.classloader.loader.Loader;
import com.epam.exception.ResourceNotFoundException;
import com.epam.util.Configuration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Yayheniy_Lepkovich on 6/12/2017.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractClassLoader extends ClassLoader {

    protected final List<Loader> loaders = new ArrayList<Loader>();

    private final Loader systemLoader = new SystemLoader();
    private final Loader parentLoader = new ParentLoader();
    private final Loader currentLoader = new CurrentLoader();

    /**
     * No arguments constructor
     */
    public AbstractClassLoader() {
        loaders.add( systemLoader );
        loaders.add( parentLoader );
        loaders.add( currentLoader );
    }

    public void addLoader(Loader loader) {
        loaders.add( loader );
    }

    /**
     * Override loadClass
     *
     * @see java.lang.ClassLoader#loadClass(java.lang.String)
     */
    @Override
    public Class loadClass(String className) throws ClassNotFoundException {
        return ( loadClass( className, true ) );
    }

    /**
     * Overrides the loadClass method to load classes from other resources,
     * JarClassLoader is the only subclass in this project that loads classes
     * from jar files
     *
     * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
     */
    @Override
    public Class loadClass(String className, boolean resolveIt) throws ClassNotFoundException {
        Collections.sort( loaders );
        Class clazz = null;
        for( Loader l : loaders ) {
            if( l.isEnabled() ) {
                clazz = l.load( className, resolveIt );
                if( clazz != null )
                    break;
            }
        }

        if( clazz == null )
            throw new ClassNotFoundException( className );

        return clazz;
    }

    /**
     * Overrides the getResourceAsStream method to load non-class resources from
     * other sources, JarClassLoader is the only subclass in this project that
     * loads non-class resources from jar files
     *
     * @see java.lang.ClassLoader#getResourceAsStream(java.lang.String)
     */
    @Override
    public InputStream getResourceAsStream(String name) {
        Collections.sort( loaders );
        InputStream is = null;
        for( Loader l : loaders ) {
            if( l.isEnabled() ) {
                is = l.loadResource( name );
                if( is != null )
                    break;
            }
        }

        if( is == null )
            throw new ResourceNotFoundException( "Resource " + name + " not found." );

        return is;

    }

    /**
     * System class loader
     *
     */
    public class SystemLoader extends Loader {

        public SystemLoader() {
            order = 4;
            enabled = Configuration.isSystemLoaderEnabled();
        }

        @Override
        public Class load(String className, boolean resolveIt) {
            Class result;
            try {
                result = findSystemClass( className );
            } catch (ClassNotFoundException e) {
                return null;
            }


            return result;
        }

        @Override
        public InputStream loadResource(String name) {
            InputStream is = getSystemResourceAsStream( name );

            if( is != null ) {

                return is;
            }

            return null;
        }
    }

    /**
     * Parent class loader
     *
     */
    public class ParentLoader extends Loader {

        public ParentLoader() {
            order = 3;
            enabled = Configuration.isParentLoaderEnabled();
        }

        @Override
        public Class load(String className, boolean resolveIt) {
            Class result;
            try {
                result = getParent().loadClass( className );
            } catch (ClassNotFoundException e) {
                return null;
            }


            return result;
        }

        @Override
        public InputStream loadResource(String name) {
            InputStream is = getParent().getResourceAsStream( name );

            if( is != null ) {

                return is;
            }
            return null;
        }

    }

    /**
     * Current class loader
     *
     */
    public class CurrentLoader extends Loader {

        public CurrentLoader() {
            order = 2;
            enabled = Configuration.isCurrentLoaderEnabled();
        }

        @Override
        public Class load(String className, boolean resolveIt) {
            Class result;
            try {
                result = getClass().getClassLoader().loadClass( className );
            } catch (ClassNotFoundException e) {
                return null;
            }


            return result;
        }

        @Override
        public InputStream loadResource(String name) {
            InputStream is = getClass().getClassLoader().getResourceAsStream( name );

            if( is != null ) {

                return is;
            }

            return null;
        }

    }

    public Loader getSystemLoader() {
        return systemLoader;
    }

    public Loader getParentLoader() {
        return parentLoader;
    }

    public Loader getCurrentLoader() {
        return currentLoader;
    }
}
