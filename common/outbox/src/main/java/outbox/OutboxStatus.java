package outbox;

public enum OutboxStatus {
    READY,   // 메시지 저장(적재)만 된 상태, 아직 발행 안됨
    SENT,    // MQ 등 외부로 정상 발행 완료
    FAILED   // 발행 시도 중 예외/장애/실패 발생 (재시도 or 보류)
}
