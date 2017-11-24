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
import com.rso.streaming.ententies.Album;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AlbumRestClient {

    //private Client client = ClientBuilder.newClient();

    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    @Inject
    private RestConfig restConfig;

    @PostConstruct
    private void init() {
        httpClient = HttpClientBuilder.create().build();
        objectMapper = new ObjectMapper();
    }

    public List<Album> getAlbums() {
        try {
            HttpGet request = new HttpGet(restConfig.getClipUrl() + "/albums");
            HttpResponse response = httpClient.execute(request);

            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();

                if (entity != null)
                    return getObjects(EntityUtils.toString(entity));
            } else {
                String msg = "Remote server '" ;//+ basePath.get() + "' is responded with status " + status + ".";
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

    private List<Album> getObjects(String json) throws IOException {
        return json == null ? new ArrayList<>() : objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Album.class));
    }

   /* public List<Album> getAlbums() {
        return client
                .target(restConfig.getClipUrl() + "/albums")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Album>>(){});

    }*/

    public void addAlbum(Album a) {
        /*client
        .target(restConfig.getClipUrl() + "/albums")
        .request(MediaType.APPLICATION_JSON)
        .post(Entity.entity(a, MediaType.APPLICATION_JSON));*/
    }


}
