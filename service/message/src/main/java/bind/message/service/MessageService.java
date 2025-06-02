package bind.message.service;


import bind.message.dto.request.MessageSendRequest;
import bind.message.dto.response.MessageResponse;
import bind.message.dto.response.MessageSimpleResponse;
import bind.message.entity.Message;
import bind.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;



    public Page<MessageSimpleResponse> getMessagesBySenderId(String senderId, Pageable page)
    {
        return null;
    }


    public Page<MessageSimpleResponse> getMessagesByReceiverId(String receiverId, Pageable page)
    {
        return null;
    }


    public Void sendMessage(MessageSendRequest req,String senderId)
    {
        Message message = Message.builder()
                .senderId(senderId)
                .receiverId(req.receiverId())
                .contents(req.content())
                .createdAt(LocalDateTime.now())
                .subject(req.subject())
                .senderDeletedAt(null)
                .receiverDeletedAt(null)
                .readAt(null)
                .build();

        messageRepository.save(message);
    }

    public MessageResponse getMessage(Long messageId)
    {
        return null;
    }

    public void deleteBySender(String senderId, Long messageId)
    {

    }
    public void deleteByReceiverId(String receiverId, Long messageId)
    {

    }

    private void deleteMessage(Long messageId)
    {

    }



}
