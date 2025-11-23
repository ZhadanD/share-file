package share.file.mappers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Properties;

import share.file.dto.GetFileDTO;
import share.file.entities.FileEntity;
import share.file.utils.PropertyUtil;

public class FileMapper {
    public GetFileDTO toGetFileDTO(FileEntity fileEntity) {
        Duration duration = Duration.between(
                fileEntity.getUploadDate(),
                LocalDateTime.now()
        );

        PropertyUtil propertyUtil = new PropertyUtil();

        Properties properties = propertyUtil.loadProperties();

        int fileLifeTime = Integer.parseInt(
            properties.getProperty("file-lifetime")
        );
        
        Long timeBeforeDeletion = fileLifeTime - duration.getSeconds();

        var getFileDTO = new GetFileDTO();
        getFileDTO.setFileName(fileEntity.getFileName());
        getFileDTO.setUuid(fileEntity.getUuid());
        getFileDTO.setLink(fileEntity.getUuid().toString());
        getFileDTO.setTimeBeforeDeletion(timeBeforeDeletion);

        return getFileDTO;
    }
}
