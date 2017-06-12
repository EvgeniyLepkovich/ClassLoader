package com.epam.exception;

import com.epam.entity.ResourceType;

/**
 * Created by Yayheniy_Lepkovich on 6/12/2017.
 */
public class ResourceNotFoundException extends JclException {
    /**
     * Default serial id
     */
    private static final long serialVersionUID = 1L;

    private String resourceName;
    private ResourceType resourceType;

    /**
     * Default constructor
     */
    public ResourceNotFoundException() {
        super();
    }

    /**
     * @param message
     */
    public ResourceNotFoundException(String message) {
        super( message );
    }

    /**
     * @param resource
     * @param message
     */
    public ResourceNotFoundException(String resource, String message) {
        super( message );
        resourceName = resource;
        determineResourceType( resource );
    }

    /**
     * @param e
     * @param resource
     * @param message
     */
    public ResourceNotFoundException(Throwable e, String resource, String message) {
        super( message, e );
        resourceName = resource;
        determineResourceType( resource );
    }

    /**
     * @param resourceName
     */
    private void determineResourceType(String resourceName) {
        if( resourceName.toLowerCase().endsWith( ".class" ) )
            resourceType = ResourceType.CLASS;
        else if( resourceName.toLowerCase().endsWith( ".properties" ) )
            resourceType = ResourceType.PROPERTIES;
        else if( resourceName.toLowerCase().endsWith( ".xml" ) )
            resourceType = ResourceType.XML;
        else
            resourceType = ResourceType.UNKNOWN;
    }

    /**
     * @return {@link ResourceType}
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * @param resourceName
     */
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * @return {@link ResourceType}
     */
    public ResourceType getResourceType() {
        return resourceType;
    }

    /**
     * @param resourceType
     */
    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }
}
