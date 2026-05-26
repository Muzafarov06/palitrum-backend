package com.example.palitrum.config;

import com.example.palitrum.model.Role;
import com.example.palitrum.model.User;
import com.example.palitrum.model.UserRelation;
import com.example.palitrum.model.UserRole;
import com.example.palitrum.repository.RoleRepository;
import com.example.palitrum.repository.UserRelationRepository;
import com.example.palitrum.repository.UserRepository;
import com.example.palitrum.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRelationRepository userRelationRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        User superAdmin = createSuperAdminUser();
        User manager = createManagerUser(superAdmin);
        User teacher = createTeacherUser(superAdmin);
        User student = createStudentUser(superAdmin);
        User parent = createParentUser(superAdmin);

        // Связываем родителя с учеником
        createParentChildRelation(parent, student, superAdmin);
    }

    private void assignRoleToUser(User user, Role role, User assignedBy) {
        // Проверяем, есть ли уже такая роль у пользователя
        boolean alreadyHasRole = userRoleRepository.findAll().stream()
                .anyMatch(ur -> ur.getUser().getId().equals(user.getId())
                        && ur.getRole().getId().equals(role.getId()));
        if (alreadyHasRole) return;

        UserRole ur = new UserRole();
        ur.setUser(user);
        ur.setRole(role);
        ur.setScopeType(null);
        ur.setScopeId(null);
        ur.setAssignedBy(assignedBy);
        ur.setAssignedAt(OffsetDateTime.now());
        userRoleRepository.save(ur);
    }

    private User createSuperAdminUser() {
        String email = "admin@example.com";
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setFirstName("Main");
            user.setLastName("Administrator");
            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode("admin123"));
            user.setStatus("ACTIVE");
            user.setStaff(true);
            userRepository.saveAndFlush(user);

            Role role = roleRepository.findByName("SUPER_ADMIN").orElseThrow();
            assignRoleToUser(user, role, null);
            return user;
        });
    }

    private User createManagerUser(User assignedBy) {
        String email = "manager@example.com";
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setFirstName("Manager");
            user.setLastName("User");
            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode("manager123"));
            user.setStatus("ACTIVE");
            user.setStaff(true);
            userRepository.saveAndFlush(user);

            Role role = roleRepository.findByName("MANAGER").orElseThrow();
            assignRoleToUser(user, role, assignedBy);
            return user;
        });
    }

    private User createTeacherUser(User assignedBy) {
        String email = "teacher@example.com";
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setFirstName("Teacher");
            user.setLastName("User");
            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode("teacher123"));
            user.setStatus("ACTIVE");
            user.setStaff(true);
            userRepository.saveAndFlush(user);

            Role role = roleRepository.findByName("TEACHER").orElseThrow();
            assignRoleToUser(user, role, assignedBy);
            return user;
        });
    }

    private User createStudentUser(User assignedBy) {
        String email = "student@example.com";
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setFirstName("Student");
            user.setLastName("User");
            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode("student123"));
            user.setStatus("ACTIVE");
            user.setStaff(false);
            userRepository.saveAndFlush(user);

            Role role = roleRepository.findByName("STUDENT").orElseThrow();
            assignRoleToUser(user, role, assignedBy);
            return user;
        });
    }

    private User createParentUser(User assignedBy) {
        String email = "parent@example.com";
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setFirstName("Parent");
            user.setLastName("User");
            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode("parent123"));
            user.setStatus("ACTIVE");
            user.setStaff(false);
            userRepository.saveAndFlush(user);

            Role role = roleRepository.findByName("PARENT").orElseThrow();
            assignRoleToUser(user, role, assignedBy);
            return user;
        });
    }

    private void createParentChildRelation(User parent, User child, User assignedBy) {
        // Проверяем, не существует ли уже связь
        boolean exists = userRelationRepository.existsByParentUserIdAndChildUserId(parent.getId(), child.getId());
        if (exists) return;

        UserRelation relation = new UserRelation();
        relation.setParentUserId(parent.getId());
        relation.setChildUserId(child.getId());
        relation.setRelationType("parent");
        relation.setVerified(true);
        userRelationRepository.save(relation);
    }
}