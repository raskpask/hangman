package Server.Model;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

public class JavaToken {
    private Key key= Keys.secretKeyFor(SignatureAlgorithm.HS256);
    public String createKey(String id){
        return Jwts.builder().setSubject(id).signWith(this.key).compact();
    }

    public boolean validateKey(String token,String name){
        try {
            Jwts.parser().setSigningKey(this.key).parseClaimsJws(token).getBody().getSubject().equals(name);
            return true;

        } catch (JwtException e) {
            return false;
        }

    }
}
