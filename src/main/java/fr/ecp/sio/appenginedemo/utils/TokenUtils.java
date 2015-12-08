package fr.ecp.sio.appenginedemo.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SignatureException;

/**
 * Some utils to encrypt and decrypt an API token
 */
public class TokenUtils {

    // Let's create a cryptographic static KEY that never change, even across server restart!
    private static final Key KEY;
    static {
        byte[] key = Base64.decodeBase64("BYWphxmLrblUgy6LzHdUfSActQw2y9SX");
        KEY = new SecretKeySpec(key, 0, key.length, "AES");
    }

    // Create a token from a user id (simply encrypt it)
    public static String generateToken(long userId) {
        // We use a third-party library that creates standard JSON web tokens (JWT)
        // Again, a builder pattern!
        // The id of the user goes into the "id" field which is already defined by the JWT specs
        return Jwts.builder()
                .setId(Long.toString(userId))
                .signWith(SignatureAlgorithm.HS512, KEY)
                .compact();
    }

    // Parse the token, decrypt and return the id
    public static long parseToken(String token) throws SignatureException {
        return Long.parseLong(Jwts.parser()
                .setSigningKey(KEY)
                .parseClaimsJws(token)
                .getBody()
                .getId());
    }

}