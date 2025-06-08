package bind.comment.dto.reponse;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponse(
        Long id,
        String authorId,
        String content,
        Boolean edited,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer level,
        List<ReplyResponse> replies, // 1레벨만, 2레벨은 null
        Boolean deleted
) {
    public record ReplyResponse(
            Long id,
            String authorId,
            String content,
            Boolean edited,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Boolean deleted
    ){}
}
