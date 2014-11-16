package ru.vav1lon.hh.parser;

import java.util.List;

/**
 * Created by Denis Kuzmin [Vavilon]
 * E-Mail: vavilon.rus@gmail.com
 * Created date: 04.11.14
 */
public class ProfValue {

    private String prof;
    private List<CityValue> values;

    public ProfValue(String prof, List<CityValue> values) {
        this.prof = prof;
        this.values = values;
    }

    public String getProf() {
        return prof;
    }

    public void setProf(String prof) {
        this.prof = prof;
    }

    public List<CityValue> getValues() {
        return values;
    }

    public void setValues(List<CityValue> values) {
        this.values = values;
    }
}
