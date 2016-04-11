package com.eurexchange.clear.frontend;

import com.eurexchange.clear.common.WrappedCheckedException;
import com.eurexchange.clear.common.value.ClearConstants;
import com.eurexchange.clear.domain.IncomingTextMessage;
import com.eurexchange.clear.interfaces.FixmlMarshallerFactoryR3;
import com.eurexchange.clear.interfaces.fixml.r3.FIXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlFormatter extends IncomingTextMessage {

    private final static Logger LOGGER = LoggerFactory.getLogger(XmlFormatter.class);

    public XmlFormatter() {

    }

    public String format(String fixmlMessage) throws JAXBException {
        FIXML fixml = null;
        try {

            fixml = unMarshall(fixmlMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return marshall(fixml);
    }

    private FIXML unMarshall(String message) throws WrappedCheckedException, JAXBException {
        Unmarshaller unmarshaller = FixmlMarshallerFactoryR3.getInstance().createUnmarshaller();
        setMessage(message);
        StringReader stringReader = new StringReader(getMessage());
        return (FIXML) unmarshaller.unmarshal(stringReader);
    }

    private static String marshall(FIXML fixmlMessage) throws JAXBException {
        StringWriter xmlWriter = new StringWriter();
        Marshaller marshaller = FixmlMarshallerFactoryR3.getInstance().createMarshaller();
        xmlWriter.append(ClearConstants.XML_HEADER);
        try {
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(fixmlMessage, xmlWriter);
        } catch (PropertyException e) {
            LOGGER.error("Can not set property {}: {}", Marshaller.JAXB_FRAGMENT, e.getMessage());
        }
        return xmlWriter.toString();
    }

    public String extract_member(String member_data) {
        Pattern pattern = Pattern.compile(("\\.") + "(.{19}).*");
        Matcher matcher = pattern.matcher(member_data);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}
