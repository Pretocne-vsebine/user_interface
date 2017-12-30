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
import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.discovery.enums.AccessType;
import com.kumuluz.ee.fault.tolerance.annotations.GroupKey;
import com.rso.streaming.ententies.Clip;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import java.io.IOException;
import java.io.InputStream;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@CircuitBreaker
@Timeout(value = 1, unit = ChronoUnit.SECONDS)
@GroupKey("clips")
@RequestScoped
public class ClipRestClient {

    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    @Inject
    @DiscoverService(value = "clip_management", version = "1.0.x", environment = "dev", accessType = AccessType.GATEWAY)
    private String urlString;

    @Inject
    @DiscoverService(value = "streaming_service", version = "1.0.x", environment = "dev", accessType = AccessType.GATEWAY)
    private String urlStringStreaming;

    @PostConstruct
    private void init() {
        httpClient = HttpClientBuilder.create().build();
        objectMapper = new ObjectMapper();
    }

    @Fallback(fallbackMethod = "getClipsFallback")
    public List<Clip> getClips() {
        try {
            HttpGet request = new HttpGet(urlString + "/v1/clips");
            HttpResponse response = httpClient.execute(request);

            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();

                if (entity != null)
                    return getObjects(EntityUtils.toString(entity));
            } else if (status == 404) { }
            else {
                String msg = "Remote server '" + urlString + "/v1/clips" + "' is responded with status " + status + ".";
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

    public List<Clip> getClipsFallback() {
        return null;
    }

    private List<Clip> getObjects(String json) throws IOException {
        return json == null ? new ArrayList<>() : objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Clip.class));
    }

    @Fallback(fallbackMethod = "addClipFallback")
    @Timeout(value = 300, unit = ChronoUnit.SECONDS) //File upload might take a while
    public void addClip(Clip a, InputStream file) {
        try {
            HttpPost request = new HttpPost(urlString + "/v1/clips");

            String json = objectMapper.writeValueAsString(a);
            StringEntity entity = new StringEntity(json);

            request.setEntity(entity);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            HttpResponse response = httpClient.execute(request);

            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {
                HttpEntity entityc = response.getEntity();

                if (entityc != null) {
                    Clip c = objectMapper.readValue(EntityUtils.toString(entityc), objectMapper.getTypeFactory().constructType(Clip.class));

                    this.saveclip(c.getID(), file);
                }
            }
            else {
                String msg = "Remote server '" + urlString + "/v1/clips" + "' is responded with status " + status + ".";
                //log.error(msg);
                throw new InternalServerErrorException(msg);
            }
        } catch (IOException e) {
            String msg = e.getClass().getName() + " occured: " + e.getMessage();
            //log.error(msg);
            throw new InternalServerErrorException(msg);
        }
    }

    private void saveclip(long id, InputStream file) throws ClientProtocolException, IOException {
        HttpPost httpPost = new HttpPost(urlStringStreaming + "/v1/clips/" + id);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", file,
                ContentType.APPLICATION_OCTET_STREAM, "file.ext");
        HttpEntity multipart = builder.build();

        httpPost.setEntity(multipart);
        httpPost.setHeader("Content-type", "application/octet-stream");

        HttpResponse response = httpClient.execute(httpPost);
        int status = response.getStatusLine().getStatusCode();

        file.close();

        if (status >= 200 && status < 300) {
            return;
        }
        else {
            String msg = "Remote server '" + urlStringStreaming + "/v1/clips" + "' is responded with status " + status + ".";
            //log.error(msg);
            throw new InternalServerErrorException(msg);
        }
    }

    public void addClipFallback(Clip a) {    }

    public String generateStreamingString(Clip c) {
        return c.toString() + "<audio controls>\n" +
                "  <source src=\"" + urlStringStreaming + "/v1/clips/" + c.getID() + "\" type=\"audio/mpeg\">\n" +
                "Your browser does not support the audio element.\n" +
                "</audio>" +"<br/>";
    }
}
