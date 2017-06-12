package com.epam.entity;

import com.epam.exception.JclException;
import com.epam.util.Configuration;

import java.io.*;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Created by Yayheniy_Lepkovich on 6/12/2017.
 */
public class JarResources {

    protected Map<String, byte[]> jarEntryContents;


    /**
     * @throws IOException
     */
    public JarResources() {
        jarEntryContents = new HashMap<String, byte[]>();
    }

    /**
     * @param name
     * @return byte[]
     */
    public byte[] getResource(String name) {
        return jarEntryContents.get( name );
    }

    /**
     * Returns an immutable Map of all jar resources
     *
     * @return Map
     */
    public Map<String, byte[]> getResources() {
        return Collections.unmodifiableMap( jarEntryContents );
    }

    /**
     * Reads the specified jar file
     *
     * @param jarFile
     * @throws IOException
     */
    public void loadJar(String jarFile) throws IOException {
        FileInputStream fis = new FileInputStream( jarFile );
        loadJar( fis );
        fis.close();
    }

    /**
     * Reads the jar file from a specified URL
     *
     * @param url
     * @throws IOException
     */
    public void loadJar(URL url) throws IOException {
        InputStream in = url.openStream();
        loadJar( in );
        in.close();
    }

    /**
     * Load the jar contents from InputStream
     *
     * @throws IOException
     */
    public void loadJar(InputStream jarStream) throws IOException {

        BufferedInputStream bis = null;
        JarInputStream jis = null;

        try {
            bis = new BufferedInputStream( jarStream );
            jis = new JarInputStream( bis );

            JarEntry jarEntry = null;
            while(( jarEntry = jis.getNextJarEntry() ) != null) {

                if( jarEntry.isDirectory() ) {
                    continue;
                }

                if( jarEntryContents.containsKey( jarEntry.getName() ) ) {
                    if( !Configuration.supressCollisionException() )
                        throw new JclException( "Class/Resource " + jarEntry.getName() + " already loaded" );
                    else {
                        continue;
                    }
                }


                byte[] b = new byte[2048];
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                int len = 0;
                while(( len = jis.read( b ) ) > 0) {
                    out.write( b, 0, len );
                }

                // add to internal resource HashMap
                jarEntryContents.put( jarEntry.getName(), out.toByteArray() );


                out.close();
            }
        } catch (NullPointerException e) {

        } finally {
            jis.close();
            bis.close();
        }
    }

    /**
     * For debugging
     *
     * @param je
     * @return String
     */
    private String dump(JarEntry je) {
        StringBuffer sb = new StringBuffer();
        if( je.isDirectory() ) {
            sb.append( "d " );
        } else {
            sb.append( "f " );
        }

        if( je.getMethod() == JarEntry.STORED ) {
            sb.append( "stored   " );
        } else {
            sb.append( "defalted " );
        }

        sb.append( je.getName() );
        sb.append( "\t" );
        sb.append( "" + je.getSize() );
        if( je.getMethod() == JarEntry.DEFLATED ) {
            sb.append( "/" + je.getCompressedSize() );
        }

        return ( sb.toString() );
    }
}
