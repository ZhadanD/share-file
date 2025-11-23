package share.file.services;

import org.mindrot.jbcrypt.BCrypt;

import share.file.dto.AuthUserDTO;
import share.file.dto.CreateUserDTO;
import share.file.dto.ResponseDTO;
import share.file.entities.UserEntity;
import share.file.mappers.UserMapper;
import share.file.repositories.UserRepository;

public class AuthService {
    private UserRepository userRepository = new UserRepository();

    private UserMapper userMapper = new UserMapper();

    private JwtService jwtService = new JwtService();

    public ResponseDTO<String> register(CreateUserDTO dto) {
        String hashPassword = BCrypt.hashpw(
                                    dto.getPassword(),
                                    BCrypt.gensalt()
                                );
        
        dto.setPassword(
            hashPassword
        );

        UserEntity userEntity = this.userMapper.toEntity(dto);

        this.userRepository.save(userEntity);

        String token = this.jwtService.generateToken(userEntity.getUsername());

        return new ResponseDTO<>(token);
    }

    public ResponseDTO<String> login(AuthUserDTO dto) {
        UserEntity userEntity = this.userRepository.findByUsername(dto.getUsername());

        if(userEntity != null && BCrypt.checkpw(dto.getPassword(), userEntity.getPassword())) {
            String token = this.jwtService.generateToken(userEntity.getUsername());

            return new ResponseDTO<>(token);
        }

        return null;
    }
}
