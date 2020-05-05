package com.sotomaque.ppmtool.security;

import com.sotomaque.ppmtool.domain.User;
import com.sotomaque.ppmtool.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static com.sotomaque.ppmtool.security.SecurityConstants.HEADER_STRING;
import static com.sotomaque.ppmtool.security.SecurityConstants.TOKEN_PREFIX;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        try {
            // get token from request
            String jwt = getJWTFromRequest(httpServletRequest);

            // ensure token has text and is valid (no exceptions come up)
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // if it does, get userID + pass it into service to load the user
                Long userId = tokenProvider.getUserIdFromJWT(jwt);
                User userDetails = customUserDetailsService.loadUserById(userId);

                // Define our Authentication method
                // NOTE: not passing any credentials because we are using a token instead
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, Collections.emptyList()
                );

                // pass request into Auth Method as Details
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                // Set SpringBoot SecurityContext with our custom Authentication Method
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception ex) {
            logger.error("Could not set user authentication in Security Context", ex);
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }


    private String getJWTFromRequest(HttpServletRequest request) {
        // get token from request header
        String bearerToken = request.getHeader(HEADER_STRING);

        // validate token format
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX))  {
            // if everything goes well, return the actual token not including the prefix / additional space
            return bearerToken.substring(7, bearerToken.length());
        }

        // otherwise return null token
        return null; // authentication should fail at this point
    }

}
