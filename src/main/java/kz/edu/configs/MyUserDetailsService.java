package kz.edu.configs;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kz.edu.model.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kz.edu.dao.UserDAO;
import kz.edu.model.Role;

@Service("userDetailsService")
public class MyUserDetailsService implements UserDetailsService {
    // Get user from the database, via Hibernate
    private UserDAO userDAO;
    @Autowired
    public void setUserDAO(UserDAO userDAO) {this.userDAO = userDAO;}

    @Override
    @Transactional(readOnly=true)
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername:"+username);
        kz.edu.model.User user = userDAO.findByUserName(username);
        System.out.println("role   : "+user.getRole().getName());
        Set<Authority> authoritySet = user.getRole().getAuthorities();
        for(Authority authority: authoritySet) {
            System.out.println("authority   : "+authority.getName());
        }
        System.out.println("email  : "+user.getEmail());
        Role role = user.getRole();
        Set<Authority> authorities = role.getAuthorities();
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        for (Authority authority : authorities) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority.getName());
            grantedAuthorities.add( grantedAuthority );
        }

        GrantedAuthority roleAuthority = new SimpleGrantedAuthority( role.getName() );
        grantedAuthorities.add( roleAuthority );

        return buildUserForAuthentication(user, grantedAuthorities);
    }

    // Converts kz.edu.model.User user to
    // org.springframework.security.core.userdetails.User
    private User buildUserForAuthentication(kz.edu.model.User user, List<GrantedAuthority> authorities) {
        return new User(user.getEmail(), user.getPassword(),true, true, true, true, authorities);
    }
}