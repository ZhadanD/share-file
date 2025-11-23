package share.file.utils;

import java.util.UUID;

import share.file.dto.GetFileDTO;

public class FileUtil {
    public static String getDiskFileName(GetFileDTO fileDTO) {
        String fileExtension = getFileExtensionSimple(fileDTO.getFileName());

        return fileDTO.getUuid().toString() + fileExtension;
    }

    public static String getDiskFileName(UUID uuid, String fileName) {
        String fileExtension = getFileExtensionSimple(fileName);

        return uuid.toString() + fileExtension;
    }

    public static String getFileExtensionSimple(String filename) {
        if (filename == null)
            return "";

        int lastDotIndex = filename.lastIndexOf('.');

        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1)
            return filename.substring(lastDotIndex);

        return "";
    }
}
