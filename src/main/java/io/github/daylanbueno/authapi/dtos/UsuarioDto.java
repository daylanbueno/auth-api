package io.github.daylanbueno.authapi.dtos;

import io.github.daylanbueno.authapi.enums.RoleEnum;

public record UsuarioDto(
        String nome,
        String login,
        String senha,
        RoleEnum role
) {
}
