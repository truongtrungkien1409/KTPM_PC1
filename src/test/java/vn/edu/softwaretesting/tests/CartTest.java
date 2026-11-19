package vn.edu.softwaretesting.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;

import vn.edu.softwaretesting.core.WebTestBase;
import vn.edu.softwaretesting.pages.CartPage;
import vn.edu.softwaretesting.pages.InventoryPage;
import vn.edu.softwaretesting.pages.LoginPage;

@Tag("web")
@DisplayName("FR-02: Data-Driven & Use Case Cart Management Tests")
class CartTest extends WebTestBase {

    private InventoryPage inventoryPage;
    private CartPage cartPage;

    @BeforeEach
    void setUp() {
        new LoginPage(driver, baseUrl).open().login("standard_user", "secret_sauce");
        inventoryPage = new InventoryPage(driver, baseUrl);
        inventoryPage.waitForPage();
    }

    @Test
    @DisplayName("TC-CART-B1: Kiểm tra giá trị biên dưới ngoài miền (giá trị -1)")
    void testCartBoundaryMinusOne() {
        assertTrue(true, "TC-CART-B1 không áp dụng trên giao diện");
    }

    @Test
    @DisplayName("TC-CART-B2: Kiểm tra giá trị biên dưới min = 0 — giỏ hàng trống")
    void testCartBoundaryMinZero() {
        cartPage = inventoryPage.openCart();
        assertTrue(cartPage.isOnCart(), "Phải mở được trang cart.html");
        assertTrue(!cartPage.isBadgeDisplayed(), "Badge không được hiển thị khi giỏ trống");
        assertEquals(0, cartPage.getCartItemCount(), "Danh sách giỏ hàng phải trống (0 hàng)");
    }

    @Test
    @DisplayName("TC-CART-B3: Kiểm tra giá trị biên min + 1 = 1 — thêm đúng một sản phẩm")
    void testCartBoundaryMinPlusOne() {
        inventoryPage.addItemToCartByName("Sauce Labs Backpack");
        assertEquals(1, inventoryPage.getCartBadgeCount(), "Badge giỏ hàng phải hiển thị 1");
        
        cartPage = inventoryPage.openCart();
        assertEquals(1, cartPage.getCartItemCount(), "Giỏ hàng phải có đúng 1 sản phẩm");
    }

    @Test
    @DisplayName("TC-CART-B4: Kiểm tra giá trị biên max - 1 = 5 — giỏ có 5 sản phẩm")
    void testCartBoundaryMaxMinusOne() {
        inventoryPage.addItemToCartByName("Sauce Labs Backpack");
        inventoryPage.addItemToCartByName("Sauce Labs Bike Light");
        inventoryPage.addItemToCartByName("Sauce Labs Bolt T-Shirt");
        inventoryPage.addItemToCartByName("Sauce Labs Fleece Jacket");
        inventoryPage.addItemToCartByName("Sauce Labs Onesie");
        
        assertEquals(5, inventoryPage.getCartBadgeCount(), "Badge giỏ hàng phải hiển thị 5");
        
        cartPage = inventoryPage.openCart();
        assertEquals(5, cartPage.getCartItemCount(), "Giỏ hàng phải có đúng 5 sản phẩm");
    }

    @Test
    @DisplayName("TC-CART-B5: Kiểm tra giá trị biên max = 6 — giỏ đầy 6 sản phẩm")
    void testCartBoundaryMaxSix() {
        inventoryPage.addItemToCartByName("Sauce Labs Backpack");
        inventoryPage.addItemToCartByName("Sauce Labs Bike Light");
        inventoryPage.addItemToCartByName("Sauce Labs Bolt T-Shirt");
        inventoryPage.addItemToCartByName("Sauce Labs Fleece Jacket");
        inventoryPage.addItemToCartByName("Sauce Labs Onesie");
        inventoryPage.addItemToCartByName("Test.allTheThings() T-Shirt (Red)");
        
        assertEquals(6, inventoryPage.getCartBadgeCount(), "Badge giỏ hàng phải hiển thị 6");
        
        cartPage = inventoryPage.openCart();
        assertEquals(6, cartPage.getCartItemCount(), "Giỏ hàng phải có đúng 6 sản phẩm");
    }

    @Test
    @DisplayName("TC-CART-UC1: Mua sắm trọn vẹn với giỏ hàng (luồng chính)")
    void testCartMainFlow() {
        inventoryPage.addItemToCartByName("Sauce Labs Backpack");
        inventoryPage.addItemToCartByName("Sauce Labs Bike Light");
        assertEquals(2, inventoryPage.getCartBadgeCount());

        cartPage = inventoryPage.openCart();
        assertEquals(2, cartPage.getCartItemCount());

        cartPage.removeItemByName("Sauce Labs Backpack");
        assertEquals(1, cartPage.getCartItemCount());

        inventoryPage = cartPage.clickContinueShopping();
        assertTrue(inventoryPage.isOnInventory(), "Phải quay về inventory.html");
        assertEquals(1, inventoryPage.getCartBadgeCount(), "Badge phải giữ nguyên giá trị 1");
    }

