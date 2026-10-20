package vn.edu.softwaretesting.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import vn.edu.softwaretesting.core.WebTestBase;
import vn.edu.softwaretesting.pages.InventoryPage;
import vn.edu.softwaretesting.pages.LoginPage;

@Tag("web")
@DisplayName("FR-02: Data-Driven Cart Management Tests")
class CartTest extends WebTestBase {

    private InventoryPage inventoryPage;

    @BeforeEach
    void setUp() {
        new LoginPage(driver, baseUrl).open().login("standard_user", "secret_sauce");
        inventoryPage = new InventoryPage(driver, baseUrl);
    }

    @ParameterizedTest(name = "TC-CART-DD-{index}: Thêm sản phẩm [{0}] giá ${1} vào giỏ")
    @CsvFileSource(resources = "/products.csv", numLinesToSkip = 1)
    void shouldAddProductFromCsvToCartSuccessfully(String productName, double price) {
        inventoryPage.addItemToCartByName(productName);
        
        assertEquals(1, inventoryPage.getCartBadgeCount(), 
                () -> "Badge giỏ hàng phải hiển thị 1 sau khi thêm sản phẩm: " + productName);
    }
}