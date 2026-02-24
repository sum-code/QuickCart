package com.quickcart.user.repository;

import com.quickcart.user.entity.AppRole;
import com.quickcart.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppRoleRepository extends JpaRepository<AppRole, Long> {
	Optional<AppRole> findByName(Role name);
}
