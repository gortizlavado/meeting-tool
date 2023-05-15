package es.gortizlavado.meetingtool.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
public class Person {
    private String name;
    private List<UUID> meetings;

    public Person() {
    }

    public Person(String name) {
        this.name = name;
        this.meetings = new ArrayList<>();
    }

    public Person(String name, List<UUID> meetings) {
        this.name = name;
        this.meetings = meetings;
    }

    public void addMeeting(UUID meetingId) {
        this.meetings.add(meetingId);
    }

    public void removeMeeting(UUID meetingId) {
        this.meetings.remove(meetingId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
