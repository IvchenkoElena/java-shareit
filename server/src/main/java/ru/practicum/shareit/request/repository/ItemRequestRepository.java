package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    ItemRequest findById(long itemRequest);

    List<ItemRequest> findByRequestorId(long requestorId);

    List<ItemRequest> findAllByRequestorIdIsNot(long userId);
}
