package fr.ecp.sio.appenginedemo.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SignatureException;
import java.util.Base64;

/**
 * Created by Michaël on 02/11/2015.
 */
public class TokenUtils {

    private static final Key KEY;
    static {
        byte[] key = Base64.getDecoder().decode("BYWphxmLrblUgy6LzHdUfSActQw2y9SX");
        KEY = new SecretKeySpec(key, 0, key.length, "AES");
    }

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