package com.gestionalmacen.config;

import com.gestionalmacen.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (!request.getURI().getPath().startsWith("/api/") || body == null || body instanceof ApiResponse<?>) {
            return body;
        }
        if (response instanceof org.springframework.http.server.ServletServerHttpResponse servletResponse
                && servletResponse.getServletResponse().getStatus() >= 400) {
            return body;
        }
        int status = response instanceof org.springframework.http.server.ServletServerHttpResponse servletResponse
            ? servletResponse.getServletResponse().getStatus() : 200;
        String message = status == 201 ? "Registro creado correctamente." : "Operación completada correctamente.";
        return ApiResponse.success(message, body);
    }
}
