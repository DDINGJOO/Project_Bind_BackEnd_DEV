package event.domain;


import event.constant.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event<T extends EventPayload> {
    private EventType type;     // 이벤트 타입 (메일 발송, 유저 탈퇴 등)
    private long timestamp;     // 이벤트 발생 시간 (ms)
    private T payload;          // 실제 데이터 (이벤트 상세 데이터)
}
