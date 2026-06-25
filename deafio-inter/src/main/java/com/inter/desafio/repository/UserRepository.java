package com.inter.desafio.repository;

import com.inter.desafio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Persistencia de usuarios (H2 in-memory).
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
