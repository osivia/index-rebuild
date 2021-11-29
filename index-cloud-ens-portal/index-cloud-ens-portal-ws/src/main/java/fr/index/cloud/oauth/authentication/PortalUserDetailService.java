package fr.index.cloud.oauth.authentication;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PortalUserDetailService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(1);
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        if( "admin".equals(username))   {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));            
        }
        
        User user = new User(username, "", authorities);
        
        return user;

    }

}
