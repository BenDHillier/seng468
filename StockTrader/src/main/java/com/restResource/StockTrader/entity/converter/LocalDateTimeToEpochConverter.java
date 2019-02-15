package com.restResource.StockTrader.entity.converter;

import javax.persistence.AttributeConverter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateTimeToEpochConverter implements AttributeConverter<LocalDateTime, Long> {

    /**
     * Converts LocalDateTime to Epoch time in milliseconds.
     */
    @Override
    public Long convertToDatabaseColumn(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    /**
     * Converts Epoch time in milliseconds to LocalDateTime.
     */
    @Override
    public LocalDateTime convertToEntityAttribute(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault());
    }
}
