package bind.message.controller;


import bind.message.dto.request.MessageSendRequest;
import bind.message.dto.response.MessageResponse;
import bind.message.dto.response.MessageSimpleResponse;
import bind.message.exception.MessageException;
import bind.message.service.MessageService;
import data.BaseResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import security.jwt.JwtProvider;

import java.util.Objects;

@RestController("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final JwtProvider jwtProvider;

    @PostMapping()
    public ResponseEntity<BaseResponse<String>> sendMessage(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody MessageSendRequest request

    ) {

        if (!bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(BaseResponse.error("토큰 정보를 해독할 수 없습니다."));
        }
        String senderId = jwtProvider.getUserIdFromToken(bearerToken);
        if (senderId == null) {
            return ResponseEntity.badRequest().body(BaseResponse.error("토큰 정보가 유효하지 않습니다."));
        }

        messageService.sendMessage(request, senderId);

        return ResponseEntity.ok(BaseResponse.success("메세지 보냈지롱~"));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<MessageResponse>> getMessage(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam("messageId") Long messageId
    ) {

        if (!bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(BaseResponse.error("토큰 정보를 해독할 수 없습니다."));
        }
        String userId = jwtProvider.getUserIdFromToken(bearerToken);
        if (userId == null) {
            return ResponseEntity.badRequest().body(BaseResponse.error("토큰 정보가 유효하지 않습니다."));
        }

        MessageResponse response = messageService.getMessage(messageId);

        return ResponseEntity.ok(BaseResponse.success(response));
    }
    /**
     * [GET] /api/messages/sender/{senderId}
     * 내가 보낸 메시지 리스트(페이징)
     */
    @GetMapping("/sendList")
    public ResponseEntity<BaseResponse<Page<MessageResponse>>> getMessagesBySender(
            @RequestParam(required = false) String senderId,
            @RequestHeader("Authorization") String bearerToken,
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String subject
    ) {
        if (!bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(BaseResponse.error("토큰 정보를 해독할 수 없습니다."));
        }
        String userId = jwtProvider.getUserIdFromToken(bearerToken);
        if (userId == null) {
            return ResponseEntity.badRequest().body(BaseResponse.error("토큰 정보가 유효하지 않습니다."));
        }
        if( userId.equals(senderId) && subject.isEmpty()) {
            Page<MessageResponse> messages = messageService.getMessagesBySenderId(senderId, pageable);
            return ResponseEntity.ok(BaseResponse.success(messages));
        }

        if (userId.equals(senderId) && !subject.isEmpty()) {
            Page<MessageResponse> messages = messageService.getMessagesBySenderIdAndSubject(senderId, subject, pageable);
            return ResponseEntity.ok(BaseResponse.success(messages));
        }
        return ResponseEntity.badRequest().body(BaseResponse.error("내가보낸  메세지만 확인 할 수 있습니다."));
    }


    @GetMapping("/receiveList")
    public ResponseEntity<BaseResponse<Page<MessageResponse>>> getMessagesByReceiver(
            @RequestParam(required = false) String receiverId,
            @RequestHeader("Authorization") String bearerToken,
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String subject
    ) {
        if (!bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(BaseResponse.error("토큰 정보를 해독할 수 없습니다."));
        }
        String userId = jwtProvider.getUserIdFromToken(bearerToken);
        if (userId == null) {
            return ResponseEntity.badRequest().body(BaseResponse.error("토큰 정보가 유효하지 않습니다."));
        }
        if (userId.equals(receiverId) && subject.isEmpty()) {
            Page<MessageResponse> messages = messageService.getMessagesBySenderId(receiverId, pageable);
            return ResponseEntity.ok(BaseResponse.success(messages));
        }
        if (userId.equals(receiverId) && !subject.isEmpty()) {
            Page<MessageResponse> messages = messageService.getMessagesBySenderIdAndSubject(receiverId, subject, pageable);
            return ResponseEntity.ok(BaseResponse.success(messages));
        }
        return ResponseEntity.badRequest().body(BaseResponse.error("내가 받은  메세지만 확인 할 수 있습니다."));
    }


    @DeleteMapping ()
    public ResponseEntity<BaseResponse<String>> deleteMessage(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam(required = true) Long messageId
    ) {
        if (!bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(BaseResponse.error("토큰 정보를 해독할 수 없습니다."));
        }
        String userId = jwtProvider.getUserIdFromToken(bearerToken);
        if (userId == null) {
            return ResponseEntity.badRequest().body(BaseResponse.error("토큰 정보가 유효하지 않습니다."));
        }

        try {
            messageService.deleteMessage(userId, messageId);
        } catch(MessageException e)
        {
                return ResponseEntity.badRequest().body(BaseResponse.error(e.getMessage()));
            }
        return ResponseEntity.ok(BaseResponse.success("메세지 삭제 완료"));
    }




}
