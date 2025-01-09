package api.giybat.uz.util;

import api.giybat.uz.dto.JwtDTO;
import api.giybat.uz.enums.ProfileRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class JwtUtil {

    // Tokenning amal qilish muddati (1 kun)
    private static final Duration TOKEN_EXPIRATION = Duration.ofDays(1);

    // JWT uchun maxfiy kalit
    private static final String SECRET_KEY = Base64.getEncoder().encodeToString(
            "veryLongSecretmazgillattayevlasharaaxmojonjinnijonsurbetbekkiydirhonuxlatdibekloxovdangasabekochkozjonduxovmashaynikmaydagapchishularnioqiganbolsangizgapyoqaniqsizmazgi"
                    .getBytes()
    );

    /**
     * ID, username va rollarga asosan JWT token yaratish.
     *
     * @param id       Foydalanuvchi ID
     * @param username Foydalanuvchi nomi
     * @param roleList Foydalanuvchi rollari
     * @return JWT token
     */
    public static String encode(Integer id, String username, List<ProfileRole> roleList) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("roles", roleList.stream()
                .map(Enum::name)
                .collect(Collectors.joining(",")));

        return buildToken(claims, username);
    }

    /**
     * Faqat foydalanuvchi ID asosida JWT token yaratish.
     *
     * @param id Foydalanuvchi ID
     * @return JWT token
     */
    public static String encode(Integer id) {
        return buildToken(Collections.emptyMap(), String.valueOf(id));
    }

    /**
     * JWT tokenni deshifrlash va foydalanuvchi ma'lumotlarini chiqarish.
     *
     * @param token JWT tokeni
     * @return JwtDTO obyekt
     */
    public static JwtDTO decode(String token) {
        try {
            Claims claims = parseToken(token);
            Integer id = claims.get("id", Integer.class);
            String roles = claims.get("roles", String.class);
            String username = claims.getSubject();

            List<ProfileRole> roleList = Arrays.stream(roles.split(","))
                    .map(ProfileRole::valueOf)
                    .collect(Collectors.toList());

            return new JwtDTO(id, username, roleList);
        } catch (JwtException e) {
            throw new RuntimeException("Token noto'g'ri yoki muddati o'tgan", e);
        }
    }

    /**
     * Foydalanuvchi ID ni olish uchun JWT tokenni deshifrlash.
     *
     * @param token JWT tokeni
     * @return Foydalanuvchi ID
     */
    public static Integer decodeRegVerToken(String token) {
        Claims claims = parseToken(token);
        return Integer.parseInt(claims.getSubject());
    }

    /**
     * SecretKey obyektini yaratish.
     *
     * @return SecretKey obyekt
     */
    private static SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Tokenni yaratish uchun yordamchi metod.
     *
     * @param claims  Qo'shimcha talablar
     * @param subject Subyekt (foydalanuvchi username yoki ID)
     * @return JWT token
     */
    private static String buildToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION.toMillis()))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * JWT tokenni deshifrlash va validatsiya qilish.
     *
     * @param token JWT tokeni
     * @return Claims obyekt
     */
    private static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
