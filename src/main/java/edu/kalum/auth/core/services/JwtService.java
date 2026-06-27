package edu.kalum.auth.core.services;

import edu.kalum.auth.core.helpers.shared.Utils;
import io.jsonwebtoken.SignatureAlgorithm;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
@Service
public class JwtService {
    @Autowired
    private Utils utils;

    private static final String SECRET_KEY = "MI_LLAVE_SECRETA_PARA_GENERAR_TOKEN";
    private static final long EXPIRATION_MS = 1000 * 60 * 60;
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public JsonObject generateToken(JsonObject user) {
        Date expiration = new Date(System.currentTimeMillis() + EXPIRATION_MS);
        String token = Jwts.builder()
                .setSubject(user.getString("username"))
                .claim("user", user.getString("username"))
                .claim("email", user.getString("email"))
                .claim("identityUser", user.getString("applicationNumber"))
                .claim("role",user.getString("roles"))
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        return new JsonObject().put("token",token).put("expiration", utils.formatToIsoUTC(expiration));
    }
}
