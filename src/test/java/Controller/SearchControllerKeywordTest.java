package Controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Issue #24
class SearchControllerKeywordTest {

    private final SearchController controller = new SearchController();

    // -------------------------------------------------------------------------
    // TC03-01: isInvalidKeyword – keyword hợp lệ
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Issue #24 - TC03-01: keyword hợp lệ, có param → không invalid")
    void isInvalidKeyword_WithValidKeyword_ShouldReturnFalse() {
        assertFalse(controller.isInvalidKeyword("avatar", true));
    }

    @Test
    @DisplayName("Issue #24 - TC03-01: keyword có khoảng trắng đầu cuối nhưng hợp lệ → không invalid")
    void isInvalidKeyword_WithTrimmedKeyword_ShouldReturnFalse() {
        assertFalse(controller.isInvalidKeyword("  avatar  ", true));
    }

    // -------------------------------------------------------------------------
    // TC03-02: isInvalidKeyword – keyword rỗng / chỉ khoảng trắng
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Issue #24 - TC03-02: keyword rỗng, có param → invalid")
    void isInvalidKeyword_WithEmptyKeyword_ShouldReturnTrue() {
        assertTrue(controller.isInvalidKeyword("", true));
    }

    @Test
    @DisplayName("Issue #24 - TC03-02: keyword chỉ chứa khoảng trắng, có param → invalid")
    void isInvalidKeyword_WithBlankKeyword_ShouldReturnTrue() {
        assertTrue(controller.isInvalidKeyword("   ", true));
    }

    @Test
    @DisplayName("Issue #24 - TC03-02: keyword null, có param → invalid")
    void isInvalidKeyword_WithNullKeyword_ShouldReturnTrue() {
        assertTrue(controller.isInvalidKeyword(null, true));
    }

    // -------------------------------------------------------------------------
    // TC03-01: isInvalidKeyword – không có param keyword (truy cập /search thẳng)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Issue #24 - TC03-01: không có param keyword → không invalid (hiển thị danh sách mặc định)")
    void isInvalidKeyword_WithoutKeywordParam_ShouldReturnFalse() {
        assertFalse(controller.isInvalidKeyword(null, false));
    }

    @Test
    @DisplayName("Issue #24 - TC03-01: keyword rỗng nhưng không có param → không invalid")
    void isInvalidKeyword_WithEmptyKeywordAndNoParam_ShouldReturnFalse() {
        assertFalse(controller.isInvalidKeyword("", false));
    }
}
