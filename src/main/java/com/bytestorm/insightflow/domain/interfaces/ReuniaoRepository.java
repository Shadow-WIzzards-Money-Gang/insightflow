package com.bytestorm.insightflow.domain.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.bytestorm.insightflow.domain.entity.Reuniao;

public interface ReuniaoRepository {
    public void save(Reuniao reuniao);
    public List<Reuniao> findAll();
    
    public Optional<Reuniao> findById(UUID id);
}
