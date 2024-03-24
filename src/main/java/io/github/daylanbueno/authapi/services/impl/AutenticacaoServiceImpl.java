package io.github.daylanbueno.authapi.services.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import io.github.daylanbueno.authapi.dtos.AuthDto;
import io.github.daylanbueno.authapi.dtos.TokenResponseDto;
import io.github.daylanbueno.authapi.infra.exceptions.BusinessException;
import io.github.daylanbueno.authapi.infra.exceptions.UnauthorizedException;
import io.github.daylanbueno.authapi.models.Usuario;
import io.github.daylanbueno.authapi.respositories.UsuarioRepository;
import io.github.daylanbueno.authapi.services.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class AutenticacaoServiceImpl implements AutenticacaoService {

    @Value("${auth.jwt.token.secret}")
    private String secretKey;

    @Value("${auth.jwt.token.expiration}")
    private Integer horaExpiracaoToken;

    @Value("${auth.jwt.refresh-token.expiration}")
    private Integer horaExpiracaoRefreshToken ;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return usuarioRepository.findByLogin(login);
    }

    @Override
    public TokenResponseDto obterToken(AuthDto authDto) {
        Usuario usuario = usuarioRepository.findByLogin(authDto.login());

        return TokenResponseDto
                .builder()
                .token(geraTokenJwt(usuario,horaExpiracaoToken))
                .refreshToken(geraTokenJwt(usuario,horaExpiracaoRefreshToken))
                .build();
    }

    public  String geraTokenJwt(Usuario usuario, Integer expiration) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            
            return JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(usuario.getLogin())
                    .withExpiresAt(geraDataExpiracao(expiration))
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao tentar gerar o token! " +exception.getMessage());
        }
    }

    public String validaTokenJwt(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);

            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    @Override
    public TokenResponseDto obterRefreshToken(String refreshToken) {

        String login = validaTokenJwt(refreshToken);
        Usuario usuario = usuarioRepository.findByLogin(login);

        if (usuario == null) {
            throw new UnauthorizedException("UnauthorizedException");
        }

        var autentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(autentication);

        return TokenResponseDto
                .builder()
                .token(geraTokenJwt(usuario,horaExpiracaoToken))
                .refreshToken(geraTokenJwt(usuario,horaExpiracaoRefreshToken))
                .build();
    }

    private Instant geraDataExpiracao(Integer expiration) {
        return LocalDateTime.now()
                .plusHours(expiration)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}
