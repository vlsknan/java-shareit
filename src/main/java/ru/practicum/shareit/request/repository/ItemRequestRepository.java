package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    @Query("select r from ItemRequest r " +
            "where r.requester.id = ?1 " +
            "order by r.id desc")
    List<ItemRequest> findAllByRequesterId(int requesterId);
}