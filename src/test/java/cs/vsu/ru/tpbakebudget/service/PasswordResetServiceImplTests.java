package cs.vsu.ru.tpbakebudget.service;

import cs.vsu.ru.tpbakebudget.model.PasswordReset;
import cs.vsu.ru.tpbakebudget.model.Users;
import cs.vsu.ru.tpbakebudget.repository.PasswordResetRepository;
import cs.vsu.ru.tpbakebudget.service.impl.PasswordResetServiceImpl;
import cs.vsu.ru.tpbakebudget.service.impl.UsersServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PasswordResetServiceImplTests {

    @Mock
    private PasswordResetRepository resetRequestRepository;

    @Mock
    private UsersServiceImpl usersService;

    @InjectMocks
    private PasswordResetServiceImpl service;

    private PasswordReset passwordReset;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        passwordReset = new PasswordReset();
        passwordReset.setId(1L);
        passwordReset.setUser(new Users());
        passwordReset.setCreationTime(LocalDateTime.now());
        passwordReset.setToken(UUID.randomUUID().toString());
        passwordReset.setUsed(false);
    }

    @Test
    public void testFindByUserId() {
        when(resetRequestRepository.findByUserId(1L)).thenReturn(passwordReset);

        PasswordReset foundResetRequest = service.findByUserId(1L);

        assertNotNull(foundResetRequest);
        assertEquals(passwordReset.getId(), foundResetRequest.getId());
        verify(resetRequestRepository, times(1)).findByUserId(1L);
    }

    @Test
    public void testCreatePasswordResetRequest() {
        when(usersService.findById(1L)).thenReturn(null);
        when(resetRequestRepository.save(any())).thenReturn(passwordReset);

        PasswordReset createdResetRequest = service.createPasswordResetRequest(1L);

        assertNotNull(createdResetRequest);
        assertEquals(passwordReset.getId(), createdResetRequest.getId());
        verify(resetRequestRepository, times(1)).save(any());
    }

    @Test
    public void testIsResetRequestValid() {
        LocalDateTime creationTime = LocalDateTime.now().minusMinutes(29);
        passwordReset.setCreationTime(creationTime);
        when(resetRequestRepository.findByUserId(1L)).thenReturn(passwordReset);

        assertTrue(service.isResetRequestValid(1L));
        verify(resetRequestRepository, times(1)).findByUserId(1L);
    }

    @Test
    public void testDelete() {
        when(resetRequestRepository.findByUserId(1L)).thenReturn(passwordReset);

        service.delete(1L);

        verify(resetRequestRepository, times(1)).delete(passwordReset);
    }

    @Test
    public void testUpdate() {
        when(resetRequestRepository.save(any())).thenReturn(passwordReset);

        PasswordReset updatedResetRequest = service.update(passwordReset);

        assertTrue(updatedResetRequest.isUsed());
        verify(resetRequestRepository, times(1)).save(passwordReset);
    }

    @Test
    public void testFindByToken() {
        when(resetRequestRepository.findByToken(passwordReset.getToken())).thenReturn(passwordReset);

        PasswordReset foundResetRequest = service.findByToken(passwordReset.getToken());

        assertNotNull(foundResetRequest);
        assertEquals(passwordReset.getId(), foundResetRequest.getId());
        verify(resetRequestRepository, times(1)).findByToken(passwordReset.getToken());
    }
}

