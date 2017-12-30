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
import com.rso.streaming.ententies.User;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/users")
public class UserServlet extends HttpServlet {

    @Inject
    private UserRestClient userRestClient;

    @Override
    @Log(LogParams.METRICS)
    @Timed(name = "UserLogin")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User u = new User();
        u.setName(request.getParameter("name"));
        u.setPassword(request.getParameter("password"));
        u = userRestClient.getUser(u);

        if (u == null) {
            response.sendRedirect("index.jsp");
        } else {
            response.sendRedirect("home.jsp");
        }
    }

    @Override
    @Log(LogParams.METRICS)
    @Timed(name = "AddUser")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User u = new User(request.getParameter("name"), request.getParameter("password"));

        userRestClient.addUser(u);
        response.sendRedirect("index.jsp");
    }
}
