package es.gortizlavado.meetingtool.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class Meeting {
    private UUID id;
    private String name;
    private Person owner;
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;
    private Set<Person> people;

    public Meeting() {
    }

    public Meeting(String name, Person owner, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.people = new HashSet<>();
        addPerson(owner);
    }

    public Meeting(UUID id, String name, Person owner, String description, LocalDateTime startDate, LocalDateTime endDate, Set<Person> people) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.people = people;
    }

    public boolean isOwner(Person asked) {
        return asked.equals(owner);
    }

    public void addPerson(Person toAdd) {
        this.people.add(toAdd);
    }

    public boolean removePerson(Person toRemove) {
        if (isOwner(toRemove)) {
            return false;
        }
        this.people.remove(toRemove);
        return true;
    }
}
