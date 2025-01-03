package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b where b.item = ?1 and b.start <= ?2 and b.end >= ?3")
    List<Booking> findCrossedBookingsByItem(Item item, LocalDateTime requestEnd, LocalDateTime requestStart);

    List<Booking> findBookingsByBookerIdAndItemIdAndStatusAndEndIsBefore(long bookerId, long itemId, Status status, LocalDateTime now);

    //List<Booking> findAllBookingsByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findAllBookingsByBookerId(long bookerId, Sort sort);//через sort правильно сделала реализацию?

    @Query("select b from Booking as b where b.booker.id = ?1 and CURRENT_TIMESTAMP between b.start and b.end order by b.start desc")
    List<Booking> findCurrentBookingsByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findBookingsByBookerIdAndStartIsAfterOrderByStartDesc(long bookerId, LocalDateTime now);

    List<Booking> findBookingsByBookerIdAndEndIsBeforeOrderByStartDesc(long bookerId, LocalDateTime now);

    List<Booking> findAllBookingsByBookerIdAndStatusOrderByStartDesc(long bookerId, Status status);

    List<Booking> findAllBookingsByItemOwnerId(long ownerId);

    @Query("select b from Booking as b where b.item.owner.id = ?1 and CURRENT_TIMESTAMP between b.start and b.end")
    List<Booking> findCurrentBookingsByOwnerId(long owner);

    List<Booking> findBookingsByItemOwnerIdAndStartIsAfter(long owner, LocalDateTime now);

    List<Booking> findBookingsByItemOwnerIdAndEndIsBefore(long owner, LocalDateTime now);

    List<Booking> findAllBookingsByItemOwnerIdAndStatus(long owner, Status status);

}
