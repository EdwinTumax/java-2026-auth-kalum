package edu.kalum.auth.core.services;


import edu.kalum.logging.core.helpers.Utils;
import io.jsonwebtoken.SignatureAlgorithm;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
@Service
@Data
public class JwtService {
    @Autowired
    private Utils utils;
    private String secretKey;
    private static final long EXPIRATION_MS = 1000 * 60 * 60;

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
                .signWith(Keys.hmacShaKeyFor(this.secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
        return new JsonObject().put("token",token).put("expiration", utils.getDateWithFormat(expiration));
    }
}
