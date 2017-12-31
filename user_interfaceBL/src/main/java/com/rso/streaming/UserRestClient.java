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
import com.rso.streaming.ententies.Album;
import com.rso.streaming.ententies.User;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@CircuitBreaker
@Timeout(value = 1, unit = ChronoUnit.SECONDS)
@GroupKey("user")
@RequestScoped
public class UserRestClient {

    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    @Inject
    @DiscoverService(value = "user_management", version = "1.0.x", environment = "dev", accessType = AccessType.GATEWAY)
    private String urlString;

    @PostConstruct
    private void init() {
        httpClient = HttpClientBuilder.create().build();
        objectMapper = new ObjectMapper();
    }

    @Fallback(fallbackMethod = "getUsersFallback")
    public User getUser(User u) {
        try {
            HttpGet request = new HttpGet(urlString + "/v1/users/" + u.getName() + "/" + u.getPassword());
            HttpResponse response = httpClient.execute(request);

            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {
                return u;
            } else if (status == 404) {
                return null;
            } else {
                String msg = "Remote server '" + urlString + "/v1/users" + "' is responded with status " + status + ".";
                //log.error(msg);
                throw new InternalServerErrorException(msg);
            }
        } catch (IOException e) {
            String msg = e.getClass().getName() + " occured: " + e.getMessage();
            //log.error(msg);
            throw new InternalServerErrorException(msg);
        }
    }

    public User getUsersFallback(User u) {
        return null;
    }

    @Fallback(fallbackMethod = "addUserFallback")
    public void addUser(User u) {
        try {
            HttpPost request = new HttpPost(urlString + "/v1/users");

            String json = objectMapper.writeValueAsString(u);
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
                String msg = "Remote server '" + urlString + "/v1/users" + "' is responded with status " + status + ".";
                //log.error(msg);
                throw new InternalServerErrorException(msg);
            }
        } catch (IOException e) {
            String msg = e.getClass().getName() + " occured: " + e.getMessage();
            //log.error(msg);
            throw new InternalServerErrorException(msg);
        }
    }

    public void addUserFallback(User u) {

    }
}
