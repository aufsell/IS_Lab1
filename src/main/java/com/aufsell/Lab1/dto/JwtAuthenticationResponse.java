package com.aufsell.Lab1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с токеном")
public class JwtAuthenticationResponse {
    @Schema(description = "Токен доступа", example = "eyJzdWIiOiJhZG1pbiIs.JhZG1pbiIsImV4cCIiIsImV4cCI6MTYyMjUwNj...")
    private String token;
}
