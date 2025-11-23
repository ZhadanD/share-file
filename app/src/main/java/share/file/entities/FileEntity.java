package share.file.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileEntity {
    private Long id;

    private String fileName;

    private UUID uuid;

    private LocalDateTime uploadDate;

    private Long userId;
}
