package ru.practicum.explore.participation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explore.participation.model.Participation;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Integer> {
    List<Participation> findAllByRequesterAndEvent(Integer userId, Integer eventId);

    List<Participation> findAllByEvent(Integer eventId);

    List<Participation> findAllByRequester(Integer userId);

    Integer countByEvent(Integer eventId);

    @Query("SELECT count(p) " +
            "FROM Participation AS p " +
            "WHERE (p.status = 'PUBLISHED' or p.status = 'CANCELED') " +
            "AND p.id IN ?1")
    Integer countBadReq(List<Integer> requestIds);

    @Query("SELECT p " +
            "FROM Participation AS p " +
            "WHERE p.id IN ?1")
    List<Participation> participationReq(List<Integer> requestIds);

    @Query("SELECT count(p) " +
            "FROM Participation AS p " +
            "WHERE p.event = ?1 " +
            "AND p.status = 'CONFIRMED'")
    Integer countByEventIdAndConfirmed(Integer eventId);
}