package io.github.daylanbueno.authapi.services;

import io.github.daylanbueno.authapi.dtos.AuthDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AutenticacaoService extends UserDetailsService {
    public String obterToken(AuthDto authDto);
}
