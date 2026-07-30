package com.example.dentistapp.security;


import com.example.dentistapp.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;


@Service
public class JwtService {



    @Value("${jwt.secret}")
    private String secret;


    @Value("${jwt.expiration}")
    private long expiration;



    private SecretKey getSigningKey(){

        return Keys.hmacShaKeyFor(
                secret.getBytes()
        );

    }

    public String generateToken(User user){


        return Jwts.builder()

                .subject(user.getEmail())

                .claim(
                        "role",
                        user.getRole().name()
                )

                .issuedAt(new Date())

                .expiration(
                        new Date(
                          System.currentTimeMillis()
                          + expiration
                        )
                )

                .signWith(getSigningKey())

                .compact();

    }

    public String extractUsername(String token){


        return extractClaims(token)
                .getSubject();

    }

    public String extractRole(String token){


        return extractClaims(token)
                .get("role", String.class);

    }

    public boolean isTokenValid(
            String token,
            String username
    ){

        return username.equals(
                extractUsername(token)
        )
        &&
        !extractClaims(token)
                .getExpiration()
                .before(new Date());

    }

    private Claims extractClaims(String token){


        return Jwts.parser()

                .verifyWith(getSigningKey())

                .build()

                .parseSignedClaims(token)

                .getPayload();

    }


}