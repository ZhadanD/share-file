package share.file.mappers;

import share.file.dto.CreateUserDTO;
import share.file.entities.UserEntity;

public class UserMapper {
    public UserEntity toEntity(CreateUserDTO dto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(dto.getUsername());
        userEntity.setPassword(dto.getPassword());

        return userEntity;
    }
}
