package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    ItemRequestServiceImpl requestService;

    @Test
    void createItemRequest_whenRequestorNotFound() {
        long requestorId = 0L;
        NewItemRequestRequest request = new NewItemRequestRequest("description");
        User requestor = new User(requestorId, "requestor name", "requestor email");
        when(userRepository.findById(requestorId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> requestService.createItemRequest(requestorId, request));

        assertEquals("Пользователь с ID " + requestorId + " не найден", thrown.getMessage());
    }

    @Test
    void getAllItemRequests_whenRequestorNotFound() {
        long requestorId = 0L;
        NewItemRequestRequest request = new NewItemRequestRequest("description");
        User requestor = new User(requestorId, "requestor name", "requestor email");
        when(userRepository.findById(requestorId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> requestService.getAllItemRequests(requestorId));

        assertEquals("Пользователь с ID " + requestorId + " не найден", thrown.getMessage());
    }

    @Test
    void getAllItemRequests_positiveCase() {
        long userId = 3L;
        User user = new User(userId, "user name", "user email");
        long requestorId = 7L;
        User requestor = new User(requestorId, "requestor name", "requestor email");
        UserDto requestorDto = new UserDto(requestorId, "requestor name", "requestor email");
        long itemRequestId = 0L;
        String description = "description";
        ItemRequest itemRequest = new ItemRequest(itemRequestId, description, requestor, LocalDateTime.now());
        ItemRequestDto requestDto = new ItemRequestDto(itemRequestId, description, requestorDto, LocalDateTime.now());
        List<ItemRequestDto> requestDtoList = List.of(requestDto);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestorIdIsNot(anyLong())).thenReturn(List.of(itemRequest));

        List<ItemRequestDto> actualRequestDtoList = requestService.getAllItemRequests(userId);

        assertEquals(requestDtoList.size(), actualRequestDtoList.size());
        assertEquals(requestDtoList.getFirst().getId(), actualRequestDtoList.getFirst().getId());
        assertEquals(requestDtoList.getFirst().getDescription(), actualRequestDtoList.getFirst().getDescription());
        verify(requestRepository).findAllByRequestorIdIsNot(userId);
    }
}