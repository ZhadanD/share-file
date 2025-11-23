package share.file.controllers;

import java.io.IOException;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import share.file.dto.CreateUserDTO;
import share.file.dto.ResponseDTO;
import share.file.services.AuthService;

public class RegisterController extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();
    
    private AuthService authService = new AuthService();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = request.getReader()
                                .lines()
                                .collect(
                                    Collectors.joining(System.lineSeparator())
                                );

        CreateUserDTO dto = this.objectMapper.readValue(json, CreateUserDTO.class);

        ResponseDTO<String> responseDTO = this.authService.register(dto);

        response.setContentType("application/json;charset=UTF-8");

        response.setStatus(HttpServletResponse.SC_CREATED);
        
        response.getWriter().println(
            this.objectMapper.writeValueAsString(responseDTO)
        );
    }
}
