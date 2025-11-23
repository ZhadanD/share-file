package share.file.defenders;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.HttpHeaders;
import share.file.services.JwtService;

public class SecurityDefender {
    private static JwtService jwtService = new JwtService();

    public static boolean protect(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer")) {
            return false;
        }

        String token = authorizationHeader.substring("Bearer".length()).trim();

        if (!jwtService.isTokenValid(token))
            return false;

        return true;
    }
}
