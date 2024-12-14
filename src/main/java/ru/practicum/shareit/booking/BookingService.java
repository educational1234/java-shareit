package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingDto addBooking(BookingDto bookingDto, Long userId) {
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));


        if (!item.isAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Владелец не может бронировать собственную вещь");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now()) || bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Даты бронирования не могут быть в прошлом");
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException("Дата начала бронирования не может быть после даты окончания");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDto updateBookingStatus(Long bookingId, boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Вы не являетесь владельцем вещи");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус бронирования уже изменён");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Нет прав на просмотр этого бронирования");
        }

        return BookingMapper.toBookingDto(booking);
    }

    public List<BookingDto> getUserBookings(Long userId, BookingState state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllBookingsByBookerId(userId);
            case PAST -> bookingRepository.findPastBookingsByBookerId(userId, LocalDateTime.now());
            case CURRENT -> bookingRepository.findCurrentBookingsByBookerId(userId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findFutureBookingsByBookerId(userId, LocalDateTime.now());
            case WAITING -> bookingRepository.findWaitingBookingsByBookerId(userId);
            case REJECTED -> bookingRepository.findRejectedBookingsByBookerId(userId);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getOwnerBookings(Long ownerId, BookingState state) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllBookingsByOwnerId(ownerId);
            case PAST -> bookingRepository.findPastBookingsByOwnerId(ownerId, LocalDateTime.now());
            case CURRENT -> bookingRepository.findCurrentBookingsByOwnerId(ownerId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findFutureBookingsByOwnerId(ownerId, LocalDateTime.now());
            case WAITING -> bookingRepository.findWaitingBookingsByOwnerId(ownerId);
            case REJECTED -> bookingRepository.findRejectedBookingsByOwnerId(ownerId);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }


}
