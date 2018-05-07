package org.openqa.selenium.remote;

import java.util.Map;

public class MyRemoteExecuteMethod implements ExecuteMethod {

    private final MyRemoteWebDriver driver;

    public MyRemoteExecuteMethod(MyRemoteWebDriver driver) {
        this.driver = driver;
    }

    public Object execute(String commandName, Map<String, ?> parameters) {
        Response response;

        if (parameters == null || parameters.isEmpty()) {
            response = driver.execute(commandName);
        } else {
            response = driver.execute(commandName, parameters);
        }

        return response.getValue();
    }
}