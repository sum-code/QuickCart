package com.quickcart.user.config;

import com.quickcart.user.entity.AppRole;
import com.quickcart.user.entity.Role;
import com.quickcart.user.repository.AppRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleDataInitializer implements CommandLineRunner {

	private final AppRoleRepository roleRepository;

	public RoleDataInitializer(AppRoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	@Override
	public void run(String... args) {
		ensure(Role.USER);
		ensure(Role.ADMIN);
	}

	private void ensure(Role role) {
		roleRepository.findByName(role).orElseGet(() -> roleRepository.save(new AppRole(role)));
	}
}
