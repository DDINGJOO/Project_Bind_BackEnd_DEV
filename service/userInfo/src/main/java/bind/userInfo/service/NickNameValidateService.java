package bind.userInfo.service;


import bind.userInfo.exception.ProfileErrorCode;
import bind.userInfo.exception.ProfileException;
import bind.userInfo.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import util.nicknamefilter.NicknameFilterService;
import util.nicknamefilter.exception.NickNameFilterException;

@Service
@RequiredArgsConstructor
public class NickNameValidateService {

    private final UserProfileRepository userProfileRepository;
    private final NicknameFilterService nicknameFilterService;


    public void isValidNickname(String nickname) {

        try {
            // 닉네임 유효성 검사
            nicknameFilterService.validateNickname(nickname);
        } catch (NickNameFilterException e) {
            throw new NickNameFilterException(e.getErrorCode(), e.getMessage());
        }
        // 중복 닉네임 검사
        if (userProfileRepository.existsByNickname(nickname)) {
            throw new ProfileException(ProfileErrorCode.NICKNAME_ALREADY_EXISTS);

        }
    }


}
