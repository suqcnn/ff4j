package org.ff4j.web.it;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;

import org.ff4j.core.Feature;
import org.ff4j.utils.FeatureJsonMarshaller;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

/*
 * #%L
 * ff4j-web
 * %%
 * Copyright (C) 2013 - 2014 Ff4J
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * Integration Test within GRIZZLY.
 * 
 * @author <a href="mailto:cedrick.lunven@gmail.com">Cedrick LUNVEN</a>
 */
public class FeatureStoreWebResourceTestIT extends AbstractWebResourceTestIT {
    
    /**
     * TDD, read the feature as a webresource => String JSON
     */
    @Test
    public void testRead() {
        // Given
        assertFF4J.assertThatFeatureExist(F4);
        // When
        WebResource wResf4 = resource().path(APIPATH).path("features").path(F4);
        ClientResponse resHttp = wResf4.get(ClientResponse.class);
        String resEntity = resHttp.getEntity(String.class);
        // Then, HTTPResponse
        Assert.assertEquals("Expected status is 200", Status.OK.getStatusCode(), resHttp.getStatus());
        Assert.assertNotNull(resEntity);
        // Then, Entity Object
        Feature f = FeatureJsonMarshaller.unMarshallFeature(resEntity);
        Assert.assertEquals(ff4j.getFeature(F4).toString(), f.toString());
    }
    
    /**
     * TDD.
     */
    @Test
    public void testReadNotFound() {
        // Given
        assertFF4J.assertThatFeatureDoesNotExist(F_DOESNOTEXIST);
        // When
        WebResource wResf4 = resource().path(APIPATH).path("features").path(F_DOESNOTEXIST);
        ClientResponse resHttp = wResf4.get(ClientResponse.class);
        String resEntity = resHttp.getEntity(String.class);
        // Then, HTTP Response
        Assert.assertEquals("Expected status is 404", Status.NOT_FOUND.getStatusCode(), resHttp.getStatus());
        // Then, Entity (erro message)
        Assert.assertNotNull(resEntity);
        Assert.assertTrue("Invalid error message : " + resEntity, resEntity.contains("not exist"));
    }

    /**
     * TDD.
     */
    @Test
    @Ignore
    public void upsertIfNotExistCreateIt() {
        // Given
        assertFF4J.assertThatFeatureDoesNotExist(FEATURE_NEW);
        // When
        WebResource webResFeat = resource().path(APIPATH).path("features").path(FEATURE_NEW);
        ClientResponse res = webResFeat.put(ClientResponse.class, new Feature(FEATURE_NEW).toString().getBytes());
        // Then
        assertFF4J.assertThatFeatureExist(FEATURE_NEW);
        Assert.assertEquals(Status.CREATED.getStatusCode(), res.getStatus());
    }

    /**
     * TDD.
     */
    @Test
    public void testReadAll_IT() throws Exception {
        // Given
        assertFF4J.assertThatStoreHasSize(5);
       final Feature f1 = ff4j.getFeature(F1);
        /*
         * ACCESS ON FF4J WebResource wRes = resource().path(APIPATH); ClientResponse httpRes = wRes.get(ClientResponse.class);
         * System.out.println(String.valueOf(httpRes.getEntity(String.class)));
         * 
         * // ACCESS ON STORE WebResource wRes2 = wRes.path("features"); ClientResponse httpRes2 =
         * wRes2.get(ClientResponse.class); System.out.println(String.valueOf(httpRes2.getEntity(String.class)));
         */

        final ResourceConfig rc = new PackagesResourceConfig("org.ff4j");
        final Map<String, Object> config = new HashMap<String, Object>();
        config.put("com.sun.jersey.api.json.POJOMappingFeature", true);
        rc.setPropertiesAndFeatures(config);

        // GET FEATURE
        WebResource wRes3 = resource().path(APIPATH).path("features").path("first");
        ClientResponse httpRes3 = wRes3.get(ClientResponse.class);
        System.out.println(String.valueOf(httpRes3.getEntity(String.class)));

        WebResource wRes0 = resource().path(APIPATH).path("features").path("tralala").path("enable");
        ClientResponse httpRes0 = wRes0.post(ClientResponse.class);
        // System.out.println(String.valueOf(httpRes0.getEntity(String.class)));
        System.out.println(String.valueOf(httpRes0.getStatus()));

        // DELETE FEATURE
        ClientResponse httpRes4 = wRes3.delete(ClientResponse.class);
        System.out.println(String.valueOf(httpRes4.getStatus()));

        // ANOTHER 404
        ClientResponse httpRes5 = wRes3.get(ClientResponse.class);
        System.out.println(String.valueOf(httpRes5.getStatus()));

        // PUT
        wRes3.put(f1.toString().getBytes());


    }

}
