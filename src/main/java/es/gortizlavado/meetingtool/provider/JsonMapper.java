package es.gortizlavado.meetingtool.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class JsonMapper {

    private final ObjectMapper objectMapper;

    public JsonMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.objectMapper = mapper;
    }

    public <T> T fetchDataByFile(Path path, Class<T> tClass) {
        byte[] dataByte;
        try {
            dataByte = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            return objectMapper.readValue(dataByte, tClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] writeBytes(Object data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
