package com.example.Notification_Service.repositories;




import com.example.Notification_Service.entities.Notification;
import com.example.Notification_Service.enums.NotificationStatus;
import com.example.Notification_Service.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientEmail(String email);

    List<Notification> findByStatus(NotificationStatus status);

    List<Notification> findByType(NotificationType type);

    List<Notification> findByReferenceIdAndReferenceType(Long referenceId, String referenceType);

    // Trouver les notifications en échec à réessayer (max 3 tentatives)
    @Query("SELECT n FROM Notification n WHERE n.status = 'FAILED' AND n.retryCount < 3")
    List<Notification> findFailedNotificationsToRetry();

    // Trouver les notifications en attente
    List<Notification> findByStatusOrderByCreatedAtAsc(NotificationStatus status);

    // Statistiques
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.status = :status")
    long countByStatus(@Param("status") NotificationStatus status);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.createdAt >= :since")
    long countNotificationsSince(@Param("since") LocalDateTime since);

    // Notifications récentes par destinataire
    List<Notification> findByRecipientEmailOrderByCreatedAtDesc(String email);

    // Recherche par période
    List<Notification> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
