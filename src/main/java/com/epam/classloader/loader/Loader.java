package com.epam.classloader.loader;

import java.io.InputStream;

/**
 * Created by Yayheniy_Lepkovich on 6/12/2017.
 */
public abstract class Loader implements Comparable<Loader> {
    // Default order
    protected int order = 5;
    // Enabled by default
    protected boolean enabled = true;

    public int getOrder() {
        return order;
    }

    /**
     * Set loading order
     *
     * @param order
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Loads the class
     *
     * @param className
     * @param resolveIt
     * @return class
     */
    public abstract Class load(String className, boolean resolveIt);

    /**
     * Loads the resource
     *
     * @param name
     * @return java.io.InputStream
     */
    public abstract InputStream loadResource(String name);

    /**
     * Checks if Loader is Enabled/Disabled
     *
     * @return boolean
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Enable/Disable Laoder
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Loader o) {
        return order - o.getOrder();
    }
}
