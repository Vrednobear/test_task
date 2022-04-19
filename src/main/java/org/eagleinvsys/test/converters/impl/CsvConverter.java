package org.eagleinvsys.test.converters.impl;

import org.eagleinvsys.exceptions.FileProcessingException;
import org.eagleinvsys.exceptions.NullCollectionException;
import org.eagleinvsys.exceptions.NullHeaderException;
import org.eagleinvsys.test.converters.Converter;
import org.eagleinvsys.test.converters.ConvertibleCollection;
import org.eagleinvsys.test.converters.ConvertibleMessage;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CsvConverter implements Converter {

    /**
     * Converts given {@link ConvertibleCollection} to CSV and outputs result as a text to the provided {@link OutputStream}
     *
     * @param collectionToConvert collection to convert to CSV format
     * @param outputStream        output stream to write CSV conversion result as text to
     */
    @Override
    public void convert(ConvertibleCollection collectionToConvert, OutputStream outputStream) {

        if (collectionToConvert == null) {
            throw new NullCollectionException("Collection can't be null");
        }

        List<String> headers = new ArrayList<>(collectionToConvert.getHeaders());
        if (headers.isEmpty()) {
            throw new NullHeaderException("Headers of collection can`t be null or empty!");
        }

        List<ConvertibleMessage> messages = new ArrayList<>();
        for (ConvertibleMessage message : collectionToConvert.getRecords()) {
            messages.add(message);
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, UTF_8))) {
            writeHeaders(headers.iterator(), writer);
            writeMessages(messages, writer, headers);
        } catch (IOException e) {
            throw new FileProcessingException("Some exception occurred during working with file", e);
        }
    }

    private void writeHeaders(Iterator<String> headIterator, Writer writer) throws IOException {
        StringBuilder headBuilder = new StringBuilder();
        String header;
        while (headIterator.hasNext()) {
            header = headIterator.next();
            if (header == null || header.isEmpty()) {
                throw new NullHeaderException("Headers of collection can`t be null or empty!");
            }
            headBuilder.append(header);

            if (headIterator.hasNext()) {
                headBuilder.append(",");
            } else {
                headBuilder.append("\n");
            }
        }
        writer.write(headBuilder.toString());
    }

    private void writeMessages(List<ConvertibleMessage> messages, Writer writer, List<String> headers) throws IOException {
        StringBuilder messageBuilder = new StringBuilder();
        if (messages.isEmpty()) {
            return;
        }
        for (ConvertibleMessage message : messages) {
            String element;
            for (int i = 0; i < headers.size(); i++) {
                element = message.getElement(headers.get(i));
                if (element != null) {
                    messageBuilder.append(element);
                }
                if (i == headers.size() - 1) {
                    messageBuilder.append('\n');
                } else {
                    messageBuilder.append(",");
                }
            }
            writer.write(messageBuilder.toString());
            messageBuilder.setLength(0);
        }
    }
}