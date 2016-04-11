package com.eurexchange.clear.frontend.trades;

import com.eurexchange.clear.common.WrappedCheckedException;
import com.eurexchange.clear.common.value.ClearConstants;
import com.eurexchange.clear.frontend.tradematch.obj.FIXMLType;
import com.eurexchange.clear.frontend.tradematch.obj.ObjectFactory;
import com.eurexchange.clear.frontend.tradematch.obj.TrdMtchRptType;

import com.eurexchange.clear.tradeimport.service.InvalidTradeMatchException;
import com.eurexchange.clear.tradeimport.service.XMLNamespaceFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.xml.bind.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MarshallerUnmarshaller  {
    private Unmarshaller unmarshaller;
    private final static Logger LOGGER = LoggerFactory.getLogger(MarshallerUnmarshaller.class);
    private static final FIXMLType FIXML_DEFAULT = new FIXMLType();
    private static final Marshaller marshaller = ToolsTradeMarshallerFactory.getInstance().createMarshaller();
    private static final ObjectFactory objectFactory = new ObjectFactory();

    public MarshallerUnmarshaller() {

    }

    public String format(String fixmlMessage) throws JAXBException {
        FIXMLType fixml = null;
        try {

            fixml = unMarshall(fixmlMessage);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return marshall(fixml);
    }

    private void setupUnmarshaller() throws JAXBException {
        this.unmarshaller = ToolsTradeMarshallerFactory.getInstance().createUnmarshaller();
        this.unmarshaller.setEventHandler(event -> {
            LOGGER.info("*** Unmarshall event: {}", event.getMessage());
            // Ignore errors
            return true;
        });
    }

    private FIXMLType unmarshalTradeReport(String xmlMessage) throws
            InvalidTradeMatchException {
        try {
            if (this.unmarshaller == null) {
                setupUnmarshaller();
            }
            SAXParserFactory factory = SAXParserFactory.newInstance();
            XMLReader reader = factory.newSAXParser().getXMLReader();
            XMLFilterImpl xmlFilter = new XMLNamespaceFilter(reader);
            reader.setContentHandler(unmarshaller.getUnmarshallerHandler());
            InputSource inputSource = new InputSource(new StringReader(xmlMessage));
            SAXSource source = new SAXSource(xmlFilter, inputSource);
            return (FIXMLType) ((JAXBElement<FIXMLType>) this.unmarshaller.unmarshal(source)).getValue();

        } catch (JAXBException | RuntimeException | SAXException | ParserConfigurationException e) {
            throw new InvalidTradeMatchException(e.getMessage(), e);
        }
    }

    public static TrdMtchRptType unmrshallTradeMatchReport(String tradeMessage) {
        return new MarshallerUnmarshaller().unmarshalTradeReport(tradeMessage).getTrdMtchRpt();
    }
    private static FIXMLType unMarshall(String message) throws WrappedCheckedException, JAXBException {
        Unmarshaller unmarshaller = ToolsTradeMarshallerFactory.getInstance().createUnmarshaller();
      //  setMessage(message);
        StringReader stringReader = new StringReader(message);

        return (FIXMLType) unmarshaller.unmarshal(stringReader);
    }

    private static String marshall(FIXMLType fixmlMessage) throws JAXBException {
        StringWriter xmlWriter = new StringWriter();
        Marshaller marshaller = ToolsTradeMarshallerFactory.getInstance().createMarshaller();
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


//    private TrdMtchRptType createTrdMtchRpt() {
//        TrdMtchRptType trdMtchRpt = objectFactory.createTrdMtchRptType();
//
//        return trdMtchRpt;
//    }
//

    public String createFiXML(TrdMtchRptType trdMtchRpt) {
    //    trdMtchRpt = createTrdMtchRpt();

        // Construct a valid FIXML class
        FIXMLType fixml = objectFactory.createFIXMLType();
        fixml.setV(FIXML_DEFAULT.getV());
        fixml.setS(FIXML_DEFAULT.getS());
        fixml.setTrdMtchRpt(trdMtchRpt);

        // Marshal FIXML class
        StringWriter xmlWriter = new StringWriter();
        try {
            marshaller.marshal(fixml, xmlWriter);
        } catch (JAXBException e) {
            throw new WrappedCheckedException("FIXML message marshalling failed", e);
        }
        return xmlWriter.toString();
    }



}
