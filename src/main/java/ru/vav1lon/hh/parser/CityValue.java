package ru.vav1lon.hh.parser;

/**
 * Created by Denis Kuzmin [Vavilon]
 * E-Mail: vavilon.rus@gmail.com
 * Created date: 04.11.14
 */
public class CityValue {

    private Integer max;
    private Integer min;

    public CityValue(Integer max, Integer min) {
        this.max = max;
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }
}
