package share.file;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.Context;

import jakarta.servlet.http.HttpServlet;

public class App {
    private static Map<String, HttpServlet> servlets = Map.of();

    private static Map<String, String> endPoints = Map.of();
    
    private static String[] content = {
        "/css/*.css",
        "/js/*.js"
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
        
        tomcat.start();

        System.out.println("Server started on port " + port);

        tomcat.getServer().await();
    }
}
