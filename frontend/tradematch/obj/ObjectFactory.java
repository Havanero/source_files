//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.02.24 at 07:03:14 PM CET 
//


package com.eurexchange.clear.frontend.tradematch.obj;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.eurexchange.clear.frontend.tradematch.obj package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _FIXML_QNAME = new QName("", "FIXML");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.eurexchange.clear.frontend.tradematch.obj
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FIXMLType }
     * 
     */
    public FIXMLType createFIXMLType() {
        return new FIXMLType();
    }

    /**
     * Create an instance of {@link ApplSeqCtrlType }
     * 
     */
    public ApplSeqCtrlType createApplSeqCtrlType() {
        return new ApplSeqCtrlType();
    }

    /**
     * Create an instance of {@link InstrmtMtchSideType }
     * 
     */
    public InstrmtMtchSideType createInstrmtMtchSideType() {
        return new InstrmtMtchSideType();
    }

    /**
     * Create an instance of {@link TrdMtchSideType }
     * 
     */
    public TrdMtchSideType createTrdMtchSideType() {
        return new TrdMtchSideType();
    }

    /**
     * Create an instance of {@link InstrmtType }
     * 
     */
    public InstrmtType createInstrmtType() {
        return new InstrmtType();
    }

    /**
     * Create an instance of {@link PtyType }
     * 
     */
    public PtyType createPtyType() {
        return new PtyType();
    }

    /**
     * Create an instance of {@link TrdMtchRptType }
     * 
     */
    public TrdMtchRptType createTrdMtchRptType() {
        return new TrdMtchRptType();
    }

    /**
     * Create an instance of {@link HdrType }
     * 
     */
    public HdrType createHdrType() {
        return new HdrType();
    }

    /**
     * Create an instance of {@link TrdRegTSType }
     * 
     */
    public TrdRegTSType createTrdRegTSType() {
        return new TrdRegTSType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FIXMLType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "FIXML")
    public JAXBElement<FIXMLType> createFIXML(FIXMLType value) {
        return new JAXBElement<FIXMLType>(_FIXML_QNAME, FIXMLType.class, null, value);
    }

}