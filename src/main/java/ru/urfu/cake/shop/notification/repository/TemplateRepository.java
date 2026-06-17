package ru.urfu.cake.shop.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.cake.shop.notification.entity.EmailTemplate;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TemplateRepository extends JpaRepository<EmailTemplate, UUID> {

    Optional<EmailTemplate> findByTemplateKey(String templateKey);

    boolean existsByTemplateKey(String templateKey);

    void deleteByTemplateKey(String templateKey);
}