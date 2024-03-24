package io.github.daylanbueno.authapi.controllers;

import io.github.daylanbueno.authapi.dtos.AuthDto;
import io.github.daylanbueno.authapi.dtos.RequestRefreshDto;
import io.github.daylanbueno.authapi.dtos.TokenResponseDto;
import io.github.daylanbueno.authapi.services.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AutenticacaoService autenticacaoService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public TokenResponseDto auth(@RequestBody AuthDto authDto) {

        var usuarioAutenticationToken = new UsernamePasswordAuthenticationToken(authDto.login(), authDto.senha());

        authenticationManager.authenticate(usuarioAutenticationToken);

        return autenticacaoService.obterToken(authDto);
    }

    @PostMapping("/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponseDto authRefreshToken(@RequestBody RequestRefreshDto requestRefreshDto) {
        return autenticacaoService.obterRefreshToken(requestRefreshDto.refreshToken());
    }
}
