package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(ItemService.class)
@AutoConfigureMockMvc
class ItemServiceTest {

    ItemService itemService;
    @MockBean
    ItemRepository itemRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    BookingRepository bookingRepository;
    @MockBean
    CommentRepository commentRepository;
    @MockBean
    ItemRequestRepository itemRequestRepository;
    User user;
    Item item;
    ItemDto itemDto;
    ItemRequest itemRequest;
    Booking booking;
    Comment comment;
    CommentDto commentDto;

    @BeforeEach
    void init() {
        user = new User(1, "user", "user@email");
        itemRequest = new ItemRequest("descr itemRequest", user, LocalDateTime.now());
        item = new Item(1, "item", "descr", true, user.getId(), itemRequest);
        itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), item.getItemRequest().getId());
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository,
                itemRequestRepository);

        booking = new Booking(1, user, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusHours(2), item, Status.APPROVED);
        comment = new Comment(1, "comment", item, user, LocalDateTime.now());
        commentDto = new CommentDto(comment.getId(), comment.getText(), comment.getItem().getId(),
                comment.getAuthor().getName(), comment.getCreated());
    }

    @Test
    void getAll() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyInt(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item)));

        List<ItemDtoInfo> res = itemService.getAll(user.getId(), 0, 2);

        assertEquals(1, res.size());
    }

    @Test
    void getById() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        ItemDtoInfo res = itemService.getById(item.getId(), user.getId());

        assertNotNull(res);
        assertEquals(item.getId(), res.getId());
        assertEquals(item.getName(), res.getName());
        assertEquals(item.getDescription(), res.getDescription());
    }

    @Test
    void getByIdNotFoundException() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(item.getId(), user.getId()));
    }

    @Test
    void create() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        when(itemRepository.save(any()))
                .thenReturn(item);
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.of(itemRequest));

        ItemDto res = itemService.create(itemDto, user.getId());

        assertNotNull(res);
        assertEquals(item.getId(), res.getId());
        assertEquals(item.getName(), res.getName());
        assertEquals(item.getDescription(), res.getDescription());
    }

    @Test
    void createNotFoundUser() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());
        when(itemRepository.save(any()))
                .thenReturn(item);
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.of(itemRequest));

        assertThrows(NotFoundException.class, () -> itemService.create(itemDto, user.getId()));
    }

    @Test
    void createItemRequestNull() {
        item.setItemRequest(null);
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        when(itemRepository.save(any()))
                .thenReturn(item);
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.of(itemRequest));

        ItemDto res = itemService.create(itemDto, user.getId());

        assertNotNull(res);
        assertEquals(item.getId(), res.getId());
        assertEquals(item.getName(), res.getName());
        assertNull(res.getRequestId());
    }

    @Test
    void createNotFoundItemRequest() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        when(itemRepository.save(any()))
                .thenReturn(item);
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(itemDto, user.getId()));
    }

    @Test
    void edit() {
        Item itemUpdate = new Item(1, "itemUp", "itemDescUp", true, itemRequest);
        ItemDto itemDtoUp = new ItemDto(itemUpdate.getId(), itemUpdate.getName(), itemUpdate.getDescription(),
                itemUpdate.getAvailable(), itemUpdate.getItemRequest().getId());

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any()))
                .thenReturn(itemUpdate);

        ItemDto res = itemService.edit(itemDtoUp, user.getId(), itemUpdate.getId());

        assertNotNull(res);
        assertEquals(itemDtoUp.getId(), res.getId());
        assertEquals(itemDtoUp.getName(), res.getName());
        assertEquals(itemDtoUp.getDescription(), res.getDescription());
    }

    @Test
    void editNullNameDescriptionAvailable() {
        Item itemUpdate = new Item(1, item.getName(), item.getDescription(), item.getAvailable(), itemRequest);
        ItemDto itemDtoUp = new ItemDto(itemUpdate.getId(), null, null, null, itemUpdate.getItemRequest().getId());
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any()))
                .thenReturn(itemUpdate);

        ItemDto res = itemService.edit(itemDtoUp, user.getId(), itemUpdate.getId());

        assertNotNull(res);
        assertEquals(itemDtoUp.getId(), res.getId());
        assertEquals(item.getName(), res.getName());
        assertEquals(item.getDescription(), res.getDescription());
    }

    @Test
    void editNotFoundExceptionItem() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.edit(itemDto, user.getId(), item.getId()));
    }

    @Test
    void editNotFoundExceptionOwner() {
        item.setOwnerId(2);
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        Exception ex = assertThrows(NotFoundException.class, () -> itemService.edit(itemDto, user.getId(), item.getId()));
        assertEquals("Редактировать информацию о вещи может только ее владелец", ex.getMessage());
    }

    @Test
    void delete() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        itemService.delete(itemDto.getId());
        List<Item> items = itemRepository.findAll();

        assertEquals(0, items.size());
    }

    @Test
    void search() {
        when(itemRepository.search(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item)));

        List<ItemDto> res = itemService.search("item", user.getId(), 0, 2);

        assertNotNull(res);
        assertEquals(1, res.size());
    }

    @Test
    void searchTextLength0() {
        when(itemRepository.search(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        List<ItemDto> res = itemService.search("", user.getId(), 0, 2);

        assertNotNull(res);
        assertEquals(0, res.size());
    }

    @Test
    void addComment() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerId(anyInt()))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDto res = itemService.addComment(user.getId(), item.getId(), commentDto);

        assertEquals(comment.getId(), res.getId());
        assertEquals(comment.getText(), res.getText());
        assertEquals(comment.getCreated(), res.getCreated());
    }

    @Test
    void addCommentNotFoundException() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.empty());
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerId(anyInt()))
                .thenReturn(List.of(booking));

        assertThrows(NotFoundException.class, () -> itemService.addComment(user.getId(), item.getId(), commentDto));
    }

    @Test
    void addCommentValidateException() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerId(anyInt()))
                .thenReturn(List.of());

        assertThrows(ValidateException.class, () -> itemService.addComment(user.getId(), item.getId(), commentDto));
    }

    @Test
    void addCommentValidateExceptionBooker() {
        User user2 = new User(2, "user2", "u2@mail");
        booking.setBooker(user2);

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerId(anyInt()))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any()))
                .thenReturn(comment);

        Exception ex = assertThrows(ValidateException.class, () -> itemService.addComment(user.getId(), item.getId(), commentDto));
        assertEquals("Оставлять отзыв может только тот пользователь, который брал эту вещь в аренду", ex.getMessage());
    }
}