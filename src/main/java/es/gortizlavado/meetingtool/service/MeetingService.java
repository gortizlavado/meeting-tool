package es.gortizlavado.meetingtool.service;

import es.gortizlavado.meetingtool.model.Meeting;
import es.gortizlavado.meetingtool.model.Person;
import es.gortizlavado.meetingtool.provider.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingService {


    public static final String BASE_PATH = "meetings/";

    private final JsonMapper jsonMapper;
    private final MemberService memberService;

    public UUID create(Meeting request) {
        Person owner = request.getOwner();
        Meeting newMeeting = new Meeting(request.getName(), owner, request.getDescription(), request.getStartDate(), request.getEndDate());
        UUID id = newMeeting.getId();
        saveDataToFileName(newMeeting, StandardOpenOption.CREATE);
        memberService.saveMember(owner, id);

        return id;
    }

    public void delete(UUID meetingId, Person request) {
        Meeting meeting = fetchMeetingDataByFile(meetingId);
        if (meeting.isOwner(request)) {
            try {
                Files.delete(createPathBy(meetingId));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            memberService.deleteMember(meetingId, meeting.getPeople());
        }
    }

    public Meeting joinPerson(UUID meetingId, Person request) {
        //TODO check if person has other meeting at that time

        Meeting meeting = fetchMeetingDataByFile(meetingId);
        meeting.addPerson(request);
        saveDataToFileName(meeting, StandardOpenOption.TRUNCATE_EXISTING);
        memberService.saveMember(request, meetingId);

        return meeting;
    }

    public Meeting removePerson(UUID meetingId, Person request) {
        Meeting meeting = fetchMeetingDataByFile(meetingId);
        if (meeting.removePerson(request)) {
            saveDataToFileName(meeting, StandardOpenOption.TRUNCATE_EXISTING);
            memberService.deleteMember(meetingId, Set.of(request));
        }

        return meeting;
    }

    public Meeting getMeeting(UUID meetingId) {
        Meeting meeting;
        try {
            meeting = fetchMeetingDataByFile(meetingId);
        } catch (Exception e) {
            meeting = new Meeting();
        }

        return meeting;
    }

    public List<Meeting> getMeetings() {
        log.info("get meetings request");
        List<Meeting> meetings = new ArrayList<>();
        try(Stream<Path> files = Files.list(Path.of(BASE_PATH)).filter(Predicate.not(Files::isDirectory))) {
            files.forEach(nameFile -> {
                Meeting meeting = fetchMeetingDataByFile(nameFile);
                meetings.add(meeting);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("result - size: {}", meetings.size());
        return meetings;
    }

    private void saveDataToFileName(Meeting meetingData, StandardOpenOption option) {
        UUID id = meetingData.getId();
        try {
            Files.write(createPathBy(id), jsonMapper.writeBytes(meetingData), option);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Meeting fetchMeetingDataByFile(UUID meetingId) {
        return jsonMapper.fetchDataByFile(createPathBy(meetingId), Meeting.class);
    }

    private Meeting fetchMeetingDataByFile(Path nameFile) {
        UUID uuid = UUID.fromString(String.valueOf(nameFile).replace(BASE_PATH, ""));
        return fetchMeetingDataByFile(uuid);
    }

    private Path createPathBy(UUID meetingId) {
        return Path.of(BASE_PATH + meetingId);
    }

}
