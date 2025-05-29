package com.converter;

import com.entity.notify.NotificationAction;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;

@Converter
public class NotificationActionConverter implements AttributeConverter<List<NotificationAction>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<NotificationAction> actions) {
        try {
            return objectMapper.writeValueAsString(actions);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi convert actions thành JSON", e);
        }
    }

    @Override
    public List<NotificationAction> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi convert JSON thành actions", e);
        }
    }
}
