package bind.message.controller;


import bind.message.dto.request.MessageSendRequest;
import bind.message.dto.response.MessageSimpleResponse;
import bind.message.service.MessageService;
import data.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import security.jwt.JwtProvider;

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

}
