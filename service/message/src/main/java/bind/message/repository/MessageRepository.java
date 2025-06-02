package bind.message.repository;

import bind.message.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MessageRepository  extends JpaRepository<Message, Long> {

    Page<Message> findBySenderId(String senderId, Pageable pageable);
    Page<Message> findByReceiverId(String receiverId, Pageable pageable);


}
