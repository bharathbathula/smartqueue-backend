package com.smartqueue.security;

import com.smartqueue.model.Staff;
import com.smartqueue.model.User;
import com.smartqueue.repository.StaffRepository;
import com.smartqueue.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Try finding as User first
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return UserDetailsImpl.build(
                    user.getId(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getRole().name()
            );
        }

        // Try finding as Staff
        Optional<Staff> staffOptional = staffRepository.findByEmail(email);
        if (staffOptional.isPresent()) {
            Staff staff = staffOptional.get();
            return UserDetailsImpl.build(
                    staff.getId(),
                    staff.getEmail(),
                    staff.getPassword(),
                    staff.getRole().name()
            );
        }

        throw new UsernameNotFoundException("User Not Found with email: " + email);
    }
}
