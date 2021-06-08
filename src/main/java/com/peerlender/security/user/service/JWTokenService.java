package com.peerlender.security.user.service;

import com.google.common.collect.ImmutableMap;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.compression.GzipCompressionCodec;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class JWTokenService implements TokenService{

    private static final String DOT = ".";
    private static final GzipCompressionCodec COMPRESSION_CODEC = new GzipCompressionCodec();
    String issuer;
    int expirationInSec;
    int clockSkewSec;
    String secretKey;

    @Autowired
    public JWTokenService(@Value("${jwt.issuer}") String issuer,
                          @Value("${jwt.expiration-sec}") int expirationInSec,
                          @Value("${jwt.clock-skew-sec}") int clockSkewSec,
                          @Value("${jwt.secret-key}")String secretKey) {
        this.issuer = issuer;
        this.expirationInSec = expirationInSec;
        this.clockSkewSec = clockSkewSec;
        this.secretKey = secretKey;
    }

    @Override
    public String permanent(Map<String, String> attributes) {
        return createNewToken(attributes,0);
    }

    @Override
    public String expiring(Map<String, String> attributes) {
        return createNewToken(attributes, expirationInSec);
    }

    @Override
    public Map<String, String> untrusted(String token) {
        final JwtParser parser = getJwtParser();

        final String noSignature = StringUtils.substringBeforeLast(token,DOT) + DOT;
        return parseClaims(() -> parser.parseClaimsJws(token).getBody());
    }

    @Override
    public Map<String, String> verify(String token) {
        final JwtParser parser = getJwtParser()
                .setSigningKey(secretKey);

        return parseClaims(() -> parser.parseClaimsJws(token).getBody());
    }

    private String createNewToken(final Map<String,String> attributes, final int expireInSec) {
        final LocalDateTime currentTime = LocalDateTime.now();
        final Claims claims = Jwts
                .claims()
                .setIssuer(issuer)
                .setIssuedAt(Date.from(currentTime.toInstant(ZoneOffset.UTC)));
        if (expireInSec > 0) {
            final LocalDateTime expireAt = currentTime.plusSeconds(expireInSec);
            claims.setExpiration(Date.from(expireAt.toInstant(ZoneOffset.UTC)));
        }
        claims.putAll(attributes);
        return Jwts
                .builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compressWith(COMPRESSION_CODEC)
                .compact();
    }

    private static Map<String,String> parseClaims(final Supplier<Claims> toClaims) {
        try {
            final Claims claims = toClaims.get();
            final ImmutableMap.Builder<String,String> builder = ImmutableMap.builder();
            claims.entrySet()
                    .stream()
                    .forEach(e -> builder.put(e.getKey(),String.valueOf(e.getValue())));

            return builder.build();
        }catch (final IllegalArgumentException | JwtException exception) {
            return ImmutableMap.of();
        }
    }

    private JwtParser getJwtParser() {
        return Jwts.parser()
                .requireIssuer(issuer)
                .setClock((Clock) this)  //watch out for this
                .setAllowedClockSkewSeconds(clockSkewSec);
    }
}
