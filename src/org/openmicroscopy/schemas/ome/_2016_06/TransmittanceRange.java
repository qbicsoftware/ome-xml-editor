//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0.1 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.02.24 at 10:51:22 AM UTC 
//


package org.openmicroscopy.schemas.ome._2016_06;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="CutIn" type="{http://www.openmicroscopy.org/Schemas/OME/2016-06}PositiveFloat" /&gt;
 *       &lt;attribute name="CutInUnit" type="{http://www.openmicroscopy.org/Schemas/OME/2016-06}UnitsLength" default="nm" /&gt;
 *       &lt;attribute name="CutOut" type="{http://www.openmicroscopy.org/Schemas/OME/2016-06}PositiveFloat" /&gt;
 *       &lt;attribute name="CutOutUnit" type="{http://www.openmicroscopy.org/Schemas/OME/2016-06}UnitsLength" default="nm" /&gt;
 *       &lt;attribute name="CutInTolerance" type="{http://www.openmicroscopy.org/Schemas/OME/2016-06}NonNegativeFloat" /&gt;
 *       &lt;attribute name="CutInToleranceUnit" type="{http://www.openmicroscopy.org/Schemas/OME/2016-06}UnitsLength" default="nm" /&gt;
 *       &lt;attribute name="CutOutTolerance" type="{http://www.openmicroscopy.org/Schemas/OME/2016-06}NonNegativeFloat" /&gt;
 *       &lt;attribute name="CutOutToleranceUnit" type="{http://www.openmicroscopy.org/Schemas/OME/2016-06}UnitsLength" default="nm" /&gt;
 *       &lt;attribute name="Transmittance" type="{http://www.openmicroscopy.org/Schemas/OME/2016-06}PercentFraction" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "TransmittanceRange")
public class TransmittanceRange {

    @XmlAttribute(name = "CutIn")
    protected Float cutIn;
    @XmlAttribute(name = "CutInUnit")
    protected String cutInUnit;
    @XmlAttribute(name = "CutOut")
    protected Float cutOut;
    @XmlAttribute(name = "CutOutUnit")
    protected String cutOutUnit;
    @XmlAttribute(name = "CutInTolerance")
    protected Float cutInTolerance;
    @XmlAttribute(name = "CutInToleranceUnit")
    protected String cutInToleranceUnit;
    @XmlAttribute(name = "CutOutTolerance")
    protected Float cutOutTolerance;
    @XmlAttribute(name = "CutOutToleranceUnit")
    protected String cutOutToleranceUnit;
    @XmlAttribute(name = "Transmittance")
    protected Float transmittance;

    /**
     * Gets the value of the cutIn property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getCutIn() {
        return cutIn;
    }

    /**
     * Sets the value of the cutIn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setCutIn(Float value) {
        this.cutIn = value;
    }

    /**
     * Gets the value of the cutInUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCutInUnit() {
        if (cutInUnit == null) {
            return "nm";
        } else {
            return cutInUnit;
        }
    }

    /**
     * Sets the value of the cutInUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCutInUnit(String value) {
        this.cutInUnit = value;
    }

    /**
     * Gets the value of the cutOut property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getCutOut() {
        return cutOut;
    }

    /**
     * Sets the value of the cutOut property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setCutOut(Float value) {
        this.cutOut = value;
    }

    /**
     * Gets the value of the cutOutUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCutOutUnit() {
        if (cutOutUnit == null) {
            return "nm";
        } else {
            return cutOutUnit;
        }
    }

    /**
     * Sets the value of the cutOutUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCutOutUnit(String value) {
        this.cutOutUnit = value;
    }

    /**
     * Gets the value of the cutInTolerance property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getCutInTolerance() {
        return cutInTolerance;
    }

    /**
     * Sets the value of the cutInTolerance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setCutInTolerance(Float value) {
        this.cutInTolerance = value;
    }

    /**
     * Gets the value of the cutInToleranceUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCutInToleranceUnit() {
        if (cutInToleranceUnit == null) {
            return "nm";
        } else {
            return cutInToleranceUnit;
        }
    }

    /**
     * Sets the value of the cutInToleranceUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCutInToleranceUnit(String value) {
        this.cutInToleranceUnit = value;
    }

    /**
     * Gets the value of the cutOutTolerance property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getCutOutTolerance() {
        return cutOutTolerance;
    }

    /**
     * Sets the value of the cutOutTolerance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setCutOutTolerance(Float value) {
        this.cutOutTolerance = value;
    }

    /**
     * Gets the value of the cutOutToleranceUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCutOutToleranceUnit() {
        if (cutOutToleranceUnit == null) {
            return "nm";
        } else {
            return cutOutToleranceUnit;
        }
    }

    /**
     * Sets the value of the cutOutToleranceUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCutOutToleranceUnit(String value) {
        this.cutOutToleranceUnit = value;
    }

    /**
     * Gets the value of the transmittance property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getTransmittance() {
        return transmittance;
    }

    /**
     * Sets the value of the transmittance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setTransmittance(Float value) {
        this.transmittance = value;
    }

}