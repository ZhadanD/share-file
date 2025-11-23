package share.file.repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.HttpHeaders;
import share.file.entities.UserEntity;
import share.file.services.JwtService;

public class UserRepository extends AbstractRepository<UserEntity> {
    
    private JwtService jwtService = new JwtService();

    public UserRepository() {
        try {
            createTableIfNotExists();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize JDBC Repository", e);
        }
    }

    @Override
    public UserEntity save(UserEntity userEntity) {
        String sql = """
            INSERT INTO users 
            (username, password) 
            VALUES (?, ?)
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, userEntity.getUsername());
            pstmt.setString(2, userEntity.getPassword());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next())
                        userEntity.setId(generatedKeys.getLong(1));
                }
            }
            
            return userEntity;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving file info to database", e);
        }
    }

    public UserEntity findByUsername(String username) {
        UserEntity userEntity = null;

        String sql = """
            SELECT * FROM 
            users u 
            WHERE u.username = ?
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                userEntity = new UserEntity();

                userEntity.setId(rs.getLong("id"));
                userEntity.setUsername(rs.getString("username"));
                userEntity.setPassword(rs.getString("password"));

                break;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving files from database", e);
        }
        
        return userEntity;
    }

    public UserEntity getCurrentUser(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        String token = authorizationHeader.substring("Bearer".length()).trim();

        String userName = this.jwtService.extractUserName(token);

        return this.findByUsername(userName);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                username VARCHAR(100) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL
            )
            """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
