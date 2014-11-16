package ru.vav1lon.hh.parser;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

/**
 * Created by Denis Kuzmin [Vavilon]
 * E-Mail: vavilon.rus@gmail.com
 * Created date: 04.11.14
 */
public class ParseHH {

    private final static CharMatcher ASCII_DIGITS = CharMatcher.inRange('0', '9').precomputed();
    private final static String SEPARETE = "—";
    private final static String PATH = "/search/vacancy";


    public static void main(String[] args) {

        Map<String, List<ProfValue>> mapaa = new HashMap<String, List<ProfValue>>();

        Map<CitiLink, List<String>> requerid = new HashMap<CitiLink, List<String>>();

        List<String> profs = Arrays.asList("java", "php", "ruby", "javascript");
        requerid.put(new CitiLink("Новосибирск", "http://novosibirsk.hh.ru", 4), profs);
        requerid.put(new CitiLink("Екатеринбург", "http://ekaterinburg.hh.ru", 3), profs);
        requerid.put(new CitiLink("Самара", "http://samara.hh.ru", 78), profs);
        requerid.put(new CitiLink("Москва", "http://hh.ru", 1), profs);
        requerid.put(new CitiLink("Санкт-Петербург", "http://spb.hh.ru", 2), profs);
        requerid.put(new CitiLink("Калининград", "http://kaliningrad.hh.ru", 0), profs);
        requerid.put(new CitiLink("Севастополь", "http://sevastopol.hh.ru", 0), profs);
//        requerid.put(new CitiLink("Минск", "http://jobs.tut.by/", 0), profs);

//        String city = "Новосибирск";
//        String url = "http://novosibirsk.hh.ru";
//        String vacName = "java";
//        String russian = "4";

        for (Map.Entry<CitiLink, List<String>> entry : requerid.entrySet()) {
            mapaa.put(entry.getKey().getName(), read(entry));
        }

        Comparator comparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return o1.equals(o2) ? 0 : (Integer) o1 > (Integer) o2 ? 1 : -1;
            }
        };
        SortedSet<Integer> maxValue;
        SortedSet<Integer> minValue;

        System.out.println(" == ");
        System.out.println("   == ");
        System.out.println("     == ");

        for (Map.Entry<String, List<ProfValue>> entry : mapaa.entrySet()) {

            maxValue = new TreeSet<Integer>(comparator);
            minValue = new TreeSet<Integer>(comparator);

            for (ProfValue profValue : entry.getValue()) {

                for (CityValue cityValue : profValue.getValues()) {
                    maxValue.add(cityValue.getMax());
                    minValue.add(cityValue.getMin());
                }

                Integer sedMax;
                if (maxValue.size() < 3) {
                    if (maxValue.size() == 0) {
                        sedMax = 0;
                    } else {
                        sedMax = maxValue.last();
                    }
                } else {
                    sedMax = maxValue.size() / 2;
                    sedMax = (Integer) maxValue.toArray()[sedMax];
                }

                Integer sedMin;
                if (minValue.size() < 3) {
                    if (minValue.size() == 0) {
                        sedMin = 0;
                    } else {
                        sedMin = minValue.last();
                    }
                } else {
                    sedMin = minValue.size() / 2;
                    sedMin = (Integer) minValue.toArray()[sedMin];
                }

                System.out.println("Город: " + entry.getKey() + ", профессия: " + profValue.getProf() + ", Avg max: " + (sedMax > sedMin ? sedMax : sedMin) + ", Avg min: " + (sedMin < sedMax ? sedMin : sedMax));
            }

        }

        System.out.println("     == ");
        System.out.println("   == ");
        System.out.println(" == ");
    }

    private static List<ProfValue> read(Map.Entry<CitiLink, List<String>> entry) {
        List<ProfValue> result = new ArrayList<ProfValue>();
        StringBuffer urlBuild;
        Document document;
        for (String prof : entry.getValue()) {

            try {
                urlBuild = new StringBuffer(entry.getKey().getLink() + PATH);
                if (entry.getKey().getArea() != 0) {
                    urlBuild.append("?area=").append(entry.getKey().getArea());
                } else {
                    urlBuild.append("?only_with_salary=false&clusters=true");
                }
                urlBuild.append("&text=").append(prof);
                if (entry.getKey().getArea() != 0) {
                    urlBuild.append("&specialization=");
                }
                urlBuild.append("&salary=&currency_code=RUR");
                document = Jsoup.connect(urlBuild.toString()).get();

                result.add(new ProfValue(prof, paggination(entry.getKey().getLink(), urlBuild.toString(), document)));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static List<CityValue> paggination(String main, String url, Document document) {
        Document page;
        Elements pages = document.select(".paging-item");

        List<CityValue> result = new ArrayList<CityValue>();
        List<CityValue> value;

        Integer curPage = 1;
        for (Element element : pages) {

            if (curPage == 1 && element.classNames().contains("paging-item_active")) {

                value = readPage(document);
                if (value == null) {
                    throw new NullPointerException("pusto, net tega");
                }
                result.addAll(value);
            }

            if (element.classNames().size() == 1 && curPage < Integer.parseInt(element.text())) {
                if (!Strings.isNullOrEmpty(element.attr("href"))) {
                    try {
                        page = Jsoup.connect(main + PATH + element.attr("href")).get();
                        value = readPage(page);
                        if (value == null) {
                            throw new NullPointerException("pusto, net tega");
                        }
                        result.addAll(value);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                curPage++;
            }
        }
        return result;
    }

    private static List<CityValue> readPage(Document document) {
        Elements newsHeadlines = document.select(".vacancy-list-item__salary");
        List<CityValue> result = new ArrayList<CityValue>();

        Integer max;
        Integer min;
        for (Element element : newsHeadlines) {

            if (element.childNodes().size() > 0 && !element.childNode(0).toString().contains("USD")) {

                String text = element.childNode(0).toString();

                if (text.contains(SEPARETE)) {
                    max = Integer.parseInt(ASCII_DIGITS.retainFrom(text.split(SEPARETE)[1]));
                    min = Integer.parseInt(ASCII_DIGITS.retainFrom(text.split(SEPARETE)[0]));
                    result.add(new CityValue(min, max));
                } else if (text.contains("от")) {
                    min = Integer.parseInt(ASCII_DIGITS.retainFrom(text));
                    result.add(new CityValue(0, min));
                } else if (text.contains("до")) {
                    max = Integer.parseInt(ASCII_DIGITS.retainFrom(text));
                    result.add(new CityValue(max, 0));
                } else {
                    max = Integer.parseInt(ASCII_DIGITS.retainFrom(text));
                    result.add(new CityValue(max, 0));
                }
            }
        }
        return result;
    }

}
