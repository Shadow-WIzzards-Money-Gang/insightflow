package com.bytestorm.insightflow.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseEntity {
    private String id;
    private LocalDateTime criadoEm;

    public BaseEntity() {
        this.id = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        this.criadoEm = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
