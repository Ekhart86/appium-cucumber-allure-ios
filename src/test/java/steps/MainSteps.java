package steps;

import io.cucumber.java.ru.И;

import static util.Wrapper.sleep;

public class MainSteps extends BaseSteps{


    @И("приложение успешно загрузилось")
    public void нажимаемНаКнопкуПоиска() {
        sleep(5000);
        makeScreenshot(0.5);
        System.out.println("Приложение успешно загрузилось");
    }

}
