package util;

import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import org.junit.Assert;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static io.appium.java_client.touch.WaitOptions.waitOptions;
import static io.appium.java_client.touch.offset.PointOption.point;
import static java.time.Duration.ofMillis;

public class Wrapper extends Assert {

    private static Logger logger = LoggerFactory.getLogger(Wrapper.class);
    private static  IOSDriver<IOSElement> driver;

    public Wrapper(IOSDriver<IOSElement> driver) {
        this.driver = driver;
    }

    /**
     * Подождать указанное количество миллисекунд
     *
     * @param milliseconds количество миллисекунд
     */
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Нажать на экран
     *
     * @param width  расстояние в процентном соотношении от левого края экрана
     * @param height расстояние в процентном соотношении от верха экрана
     */
    public static void tapByPercentage(double width, double height) {
        Dimension size = driver.manage().window().getSize();
        int xHeight = (int) (size.height * height);
        int yWidth = (int) (size.width * width);

        new TouchAction(driver)
                .tap(point(yWidth, xHeight))
                .waitAction(waitOptions(Duration.ofMillis(250)))
                .perform();
    }

    /**
     * Горизонтальный свайп
     *
     * @param startPercentage  начало свайпа в процентном соотношении от левого края экрана
     * @param endPercentage    конец свайпа в процентном соотношении от левого края экрана
     * @param anchorPercentage ось свайпа
     */
    public static void horizontalSwipeByPercentage(double startPercentage, double endPercentage, double anchorPercentage) {
        sleep(300);
        Dimension size = driver.manage().window().getSize();
        int anchor = (int) (size.height * anchorPercentage);
        int startPoint = (int) (size.width * startPercentage);
        int endPoint = (int) (size.width * endPercentage);

        TouchAction action = new TouchAction(driver)
                .press(point(startPoint, anchor))
                .waitAction(waitOptions(ofMillis(1000)))
                .moveTo(point(endPoint, anchor)).release().perform();
        action.release().perform();
    }

    /**
     * Вертикальный свайп
     *
     * @param startPercentage  начало свайпа в процентном соотношении от верха экрана
     * @param endPercentage    конец свайпа в процентном соотношении от верха экрана
     * @param anchorPercentage ось свайпа
     */
    public static void verticalSwipeByPercentages(double startPercentage, double endPercentage, double anchorPercentage) {
        sleep(300);
        Dimension size = driver.manage().window().getSize();
        int anchor = (int) (size.width * anchorPercentage);
        int startPoint = (int) (size.height * startPercentage);
        int endPoint = (int) (size.height * endPercentage);

        TouchAction action = new TouchAction(driver)
                .press(point(anchor, startPoint))
                .waitAction(waitOptions(ofMillis(1000)))
                .moveTo(point(anchor, endPoint));
        action.release().perform();

    }

    /**
     * Метод кликающий по элементу
     */
    public static void clickElement(MobileElement element) {
        waitElement(element, 15);
        element.click();
    }

    /**
     * Подождать элемент определённое количество времени
     *
     * @param timeSeconds время в секундах которое необходимо ждать элемент
     */
    public static void waitElement(MobileElement element, int timeSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeSeconds);
            wait.until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            logger.error("Could not find item : " + element.toString());
            throw new AssertionError("Не удалось найти элемент " + element.toString());
        }
    }



    /**
     * Метод проверяет что переданный текст отображается на экране
     */
    public static void textShouldBeVisible(String text) {
        IOSElement element;
        try {
            element = driver.findElementByXPath("//*[@text = '" + text + "']");
        } catch (Exception e) {
            logger.error("Element with text" + text + "not found!");
            throw new AssertionError("Элемент с текстом " + text + " не найден!");
        }
        assertTrue("Текст '" + text + "' должен быть видимым!", element.isDisplayed());
    }

    /**
     * Метод проверяет что переданная часть текста отображается на экране
     */
    public static void partTextShouldBeVisible(String text) {
        IOSElement element;
        try {
            element = driver.findElementByXPath("//*[contains(@text,'" + text + "')]");
            System.out.println("Полный текст в элементе: " + element.getText());
        } catch (Exception e) {
            logger.error("Element with text" + text + "not found!");
            throw new AssertionError("Элемент с текстом " + text + " не найден!");
        }
        assertTrue("Текст '" + text + "' должен быть видимым!", element.isDisplayed());
    }

    /**
     * Метод проверяет существует ли элемент , поиск по ID
     */
    public static boolean existsElement(String id) {
        try {
            driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
            driver.findElementById(id);
            driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        } catch (NoSuchElementException e) {
            driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
            return false;
        }
        return true;
    }

    /**
     * Метод кликает по элементу найденному по тексту
     */
    public static void clickElementByText(String text) {
        IOSElement element;
        try {
            element = driver.findElementByXPath("//*[@text = '" + text + "']");
        } catch (Exception e) {
            logger.error("Element with text" + text + "not found!");
            throw new AssertionError("Элемент с текстом " + text + " не найден!");
        }
        clickElement(element);
    }

    /**
     * Метод кликает по элементу найденному по частичному совпадению текста
     */
    public static void clickElementWithText(String text) {
        IOSElement element;
        try {
            element = driver.findElementByXPath("//*[contains(@text,'" + text + "')]");
        } catch (Exception e) {
            logger.error("Element with text" + text + "not found!");
            throw new AssertionError("Элемент с текстом " + text + " не найден!");
        }
        clickElement(element);
    }

    /**
     * Метод проверяет видимость элемента на странице
     */
    public static void elementShouldBeVisible(MobileElement element) {
        waitElement(element, 20);
        assertTrue("Элемент " + element.getText() + " должен быть видимым", element.isDisplayed());
    }

}
