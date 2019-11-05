package Model;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

public class jjwt {
    private Key key= Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private String databaseID= "Jakob";
    public String createKey(String id){
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return Jwts.builder().setSubject(id).signWith(key).compact();
    }

    public boolean validateKey(String token){
        Jwts.parser().setSigningKey(this.key).parseClaimsJws(token).getBody().getSubject().equals(this.databaseID);
        return true;
    }
}
