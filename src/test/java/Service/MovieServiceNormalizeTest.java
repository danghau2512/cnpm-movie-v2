package Service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

// Issue #24, #52
class MovieServiceNormalizeTest {

    private final MovieService movieService = new MovieService();

    // -------------------------------------------------------------------------
    // Helper: gọi private method stripAccents via reflection
    // -------------------------------------------------------------------------

    private String callStripAccents(String text) throws Exception {
        Method method = MovieService.class.getDeclaredMethod("stripAccents", String.class);
        method.setAccessible(true);
        return (String) method.invoke(movieService, text);
    }

    // =========================================================================
    // normalizeKeyword – Issue #24
    // =========================================================================

    @Test
    @DisplayName("Issue #24 - TC03-01: normalizeKeyword null trả về chuỗi rỗng")
    void normalizeKeyword_WithNull_ShouldReturnEmpty() {
        assertEquals("", movieService.normalizeKeyword(null));
    }

    @Test
    @DisplayName("Issue #24 - TC03-01: normalizeKeyword rỗng trả về chuỗi rỗng")
    void normalizeKeyword_WithEmptyString_ShouldReturnEmpty() {
        assertEquals("", movieService.normalizeKeyword(""));
    }

    @Test
    @DisplayName("Issue #24 - TC03-01: normalizeKeyword cắt bỏ khoảng trắng đầu cuối")
    void normalizeKeyword_WithLeadingAndTrailingSpaces_ShouldTrim() {
        assertEquals("avatar", movieService.normalizeKeyword("  avatar  "));
    }

    @Test
    @DisplayName("Issue #24 - TC03-01: normalizeKeyword gộp nhiều khoảng trắng liên tiếp thành một")
    void normalizeKeyword_WithMultipleSpaces_ShouldCollapse() {
        assertEquals("hanh dong", movieService.normalizeKeyword("hanh  dong"));
    }

    @Test
    @DisplayName("Issue #24 - TC03-01: normalizeKeyword hợp lệ không bị thay đổi")
    void normalizeKeyword_WithValidKeyword_ShouldReturnUnchanged() {
        assertEquals("Avatar", movieService.normalizeKeyword("Avatar"));
    }

    @Test
    @DisplayName("Issue #24 - TC03-01: normalizeKeyword khoảng trắng nhiều đầu cuối và giữa")
    void normalizeKeyword_WithMixedSpaces_ShouldNormalize() {
        assertEquals("hanh dong viet nam", movieService.normalizeKeyword("  hanh   dong   viet   nam  "));
    }

    // =========================================================================
    // stripAccents – Issue #52
    // =========================================================================

    @Test
    @DisplayName("Issue #52 - TC03-04: stripAccents chuỗi null trả về rỗng")
    void stripAccents_WithNull_ShouldReturnEmpty() throws Exception {
        assertEquals("", callStripAccents(null));
    }

    @Test
    @DisplayName("Issue #52 - TC03-04: stripAccents chuỗi rỗng trả về rỗng")
    void stripAccents_WithEmpty_ShouldReturnEmpty() throws Exception {
        assertEquals("", callStripAccents(""));
    }

    @Test
    @DisplayName("Issue #52 - TC03-04: stripAccents chuỗi không dấu không bị thay đổi")
    void stripAccents_WithNoAccents_ShouldReturnUnchanged() throws Exception {
        assertEquals("avatar", callStripAccents("avatar"));
    }

    @Test
    @DisplayName("Issue #52 - TC03-04: stripAccents xóa dấu tiếng Việt thông thường")
    void stripAccents_WithVietnameseAccents_ShouldRemoveAccents() throws Exception {
        assertEquals("Hanh Dong", callStripAccents("Hành Động"));
    }

    @Test
    @DisplayName("Issue #52 - TC03-04: stripAccents xử lý đặc biệt ký tự đ → d")
    void stripAccents_WithLowercaseDCharacter_ShouldConvertToD() throws Exception {
        assertEquals("duong", callStripAccents("đường"));
    }

    @Test
    @DisplayName("Issue #52 - TC03-04: stripAccents xử lý đặc biệt ký tự Đ → D")
    void stripAccents_WithUppercaseDCharacter_ShouldConvertToD() throws Exception {
        String result = callStripAccents("Đường Phố");
        assertTrue(result.startsWith("D"), "Đ phải được chuyển thành D");
        assertFalse(result.contains("Đ"), "Kết quả không được chứa Đ");
    }

    @Test
    @DisplayName("Issue #52 - TC03-04: stripAccents từ khóa 'hanh dong' phải khớp 'Hành Động' sau normalize")
    void stripAccents_SearchKeywordMatchesVietnameseTitle() throws Exception {
        String keyword = callStripAccents("hanh dong").toLowerCase();
        String title   = callStripAccents("Hành Động").toLowerCase();
        assertTrue(title.contains(keyword));
    }

    @Test
    @DisplayName("Issue #52 - TC03-04: stripAccents từ khóa 'duong pho' phải khớp 'Đường Phố' sau normalize")
    void stripAccents_KeywordWithDCharacterMatchesTitle() throws Exception {
        String keyword = callStripAccents("duong pho").toLowerCase();
        String title   = callStripAccents("Đường Phố").toLowerCase();
        assertTrue(title.contains(keyword));
    }
}
