package share.file.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import share.file.utils.PropertyUtil;

import java.security.Key;

public class JwtService {
    private String jwtSigningKey;

    public JwtService() {
        PropertyUtil propertyUtil = new PropertyUtil();

        Properties properties = propertyUtil.loadProperties();

        this.jwtSigningKey = properties.getProperty("jwt.secret");
    }

    public String extractUserName(String token) {
        return this.extractClaim(token, Claims::getSubject);
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        
        claims.put("username", username);

        return generateToken(username, claims);
    }

    public boolean isTokenValid(String token) {
        final String userName = this.extractUserName(token);
        
        return (!userName.equals("")) && !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String generateToken(String username, Map<String, Object> extraClaims) {
        return Jwts.builder().setClaims(extraClaims).setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 100000 * 60 * 24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
