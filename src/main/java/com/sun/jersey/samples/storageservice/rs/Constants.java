package com.sun.jersey.samples.storageservice.rs;

/**
 * @author Diptamay Sanyal (diptamay@yahoo.com)
 */
public interface Constants {
    String VERSION = "version";
    String BINDING = "storage";
    String DEFAULT_VERSION_STRING = "_v1_0";
    String DEFAULT_BINDING_NAME = new StringBuilder(BINDING).append(DEFAULT_VERSION_STRING).toString();
}
