package com.cinebook.tests.f02;

import com.cinebook.base.TechniqueTest;
import com.cinebook.pages.SearchTechniquePage;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class F02EquivalencePartitionTest extends TechniqueTest {
    private SearchTechniquePage search;

    @BeforeEach
    void openSearchPage() {
        search = new SearchTechniquePage(driver, baseUrl);
    }

    @Test
    @Order(1)
    @DisplayName("F02_Search_002 - EP1: Từ khóa khớp tên phim")
    void timTheoTenPhim() {
        demonstrate("F02_Search_002", "EP1 - Tìm Phí Phông", () -> {
            search.submitKeyword("Phí Phông");
            search.assertTitleMatch();
        });
    }

    @Test
    @Order(2)
    @DisplayName("F02_Search_003 - EP2: Từ khóa khớp thể loại")
    void timTheoTheLoai() {
        demonstrate("F02_Search_003", "EP2 - Tìm Horror", () -> {
            search.submitKeyword("Horror");
            search.assertGenreMatch();
        });
    }

    @Test
    @Order(3)
    @DisplayName("F02_Search_004 - EP3: Từ khóa không có kết quả")
    void tuKhoaKhongCoKetQua() {
        demonstrate("F02_Search_004", "EP3 - Không tìm thấy phim", () -> {
            search.submitKeyword("ZZZKHONGCOPHIM987");
            search.assertNoMatch();
        });
    }

    @Test
    @Order(4)
    @DisplayName("F02_Search_005 - EP4: Từ khóa rỗng")
    void tuKhoaRong() {
        demonstrate("F02_Search_005", "EP4 - Để trống ô tìm kiếm", () -> {
            search.submitKeyword("");
            search.assertInvalidKeyword();
        });
    }

    @Test
    @Order(5)
    @DisplayName("F02_Search_006 - EP4: Từ khóa chỉ có khoảng trắng")
    void tuKhoaChiCoKhoangTrang() {
        demonstrate("F02_Search_006", "EP4 - Nhập đúng ba dấu cách", () -> {
            search.submitKeyword("   ");
            search.assertInvalidKeyword();
        });
    }
}
