package util.nicknamefilter;

import org.springframework.stereotype.Service;

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
        if (containsBadWord(nickname)) {
            throw new IllegalArgumentException("부적절한 닉네임(금지어 포함)");
        }
        // TODO: 길이, 패턴, 한글/영문 제한 등 추가 검증도 여기에
    }
}
