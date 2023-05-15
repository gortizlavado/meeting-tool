package es.gortizlavado.meetingtool.service;

import es.gortizlavado.meetingtool.model.Person;
import es.gortizlavado.meetingtool.provider.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    public static final String BASE_PATH = MeetingService.BASE_PATH + "member/";

    private final JsonMapper jsonMapper;

    public void saveMember(Person member, UUID id) {
        String name = member.getName();
        if (Files.exists(createPathBy(name))) {
            member = fetchMemberDataByFile(name);
        }
        member.addMeeting(id);
        saveDataToFileName(member, StandardOpenOption.CREATE);
    }

    public void deleteMember(UUID id, Set<Person> people) {
        people.forEach(person -> {
            Person member = fetchMemberDataByFile(person.getName());
            member.removeMeeting(id);
            saveDataToFileName(member, StandardOpenOption.TRUNCATE_EXISTING);
        });
    }

    private void saveDataToFileName(Person memberData, StandardOpenOption option) {
        String name = memberData.getName();
        try {
            Files.write(Path.of(BASE_PATH + name), jsonMapper.writeBytes(memberData), option);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Person fetchMemberDataByFile(String name) {
        Path path = createPathBy(name);
        return jsonMapper.fetchDataByFile(path, Person.class);
    }

    private Path createPathBy(String name) {
        return Path.of(BASE_PATH + name);
    }

}
