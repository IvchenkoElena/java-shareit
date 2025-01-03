package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.NewItemRequestRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ItemRequestMapperTest {
    private final LocalDateTime now = LocalDateTime.now();

    private final User user = new User(1L, "john.doe@mail.com", "John Doe");
    private final User requestor = new User(2L, "requestor name", "requestor email");
    private final UserDto requestorDto = new UserDto(2L, "requestor name", "requestor email");
    private final ItemRequest itemRequest = new ItemRequest(1L, "description", requestor, now);

    private final Item item = new Item(1L, "name", "description", user, Boolean.TRUE,itemRequest);

    private final NewItemRequestRequest newRequest = new NewItemRequestRequest("description");

    private final ItemRequestDto dto = new ItemRequestDto(1L, "description", requestorDto, now);

    @Test
    public void toItemRequestDtoTest() {
        ItemRequestDto userDto = ItemRequestMapper.mapToItemRequestDto(itemRequest);
        assertThat(userDto, equalTo(dto));
    }

    @Test
    public void toItemRequest() {
        ItemRequest ir = ItemRequestMapper.mapToItemRequest(requestor, newRequest);
        assertThat(ir.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(ir.getRequestor(), equalTo(itemRequest.getRequestor()));
    }
}