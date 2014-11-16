package ru.vav1lon.hh.parser;

/**
 * Created by Denis Kuzmin [Vavilon]
 * E-Mail: vavilon.rus@gmail.com
 * Created date: 04.11.14
 */
public class CitiLink {

    private String name;
    private String link;
    private Integer area;

    public CitiLink(String name, String link, Integer area) {
        this.name = name;
        this.link = link;
        this.area = area;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public Integer getArea() {
        return area;
    }
}
