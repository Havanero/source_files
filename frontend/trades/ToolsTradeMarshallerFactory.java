package com.eurexchange.clear.frontend.trades;

import com.eurexchange.clear.common.WrappedCheckedException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class ToolsTradeMarshallerFactory {
    static final String TRADE_MATCH_PACKAGE = "com.eurexchange.clear.frontend.tradematch.obj";

    private static final ToolsTradeMarshallerFactory INSTANCE = new ToolsTradeMarshallerFactory();
    private JAXBContext context;

    private ToolsTradeMarshallerFactory() {

        try {
            context = JAXBContext.newInstance(TRADE_MATCH_PACKAGE);
        } catch (JAXBException e) {
            throw new WrappedCheckedException(e);
        }

    }

    public static ToolsTradeMarshallerFactory getInstance() {
        return INSTANCE;
    }

    public Unmarshaller createUnmarshaller() {
        try {
            return context.createUnmarshaller();
        } catch (JAXBException e) {
            throw new WrappedCheckedException(e);
        }
    }

    public Marshaller createMarshaller() {
        try {
            return context.createMarshaller();
        } catch (JAXBException e) {
            throw new WrappedCheckedException(e);
        }
    }
}
