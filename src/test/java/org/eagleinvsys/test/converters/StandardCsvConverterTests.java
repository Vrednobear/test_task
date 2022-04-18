package org.eagleinvsys.test.converters;

import org.eagleinvsys.execption.NullCollectionException;
import org.eagleinvsys.execption.NullHeaderException;
import org.eagleinvsys.test.converters.impl.CsvConverter;
import org.eagleinvsys.test.converters.impl.StandardCsvConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StandardCsvConverterTests {
    private final List<String> testDataCsvFormat = new ArrayList<>();
    private final CsvConverter csvConverter = new CsvConverter();
    private final StandardCsvConverter standartCsvConverter = new StandardCsvConverter(csvConverter);
    private final List<Map<String, String>> collectionToConvert = new ArrayList<>();

    {
        testDataCsvFormat.add("date,country,city");
        testDataCsvFormat.add("12.06.1998,,Chelyabinsk");
        testDataCsvFormat.add("02.02.1998,Russia,Chelyabinsk");
        testDataCsvFormat.add(",USA,Boston");
        testDataCsvFormat.add("13.09.2019,Hungary,");
        testDataCsvFormat.add(",,");
    }

    @TempDir
    public Path folder;
    private Path file;

    @BeforeEach
    public void setUp() throws IOException {
        file = Files.createFile(folder.resolve("test.csv"));
        collectionToConvert.clear();
    }

    @Test
    public void filesEquality() throws IOException {

        prepareData();

        try (FileOutputStream outputStream = new FileOutputStream(file.toFile())) {
            standartCsvConverter.convert(collectionToConvert, outputStream);
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile(), StandardCharsets.UTF_8))) {
            List<String> linesFromCsvFile = reader.lines().collect(Collectors.toList());
            assertEquals(6, linesFromCsvFile.size());

            for (int i = 0; i < linesFromCsvFile.size(); i++) {
                assertEquals(testDataCsvFormat.get(i), linesFromCsvFile.get(i));
            }
        }
    }

    @Test
    public void headerIsNull() throws IOException {
        final Map<String, String> line1 = new HashMap<>();
        line1.put(null, "12.06.1998");
        line1.put("country", "Russia");
        line1.put("city", "Chelyabinsk");

        collectionToConvert.add(line1);

        try (FileOutputStream outputStream = new FileOutputStream(file.toFile())) {
            assertThrows(NullHeaderException.class,
                    () -> standartCsvConverter.convert(collectionToConvert, outputStream));
        }
    }

    @Test
    public void CollectionIsNull() throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file.toFile())) {
            assertThrows(NullCollectionException.class, () -> standartCsvConverter.convert(null, outputStream));
        }
    }

    @Test
    public void HeaderIsEmpty() throws IOException {

        final Map<String, String> line1 = new HashMap<>();
        line1.put("", "12.06.1998");
        line1.put("country", "Russia");
        line1.put("city", "Chelyabinsk");

        collectionToConvert.add(line1);

        try (FileOutputStream outputStream = new FileOutputStream(file.toFile())) {
            assertThrows(NullHeaderException.class,
                    () -> standartCsvConverter.convert(collectionToConvert, outputStream));
        }
    }

    @Test
    public void MessageCollectionIsEmpty() throws IOException {

        String testString1 = "date,country,city";
        String testString2 = ",,";

        final Map<String, String> line1 = new HashMap<>();
        line1.put("date", "");
        line1.put("country", "");
        line1.put("city", "");

        collectionToConvert.add(line1);

        try (FileOutputStream outputStream = new FileOutputStream(file.toFile())) {
            standartCsvConverter.convert(collectionToConvert, outputStream);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile(), StandardCharsets.UTF_8))) {
            List<String> linesFromCsvFile = reader.lines().collect(Collectors.toList());
            assertEquals(testString1, linesFromCsvFile.get(0));
            assertEquals(testString2, linesFromCsvFile.get(1));
        }
    }

    private void prepareData() {
        final Map<String, String> line1 = new HashMap<>();
        line1.put("date", "12.06.1998");
        line1.put("country", null);
        line1.put("city", "Chelyabinsk");

        final Map<String, String> line2 = new HashMap<>();
        line2.put("date", "02.02.1998");
        line2.put("country", "Russia");
        line2.put("city", "Chelyabinsk");

        final Map<String, String> line3 = new HashMap<>();
        line3.put("date", null);
        line3.put("country", "USA");
        line3.put("city", "Boston");

        final Map<String, String> line4 = new HashMap<>();
        line4.put("date", "13.09.2019");
        line4.put("country", "Hungary");
        line4.put("city", null);

        final Map<String, String> line5 = new HashMap<>();
        line5.put("date", null);
        line5.put("country", null);
        line5.put("city", null);

        collectionToConvert.add(line1);
        collectionToConvert.add(line2);
        collectionToConvert.add(line3);
        collectionToConvert.add(line4);
        collectionToConvert.add(line5);
    }
}