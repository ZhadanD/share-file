package share.file.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetFileDTO {
    private String fileName;

    private UUID uuid;

    private String link;

    private Long timeBeforeDeletion;
}
