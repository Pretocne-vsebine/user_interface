<!--
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
-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Streaming service</title>
</head>
<body>

    <h2>Album list:</h2>
    <jsp:include page="${pageContext.request.contextPath}/albums"/>

    <h2>Clips list:</h2>
    <jsp:include page="${pageContext.request.contextPath}/clips"/>


    <h2>Add new</h2>
    <h3>album:</h3>
    <form action="${pageContext.request.contextPath}/albums" method="post">
        <label for="albumTitle">Title:
            <input type="text" id="albumTitle" name="albumTitle"/>
        </label>
        <label for="albumArtist">Artist:
            <input type="text" id="albumArtist" name="albumArtist"/>
        </label>
        <br/>
        <br/>
        <input type="submit" name="submit" value="Add"/>
        <br/>
    </form>

    <h3>clip:</h3>
    <form action="${pageContext.request.contextPath}/clips" enctype="multipart/form-data" method="post">
        <label for="clipAuthor">Author:
            <input type="text" id="clipAuthor" name="clipAuthor"/>
        </label>
        <label for="clipTitle">Title:
            <input type="text" id="clipTitle" name="clipTitle"/>
        </label>
        <label for="trackNumber">Track number:
            <input type="number" id="trackNumber" name="trackNumber"/>
        </label>
        <label for="clipDate">Release date:
            <input type="datetime-local" id="clipDate" name="clipDate"/>
        </label>
        <label for="clipAlbumID">Album ID:
            <input type="number" id="clipAlbumID" name="clipAlbumID"/>
        </label>
        <label for="fileID">Upload mp3 file:
            <input type="file" id="fileID" name="file" />
        </label>
        <br/>
        <br/>
        <input type="submit" id="submit" name="submit" value="Add"/>
        <br/>
    </form>

    <a href="index.jsp">logout</a>|
</body>
</html>