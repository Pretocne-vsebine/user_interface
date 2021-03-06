/*
 *  Copyright (c) 2014-2017 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
*/
package com.rso.streaming;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.runtime.EeRuntime;
import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.discovery.enums.AccessType;
import com.kumuluz.ee.fault.tolerance.annotations.GroupKey;
import com.rso.streaming.ententies.Album;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.ThreadContext;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@CircuitBreaker
@Timeout(value = 1, unit = ChronoUnit.SECONDS)
@GroupKey("clips")
@RequestScoped
public class AlbumRestClient {

    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    @Inject
    @DiscoverService(value = "clip_management", version = "1.0.x", environment = "dev", accessType = AccessType.GATEWAY)
    private String urlString;

    /*@Inject
    private RestConfig restConfig;*/

    @PostConstruct
    private void init() {
        httpClient = HttpClientBuilder.create().build();
        objectMapper = new ObjectMapper();
    }

    @Fallback(fallbackMethod = "getAlbumsFallback")
    public List<Album> getAlbums() {
        try {
            HttpGet request = new HttpGet(urlString + "/v1/albums");
            HttpResponse response = httpClient.execute(request);

            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();

                if (entity != null)
                    return getObjects(EntityUtils.toString(entity));
            } else if (status == 404) { }
            else {
                String msg = "Remote server '" + urlString + "/v1/albums" + "' is responded with status " + status + ".";
                //log.error(msg);
                throw new InternalServerErrorException(msg);
            }

        } catch (IOException e) {
            String msg = e.getClass().getName() + " occured: " + e.getMessage();
            //log.error(msg);
            throw new InternalServerErrorException(msg);
        }
        return new ArrayList<>();
    }

    public List<Album> getAlbumsFallback() {
        return null;
    }

    private List<Album> getObjects(String json) throws IOException {
        return json == null ? new ArrayList<>() : objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Album.class));
    }

    @Fallback(fallbackMethod = "getAlbumFallback")
    public Album getAlbum(Long ID) {
        try {
            HttpGet request = new HttpGet(urlString + "/v1/albums/" + ID);
            HttpResponse response = httpClient.execute(request);

            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();

                if (entity != null)
                    return  objectMapper.readValue(EntityUtils.toString(entity), objectMapper.getTypeFactory().constructType(Album.class));
            } else {
                String msg = "Remote server '" + urlString + "/v1/albums" + "' is responded with status " + status + ".";
                //log.error(msg);
                throw new InternalServerErrorException(msg);
            }
        } catch (IOException e) {
            String msg = e.getClass().getName() + " occured: " + e.getMessage();
            //log.error(msg);
            throw new InternalServerErrorException(msg);
        }
        return null;
    }

    public Album getAlbumFallback(Long ID) {
        return null;
    }

    @Fallback(fallbackMethod = "addAlbumFallback")
    public void addAlbum(Album a) {
        try {
            HttpPost request = new HttpPost(urlString + "/v1/albums");

            String json = objectMapper.writeValueAsString(a);
            StringEntity entity = new StringEntity(json);

            request.setEntity(entity);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            HttpResponse response = httpClient.execute(request);

            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {
                return;
            }
            else {
                String msg = "Remote server '" + urlString + "/v1/albums" + "' is responded with status " + status + ".";
                //log.error(msg);
                throw new InternalServerErrorException(msg);
            }
        } catch (IOException e) {
            String msg = e.getClass().getName() + " occured: " + e.getMessage();
            //log.error(msg);
            throw new InternalServerErrorException(msg);
        }
    }

    public void addAlbumFallback(Album a) {

    }
}
