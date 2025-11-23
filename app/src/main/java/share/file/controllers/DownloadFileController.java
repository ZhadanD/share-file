package share.file.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import share.file.dto.GetFileDTO;
import share.file.services.FileService;
import share.file.utils.FileUtil;

public class DownloadFileController extends HttpServlet {
    private FileService fileService = new FileService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String uuid = request.getParameter("file");

        if (uuid == null || uuid.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File name is required");

            return;
        }

        try {
            GetFileDTO fileDTO = this.fileService.getFileByUUID(uuid);

            if(fileDTO == null) {
                response.setStatus(404);

                return;
            }

            String diskFileName = FileUtil.getDiskFileName(fileDTO);

            InputStream is = getClass().getResourceAsStream("/static/files/" + diskFileName);

            if (is == null) {
                response.setStatus(404);

                return;
            }

            response.setContentType("application/octet-stream");
            response.setStatus(200);

            response.setHeader(
                "Content-Disposition", 
                    String.format("attachment; filename=\"%s\"", fileDTO.getFileName())
            );

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

            return;
        }
    }
}
