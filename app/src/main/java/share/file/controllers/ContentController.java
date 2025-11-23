package share.file.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ContentController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        switch (path) {
            case "/auth/register":
                path = "/pages/auth/register.html";
                break;
            case "/auth/login":
                path = "/pages/auth/login.html";
                break;
            case "/myFiles":
                path = "/pages/myFiles.html";
                break;
        }

        try {
            InputStream is = getClass().getResourceAsStream("/static" + path);

            if (is == null) {
                response.setStatus(404);

                return;
            }

            String contentType = this.getContentType(path);

            response.setContentType(contentType);

            response.setStatus(200);

            OutputStream os = response.getOutputStream();

            byte[] buffer = new byte[1024];

            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            os.close();
            is.close();
        } catch (Exception e) {
            response.setStatus(500);
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=UTF-8";
        if (path.endsWith(".css")) return "text/css; charset=UTF-8";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        return "text/plain; charset=UTF-8";
    }
}
