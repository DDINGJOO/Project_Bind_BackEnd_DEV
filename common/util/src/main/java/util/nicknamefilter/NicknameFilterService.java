package util.nicknamefilter;

import org.springframework.stereotype.Service;
import util.nicknamefilter.exception.NickNameFilterErrorCode;
import util.nicknamefilter.exception.NickNameFilterException;

@Service
public class NicknameFilterService {
    private final BadWordLoader badWordLoader;

    public NicknameFilterService(BadWordLoader badWordLoader) {
        this.badWordLoader = badWordLoader;
    }

    /**
     * 닉네임에 금지어 포함 여부 반환
     */
    public boolean containsBadWord(String nickname) {
        if (nickname == null || nickname.isBlank()) return false;

        String lowerNick = nickname.toLowerCase();
        for (String bad : badWordLoader.getBadWords()) {
            if (lowerNick.contains(bad)) return true;
        }
        return false;
    }

    /**
     * 닉네임이 적합한지 검사, 부적합하면 예외 발생 (혹은 메시지 리턴 등)
     */
    public void validateNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new NickNameFilterException(NickNameFilterErrorCode.NICKNAME_EMPTY);
        }
        if (containsBadWord(nickname)) {
            throw new NickNameFilterException(NickNameFilterErrorCode.NICKNAME_CONTAINS_BAD_WORD);
        }

        if (nickname.length() < 2) {
            throw new NickNameFilterException(NickNameFilterErrorCode.NICKNAME_TOO_SHORT);
        }
        if (nickname.length() > 20) {
            throw new NickNameFilterException(NickNameFilterErrorCode.NICKNAME_TOO_LONG);
        }
        if (nickname.contains(" ")) {
            throw new NickNameFilterException(NickNameFilterErrorCode.NICKNAME_CONTAINS_SPACES);
        }




    }
}
