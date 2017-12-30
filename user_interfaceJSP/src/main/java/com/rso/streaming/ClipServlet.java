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

import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.logs.cdi.LogParams;
import com.rso.streaming.ententies.Album;
import com.rso.streaming.ententies.Clip;
import com.rso.streaming.logger.LogContextInterceptor;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@WebServlet("/clips")
@MultipartConfig
public class ClipServlet extends HttpServlet {

    @Inject
    private ClipRestClient clipRestClient;

    @Inject
    private AlbumRestClient albumRestClient;

    @Override
    @Log(LogParams.METRICS)
    @Timed(name = "GetClips")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Clip> clips = clipRestClient.getClips();

        if (clips == null || clips.isEmpty()) {
            response.getWriter().write("No clips found!<br/>");
        } else {
            for(Clip c : clips) {
                response.getWriter().write(c.toString() + "<audio controls>\n" +
                        "  <source src=\"http://localhost:8083/v1/clips/" + c.getID() + "\" type=\"audio/mpeg\">\n" +
                        "Your browser does not support the audio element.\n" +
                        "</audio>" +"<br/>");
            }
        }
    }

    @Override
    @Log(LogParams.METRICS)
    @Timed(name = "AddClip")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LocalDateTime in = LocalDateTime.parse(request.getParameter("clipDate"), DateTimeFormatter.ISO_DATE_TIME);
        Date d = Date.from(in.atZone(ZoneId.systemDefault()).toInstant());

        Album a = albumRestClient.getAlbum(Long.parseLong(request.getParameter("clipAlbumID")));

        Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
        InputStream fileContent = filePart.getInputStream();

        Clip c = new Clip(request.getParameter("clipAuthor"), request.getParameter("clipTitle"), d, a, Integer.parseInt(request.getParameter("trackNumber")));
        clipRestClient.addClip(c, fileContent);

        response.sendRedirect("home.jsp");
    }
}
