package hooks;


import io.appium.java_client.ios.IOSStartScreenRecordingOptions;
import io.cucumber.core.api.Scenario;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Base64;

import static io.cucumber.core.event.Status.PASSED;
import static runner.RunnerTest.*;

public class Hooks {
    private static Logger logger = LoggerFactory.getLogger(Hooks.class);

    @Before
    public void startScenario(Scenario scenario) {
        logger.info("------------------------------------------------------------");
        logger.info("Запуск сценария - '" + scenario.getName() + "'");
        logger.info("------------------------------------------------------------");
        if (VIDEO_RECORDING) {
            logger.info("Начинаем запись видео теста " + scenario.getName());
            driver.startRecordingScreen(new IOSStartScreenRecordingOptions()
                    .withTimeLimit(Duration.ofSeconds(500L)));

        }
    }

    @After
    public static void checkScenarioResult(Scenario scenario) {
        logger.info("------------------------------------------------------------");
        System.out.println("Сценарий '" + scenario.getName() + "' - " + scenario.getStatus());
        if (!scenario.getStatus().equals(PASSED)) {
            makeScreenshot(0.5);
        }
        driver.resetApp();
        if (VIDEO_RECORDING) {
            String video = driver.stopRecordingScreen();
            if (video != null) {
                logger.info("Прикрепляем видео к отчёту");
                Allure.getLifecycle().addAttachment("Видеозапись выполнения теста", "video/mp4", "mp4", Base64.getDecoder().decode(video));
            } else {
                System.out.println("Не удалось получить видеозапись выполнения теста!");
            }
        }
        logger.info("------------------------------------------------------------");
    }

}
