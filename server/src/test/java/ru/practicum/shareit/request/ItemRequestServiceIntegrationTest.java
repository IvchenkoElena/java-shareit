package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.dto.NewItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private NewItemRequestRequest itemRequestInputDto;
    private ItemRequestDto itemRequestOutputDto;
    private Long userId;
    private Long requestId;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user = userRepository.save(user);
        userId = user.getId();

        itemRequestInputDto = new NewItemRequestRequest();
        itemRequestInputDto.setDescription("Test Request");

        itemRequestOutputDto = itemRequestService.createItemRequest(userId, itemRequestInputDto);
        requestId = itemRequestOutputDto.getId();
    }

    @Test
    void testCreateItemRequest() {
        NewItemRequestRequest newItemRequestInputDto = new NewItemRequestRequest();
        newItemRequestInputDto.setDescription("New Test Request");

        ItemRequestDto newItemRequestOutputDto = itemRequestService.createItemRequest(userId, newItemRequestInputDto);
        assertNotNull(newItemRequestOutputDto);
        assertEquals(newItemRequestInputDto.getDescription(), newItemRequestOutputDto.getDescription());
    }

    @Test
    void testGetItemRequestsByRequestorId() {
        List<ItemRequestWithAnswersDto> requests = itemRequestService.getItemRequestsByRequestorId(userId);
        assertFalse(requests.isEmpty());
        assertEquals(1, requests.size());
        assertEquals(itemRequestOutputDto.getId(), requests.get(0).getId());
    }

    @Test
    void testGetOneDetailedById() {
        ItemRequestWithAnswersDto request = itemRequestService.getItemRequestByRequestId(userId, requestId);
        assertNotNull(request);
        assertEquals(itemRequestOutputDto.getId(), request.getId());
        assertEquals(itemRequestOutputDto.getDescription(), request.getDescription());
    }

    @Test
    void testGetOneDetailedByIdNotFound() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestByRequestId(userId, 999L));
    }
}