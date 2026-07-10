package com.prenotazioni.security;

import com.prenotazioni.po.AccountPo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private Long jwtExpirationMs;

    public String generaToken(AccountPo accountPo) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("idAccount", accountPo.getIdAccount());
        claims.put("ruolo", accountPo.getRuoloAccount().name());

        if (accountPo.getUtentePo() != null) {
            claims.put("idUtente", accountPo.getUtentePo().getIdUtente());
        }

        if (accountPo.getCollaboratorePo() != null) {
            claims.put("idCollaboratore", accountPo.getCollaboratorePo().getIdCollaboratore());
        }

        Date dataCreazione = new Date();
        Date dataScadenza = new Date(dataCreazione.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(accountPo.getEmailAccount())
                .setIssuedAt(dataCreazione)
                .setExpiration(dataScadenza)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String estraiEmail(String token) {
        return estraiClaims(token).getSubject();
    }

    public boolean tokenValido(String token, String emailAccount) {

        String emailToken = estraiEmail(token);

        return emailToken.equals(emailAccount) && !tokenScaduto(token);
    }

    private boolean tokenScaduto(String token) {
        return estraiClaims(token).getExpiration().before(new Date());
    }

    private Claims estraiClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}