package com.anchor.api.data.transfer.sep10;

import com.anchor.api.util.Emoji;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.logging.Logger;

/*
    Our simple static class that demonstrates how to create and decode JWTs.
 */
@Service
public class JWTokenService {
    public JWTokenService() {
        LOGGER.info(Emoji.KEY + Emoji.KEY + "JWToken Service constructed "
        .concat(Emoji.PIG));
    }

    @Value("${jwtKey}")
    private String jwtKey;

    public static final Logger LOGGER = Logger.getLogger(JWTokenService.class.getSimpleName());
    public static final String em = "\uD83D\uDD11 \uD83D\uDD11 ";

    public  String createJWToken(String id, String issuer, String subject, long ttlMillis) {

        LOGGER.info(em +" The JWT signature algorithm we will be using to sign the token");
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        LOGGER.info(em +" We will sign our JWT with  \uD83C\uDF4E SignatureAlgorithm.HS256 and our ApiKey secret");
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        LOGGER.info(em +" Let's set the JWT Claims ... \uD83D\uDC3C issuer, date, subject ...");
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signatureAlgorithm, signingKey);

        LOGGER.info(em +" if it has been specified,  \uD83C\uDF4E add the expiration constraint");
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        LOGGER.info(em +" Building the JWT and serializing it to a compact, URL-safe string");
        String token = builder.compact();
        String em2 = Emoji.LEAF + Emoji.LEAF + Emoji.LEAF;
        LOGGER.info(em + em2 + "JWTokenService: Token generated: ".concat(em).concat(token));
        return token;
    }

    public  Claims decodeJWT(String jwt) {

        //This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(jwtKey))
                .parseClaimsJws(jwt).getBody();
        return claims;
    }


}
