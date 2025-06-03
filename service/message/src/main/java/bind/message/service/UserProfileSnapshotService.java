package bind.message.service;


import bind.message.entity.UserProfileSnapshot;
import bind.message.repository.UserProfileSnapshotRepository;
import event.dto.ProfileCreatedEventPayload;
import event.dto.ProfileUpdatedEventPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileSnapshotService {
    private final UserProfileSnapshotRepository userProfileSnapshotRepository;




    public void upDateUser(ProfileUpdatedEventPayload payload) {
        // UserBatchTable 엔티티 생성 및 저장
        // 예시로 간단한 로직을 작성합니다.
        // 실제 로직은 필요에 따라 구현하세요.
        userProfileSnapshotRepository.save(UserProfileSnapshot.builder()
                        .nickName(payload.getNickName())
                        .userId(payload.getUserId())
                        .profileUrl(payload.getImageId())
                .build());
    }


    public void saveUser(ProfileCreatedEventPayload payload) {
        // UserBatchTable 엔티티 생성 및 저장
        // 예시로 간단한 로직을 작성합니다.
        // 실제 로직은 필요에 따라 구현하세요.
        userProfileSnapshotRepository.save(UserProfileSnapshot.builder()
                .nickName(payload.getNickName())
                .userId(payload.getUserId())
                .profileUrl(payload.getImageId())
                .build());
    }



}
