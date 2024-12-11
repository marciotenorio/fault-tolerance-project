package com.fidelity.bonus;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Márcio Tenório
 * @since 10/12/2024
 */

@Component
public class Filter extends OncePerRequestFilter {

    private final ErrorSetterRepository repository;

    public Filter(ErrorSetterRepository repository) {
        this.repository = repository;
    }

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        double percent = ThreadLocalRandom.current().nextDouble();
       ErrorSetter errorSetter = repository.findById(1)
                .orElseThrow();

       if(errorSetter.isDelayed()) {
           var targetTime = errorSetter.getStart().plusSeconds(30);

           if(targetTime.isBefore(LocalDateTime.now())) {
               errorSetter.setDelayed(false);
               errorSetter.setStart(null);
               repository.save(errorSetter);
           }
       }
       else if(percent <= 0.1) {
               var error = new ErrorSetter(1, true, LocalDateTime.now());
               repository.save(error);
           }

        filterChain.doFilter(request, response);
    }
}
