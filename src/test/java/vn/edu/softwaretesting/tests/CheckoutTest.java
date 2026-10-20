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
        // Bước 1: Đăng nhập vào hệ thống bằng tài khoản tiêu chuẩn
        new LoginPage(driver, baseUrl).open().login("standard_user", "secret_sauce");

        // Bước 2: Thêm sản phẩm vào giỏ hàng và mở giỏ hàng qua Page Object
        InventoryPage inventoryPage = new InventoryPage(driver, baseUrl).waitForPage();
        CartPage cartPage = inventoryPage.addToCart("sauce-labs-backpack").openCart();

        // Bước 3: Nhấn checkout để điều hướng đến trang Checkout Step One
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

        assertTrue(stepOnePage.isErrorDisplayed(),
                testCaseId + ": Lỗi thông tin không hiển thị khi bỏ trống trường bắt buộc");
        assertEquals(expectedError, stepOnePage.getErrorMessage(),
                testCaseId + ": Nội dung thông báo lỗi không khớp kỳ vọng");
    }

    @Test
    @DisplayName("TC-CHK-CALC-01: Xac thuc tinh toan cong thuc Thue 8% va Tong tien")
    void verifyTaxAndTotalCalculation() {
        CheckoutStepTwoPage stepTwoPage = stepOnePage.proceedToStepTwo("Nguyen", "Van A", "70000");

        double itemTotal = stepTwoPage.getItemTotal();
        double actualTax = stepTwoPage.getTax();
        double actualTotal = stepTwoPage.getTotal();

        // Công thức tính thuế 8% làm tròn 2 chữ số thập phân
        double expectedTax = Math.round(itemTotal * 0.08 * 100.0) / 100.0;
        double expectedTotal = Math.round((itemTotal + expectedTax) * 100.0) / 100.0;

        assertEquals(expectedTax, actualTax, 0.01,
                "Thuế thực tế không khớp với công thức tính thuế 8%");
        assertEquals(expectedTotal, actualTotal, 0.01,
                "Tổng tiền không khớp với công thức Item Total + Tax");
    }
}
