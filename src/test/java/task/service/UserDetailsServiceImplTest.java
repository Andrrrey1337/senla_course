package task.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import task.dao.UserDao;
import task.model.Role;
import task.model.User;

import java.util.Set;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void loadUserByUsername_Success() {
        String username = "admin";
        String password = "password";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword(password);
        user.setRoles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER));

        when(userDao.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetails result = userDetailsService.loadUserByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());
        assertEquals(2, result.getAuthorities().size());

        verify(userDao, times(1)).findByUsername(username);
    }

    @Test
    public void loadUserByUsername_UserNotFound() {
        String username = "unknownUser";

        when(userDao.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));

        verify(userDao, times(1)).findByUsername(username);
    }
}