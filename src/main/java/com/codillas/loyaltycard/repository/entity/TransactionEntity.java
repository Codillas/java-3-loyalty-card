package com.codillas.loyaltycard.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "card_id")
    private UUID cardId;

    @Column(name = "admin_id")
    private UUID adminId;

    @Column
    @Enumerated(EnumType.STRING)
    private Direction direction;

    @Column
    private int amount;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column
    private String note;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}
