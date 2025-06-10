package event.Deserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import event.constant.EventType;
import event.domain.Event;
import event.domain.EventPayload;
import event.dto.EmailVerificationEventPayload;
import event.dto.UserProfileCreatedEventPayload;
import event.dto.UserRegisteredEventPayload;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class GenericEventDeserializer implements Deserializer<Event<? extends EventPayload>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Map<EventType, Class<? extends EventPayload>> PAYLOAD_TYPE_MAP = new HashMap<>();

    static {
        PAYLOAD_TYPE_MAP.put(EventType.EMAIL_VERIFICATION, EmailVerificationEventPayload.class);
        PAYLOAD_TYPE_MAP.put(EventType.USER_PROFILE_CREATED, UserProfileCreatedEventPayload.class);
        PAYLOAD_TYPE_MAP.put(EventType.USER_PROFILE_NICKNAME_UPDATED, UserProfileCreatedEventPayload.class);
        PAYLOAD_TYPE_MAP.put(EventType.USER_WITHDRAWN, UserProfileCreatedEventPayload.class);
        PAYLOAD_TYPE_MAP.put(EventType.USER_REGISTERED, UserRegisteredEventPayload.class);

        // 여기에 다른 이벤트 타입과 페이로드 클래스를 추가할 수 있다.

        // 추후 이벤트 추가시 여기에만 추가하면 된다
    }

    @Override
    public Event<?> deserialize(String topic, byte[] data) {
        try {
            JsonNode rootNode = objectMapper.readTree(data);
            EventType type = EventType.valueOf(rootNode.get("type").asText());
            long timestamp = rootNode.get("timestamp").asLong();

            JsonNode payloadNode = rootNode.get("payload");
            Class<? extends EventPayload> payloadClass = PAYLOAD_TYPE_MAP.get(type);

            if (payloadClass == null) {
                log.error("지원하지 않는 이벤트 타입 수신: {}", type);
                throw new IllegalArgumentException("지원하지 않는 이벤트 타입: " + type);
            }

            EventPayload payload = objectMapper.treeToValue(payloadNode, payloadClass);

            return Event.builder()
                    .type(type)
                    .timestamp(timestamp)
                    .payload(payload)
                    .build();

        } catch (JsonProcessingException e) {
            log.error("이벤트 역직렬화 실패: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("이벤트 타입 역직렬화 실패: {}", e.getMessage());
            throw new RuntimeException("이벤트 타입 역직렬화 실패", e);
        }
    }
}
