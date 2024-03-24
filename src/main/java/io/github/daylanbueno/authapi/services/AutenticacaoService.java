package io.github.daylanbueno.authapi.services;

import io.github.daylanbueno.authapi.dtos.AuthDto;
import io.github.daylanbueno.authapi.dtos.TokenResponseDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AutenticacaoService extends UserDetailsService {
    public TokenResponseDto obterToken(AuthDto authDto);
    public String validaTokenJwt(String token);

    TokenResponseDto obterRefreshToken(String s);
}
