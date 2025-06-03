package bind.message.service;


import bind.message.dto.request.MessageSendRequest;
import bind.message.dto.response.MessageResponse;
import bind.message.dto.response.MessageSimpleResponse;
import bind.message.entity.Message;
import bind.message.entity.UserProfileSnapshot;
import bind.message.exception.MessageErrorCode;
import bind.message.exception.MessageException;
import bind.message.repository.MessageRepository;
import bind.message.repository.UserProfileSnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserProfileSnapshotRepository userProfileSnapshotRepository;




    public Page<MessageResponse> getMessagesBySenderId(String senderId, Pageable page){

            Page<Message> messages = messageRepository.findAllBySenderId(senderId, page);

            // 1. 메시지에서 모든 sender/receiver userId 추출 (중복 제거)
            Set<String> userIds = messages.stream()
                    .flatMap(m -> Stream.of(m.getSenderId(), m.getReceiverId()))
                    .collect(Collectors.toSet());

            // 2. UserProfileSnapshot 테이블/캐시에서 userIds IN (...) 쿼리 한 번
            Map<String, UserProfileSnapshot> profiles =
                    userProfileSnapshotRepository.findAllByUserIdIn(userIds)
                            .stream().collect(Collectors.toMap(UserProfileSnapshot::getUserId, Function.identity()));

            // 3. 메시지별로 sender/receiverProfile 조합
            return messages.map(message -> {
                UserProfileSnapshot senderProfile = profiles.get(message.getSenderId());
                UserProfileSnapshot receiverProfile = profiles.get(message.getReceiverId());
                return convertToResponse(message, senderProfile, receiverProfile);
            });
    }

    public Page<MessageResponse> getMessagesBySenderIdAndSubject(String userId, String subject, Pageable page)
    {
        Page<Message> messages = messageRepository.findBySenderIdAndSubjectContaining(userId, subject, page);

        // 1. 메시지에서 모든 sender/receiver userId 추출 (중복 제거)
        Set<String> userIds = messages.stream()
                .flatMap(m -> Stream.of(m.getSenderId(), m.getReceiverId()))
                .collect(Collectors.toSet());

        // 2. UserProfileSnapshot 테이블/캐시에서 userIds IN (...) 쿼리 한 번
        Map<String, UserProfileSnapshot> profiles =
                userProfileSnapshotRepository.findAllByUserIdIn(userIds)
                        .stream().collect(Collectors.toMap(UserProfileSnapshot::getUserId, Function.identity()));

        // 3. 메시지별로 sender/receiverProfile 조합
        return messages.map(message -> {
            UserProfileSnapshot senderProfile = profiles.get(message.getSenderId());
            UserProfileSnapshot receiverProfile = profiles.get(message.getReceiverId());
            return convertToResponse(message, senderProfile, receiverProfile);
        });
    }


    public Page<MessageResponse> getMessagesByReceiverId(String receiverId, Pageable page)
    {
        Page<Message> messages = messageRepository.findAllBySenderId(receiverId, page);

        // 1. 메시지에서 모든 sender/receiver userId 추출 (중복 제거)
        Set<String> userIds = messages.stream()
                .flatMap(m -> Stream.of(m.getSenderId(), m.getReceiverId()))
                .collect(Collectors.toSet());

        // 2. UserProfileSnapshot 테이블/캐시에서 userIds IN (...) 쿼리 한 번
        Map<String, UserProfileSnapshot> profiles =
                userProfileSnapshotRepository.findAllByUserIdIn(userIds)
                        .stream().collect(Collectors.toMap(UserProfileSnapshot::getUserId, Function.identity()));

        // 3. 메시지별로 sender/receiverProfile 조합
        return messages.map(message -> {
            UserProfileSnapshot senderProfile = profiles.get(message.getSenderId());
            UserProfileSnapshot receiverProfile = profiles.get(message.getReceiverId());
            return convertToResponse(message, senderProfile, receiverProfile);
        });
    }

    public Page<MessageResponse> getMessagesByReceiverId(String receiverId, Pageable page, String title, String nickname)
    {
        Page<Message> messages = messageRepository.findAllByReceiverId(receiverId, page);

        // 1. 메시지에서 모든 sender/receiver userId 추출 (중복 제거)
        Set<String> userIds = messages.stream()
                .flatMap(m -> Stream.of(m.getSenderId(), m.getReceiverId()))
                .collect(Collectors.toSet());

        // 2. UserProfileSnapshot 테이블/캐시에서 userIds IN (...) 쿼리 한 번
        Map<String, UserProfileSnapshot> profiles =
                userProfileSnapshotRepository.findAllByUserIdIn(userIds)
                        .stream().collect(Collectors.toMap(UserProfileSnapshot::getUserId, Function.identity()));

        // 3. 메시지별로 sender/receiverProfile 조합
        return messages.map(message -> {
            UserProfileSnapshot senderProfile = profiles.get(message.getSenderId());
            UserProfileSnapshot receiverProfile = profiles.get(message.getReceiverId());
            return convertToResponse(message, senderProfile, receiverProfile);
        });
    }


    public void sendMessage(MessageSendRequest req,String senderId)
    {
        Message message = Message.builder()
                .contents(req.content())
                .subject(req.subject())
                .senderId(senderId)
                .receiverId(req.receiverId())
                .createdAt(LocalDateTime.now())
                .build();

        messageRepository.save(message);
        log.info("메세지 전송 완료: {}", message.getId());
    }

    public MessageResponse getMessage(Long messageId)
    {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND));
        UserProfileSnapshot senderProfile = userProfileSnapshotRepository.findByUserId(message.getSenderId())
                .orElseThrow(() -> new MessageException(MessageErrorCode.MESSAGE_SENDER_NOT_FOUND));
        UserProfileSnapshot receiverProfile = userProfileSnapshotRepository.findByUserId(message.getReceiverId())
                .orElseThrow(() ->  new MessageException(MessageErrorCode.MESSAGE_RECEIVER_NOT_FOUND));
        return convertToResponse(message, senderProfile, receiverProfile);

    }


    public void deleteMessage(String userId, Long messageId)
    {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND));
        if (message.getSenderId().equals(userId)) {
            deleteBySender(userId, message);
        } else if (message.getReceiverId().equals(userId)) {
            deleteByReceiverId(userId, message);
        } else {
            throw new MessageException(MessageErrorCode.MESSAGE_USER_NOT_MATCH);
        }
    }
    private void deleteBySender(String senderId, Message message)
    {



        if (message.getSenderDeletedAt() != null) {
            throw new MessageException(MessageErrorCode.MESSAGE_ALREADY_DELETED_BY_SENDER);
        }
        message.setSenderDeletedAt(LocalDateTime.now());
    }
    private void deleteByReceiverId(String receiverId, Message message)
    {

        if (message.getReceiverDeletedAt() != null) {
            throw new MessageException(MessageErrorCode.MESSAGE_ALREADY_DELETED_BY_RECEIVER);
        }
        message.setReceiverDeletedAt(LocalDateTime.now());

    }



    private MessageResponse convertToResponse(Message message, UserProfileSnapshot senderProfile, UserProfileSnapshot receiverProfile) {
        return MessageResponse.builder()
                .contents(message.getContents())
                .createdAt(message.getCreatedAt())
                .receiverId(receiverProfile.getUserId())
                .receiverNickName(receiverProfile.getNickName())
                .receiverProfileImageUrl(receiverProfile.getProfileUrl())
                .senderId(senderProfile.getUserId())
                .senderNickName(senderProfile.getNickName())
                .senderProfileImageUrl(senderProfile.getProfileUrl())
                .subject(message.getSubject())
                .build();

    }



}
