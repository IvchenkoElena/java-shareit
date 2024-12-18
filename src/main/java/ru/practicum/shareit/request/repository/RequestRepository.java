package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    ItemRequest findById(long itemRequest);
}
