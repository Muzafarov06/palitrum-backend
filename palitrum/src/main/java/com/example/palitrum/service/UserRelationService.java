package com.example.palitrum.service;

import com.example.palitrum.dto.UserRelationDTO;
import com.example.palitrum.dto.UserResponseDto;
import com.example.palitrum.model.User;
import com.example.palitrum.model.UserRelation;
import com.example.palitrum.repository.UserRelationRepository;
import com.example.palitrum.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRelationService {

    private final UserRelationRepository repository;
    private final UserRepository userRepository;

    public UserRelationService(UserRelationRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public List<UserRelationDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserRelationDTO> getVerifiedByParent(Long parentId) {
        return repository.findByParentUserIdAndVerifiedTrue(parentId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // НОВЫЙ МЕТОД: получить детей родителя
    public List<UserResponseDto> getChildrenByParent(Long parentId) {
        List<UserRelation> relations = repository.findByParentUserIdAndVerifiedTrue(parentId);
        List<Long> childIds = relations.stream()
                .map(UserRelation::getChildUserId)
                .collect(Collectors.toList());
        if (childIds.isEmpty()) return List.of();
        return userRepository.findAllById(childIds).stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    // НОВЫЙ МЕТОД: получить родителей ребёнка
    public List<UserResponseDto> getParentsByChild(Long childId) {
        List<UserRelation> relations = repository.findByChildUserIdAndVerifiedTrue(childId);
        List<Long> parentIds = relations.stream()
                .map(UserRelation::getParentUserId)
                .collect(Collectors.toList());
        if (parentIds.isEmpty()) return List.of();
        return userRepository.findAllById(parentIds).stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public UserRelationDTO create(UserRelationDTO dto) {
        UserRelation relation = new UserRelation(
                dto.getParentUserId(),
                dto.getChildUserId(),
                dto.getRelationType(),
                dto.isVerified()
        );
        repository.save(relation);
        return toDTO(relation);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private UserRelationDTO toDTO(UserRelation relation) {
        return new UserRelationDTO(
                relation.getId(),
                relation.getParentUserId(),
                relation.getChildUserId(),
                relation.getRelationType(),
                relation.isVerified(),
                relation.getCreatedAt()
        );
    }

    // Преобразование User -> UserResponseDto
    private UserResponseDto mapToUserResponse(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setMiddleName(user.getMiddleName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setBirthDate(user.getBirthDate());
        dto.setStatus(user.getStatus());
        dto.setStaff(user.isStaff());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        // Если нужны роли, можно добавить
        // dto.setRoles(user.getRoles().stream().map(ur -> ur.getRole().getName()).collect(Collectors.toList()));
        return dto;
    }
}