package bind.message.repository;

import bind.message.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface MessageRepository  extends JpaRepository<Message, Long> {

    Page<Message> findAllBySenderId(String senderId, Pageable pageable);
    Page<Message> findAllByReceiverId(String receiverId, Pageable pageable);


    List<Message> findAllBySenderDeletedAtIsNotNullAndReceiverDeletedAtIsNotNull();

    Page<Message> findBySenderIdAndSubjectContaining(String senderId, String subject, Pageable pageable);
}
