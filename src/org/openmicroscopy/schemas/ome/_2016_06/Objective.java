//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0.1 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.02.24 at 10:51:22 AM UTC 
//


package org.openmicroscopy.schemas.ome._2016_06;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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
 *     &lt;extension base="{http://www.openmicroscopy.org/Schemas/OME/2016-06}ManufacturerSpec"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.openmicroscopy.org/Schemas/OME/2016-06}AnnotationRef" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="ID" use="required" type="{http://www.openmicroscopy.org/Schemas/OME/2016-06}ObjectiveID" /&gt;
 *       &lt;attribute name="Correction"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="UV"/&gt;
 *             &lt;enumeration value="PlanApo"/&gt;
 *             &lt;enumeration value="PlanFluor"/&gt;
 *             &lt;enumeration value="SuperFluor"/&gt;
 *             &lt;enumeration value="VioletCorrected"/&gt;
 *             &lt;enumeration value="Achro"/&gt;
 *             &lt;enumeration value="Achromat"/&gt;
 *             &lt;enumeration value="Fluor"/&gt;
 *             &lt;enumeration value="Fl"/&gt;
 *             &lt;enumeration value="Fluar"/&gt;
 *             &lt;enumeration value="Neofluar"/&gt;
 *             &lt;enumeration value="Fluotar"/&gt;
 *             &lt;enumeration value="Apo"/&gt;
 *             &lt;enumeration value="PlanNeofluar"/&gt;
 *             &lt;enumeration value="Other"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="Immersion"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="Oil"/&gt;
 *             &lt;enumeration value="Water"/&gt;
 *             &lt;enumeration value="WaterDipping"/&gt;
 *             &lt;enumeration value="Air"/&gt;
 *             &lt;enumeration value="Multi"/&gt;
 *             &lt;enumeration value="Glycerol"/&gt;
 *             &lt;enumeration value="Other"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="LensNA" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="NominalMagnification" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="CalibratedMagnification" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="WorkingDistance" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="WorkingDistanceUnit" type="{http://www.openmicroscopy.org/Schemas/OME/2016-06}UnitsLength" default="µm" /&gt;
 *       &lt;attribute name="Iris" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "annotationRef"
})
@XmlRootElement(name = "Objective")
public class Objective
    extends ManufacturerSpec
{

    @XmlElement(name = "AnnotationRef")
    protected List<AnnotationRef> annotationRef;
    @XmlAttribute(name = "ID", required = true)
    protected String id;
    @XmlAttribute(name = "Correction")
    protected String correction;
    @XmlAttribute(name = "Immersion")
    protected String immersion;
    @XmlAttribute(name = "LensNA")
    protected Float lensNA;
    @XmlAttribute(name = "NominalMagnification")
    protected Float nominalMagnification;
    @XmlAttribute(name = "CalibratedMagnification")
    protected Float calibratedMagnification;
    @XmlAttribute(name = "WorkingDistance")
    protected Float workingDistance;
    @XmlAttribute(name = "WorkingDistanceUnit")
    protected String workingDistanceUnit;
    @XmlAttribute(name = "Iris")
    protected Boolean iris;

    /**
     * Gets the value of the annotationRef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the annotationRef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnnotationRef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AnnotationRef }
     * 
     * 
     */
    public List<AnnotationRef> getAnnotationRef() {
        if (annotationRef == null) {
            annotationRef = new ArrayList<AnnotationRef>();
        }
        return this.annotationRef;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setID(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the correction property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCorrection() {
        return correction;
    }

    /**
     * Sets the value of the correction property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCorrection(String value) {
        this.correction = value;
    }

    /**
     * Gets the value of the immersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImmersion() {
        return immersion;
    }

    /**
     * Sets the value of the immersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImmersion(String value) {
        this.immersion = value;
    }

    /**
     * Gets the value of the lensNA property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getLensNA() {
        return lensNA;
    }

    /**
     * Sets the value of the lensNA property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setLensNA(Float value) {
        this.lensNA = value;
    }

    /**
     * Gets the value of the nominalMagnification property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getNominalMagnification() {
        return nominalMagnification;
    }

    /**
     * Sets the value of the nominalMagnification property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setNominalMagnification(Float value) {
        this.nominalMagnification = value;
    }

    /**
     * Gets the value of the calibratedMagnification property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getCalibratedMagnification() {
        return calibratedMagnification;
    }

    /**
     * Sets the value of the calibratedMagnification property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setCalibratedMagnification(Float value) {
        this.calibratedMagnification = value;
    }

    /**
     * Gets the value of the workingDistance property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getWorkingDistance() {
        return workingDistance;
    }

    /**
     * Sets the value of the workingDistance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setWorkingDistance(Float value) {
        this.workingDistance = value;
    }

    /**
     * Gets the value of the workingDistanceUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWorkingDistanceUnit() {
        if (workingDistanceUnit == null) {
            return "\u00b5m";
        } else {
            return workingDistanceUnit;
        }
    }

    /**
     * Sets the value of the workingDistanceUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWorkingDistanceUnit(String value) {
        this.workingDistanceUnit = value;
    }

    /**
     * Gets the value of the iris property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIris() {
        return iris;
    }

    /**
     * Sets the value of the iris property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIris(Boolean value) {
        this.iris = value;
    }

}
