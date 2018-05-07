package org.openqa.selenium.remote.internal;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.openqa.selenium.remote.MyRemoteWebDriver;
import org.openqa.selenium.remote.MyRemoteWebElement;

import java.util.Collection;
import java.util.Map;

public class MyJsonToWebElementConverter implements Function<Object, Object> {
    private final MyRemoteWebDriver driver;

    public MyJsonToWebElementConverter(MyRemoteWebDriver driver) {
        this.driver = driver;
    }

    public Object apply(Object result) {
        if (result instanceof Collection<?>) {
            Collection<?> results = (Collection<?>) result;
            return Lists.newArrayList(Iterables.transform(results, this));
        }

        if (result instanceof Map<?, ?>) {
            Map<?, ?> resultAsMap = (Map<?, ?>) result;
            if (resultAsMap.containsKey("ELEMENT")) {
                MyRemoteWebElement element = newRemoteWebElement();
                element.setId(String.valueOf(resultAsMap.get("ELEMENT")));
                element.setFileDetector(driver.getFileDetector());
                return element;
            } else if (resultAsMap.containsKey("element-6066-11e4-a52e-4f735466cecf")) {
                MyRemoteWebElement element = newRemoteWebElement();
                element.setId(String.valueOf(resultAsMap.get("element-6066-11e4-a52e-4f735466cecf")));
                element.setFileDetector(driver.getFileDetector());
                return element;
            } else {
                return Maps.transformValues(resultAsMap, this);
            }
        }

        if (result instanceof Number) {
            if (result instanceof Float || result instanceof Double) {
                return ((Number) result).doubleValue();
            }
            return ((Number) result).longValue();
        }

        return result;
    }

    protected MyRemoteWebElement newRemoteWebElement() {
        MyRemoteWebElement toReturn = new MyRemoteWebElement();
        toReturn.setParent(driver);
        return toReturn;
    }
}
