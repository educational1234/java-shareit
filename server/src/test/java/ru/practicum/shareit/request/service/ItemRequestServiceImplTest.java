package ru.practicum.shareit.request.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static java.time.LocalDateTime.now;
import static java.util.List.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {
    private final ItemRequestService itemRequestService;
    private final EntityManager entityManager;
    private final UserService userService;
    private ItemRequestDto itemRequestDto;
    private UserDto user;

    @BeforeEach
    void initialize() {
        var userDto = new UserDto(
                null,
                "John",
                "john@mail.com"
        );
        user = userService.save(userDto);
        itemRequestDto = new ItemRequestDto(
                1L,
                "description",
                user.getId(),
                now(),
                of()
        );
    }

    private void saveItemRequests() {
        var itemRequestDto1 = new ItemRequestDto(
                1L,
                "happy Rabbit",
                user.getId(),
                now(),
                of()
        );
        var itemRequestDto2 = new ItemRequestDto(
                2L,
                "Bad clown",
                user.getId(),
                now(),
                of()
        );
        itemRequestService.save(itemRequestDto1, user.getId());
        itemRequestService.save(itemRequestDto2, user.getId());
    }


    @Test
    void saveItemRequestTest() {
        itemRequestService.save(itemRequestDto, user.getId());
        var itemRequest = entityManager.createQuery(
                "SELECT itemRequest " +
                        "FROM ItemRequest itemRequest",
                ItemRequest.class).getSingleResult();
        assertThat(itemRequest.getRequester().getId(), equalTo(itemRequestDto.getRequesterId()));
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequest.getId(), notNullValue());
    }

    @Test
    void getItemRequestByIdTest() {
        itemRequestService.save(itemRequestDto, user.getId());
        var itemRequest = entityManager.createQuery(
                "SELECT itemRequest " +
                        "FROM ItemRequest itemRequest",
                ItemRequest.class).getSingleResult();
        var itemRequestById = itemRequestService.getItemRequestById(itemRequest.getId(), user.getId());
        assertThat(itemRequestById.getRequesterId(), equalTo(itemRequest.getRequester().getId()));
        assertThat(itemRequestById.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(itemRequestById.getId(), equalTo(itemRequest.getId()));
    }

    @Test
    void getAllItemRequestsTest() {
        saveItemRequests();
        var allItemRequests = itemRequestService.getAllItemRequests(user.getId());
        var itemRequests = entityManager.createQuery(
                        "SELECT itemRequest " +
                                "FROM ItemRequest itemRequest " +
                                "WHERE itemRequest.requester.id = :id " +
                                "ORDER BY itemRequest.created DESC ",
                        ItemRequest.class)
                .setParameter("id", user.getId())
                .getResultList();
        assertThat(allItemRequests.get(0).getId(), equalTo(itemRequests.get(0).getId()));
        assertThat(allItemRequests.size(), equalTo(itemRequests.size()));
    }

    @Test
    void getAllItemRequestsByOwnerTest() {
        saveItemRequests();
        var allItemRequests = itemRequestService.getAllItemRequests(null, null, user.getId());
        var itemRequests = entityManager.createQuery(
                        "SELECT itemRequest " +
                                "FROM ItemRequest itemRequest " +
                                "WHERE itemRequest.requester.id <> :id " +
                                "ORDER BY itemRequest.created DESC ",
                        ItemRequest.class)
                .setParameter("id", user.getId())
                .getResultList();
        assertThat(allItemRequests.size(), equalTo(itemRequests.size()));
        assertThat(allItemRequests.size(), equalTo(0));
    }

    @Test
    void getAllItemRequests2Test() {
        saveItemRequests();
        var userDto = userService.save(
                new UserDto(
                        null,
                        "Clara",
                        "clara@mail.ru")
        );
        var allItemRequests = itemRequestService.getAllItemRequests(null, null, userDto.getId());
        var itemRequests = entityManager.createQuery(
                        "SELECT itemRequest " +
                                "FROM ItemRequest itemRequest " +
                                "WHERE itemRequest.requester.id <> :id " +
                                "ORDER BY itemRequest.created DESC ",
                        ItemRequest.class)
                .setParameter("id", userDto.getId())
                .getResultList();
        assertThat(allItemRequests.get(0).getId(), equalTo(itemRequests.get(0).getId()));
        assertThat(allItemRequests.size(), equalTo(itemRequests.size()));
    }
}
