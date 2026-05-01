package tn.enicarthage.plate_be.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "trace_logins", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_action", columnList = "action"),
    @Index(name = "idx_date", columnList = "date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraceLogin {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 254)
    private String email;
    
    @Column(nullable = false, length = 50)
    private String action;
    
    @Column(nullable = false)
    private LocalDateTime date;
    
    @Column(length = 45)
    private String ipAddress;
    
    @Column(length = 500)
    private String userAgent;
    
    @Column(length = 255)
    private String details;
    
    @Column(nullable = false)
    @Builder.Default
    private String status = "SUCCESS";

}

