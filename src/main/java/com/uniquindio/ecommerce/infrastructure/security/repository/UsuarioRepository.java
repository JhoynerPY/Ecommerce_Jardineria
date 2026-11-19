package com.uniquindio.ecommerce.infrastructure.security.repository;

import com.uniquindio.ecommerce.infrastructure.security.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, String> {

    Optional<UsuarioEntity> findByEmailAndDeletedFalse(String email);

    boolean existsByEmailAndDeletedFalse(String email);

    @Query("SELECT u FROM UsuarioEntity u WHERE u.deleted = false AND u.activo = true")
    List<UsuarioEntity> findAllActive();

    @Query("SELECT u FROM UsuarioEntity u WHERE u.deleted = false AND :rol MEMBER OF u.roles")
    List<UsuarioEntity> findByRol(@Param("rol") String rol);
}