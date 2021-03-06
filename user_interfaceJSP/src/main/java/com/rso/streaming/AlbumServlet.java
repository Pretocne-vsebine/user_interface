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

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.logs.cdi.LogParams;
import com.rso.streaming.ententies.*;
import com.rso.streaming.logger.LogContextInterceptor;
import org.eclipse.microprofile.metrics.annotation.Timed;

import java.util.List;

@WebServlet("/albums")
public class AlbumServlet extends HttpServlet {

    @Inject
    private AlbumRestClient albumRestClient;

    @Override
    @Log(LogParams.METRICS)
    @Timed(name = "GetAlbums")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Album> albums = albumRestClient.getAlbums();

        if (albums == null || albums.isEmpty()) {
            response.getWriter().write("No albums found!<br/>");
        } else {
            for(Album a : albums) {
                response.getWriter().write(a.toString() + "<br/>");
            }
        }
    }

    @Override
    @Log(LogParams.METRICS)
    @Timed(name = "AddAlbum")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Album a = new Album(request.getParameter("albumTitle"), request.getParameter("albumArtist"));

        albumRestClient.addAlbum(a);
        response.sendRedirect("home.jsp");
    }
}
