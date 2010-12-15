package com.sun.jersey.samples.storageservice.rs.jibx;

import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * @author Diptamay Sanyal (diptamay@yahoo.com)
 */
@Provider
@Produces(MediaType.APPLICATION_XML)
public class JiBXBodyWriter extends JiBXBodyHandler implements MessageBodyWriter<Object> {

    @Context UriInfo uriInfo;

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, java.lang.annotation.Annotation[] annotations, MediaType mediaType) {
        try {
            getFactory(type);
        } catch (JiBXException e) {
            //e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public long getSize(Object o, Class<?> type, Type genericType, java.lang.annotation.Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Object o, Class<?> type, Type genericType, java.lang.annotation.Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        try {
            IBindingFactory factory = getFactory(uriInfo, type);
            IMarshallingContext context = factory.createMarshallingContext();
            context.setIndent(2);
            context.marshalDocument(o, "UTF-8", true, entityStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
