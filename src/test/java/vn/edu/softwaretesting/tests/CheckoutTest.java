package vn.edu.softwaretesting.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import vn.edu.softwaretesting.core.WebTestBase;
import vn.edu.softwaretesting.pages.CartPage;
import vn.edu.softwaretesting.pages.CheckoutStepOnePage;
import vn.edu.softwaretesting.pages.CheckoutStepTwoPage;
import vn.edu.softwaretesting.pages.InventoryPage;
import vn.edu.softwaretesting.pages.LoginPage;

@Tag("web")
@DisplayName("FR-03: Checkout & Payment")
class CheckoutTest extends WebTestBase {

    private CheckoutStepOnePage stepOnePage;

    @BeforeEach
    void setUpCheckout() {
        // Đăng nhập -> Vào Inventory -> Thêm hàng -> Vào Cart -> Sang Checkout Step One
        new LoginPage(driver, baseUrl).open().login("standard_user", "secret_sauce");
        InventoryPage inventoryPage = new InventoryPage(driver, baseUrl);
        CartPage cartPage = inventoryPage.addToCart("sauce-labs-backpack").openCart();
        stepOnePage = cartPage.checkout();
    }

    @Test
    @DisplayName("TC-CHK-UC-01: Thanh toan don hang thanh cong tu gio hang den man hinh hoan tat")
    void checkoutEndToEndSuccessfully() {
        CheckoutStepTwoPage stepTwoPage = stepOnePage.proceedToStepTwo("John", "Doe", "70000");
        assertTrue(stepTwoPage.isOnStepTwo(), "Không chuyển được sang màn hình xác nhận thanh toán (Step Two)");

        stepTwoPage.clickFinish();
        assertTrue(stepTwoPage.isOrderCompleted(), "Đơn hàng chưa hoàn tất thành công");
        assertTrue(stepTwoPage.getCompleteMessage().contains("Thank you for your order!"),
                "Thông điệp cảm ơn không hiển thị đúng");
    }

    @Test
    @DisplayName("TC-CHK-UC-02: Huy thanh toan tai man hinh Step Two quay ve trang san pham inventory.html")
    void cancelOnStepTwoShouldReturnToInventory() {
        CheckoutStepTwoPage stepTwoPage = stepOnePage.proceedToStepTwo("John", "Doe", "70000");
        assertTrue(stepTwoPage.isOnStepTwo(), "Không chuyển được sang màn hình xác nhận thanh toán (Step Two)");

        stepTwoPage.clickCancel();
        InventoryPage inventoryPage = new InventoryPage(driver, baseUrl);
        assertTrue(inventoryPage.isOnInventory(),
                "Bấm Cancel ở Step Two nhưng không quay về trang danh sách sản phẩm inventory.html");
    }

    @Test
    @DisplayName("TC-CHK-UC-03: Huy thanh toan tai man hinh Step One quay ve gio hang cart.html")
    void cancelOnStepOneShouldReturnToCart() {
        stepOnePage.clickCancel();
        CartPage cartPage = new CartPage(driver, baseUrl);
        assertTrue(cartPage.isOnCart(),
                "Bấm Cancel ở Step One nhưng không quay về trang giỏ hàng cart.html");
        assertTrue(cartPage.hasProducts(),
                "Sau khi bấm Cancel ở Step One, sản phẩm trong giỏ hàng phải còn nguyên");
    }

    @Test
    @DisplayName("TC-CHK-UC-04: Bam nut Back Home tai man hinh hoan tat quay ve trang san pham va reset gio hang")
    void backHomeShouldReturnToInventoryAndResetCart() {
        CheckoutStepTwoPage stepTwoPage = stepOnePage.proceedToStepTwo("John", "Doe", "70000");
        stepTwoPage.clickFinish();
        assertTrue(stepTwoPage.isOrderCompleted(), "Đơn hàng chưa hoàn tất thành công");

        stepTwoPage.clickBackHome();
        InventoryPage inventoryPage = new InventoryPage(driver, baseUrl);
        assertTrue(inventoryPage.isOnInventory(),
                "Bấm Back Home nhưng không quay về trang danh sách sản phẩm inventory.html");
        assertTrue(inventoryPage.isCartEmpty(),
                "Giỏ hàng chưa được làm rỗng sau khi bấm Back Home");
    }

