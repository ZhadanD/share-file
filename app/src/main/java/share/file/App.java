package share.file;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;

import jakarta.servlet.http.HttpServlet;
import share.file.controllers.AuthController;
import share.file.controllers.ContentController;
import share.file.controllers.DownloadFileController;
import share.file.controllers.FileController;
import share.file.controllers.RegisterController;

public class App {
    private static Map<String, HttpServlet> servlets = Map.of(
        "registerController", new RegisterController(),
        "authController", new AuthController(),
        "fileController", new FileController(),
        "downloadFileController", new DownloadFileController()
    );

    private static Map<String, String> endPoints = Map.of(
        "/api/auth/register", "registerController",
        "/api/auth/login", "authController",
        "/api/files/upload", "fileController",
        "/api/files", "fileController",
        "/api/files/download", "downloadFileController"
    );
    
    private static String[] content = {
        "/auth/register",
        "/css/register.css",
        "/js/register.js",
        "/auth/login",
        "/js/login.js",
        "/myFiles",
        "/css/myFiles.css",
        "/js/myFiles.js",
        "/css/colors.css",
        "/css/commonStyles.css"
    };

    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();

        int port = 8080;

        Connector connector = new Connector();
        connector.setPort(port);
        tomcat.setConnector(connector);
        
        String contextPath = "";
        String docBase = new File("src/main/webapp").getAbsolutePath();
        
        Context context = tomcat.addContext(contextPath, docBase);

        for (Entry<String, HttpServlet> entry : servlets.entrySet())
            Tomcat.addServlet(context, entry.getKey(), entry.getValue());

        for (Entry<String, String> entry : endPoints.entrySet())
            context.addServletMappingDecoded(entry.getKey(), entry.getValue());
        
        Wrapper contentController = Tomcat.addServlet(
            context, 
            "contentController", 
            new ContentController()
        );

        for (String resource : content)
            contentController.addMapping(resource);

        tomcat.start();

        System.out.println("Server started on port " + port);

        tomcat.getServer().await();
    }
}
