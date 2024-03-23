package io.github.daylanbueno.authapi.services.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import io.github.daylanbueno.authapi.dtos.AuthDto;
import io.github.daylanbueno.authapi.dtos.TokenResponseDto;
import io.github.daylanbueno.authapi.models.Usuario;
import io.github.daylanbueno.authapi.respositories.UsuarioRepository;
import io.github.daylanbueno.authapi.services.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class AutenticacaoServiceImpl implements AutenticacaoService {

    private String  secret = "my-secret";
    private Integer expirationAcessToken = 1;
    private Integer expirarionRefreshToken = 8;

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
                .token(buildToken(usuario, expirationAcessToken))
                .refreshToken(buildToken(usuario, expirarionRefreshToken))
                .build();
    }

    public  String buildToken(Usuario usuario, Integer expiration) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("my-secret");
            
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
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    private Instant geraDataExpiracao(Integer expiration) {
        return LocalDateTime.now()
                .plusHours(expiration)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}
