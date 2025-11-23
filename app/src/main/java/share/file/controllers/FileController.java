package share.file.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import share.file.defenders.SecurityDefender;
import share.file.dto.GetFileDTO;
import share.file.dto.ResponseDTO;
import share.file.services.FileService;

@MultipartConfig
public class FileController extends HttpServlet {
    private FileService fileService = new FileService();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        if(!SecurityDefender.protect(request)) {
            response.setStatus(403);

            return;
        }

        ResponseDTO<List<GetFileDTO>> files = this.fileService.getFiles(request);

        response.setContentType("application/json;charset=UTF-8");

        response.setStatus(HttpServletResponse.SC_OK);
        
        response.getWriter().println(
            this.objectMapper.writeValueAsString(files)
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        if(!SecurityDefender.protect(request)) {
            response.setStatus(403);

            return;
        }

        PrintWriter out = response.getWriter();

        if (!JakartaServletFileUpload.isMultipartContent(request)) {
            out.println("Error: Form must have enctype=multipart/form-data");

            return;
        }

        ResponseDTO<GetFileDTO> fileDTO = this.fileService.saveFile(request);

        if(fileDTO == null) {
            response.setStatus(500);

            return;
        }

        response.setContentType("application/json;charset=UTF-8");

        response.setStatus(HttpServletResponse.SC_CREATED);
        
        response.getWriter().println(
            this.objectMapper.writeValueAsString(fileDTO)
        );
    }
}