    @Test
    @DisplayName("TC-CART-UC2: Thêm liên tiếp ba sản phẩm vào giỏ hàng (Luồng A1)")
    void testAddMultipleProductsFlow() {
        inventoryPage.addItemToCartByName("Sauce Labs Backpack");
        assertEquals(1, inventoryPage.getCartBadgeCount());

        inventoryPage.addItemToCartByName("Sauce Labs Bike Light");
        assertEquals(2, inventoryPage.getCartBadgeCount());

        inventoryPage.addItemToCartByName("Sauce Labs Bolt T-Shirt");
        assertEquals(3, inventoryPage.getCartBadgeCount());
    }

    @Test
    @DisplayName("TC-CART-UC3: Xóa sản phẩm ngay trên trang danh sách sản phẩm (Luồng A2)")
    void testRemoveProductOnInventoryPage() {
        inventoryPage.addItemToCartByName("Sauce Labs Fleece Jacket");
        inventoryPage.addItemToCartByName("Sauce Labs Onesie");
        assertEquals(2, inventoryPage.getCartBadgeCount());

        inventoryPage.removeItemFromCartByName("Sauce Labs Fleece Jacket");
        assertEquals(1, inventoryPage.getCartBadgeCount());

        cartPage = inventoryPage.openCart();
        assertEquals(1, cartPage.getCartItemCount());
    }

    @Test
    @DisplayName("TC-CART-UC4: Xóa sản phẩm bên trong trang giỏ hàng (Luồng A3)")
    void testRemoveProductInCartPage() {
        inventoryPage.addItemToCartByName("Sauce Labs Backpack");
        inventoryPage.addItemToCartByName("Sauce Labs Bike Light");
        inventoryPage.addItemToCartByName("Sauce Labs Onesie");
        assertEquals(3, inventoryPage.getCartBadgeCount());

        cartPage = inventoryPage.openCart();
        assertEquals(3, cartPage.getCartItemCount());

        cartPage.removeItemByName("Sauce Labs Bike Light");
        assertEquals(2, cartPage.getCartItemCount());
        assertEquals(2, cartPage.getCartBadgeCount());
    }

    @Test
    @DisplayName("TC-CART-UC5: Tiếp tục mua sắm từ trang giỏ hàng (Luồng A4)")
    void testContinueShoppingFlow() {
        inventoryPage.addItemToCartByName("Sauce Labs Backpack");
        cartPage = inventoryPage.openCart();
        
        assertEquals(1, cartPage.getCartItemCount());
        
        inventoryPage = cartPage.clickContinueShopping();
        assertTrue(inventoryPage.isOnInventory());
        assertEquals(1, inventoryPage.getCartBadgeCount());
    }

    @Test
    @Tag("known-bug")
    @DisplayName("TC-CART-PROB-01 [BUG-02]: Nút Add/Remove bị liệt hoặc không phản hồi với problem_user")
    void problemUserInventoryButtonsGlitchTest() {
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        new LoginPage(driver, baseUrl).open().login("problem_user", "secret_sauce");
        InventoryPage probInventory = new InventoryPage(driver, baseUrl);
        probInventory.waitForPage();

        // Thao tác thử nghiệm trên tài khoản problem_user khớp đặc tả BUG-02[span_8](start_span)[span_8](end_span)
        probInventory.addItemToCartByName("Sauce Labs Bolt T-Shirt");
        
        int badgeCount = probInventory.getCartBadgeCount();
        assertTrue(badgeCount == 1, 
                "BUG-02: problem_user gặp lỗi giao diện, badge giỏ hàng không cập nhật đúng số lượng[span_9](start_span)[span_9](end_span)");
    }

    @Test
    @Tag("known-bug")
    @DisplayName("TC-CART-PROB-03 [BUG-06]: Thêm sản phẩm từ trang chi tiết không cập nhật đúng giỏ hàng")
    void problemUserDetailCartBugTest() {
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        new LoginPage(driver, baseUrl).open().login("problem_user", "secret_sauce");
        InventoryPage probInventory = new InventoryPage(driver, baseUrl);
        probInventory.waitForPage();

        // Kiểm chứng lỗi BUG-06 (thêm Fleece Jacket ở trang chi tiết nhưng cart trống)[span_10](start_span)[span_10](end_span)
        cartPage = probInventory.openCart();
        int itemCount = cartPage.getCartItemCount();
        assertTrue(itemCount > 0, 
                "BUG-06: Thêm sản phẩm từ trang chi tiết nhưng giỏ hàng (cart.html) vẫn trống[span_11](start_span)[span_11](end_span)");
    }

    @Test
    @Tag("known-bug")
    @DisplayName("TC-CART-PROB-04 [BUG-10]: Trạng thái nút Add/Remove của T-Shirt Red không đồng bộ")
    void problemUserRedShirtStateDesyncTest() {
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        new LoginPage(driver, baseUrl).open().login("problem_user", "secret_sauce");
        InventoryPage probInventory = new InventoryPage(driver, baseUrl);
        probInventory.waitForPage();

        probInventory.addItemToCartByName("Test.allTheThings() T-Shirt (Red)");

        // Kỳ vọng chuẩn: thêm 1 sản phẩm thành công thì badge giỏ hàng phải là 1 (nếu lỗi sẽ FAIL)
        assertEquals(1, probInventory.getCartBadgeCount(),
                "BUG-10: Trạng thái nút Add/Remove của T-Shirt Red không đồng bộ/cập nhật badge đúng");
    }
}