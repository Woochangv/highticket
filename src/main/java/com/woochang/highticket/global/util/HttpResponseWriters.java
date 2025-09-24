package com.woochang.highticket.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.StandardCharset;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;

import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpResponseWriters {

    public static void writeJson(HttpServletResponse response, int status,
                                 Object body, ObjectMapper objectMapper) throws IOException {

        if (response.isCommitted()) return;

        response.setStatus(status);
        response.setCharacterEncoding(StandardCharset.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), body);
        response.getWriter().flush();

    }
}
