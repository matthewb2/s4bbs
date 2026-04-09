package com.example.board.repository;

import com.example.board.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByName(String name);

    Page<User> findById(Long id, Pageable pageable);
    Page<User> findByEmailContaining(String email, Pageable pageable);
    Page<User> findByName(String name, Pageable pageable);
    Page<User> findByPhone(String phone, Pageable pageable);
    Page<User> findByType(String type, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.address LIKE %:address%")
    Page<User> findByAddressContaining(@Param("address") String address, Pageable pageable);

    Optional<User> findByLoginTypeAndEmail(String loginType, String email);
}