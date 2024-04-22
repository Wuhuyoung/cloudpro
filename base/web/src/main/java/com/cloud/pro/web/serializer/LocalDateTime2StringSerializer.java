package com.cloud.pro.web.serializer;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * LocalDateTime转String的JSON序列化器
 * 用于返回实体LocalDateTime类型字段的自动序列化
 */
public class LocalDateTime2StringSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (Objects.isNull(localDateTime)) {
            jsonGenerator.writeString(StringUtils.EMPTY);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            jsonGenerator.writeString(localDateTime.format(formatter));
        }
    }
}