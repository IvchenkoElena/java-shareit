package ru.practicum.shareit.request.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;


/**
 * TODO Sprint add-item-requests.
 */
@Entity
@Table(name = "requests")
@Data
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User requestor;

    //private Instant created = Instant.now();
}
