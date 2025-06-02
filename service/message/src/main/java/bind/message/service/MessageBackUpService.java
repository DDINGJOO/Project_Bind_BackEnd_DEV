package bind.message.service;

import bind.message.entity.Message;
import bind.message.entity.MessageBackup;
import bind.message.repository.MessageBackupRepository;
import bind.message.repository.MessageRepository;
import bind.message.repository.UserTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageBackUpService {

    private final MessageRepository messageRepository;
    private final MessageBackupRepository messageBackupRepository;
    private final UserTableRepository userTableRepository;


    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void archiveDeletedMessages() {
        // 양쪽 모두 삭제한 메시지 찾기
        List<Message> deletable = messageRepository
                .findAllBySenderDeletedAtIsNotNullAndReceiverDeletedAtIsNotNull();

        for (Message message : deletable) {
            // 백업 테이블로 이관
            MessageBackup backup = MessageBackup.builder()
                    .id(message.getId())
                    .contents(message.getContents())
                    .senderId(message.getSenderId())
                    .receiverId(message.getReceiverId())
                    .subject(message.getSubject())
                    .createdAt(message.getCreatedAt())
                    .senderDeletedAt(message.getSenderDeletedAt())
                    .receiverDeletedAt(message.getReceiverDeletedAt())
                    .movedAt(LocalDateTime.now())
                    .build();
            messageBackupRepository.save(backup);

            // 원본 테이블에서 삭제
            messageRepository.delete(message);
        }
    }
}
