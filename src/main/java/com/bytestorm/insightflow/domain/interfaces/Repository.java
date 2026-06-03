package com.bytestorm.insightflow.domain.interfaces;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {
    public void save(T entity);
    public List<T> findAll();
    public Optional<T> findById(ID id);
}
