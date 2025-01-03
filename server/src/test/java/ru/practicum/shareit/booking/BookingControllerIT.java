package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerIT {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final String urlTemplate = "/bookings";
    private final String headerUserId = "X-Sharer-User-Id";

    @Test
    void createBookingTest() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "name", "description", Boolean.TRUE, 1L, 1L);
        UserDto userDto = new UserDto(1L, "john.doe@mail.com", "John Doe");
        BookingDto requestDto = new BookingDto(1L, LocalDateTime.of(2022, 7, 3, 19, 30, 1),
                LocalDateTime.of(2022, 7, 3, 19, 30, 1), itemDto, userDto, Status.APPROVED);

        when(bookingService.createBooking(anyLong(), any())).thenReturn(requestDto);

        mvc.perform(post(urlTemplate)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserId, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(content().json(mapper.writeValueAsString(requestDto)));
    }

    @Test
    void approveBookingTest() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "name", "description", Boolean.TRUE, 1L, 1L);
        UserDto userDto = new UserDto(1L, "john.doe@mail.com", "John Doe");
        BookingDto requestDto = new BookingDto(1L, LocalDateTime.of(2022, 7, 3, 19, 30, 1),
                LocalDateTime.of(2022, 7, 3, 19, 30, 1), itemDto, userDto, Status.APPROVED);

        when(bookingService.approveBooking(anyLong(), anyLong(), any())).thenReturn(requestDto);

        mvc.perform(patch(urlTemplate + "/" + requestDto.getId())
                        .param("approved", String.valueOf(true))
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(headerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(content().json(mapper.writeValueAsString(requestDto)));
    }

    @Test
    void getBookingByIdTest() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "name", "description", Boolean.TRUE, 1L, 1L);
        UserDto userDto = new UserDto(1L, "john.doe@mail.com", "John Doe");
        BookingDto requestDto = new BookingDto(1L, LocalDateTime.of(2022, 7, 3, 19, 30, 1),
                LocalDateTime.of(2022, 7, 3, 19, 30, 1), itemDto, userDto, Status.APPROVED);

        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(requestDto);

        mvc.perform(get(urlTemplate + "/" + requestDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(headerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(requestDto)))
                .andExpect(jsonPath("$.item").exists())
                .andExpect(jsonPath("$.item.id").value(is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.name").value(is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.item.description").value(is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.booker").exists())
                .andExpect(jsonPath("$.booker.id").value(is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.email").value(is(userDto.getEmail()), String.class))
                .andExpect(jsonPath("$.booker.name").value(is(userDto.getName()), String.class));
    }

    @Test
    void getBookingsByBookerIdTest() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "name", "description", Boolean.TRUE, 1L, 1L);
        UserDto userDto = new UserDto(1L, "john.doe@mail.com", "John Doe");
        BookingDto requestDto1 = new BookingDto(1L, LocalDateTime.of(2022, 7, 3, 19, 30, 1),
                LocalDateTime.of(2022, 7, 3, 19, 30, 1), itemDto, userDto, Status.APPROVED);
        BookingDto requestDto2 = new BookingDto(2L, LocalDateTime.of(2022, 7, 3, 19, 30, 1),
                LocalDateTime.of(2022, 7, 3, 19, 30, 1), itemDto, userDto, Status.REJECTED);

        List<BookingDto> newRequests = List.of(requestDto1, requestDto2);

        when(bookingService.getBookingsByBookerId(anyLong(), any())).thenReturn(newRequests);

        mvc.perform(get(urlTemplate)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(headerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(is(newRequests.getFirst().getId()), Long.class))
                .andExpect(jsonPath("$[1].id").value(is(newRequests.getLast().getId()), Long.class))
                .andExpect(content().json(mapper.writeValueAsString(newRequests)));
    }

    @Test
    void getBookingsByOwnerIdTest() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "name", "description", Boolean.TRUE, 1L, 1L);
        UserDto userDto = new UserDto(1L, "john.doe@mail.com", "John Doe");
        BookingDto requestDto1 = new BookingDto(1L, LocalDateTime.of(2022, 7, 3, 19, 30, 1),
                LocalDateTime.of(2022, 7, 3, 19, 30, 1), itemDto, userDto, Status.APPROVED);
        BookingDto requestDto2 = new BookingDto(2L, LocalDateTime.of(2022, 7, 3, 19, 30, 1),
                LocalDateTime.of(2022, 7, 3, 19, 30, 1), itemDto, userDto, Status.REJECTED);

        List<BookingDto> newRequests = List.of(requestDto1, requestDto2);

        when(bookingService.getBookingsByOwnerId(anyLong(), anyString())).thenReturn(newRequests);

        mvc.perform(get(urlTemplate + "/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(headerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(is(newRequests.getFirst().getId()), Long.class))
                .andExpect(jsonPath("$[1].id").value(is(newRequests.getLast().getId()), Long.class))
                .andExpect(content().json(mapper.writeValueAsString(newRequests)));
    }
}