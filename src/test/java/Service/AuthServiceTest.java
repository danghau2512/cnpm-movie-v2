package Service;

import Dao.UserDAO;
import Model.User;
import Util.PasswordUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    @Test void registrationHashesPasswordAndNormalizesEmail() {
        UserDAO dao = mock(UserDAO.class);
        new AuthService(dao).register(" Nguyễn Văn A ", " A@Example.com ", "0901234567", "secret123", "secret123");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(dao).create(captor.capture());
        User user = captor.getValue();
        assertEquals("a@example.com", user.getEmail());
        assertEquals("Nguyễn Văn A", user.getFullName());
        assertNotEquals("secret123", user.getPasswordHash());
        assertTrue(PasswordUtil.matches("secret123", user.getPasswordHash()));
    }
    @Test void rejectsDuplicateEmailAndInvalidConfirmationWithoutCreatingAccount() {
        UserDAO dao = mock(UserDAO.class); AuthService service = new AuthService(dao);
        assertThrows(IllegalArgumentException.class, () -> service.register("A", "a@example.com", "0901234567", "secret123", "different"));
        when(dao.findByEmail("a@example.com")).thenReturn(new User());
        assertThrows(IllegalArgumentException.class, () -> service.register("A", "a@example.com", "0901234567", "secret123", "secret123"));
        verify(dao, never()).create(any());
    }
    @Test void legacyLoginUpgradesPasswordAndLockedAccountIsRejected() {
        UserDAO dao = mock(UserDAO.class); User user = new User();
        user.setId(5); user.setStatus("ACTIVE"); user.setPasswordHash("oldpassword");
        when(dao.findByEmail("a@example.com")).thenReturn(user);
        assertSame(user, new AuthService(dao).login("a@example.com", "oldpassword"));
        verify(dao).upgradePassword(eq(5), eq("oldpassword"), startsWith("pbkdf2$"));
        assertTrue(PasswordUtil.matches("oldpassword", user.getPasswordHash()));
        user.setStatus("LOCKED");
        assertNull(new AuthService(dao).login("a@example.com", "oldpassword"));
    }
    @Test void hashPreservesPasswordSpacesAndUsesDifferentSalts() {
        String one = PasswordUtil.hash(" password "); String two = PasswordUtil.hash(" password ");
        assertNotEquals(one, two); assertTrue(PasswordUtil.matches(" password ", one));
        assertFalse(PasswordUtil.matches("password", one)); assertFalse(PasswordUtil.matches("bad", "pbkdf2$bad"));
    }
}
