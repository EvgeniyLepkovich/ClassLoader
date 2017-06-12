package com.epam.util;

import com.epam.classloader.AbstractClassLoader;
import com.epam.classloader.JarClassLoader;

/**
 * Created by Yayheniy_Lepkovich on 6/12/2017.
 */
public class Configuration {

    private static final String JCL_SUPPRESS_COLLISION_EXCEPTION = "jcl.suppressCollisionException";

    public static boolean supressCollisionException() {
        if( System.getProperty( JCL_SUPPRESS_COLLISION_EXCEPTION ) == null )
            return true;

        return Boolean.parseBoolean( System.getProperty( JCL_SUPPRESS_COLLISION_EXCEPTION ) );
    }

    /**
     * Check to see if Loader is enabled/disabled The Loader can be
     * enabled/disabled programmatically or by passing the class names as
     * argument e.g. <br>
     * -Dxeus.jcl.AbstractClassLoader.ParentLoader=false
     *
     * @param clazz
     * @return
     */
    public static boolean isLoaderEnabled(Class clazz) {
        if( System.getProperty( clazz.getName() ) == null )
            return true;

        return Boolean.parseBoolean( System.getProperty( clazz.getName() ) );
    }

    public static boolean isLocalLoaderEnabled() {
        return isLoaderEnabled( JarClassLoader.LocalLoader.class );
    }

    public static boolean isCurrentLoaderEnabled() {
        return isLoaderEnabled( AbstractClassLoader.CurrentLoader.class );
    }

    public static boolean isParentLoaderEnabled() {
        return isLoaderEnabled( AbstractClassLoader.ParentLoader.class );
    }

    public static boolean isSystemLoaderEnabled() {
        return isLoaderEnabled( AbstractClassLoader.SystemLoader.class );
    }
}
