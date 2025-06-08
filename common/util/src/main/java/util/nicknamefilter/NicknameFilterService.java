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
        if (nickname.length() < 2 || nickname.length() > 20) {
            throw new IllegalArgumentException("닉네임은 2자 이상 20자 이하로 입력해주세요.");
        }
        if (!nickname.matches("^[a-zA-Z0-9가-힣_]+$")) {
            throw new IllegalArgumentException("닉네임은 한글, 영문, 숫자, 밑줄(_)만 사용할 수 있습니다.");
        }
        if (nickname.startsWith("_") || nickname.endsWith("_")) {
            throw new IllegalArgumentException("닉네임은 밑줄(_)로 시작하거나 끝날 수 없습니다.");
        }
        if (nickname.contains("__")) {
            throw new IllegalArgumentException("닉네임에 연속된 밑줄(__)은 사용할 수 없습니다.");
        }
        if (nickname.chars().anyMatch(Character::isWhitespace)) {
            throw new IllegalArgumentException("닉네임에 공백을 포함할 수 없습니다.");
        }



    }
}
