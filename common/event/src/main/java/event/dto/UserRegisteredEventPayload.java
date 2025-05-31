package event.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import event.constant.EventType;
import event.domain.EventPayload;
import lombok.*;
import org.springframework.kafka.annotation.EnableKafka;


@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
@JsonTypeName("UserRegisteredEventPayload")
public class UserRegisteredEventPayload implements EventPayload {
    private String userId;
    private String token;


}
