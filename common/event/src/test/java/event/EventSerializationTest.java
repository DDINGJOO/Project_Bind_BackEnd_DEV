package event;


import org.junit.jupiter.api.Test;
import payload.UserLoginEventPayload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


//Event 직렬화, 역직렬화 테스트
public class EventSerializationTest {

    @Test
    void testJavaSerialization() throws Exception {
        // Given
        UserLoginEventPayload payload = UserLoginEventPayload.builder()
                .userId(UUID.randomUUID().toString())
                .ipAddress("127.0.0.1")
                .userAgent("JUnit")
                .success(true)
                .loginAt(LocalDateTime.now())
                .build();

        Event<UserLoginEventPayload> originalEvent = Event.<UserLoginEventPayload>builder()
                .type("user.login.attempt")
                .payload(payload)
                .occurredAt(LocalDateTime.now())
                .build();

        // When
        byte[] serialized;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(originalEvent);
            serialized = bos.toByteArray();
        }

        Event<?> deserialized;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
             ObjectInputStream in = new ObjectInputStream(bis)) {
            deserialized = (Event<?>) in.readObject();
        }

        // Then
        assertThat(deserialized).isNotNull();
        assertThat(deserialized.getType()).isEqualTo(originalEvent.getType());
        assertThat(deserialized.getOccurredAt()).isEqualTo(originalEvent.getOccurredAt());
    }
}
