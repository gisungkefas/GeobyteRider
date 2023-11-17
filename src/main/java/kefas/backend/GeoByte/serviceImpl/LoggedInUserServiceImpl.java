package kefas.backend.GeoByte.serviceImpl;

import kefas.backend.GeoByte.entity.Users;
import kefas.backend.GeoByte.repository.UserRepository;
import kefas.backend.GeoByte.service.LoggedInUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoggedInUserServiceImpl implements LoggedInUserService {
    private final UserRepository userRepository;
    @Override
    public Users fetchLoggedInUser() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return  userRepository.findByEmail(user.getUsername()).get();

    }
}