    @Test
    @DisplayName("TC-CHK-DT-01: Dien day du thong tin hop le chuyen sang man hinh xac nhan thanh toan")
    void validInformationShouldProceedToStepTwo() {
        CheckoutStepTwoPage stepTwoPage = stepOnePage.proceedToStepTwo("Nguyen", "Van A", "70000");
        assertTrue(stepTwoPage.isOnStepTwo(), "Lẽ ra phải chuyển sang Step Two khi điền đủ 3 trường hợp lệ");
    }

    @ParameterizedTest(name = "{0}: firstName=''{1}'', lastName=''{2}'', postalCode=''{3}''")
    @DisplayName("TC-CHK-DT: Kiem tra cac truong bat buoc form thanh toan theo bang quyet dinh")
    @CsvSource({
            "TC-CHK-DT-02, '', Doe, 70000, Error: First Name is required",
            "TC-CHK-DT-03, John, '', 70000, Error: Last Name is required",
            "TC-CHK-DT-04, John, Doe, '', Error: Postal Code is required"
    })
    void requiredFieldValidation(
            String testCaseId,
            String firstName,
            String lastName,
            String postalCode,
            String expectedError) {

        stepOnePage.enterInformation(firstName, lastName, postalCode);
        stepOnePage.clickContinue();

        String actualError = stepOnePage.getErrorMessage();
        assertTrue(!actualError.isEmpty(), testCaseId + ": Lỗi thông tin không hiển thị khi bỏ trống trường bắt buộc");
        assertEquals(expectedError, actualError, testCaseId + ": Nội dung thông báo lỗi không khớp kỳ vọng");
    }

    @Test
    @DisplayName("TC-CHK-CALC-01: Xac thuc tinh toan cong thuc Thue 8% va Tong tien")
    void verifyTaxAndTotalCalculation() {
        CheckoutStepTwoPage stepTwoPage = stepOnePage.proceedToStepTwo("Nguyen", "Van A", "70000");

        double itemTotal = stepTwoPage.getItemTotal();
        double actualTax = stepTwoPage.getTax();
        double actualTotal = stepTwoPage.getTotal();

        double expectedTax = Math.round(itemTotal * 0.08 * 100.0) / 100.0;
        double expectedTotal = Math.round((itemTotal + expectedTax) * 100.0) / 100.0;

        assertEquals(expectedTax, actualTax, 0.01,
                "Thuế thực tế không khớp với công thức tính thuế 8%");
        assertEquals(expectedTotal, actualTotal, 0.01,
                "Tổng tiền không khớp với công thức Item Total + Tax");
    }

    @Test
    @Tag("known-bug")
    @DisplayName("TC-CHK-BUG-01 [BUG-01]: He thong khong duoc phep cho checkout khi gio hang hoan toan rong")
    void emptyCartShouldNotAllowCheckout() {
        new LoginPage(driver, baseUrl).open().login("standard_user", "secret_sauce");
        InventoryPage inventoryPage = new InventoryPage(driver, baseUrl);
        CartPage cartPage = inventoryPage.openCart();

        assertTrue(cartPage.isOnCart(), "Chưa ở màn hình giỏ hàng cart.html");
        cartPage.checkout();

        assertTrue(cartPage.isOnCart(),
                "Hệ thống cho phép checkout khi giỏ hàng rỗng, vi phạm logic nghiệp vụ thương mại điện tử");
    }

    @Test
    @Tag("known-bug")
    @DisplayName("TC-CHK-BUG-02 [BUG-02]: problem_user khong the nhap duoc Last Name tai man hinh checkout")
    void problemUserCannotEnterLastName() {
        new LoginPage(driver, baseUrl).open().login("problem_user", "secret_sauce");
        InventoryPage inventoryPage = new InventoryPage(driver, baseUrl);
        CartPage cartPage = inventoryPage.addToCart("sauce-labs-backpack").openCart();
        CheckoutStepOnePage stepOne = cartPage.checkout();

        CheckoutStepTwoPage stepTwo = stepOne.proceedToStepTwo("An", "Nguyen", "70000");

        assertTrue(stepTwo.isOnStepTwo(),
                "problem_user bị lỗi giao diện chặn không nhập được Last Name, không thể sang Step Two");
    }
}