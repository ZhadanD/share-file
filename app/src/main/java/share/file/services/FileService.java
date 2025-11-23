package share.file.services;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;

import jakarta.servlet.http.HttpServletRequest;
import share.file.dto.GetFileDTO;
import share.file.dto.ResponseDTO;
import share.file.entities.FileEntity;
import share.file.entities.UserEntity;
import share.file.mappers.FileMapper;
import share.file.repositories.FileRepository;
import share.file.repositories.UserRepository;
import share.file.utils.FileUtil;

public class FileService {
    private FileRepository fileRepository = new FileRepository();

    private UserRepository userRepository = new UserRepository();

    private FileMapper fileMapper = new FileMapper();

    private final String UPLOAD_DIR = "/static/files";

    public ResponseDTO<List<GetFileDTO>> getFiles(HttpServletRequest request) {
        UserEntity currentUser = this.userRepository.getCurrentUser(request);

        List<FileEntity> fileEntities = this.fileRepository.getFilesByUserId(currentUser.getId());

        List<GetFileDTO> response = new ArrayList<>();

        for (FileEntity fileEntity : fileEntities) {
            GetFileDTO fileDTO = this.fileMapper.toGetFileDTO(fileEntity);

            if (fileDTO.getTimeBeforeDeletion() <= 0) {
                try {
                    this.deleteFile(
                            fileEntity.getUuid(),
                            fileEntity.getFileName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                response.add(fileDTO);
            }
        }

        return new ResponseDTO<>(response);
    }

    public GetFileDTO getFileByUUID(String uuid) {
        FileEntity fileEntity = this.fileRepository.getFileByUUID(uuid);

        GetFileDTO fileDTO = this.fileMapper.toGetFileDTO(fileEntity);

        if (fileDTO.getTimeBeforeDeletion() <= 0) {
            try {
                this.deleteFile(
                    fileEntity.getUuid(),
                    fileEntity.getFileName()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return fileDTO;
        }

        return null;
    }

    public void deleteFile(UUID uuid, String fileName) throws Exception {
        URI dirUri = getClass().getResource(UPLOAD_DIR).toURI();

        Path dirPath = Paths.get(dirUri);

        Path filePath = dirPath.resolve(
            FileUtil.getDiskFileName(uuid, fileName)
        );

        Files.delete(filePath);

        this.fileRepository.deleteFileByUUID(uuid);
    }

    public ResponseDTO<GetFileDTO> saveFile(HttpServletRequest request) {
        UserEntity currentUser = this.userRepository.getCurrentUser(request);

        DiskFileItemFactory factory = DiskFileItemFactory.builder().get();

        var upload = new JakartaServletFileUpload(factory);

        try {
            List<FileItem> items = upload.parseRequest(request);

            for (FileItem item : items) {
                if (!item.isFormField()) {
                    String fileName = item.getName();

                    if (fileName != null && !fileName.isEmpty()) {
                        URI dirUri = getClass().getResource(UPLOAD_DIR).toURI();

                        Path dirPath = Paths.get(dirUri);

                        UUID uuid = UUID.randomUUID();

                        Path filePath = dirPath.resolve(
                            FileUtil.getDiskFileName(uuid, fileName)
                        );

                        Files.write(filePath, item.get());

                        var fileEntity = new FileEntity();
                        fileEntity.setFileName(fileName);
                        fileEntity.setUuid(uuid);
                        fileEntity.setUploadDate(
                                LocalDateTime.now());
                        fileEntity.setUserId(currentUser.getId());

                        this.fileRepository.save(fileEntity);

                        GetFileDTO fileDTO = this.fileMapper.toGetFileDTO(fileEntity);

                        return new ResponseDTO<>(fileDTO);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
