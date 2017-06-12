package com.epam.util;

import com.epam.exception.JclException;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by Yayheniy_Lepkovich on 6/12/2017.
 */
@SuppressWarnings("unchecked")
public class JclUtil {

    /**
     * Casts the object ref to the passed interface class ref. It actually
     * returns a dynamic proxy for the passed object
     *
     * @param object
     * @param classes
     * @return castable
     * @return casted
     */
    public static Object toCastable(Object object, Class[] classes) {
        JclProxyHandler handler = new JclProxyHandler( object );
        return Proxy.newProxyInstance( JclUtil.class.getClassLoader(), classes, handler );
    }

    /**
     * Casts the object ref to the passed interface class ref
     *
     * @param object
     * @param clazz
     * @return castable
     * @return casted
     */
    public static Object toCastable(Object object, Class clazz) {
        return toCastable( object, new Class[] { clazz } );
    }

    /**
     * Casts the object ref to the passed interface class ref and returns it
     *
     * @param object
     * @param clazz
     * @return T reference
     * @return casted
     */
    public static <T> T cast(Object object, Class<T> clazz) {
        return (T) toCastable( object, clazz );
    }

    /**
     * Deep clones the Serializable objects in the current classloader
     *
     * @param original
     * @return clone
     */
    public static Object clone(Object original) {
        Object clone = null;
        try {
            // Increased buffer size to speed up writing
            ByteArrayOutputStream bos = new ByteArrayOutputStream( 5120 );
            ObjectOutputStream out = new ObjectOutputStream( bos );
            out.writeObject( original );
            out.flush();
            out.close();

            ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( bos.toByteArray() ) );
            clone = in.readObject();

            in.close();
            bos.close();

            return clone;
        } catch (IOException e) {
            throw new JclException( e );
        } catch (ClassNotFoundException cnfe) {
            throw new JclException( cnfe );
        }
    }

    /**
     * proxy method invocation handler
     *
     */
    private static class JclProxyHandler implements InvocationHandler {
        private final Object delegate;

        public JclProxyHandler(Object delegate) {
            this.delegate = delegate;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
         *      java.lang.reflect.Method, java.lang.Object[])
         */
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Method delegateMethod = delegate.getClass().getMethod( method.getName(), method.getParameterTypes() );
            return delegateMethod.invoke( delegate, args );
        }
    }

}
