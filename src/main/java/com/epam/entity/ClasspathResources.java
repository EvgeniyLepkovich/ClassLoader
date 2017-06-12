package com.epam.entity;

import com.epam.exception.JclException;
import com.epam.exception.ResourceNotFoundException;
import com.epam.util.Configuration;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by Yayheniy_Lepkovich on 6/12/2017.
 */
public class ClasspathResources extends JarResources {
    /**
     * Reads the resource content
     *
     * @param resource
     * @throws IOException
     */
    private void loadResourceContent(String resource) throws IOException {
        File resourceFile = new File( resource );

        FileInputStream fis = new FileInputStream( resourceFile );

        byte[] content = new byte[(int) resourceFile.length()];
        fis.read( content );

        if( jarEntryContents.containsKey( resourceFile.getName() ) ) {
            if( !Configuration.supressCollisionException() )
                throw new JclException( "Resource " + resourceFile.getName() + " already loaded" );
            else {
                return;
            }
        }

        fis.close();

        jarEntryContents.put( resourceFile.getName(), content );
    }

    /**
     * Attempts to load a remote resource (jars, properties files, etc)
     *
     * @param url
     * @throws IOException
     */
    private void loadRemoteResource(URL url) throws IOException {

        if( url.toString().toLowerCase().endsWith( ".jar" ) ) {
            loadJar( url );
            return;
        }

        InputStream stream = url.openStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int byt;
        while(( ( byt = stream.read() ) != -1 )) {
            out.write( byt );
        }

        byte[] content = out.toByteArray();

        if( jarEntryContents.containsKey( url.toString() ) ) {
            if( !Configuration.supressCollisionException() )
                throw new JclException( "Resource " + url.toString() + " already loaded" );
            else {
                return;
            }
        }

        jarEntryContents.put( url.toString(), content );

        out.close();
        stream.close();
    }

    /**
     * Reads the class content
     *
     * @param clazz
     * @param pack
     * @throws IOException
     */
    private void loadClassContent(String clazz, String pack) throws IOException {
        File cf = new File( clazz );
        FileInputStream fis = new FileInputStream( cf );

        byte[] content = new byte[(int) cf.length()];
        fis.read( content );

        String entryName = pack + "/" + cf.getName();

        if( jarEntryContents.containsKey( entryName ) ) {
            if( !Configuration.supressCollisionException() )
                throw new JclException( "Class " + entryName + " already loaded" );
            else {
                return;
            }
        }

        fis.close();

        jarEntryContents.put( entryName, content );
    }

    /**
     * Reads local and remote resources
     *
     * @param url
     * @throws IOException
     */
    public void loadResource(URL url) throws IOException {
        try {
            // Is Local
            loadResource( new File( url.toURI() ), "" );
        } catch (IllegalArgumentException iae) {
            // Is Remote
            loadRemoteResource( url );
        } catch (URISyntaxException e) {
            throw new JclException( "URISyntaxException", e );
        }
    }

    /**
     * Reads local resources from - Jar files - Class folders - Jar Library
     * folders
     *
     * @param path
     * @throws IOException
     */
    public void loadResource(String path) throws IOException {
        loadResource( new File( path ), "" );
    }

    /**
     * Reads local resources from - Jar files - Class folders - Jar Library
     * folders
     *
     * @param fol
     * @param packName
     * @throws IOException
     */
    private void loadResource(File fol, String packName) throws IOException {
        if( fol.isFile() ) {
            if( fol.getName().toLowerCase().endsWith( ".class" ) ) {
                loadClassContent( fol.getAbsolutePath(), packName );
            } else {
                if( fol.getName().toLowerCase().endsWith( ".jar" ) ) {
                    loadJar( fol.getAbsolutePath() );
                } else {
                    loadResourceContent( fol.getAbsolutePath() );
                }
            }

            return;
        }

        if( fol.list() != null ) {
            for( String f : fol.list() ) {
                File fl = new File( fol.getAbsolutePath() + "/" + f );

                String pn = packName;

                if( fl.isDirectory() ) {

                    if( !pn.equals( "" ) )
                        pn = pn + "/";

                    pn = pn + fl.getName();
                }

                loadResource( fl, pn );
            }
        }
    }

    /**
     * Removes the loaded resource
     *
     * @param resource
     * @throws ResourceNotFoundException
     */
    public void unload(String resource) throws ResourceNotFoundException {
        if( jarEntryContents.containsKey( resource ) ) {
            jarEntryContents.remove( resource );
        } else {
            throw new ResourceNotFoundException( resource, "Resource not found in local ClasspathResources" );
        }
    }
}
