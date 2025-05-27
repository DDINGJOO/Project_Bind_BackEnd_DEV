package event.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import event.events.Event;
import event.payload.EmailVerificationEventPayload;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class EmailEventDeserializer implements Deserializer<Event<EmailVerificationEventPayload>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        objectMapper.findAndRegisterModules();
    }

    @Override
    public Event<EmailVerificationEventPayload> deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data,
                    objectMapper.getTypeFactory().constructParametricType(Event.class, EmailVerificationEventPayload.class));
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize Event<EmailVerificationEventPayload>", e);
        }
    }
}
