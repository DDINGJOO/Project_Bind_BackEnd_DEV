package bind.comment.dto.request;

public record CommentCreateRequest(
        String referenceId, // 게시글 id
        String authorId,
        String content,
        Long parentId // 1레벨: null, 2레벨: 상위 댓글 id
) {}
