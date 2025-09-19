package co.com.pragma.crediya.model.jwt.gateways;

import co.com.pragma.crediya.model.jwt.Jwt;

public interface JwtProviderPort {

    Jwt parseToken(String token);

}
