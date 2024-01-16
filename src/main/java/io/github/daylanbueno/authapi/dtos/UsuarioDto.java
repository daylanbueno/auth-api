package io.github.daylanbueno.authapi.dtos;

public record UsuarioDto(
        String nome,
        String login,
        String senha
) {
}
