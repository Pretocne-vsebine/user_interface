package com.rso.streaming.health;

import com.rso.streaming.AlbumRestClient;
import com.rso.streaming.ententies.Album;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import java.util.List;

@Health
@ApplicationScoped
public class WebHealthCheck implements HealthCheck {

    @Inject
    private AlbumRestClient albumRestClient;

    @Override
    public HealthCheckResponse call() {
        try {
            List<Album> albums = albumRestClient.getAlbums();
            return HealthCheckResponse.named(WebHealthCheck.class.getSimpleName()).up().build();
        } catch (InternalServerErrorException e){
            return HealthCheckResponse.named(WebHealthCheck.class.getSimpleName()).down().build();
        }
    }
}
