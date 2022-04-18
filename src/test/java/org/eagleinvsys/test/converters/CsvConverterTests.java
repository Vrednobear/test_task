package org.eagleinvsys.test.converters;

import org.eagleinvsys.execption.NullCollectionException;
import org.eagleinvsys.execption.NullHeaderException;
import org.eagleinvsys.test.converters.TestConvertibleCollectionImpl.ConvertibleMessageImpl;
import org.eagleinvsys.test.converters.impl.CsvConverter;
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
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class CsvConverterTests {

    private final List<String> testDataCsvFormat = new ArrayList<>();
    private final CsvConverter csvConverter = new CsvConverter();
    private final List<String> headers = new ArrayList<>();
    private final List<ConvertibleMessage> messages = new ArrayList<>();

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
        headers.clear();
        messages.clear();
    }

    @Test
    public void filesEquality() throws IOException {
        headers.add("date");
        headers.add("country");
        headers.add("city");

        messages.add(new ConvertibleMessageImpl(headers, "12.06.1998", null, "Chelyabinsk"));
        messages.add(new ConvertibleMessageImpl(headers, "02.02.1998", "Russia", "Chelyabinsk"));
        messages.add(new ConvertibleMessageImpl(headers, null, "USA", "Boston"));
        messages.add(new ConvertibleMessageImpl(headers, "13.09.2019", "Hungary", null));
        messages.add(new ConvertibleMessageImpl(headers, null, null, null));

        try (FileOutputStream outputStream = new FileOutputStream(file.toFile())) {
            csvConverter.convert(new TestConvertibleCollectionImpl(headers, messages), outputStream);
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
        headers.add("date");
        headers.add("country");
        headers.add(null);

        messages.add(new ConvertibleMessageImpl(headers, "12.06.1998", null, "Chelyabinsk"));
        messages.add(new ConvertibleMessageImpl(headers, "02.02.1998", "Russia", "Chelyabinsk"));
        messages.add(new ConvertibleMessageImpl(headers, null, "USA", "Boston"));
        messages.add(new ConvertibleMessageImpl(headers, "13.09.2019", "Hungary", null));
        messages.add(new ConvertibleMessageImpl(headers, null, null, null));

        try (FileOutputStream outputStream = new FileOutputStream(file.toFile())) {
            assertThrows(NullHeaderException.class,
                    () -> csvConverter.convert(new TestConvertibleCollectionImpl(headers, messages), outputStream));
        }
    }

    @Test
    public void HeadersListIsEmpty() throws IOException {

        messages.add(new ConvertibleMessageImpl(headers, "12.06.1998", null, "Chelyabinsk"));

        try (FileOutputStream outputStream = new FileOutputStream(file.toFile())) {
            assertThrows(NullHeaderException.class,
                    () -> csvConverter.convert(new TestConvertibleCollectionImpl(headers, messages), outputStream));
        }
    }

    @Test
    public void MessageCollectionIsEmpty() throws IOException {
        headers.add("date");
        headers.add("country");
        headers.add("city");

        String testString = "date,country,city";

        try (FileOutputStream outputStream = new FileOutputStream(file.toFile())) {
            csvConverter.convert(new TestConvertibleCollectionImpl(headers, messages), outputStream);
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile(), StandardCharsets.UTF_8))) {
            List<String> linesFromCsvFile = reader.lines().collect(Collectors.toList());
            for (String line : linesFromCsvFile) {
                assertEquals(testString, line);
            }
        }
    }

    @Test
    public void CollectionIsNull() throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file.toFile())) {
            assertThrows(NullCollectionException.class, () -> csvConverter.convert(null, outputStream));
        }
    }


}
