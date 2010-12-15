/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.jersey.samples.storageservice.rs.resources;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.samples.storageservice.MemoryStore;
import com.sun.jersey.samples.storageservice.types.Container;
import com.sun.jersey.samples.storageservice.types.Item;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Iterator;

/**
 * @author Paul.Sandoz@Sun.Com
 * @author Diptamay Sanyal (diptamay@yahoo.com)
 */
@Produces(MediaType.APPLICATION_XML)
public class ContainerResource {
    @Context UriInfo uriInfo;
    @Context Request request;
    String container;

    ContainerResource(UriInfo uriInfo, Request request, String container) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.container = container;
    }

    @GET
    public Response getContainer(@QueryParam("search") String search) {
        System.out.println("GET CONTAINER " + container + ", search = " + search);

        Container c = MemoryStore.MS.getContainer(container);
        if (c == null)
            throw new NotFoundException("Container not found");


        if (search != null) {
            c = c.clone();
            Iterator<Item> i = c.getItem().iterator();
            byte[] searchBytes = search.getBytes();
            while (i.hasNext()) {
                if (!match(searchBytes, container, i.next().getName()))
                    i.remove();
            }
        }

        //return Response.ok(c,MediaType.APPLICATION_XML).build();
        return Response.ok(c).build();
    }

    @PUT
    public Response putContainer() {
        System.out.println("PUT CONTAINER " + container);

        URI uri = uriInfo.getAbsolutePath();
        Container c = new Container(container, uri.toString());

        Response r;
        if (!MemoryStore.MS.hasContainer(c)) {
            r = Response.created(uri).build();
        } else {
            r = Response.noContent().build();
        }

        MemoryStore.MS.createContainer(c);
        return r;
    }

    @DELETE
    public void deleteContainer() {
        System.out.println("DELETE CONTAINER " + container);

        Container c = MemoryStore.MS.deleteContainer(container);
        if (c == null)
            throw new NotFoundException("Container not found");
    }


    @Path("{item: .+}")
    public ItemResource getItemResource(@PathParam("item") String item) {
        return new ItemResource(uriInfo, request, container, item);
    }

    private boolean match(byte[] search, String container, String item) {
        byte[] b = MemoryStore.MS.getItemData(container, item);

        OUTER:
        for (int i = 0; i < b.length - search.length + 1; i++) {
            for (int j = 0; j < search.length; j++) {
                if (b[i + j] != search[j])
                    continue OUTER;
            }

            return true;
        }

        return false;
    }
}
