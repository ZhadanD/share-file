package share.file.repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import share.file.entities.FileEntity;

public class FileRepository extends AbstractRepository<FileEntity>{
    public FileRepository() {
        try {
            createTableIfNotExists();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize JDBC Repository", e);
        }
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS files (
                id SERIAL PRIMARY KEY,
                uuid VARCHAR(100) UNIQUE NOT NULL,
                file_name VARCHAR(500) NOT NULL,
                upload_date TIMESTAMP NOT NULL,
                user_id INT NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
            )
            """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public FileEntity save(FileEntity fileEntity) {
        String sql = """
            INSERT INTO files 
            (file_name, uuid, upload_date, user_id) 
            VALUES (?, ?, ?, ?)
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, fileEntity.getFileName());
            pstmt.setString(2, fileEntity.getUuid().toString());
            pstmt.setTimestamp(3, Timestamp.valueOf(fileEntity.getUploadDate()));
            pstmt.setLong(4, fileEntity.getUserId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next())
                        fileEntity.setId(generatedKeys.getLong(1));
                }
            }
            
            return fileEntity;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error saving file info to database", e);
        }
    }

    public List<FileEntity> getFilesByUserId(Long userId) {
        List<FileEntity> files = new ArrayList<>();

        String sql = """
            SELECT * FROM files f
            WHERE f.user_id = ?
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, userId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                FileEntity fileEntity = this.mapResultSetToFileEntity(rs);

                files.add(fileEntity);
            }    
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving files from database", e);
        }
        
        return files;
    }

    public FileEntity getFileByUUID(String uuid) {
        String sql = """
            SELECT * FROM files f
            WHERE f.uuid = ?
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                FileEntity fileEntity = this.mapResultSetToFileEntity(rs);

                return fileEntity;
            }    
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving files from database", e);
        }
        
        return null;
    }

    public void deleteFileByUUID(UUID uuid) {
        String sql = """
            DELETE FROM files f
            WHERE f.uuid = ?
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());

            pstmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving files from database", e);
        }
    }

    private FileEntity mapResultSetToFileEntity(ResultSet rs) throws SQLException {
        FileEntity fileEntity = new FileEntity();

        fileEntity.setId(rs.getLong("id"));
        fileEntity.setUuid(UUID.fromString(rs.getString("uuid")));
        fileEntity.setFileName(rs.getString("file_name"));
        fileEntity.setUploadDate(rs.getTimestamp("upload_date").toLocalDateTime());
        
        return fileEntity;
    }
}
