package com.peerlender.security.user.repository;

import com.peerlender.security.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
