package es.gortizlavado.meetingtool.service;

import es.gortizlavado.meetingtool.model.Meeting;
import es.gortizlavado.meetingtool.model.Person;
import es.gortizlavado.meetingtool.provider.JsonMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

class MeetingServiceTest {

    private final JsonMapper jsonMapper = new JsonMapper();
    private final MeetingService service = new MeetingService(jsonMapper, new MemberService(jsonMapper));

    private Meeting meeting;
    private Person owner;

    @BeforeEach
    void setUp() {
        owner = new Person("thomas");
        meeting = new Meeting("name", owner, "description", LocalDateTime.of(2023, 1, 1, 9, 30), LocalDateTime.of(2023, 1, 2, 11, 0));
    }

    @AfterAll
    static void afterAll() {

    }

    @Test
    void should_createMeeting_and_delete() throws IOException {
        UUID uuid = service.create(this.meeting);
        System.out.println("ID --> " + uuid);
        Path basePath = Path.of(MeetingService.BASE_PATH);
        Stream<Path> checkOne = Files.list(basePath).filter(Predicate.not(Files::isDirectory));
        Assertions.assertEquals(1L, checkOne.count());

        service.delete(uuid, new Person("alfred"));
        Stream<Path> checkTwo = Files.list(basePath).filter(Predicate.not(Files::isDirectory));
        Assertions.assertEquals(1L, checkTwo.count());

        service.delete(uuid, owner);
        Stream<Path> checkThree = Files.list(basePath).filter(Predicate.not(Files::isDirectory));
        Assertions.assertEquals(0L, checkThree.count());
    }

    @Test
    void should_createMeeting_and_joinPerson() {
        UUID uuid = service.create(this.meeting);
        System.out.println("ID --> " + uuid);

        service.joinPerson(uuid, new Person("alfred"));
        Path alfredPath = Path.of(MemberService.BASE_PATH + "alfred");
        Person alfredCheckOne = jsonMapper.fetchDataByFile(alfredPath, Person.class);
        Assertions.assertTrue(alfredCheckOne.getMeetings().size() > 0);
        Assertions.assertTrue(alfredCheckOne.getMeetings().contains(uuid));

        service.removePerson(uuid, new Person("alfred"));
        Person alfredCheckTwo = jsonMapper.fetchDataByFile(alfredPath, Person.class);
        Assertions.assertFalse(alfredCheckTwo.getMeetings().contains(uuid));
    }

    @Test
    void should_getAllMeeting() {
        List<Meeting> meetings = service.getMeetings();
        System.out.println("numbers --> " + meetings.size());
    }

}