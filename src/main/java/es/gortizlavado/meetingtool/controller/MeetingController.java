package es.gortizlavado.meetingtool.controller;

import es.gortizlavado.meetingtool.model.Meeting;
import es.gortizlavado.meetingtool.model.Person;
import es.gortizlavado.meetingtool.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequestMapping(path = "/api/meeting")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping
    public String getMeetings(Model model) {
        List<Meeting> meetings = meetingService.getMeetings();
        if (!meetings.isEmpty()) {
            model.addAttribute(meetings);
        }
        return "meetingList";
    }

    @PostMapping
    public String create(Meeting request) {
        UUID uuid = meetingService.create(request);
        return "redirect:/api/meeting/" + uuid;
    }

    @GetMapping(path = "/{id}")
    public String getMeeting(@PathVariable("id") UUID meetingId, Model model) {
        Meeting meeting = meetingService.getMeeting(meetingId);
        if (Objects.nonNull(meeting)) {
            model.addAttribute(meeting);
        }
        return "meeting";
    }

    @DeleteMapping(path = "/{id}")
    public String delete(@PathVariable("id") UUID meetingId, Person request) {
        meetingService.delete(meetingId, request);
        return "meeting";
    }

    @PatchMapping(path = "/{id}/add")
    public String joinPerson(@PathVariable("id") UUID meetingId, Person request) {
        Person person = new Person(request.getName());
        meetingService.joinPerson(meetingId, person);
        return "redirect:/api/meeting/" + meetingId;
    }

    @DeleteMapping(path = "/{id}/remove")
    public String removePerson(@PathVariable("id") UUID meetingId, Person request) {
        meetingService.removePerson(meetingId, request);
        return "redirect:/api/meeting/" + meetingId;
    }

}
