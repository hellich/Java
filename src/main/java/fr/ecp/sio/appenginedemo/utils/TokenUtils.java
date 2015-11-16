package fr.ecp.sio.appenginedemo.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import java.security.Key;
import java.security.SignatureException;

/**
 * Created by Michaël on 02/11/2015.
 */
public class TokenUtils {

    private static final Key KEY = MacProvider.generateKey();

    public static String generateToken(long userId) {
        return Jwts.builder()
                .setId(Long.toString(userId))
                .signWith(SignatureAlgorithm.HS512, KEY)
                .compact();
    }

    public static long parseToken(String token) throws SignatureException {
        return Long.parseLong(Jwts.parser()
                .setSigningKey(KEY)
                .parseClaimsJws(token)
                .getBody()
                .getId());
    }

}