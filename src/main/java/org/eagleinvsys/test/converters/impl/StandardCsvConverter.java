package org.eagleinvsys.test.converters.impl;

import org.eagleinvsys.exceptions.NullCollectionException;
import org.eagleinvsys.exceptions.NullHeaderException;
import org.eagleinvsys.test.converters.ConvertibleCollection;
import org.eagleinvsys.test.converters.ConvertibleMessage;
import org.eagleinvsys.test.converters.StandardConverter;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class StandardCsvConverter implements StandardConverter {

    private final CsvConverter csvConverter;

    public StandardCsvConverter(CsvConverter csvConverter) {
        this.csvConverter = csvConverter;
    }

    /**
     * Converts given {@link List<Map>} to CSV and outputs result as a text to the provided {@link OutputStream}
     *
     * @param collectionToConvert collection to convert to CSV format. All maps must have the same set of keys
     * @param outputStream        output stream to write CSV conversion result as text to
     */
    @Override
    public void convert(List<Map<String, String>> collectionToConvert, OutputStream outputStream) {

        if (collectionToConvert == null) {
            throw new NullCollectionException("Collection can't be null");
        }

        ConvertibleCollection collection = new ConvertibleCollection() {

            @Override
            public Collection<String> getHeaders() {
                List<String> list = new ArrayList<>(collectionToConvert.get(0).keySet());
                if(list.contains("")){
                    throw new NullHeaderException("Headers of collection can`t be null or empty!");
                }
                return list;
            }

            @Override
            public Iterable<ConvertibleMessage> getRecords() {
                List<ConvertibleMessage> list = new ArrayList<>();
                for (Map<String, String> map : collectionToConvert) {
                    list.add(map::get);
                }
                return list;
            }
        };

        csvConverter.convert(collection, outputStream);

    }

}