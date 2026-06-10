package com.bytestorm.insightflow.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseEntity {
    private UUID id;
    private LocalDateTime criadoEm;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        this.criadoEm = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
