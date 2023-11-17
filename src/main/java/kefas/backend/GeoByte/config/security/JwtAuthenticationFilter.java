package kefas.backend.GeoByte.config.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kefas.backend.GeoByte.config.LocalMemStorage;
import lombok.RequiredArgsConstructor;
import kefas.backend.GeoByte.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtUtils;
  private final UserRepository repository;

  private final LocalMemStorage localMemStorage;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      String authHeader = request.getHeader("Authorization");
      String userEmail = null;
      String jwt = null;

      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        jwt = authHeader.substring(7);
        userEmail = jwtUtils.extractUsername(jwt);
      }

      if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = new UserPrincipal(this.repository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found")));

        if (jwtUtils.isTokenValid(jwt, userDetails)) {
          UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                  new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

          usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }

        boolean tokenBlackListed = localMemStorage.isTokenBlackListed(userEmail, jwt);
        if(tokenBlackListed) {
          throw new RuntimeException("Your token has expired");
        }
      }

      filterChain.doFilter(request, response);
    } catch (JwtException ex) {
      ex.printStackTrace();
      filterChain.doFilter(request, response);
    }
  }
}
