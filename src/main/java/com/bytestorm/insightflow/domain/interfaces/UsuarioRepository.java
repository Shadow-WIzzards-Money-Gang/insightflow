package com.bytestorm.insightflow.domain.interfaces;

import java.util.List;
import java.util.Optional;

import com.bytestorm.insightflow.domain.entity.Usuario;

public interface UsuarioRepository {
    public void save(Usuario usuario);
    public List<Usuario> findAll();
    
    public Optional<Usuario> findByEmail(String email);
}
