package org.eagleinvsys.test.converters;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestConvertibleCollectionImpl implements ConvertibleCollection {

    List<String> headers;
    List<ConvertibleMessage> messages;

    public TestConvertibleCollectionImpl(List<String> headers, List<ConvertibleMessage> messages) {
        this.headers = headers;
        this.messages = messages;
    }

    @Override
    public Collection<String> getHeaders() {
        return headers;
    }

    @Override
    public Iterable<ConvertibleMessage> getRecords() {
        return messages;
    }


    static class ConvertibleMessageImpl implements ConvertibleMessage {
        Map<String, String> elements = new HashMap<>();

        public ConvertibleMessageImpl(List<String> headers, String... elements) {
            for (int i = 0; i < headers.size(); i++) {
                this.elements.put(headers.get(i), elements[i]);
            }
        }

        @Override
        public String getElement(String elementId) {
            return elements.get(elementId);
        }
    }
}
