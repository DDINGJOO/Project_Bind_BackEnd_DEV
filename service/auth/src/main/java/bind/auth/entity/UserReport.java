package bind.auth.entity;


import data.enums.auth.ReportType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_report")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    private User reportedUserId;

    @ManyToOne
    private User reporterUserId;

    @Enumerated(EnumType.STRING)
    private ReportType reportType; // e.g., "abuse", "spam", "harassment"


    private String details;

    private LocalDateTime reportedAt;
    private boolean isResolved; //
    private LocalDateTime resolvedAt;

}
