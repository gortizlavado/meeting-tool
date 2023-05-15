package es.gortizlavado.meetingtool.controller;

import es.gortizlavado.meetingtool.model.Meeting;
import es.gortizlavado.meetingtool.model.Person;
import es.gortizlavado.meetingtool.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/meeting")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping
    public List<Meeting> getMeetings() {
        return meetingService.getMeetings();
    }

    @PostMapping
    public String create(Meeting request) {
        return meetingService.create(request).toString();
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable("id") UUID meetingId, Person request) {
        meetingService.delete(meetingId, request);
    }

    @PatchMapping(path = "/{id}/add")
    public Meeting joinPerson(@PathVariable("id") UUID meetingId, Person request) {
        return meetingService.joinPerson(meetingId, request);
    }

    @PatchMapping(path = "/{id}/remove")
    public Meeting removePerson(@PathVariable("id") UUID meetingId, Person request) {
        return meetingService.removePerson(meetingId, request);
    }

}
