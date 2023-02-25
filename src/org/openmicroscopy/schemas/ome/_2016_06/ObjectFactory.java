//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0.1 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.02.24 at 10:51:22 AM UTC 
//


package org.openmicroscopy.schemas.ome._2016_06;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openmicroscopy.schemas.ome._2016_06 package. 
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

    private final static QName _LightSourceGroup_QNAME = new QName("http://www.openmicroscopy.org/Schemas/OME/2016-06", "LightSourceGroup");
    private final static QName _MetadataOnly_QNAME = new QName("http://www.openmicroscopy.org/Schemas/OME/2016-06", "MetadataOnly");
    private final static QName _Laser_QNAME = new QName("http://www.openmicroscopy.org/Schemas/OME/2016-06", "Laser");
    private final static QName _Arc_QNAME = new QName("http://www.openmicroscopy.org/Schemas/OME/2016-06", "Arc");
    private final static QName _Filament_QNAME = new QName("http://www.openmicroscopy.org/Schemas/OME/2016-06", "Filament");
    private final static QName _LightEmittingDiode_QNAME = new QName("http://www.openmicroscopy.org/Schemas/OME/2016-06", "LightEmittingDiode");
    private final static QName _GenericExcitationSource_QNAME = new QName("http://www.openmicroscopy.org/Schemas/OME/2016-06", "GenericExcitationSource");
    private final static QName _ShapeGroup_QNAME = new QName("http://www.openmicroscopy.org/Schemas/OME/2016-06", "ShapeGroup");
    private final static QName _Rectangle_QNAME = new QName("http://www.openmicroscopy.org/Schemas/OME/2016-06", "Rectangle");
    private final static QName _Mask_QNAME = new QName("http://www.openmicroscopy.org/Schemas/OME/2016-06", "Mask");
    private final static QName _Point_QNAME = new QName("http://www.openmicroscopy.org/Schemas/OME/2016-06", "Point");
    private final static QName _Ellipse_QNAME = new QName("http://www.openmicroscopy.org/Schemas/OME/2016-06", "Ellipse");
    private final static QName _Line_QNAME = new QName("http://www.openmicroscopy.org/Schemas/OME/2016-06", "Line");
    private final static QName _Polyline_QNAME = new QName("http://www.openmicroscopy.org/Schemas/OME/2016-06", "Polyline");
    private final static QName _Polygon_QNAME = new QName("http://www.openmicroscopy.org/Schemas/OME/2016-06", "Polygon");
    private final static QName _Label_QNAME = new QName("http://www.openmicroscopy.org/Schemas/OME/2016-06", "Label");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openmicroscopy.schemas.ome._2016_06
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Screen }
     * 
     */
    public Screen createScreen() {
        return new Screen();
    }

    /**
     * Create an instance of {@link TiffData }
     * 
     */
    public TiffData createTiffData() {
        return new TiffData();
    }

    /**
     * Create an instance of {@link XMLAnnotation }
     * 
     */
    public XMLAnnotation createXMLAnnotation() {
        return new XMLAnnotation();
    }

    /**
     * Create an instance of {@link ROI }
     * 
     */
    public ROI createROI() {
        return new ROI();
    }

    /**
     * Create an instance of {@link OME }
     * 
     */
    public OME createOME() {
        return new OME();
    }

    /**
     * Create an instance of {@link Map }
     * 
     */
    public Map createMap() {
        return new Map();
    }

    /**
     * Create an instance of {@link Rights }
     * 
     */
    public Rights createRights() {
        return new Rights();
    }

    /**
     * Create an instance of {@link Project }
     * 
     */
    public Project createProject() {
        return new Project();
    }

    /**
     * Create an instance of {@link ExperimenterRef }
     * 
     */
    public ExperimenterRef createExperimenterRef() {
        return new ExperimenterRef();
    }

    /**
     * Create an instance of {@link Reference }
     * 
     */
    public Reference createReference() {
        return new Reference();
    }

    /**
     * Create an instance of {@link ExperimenterGroupRef }
     * 
     */
    public ExperimenterGroupRef createExperimenterGroupRef() {
        return new ExperimenterGroupRef();
    }

    /**
     * Create an instance of {@link DatasetRef }
     * 
     */
    public DatasetRef createDatasetRef() {
        return new DatasetRef();
    }

    /**
     * Create an instance of {@link AnnotationRef }
     * 
     */
    public AnnotationRef createAnnotationRef() {
        return new AnnotationRef();
    }

    /**
     * Create an instance of {@link Dataset }
     * 
     */
    public Dataset createDataset() {
        return new Dataset();
    }

    /**
     * Create an instance of {@link ImageRef }
     * 
     */
    public ImageRef createImageRef() {
        return new ImageRef();
    }

    /**
     * Create an instance of {@link Folder }
     * 
     */
    public Folder createFolder() {
        return new Folder();
    }

    /**
     * Create an instance of {@link FolderRef }
     * 
     */
    public FolderRef createFolderRef() {
        return new FolderRef();
    }

    /**
     * Create an instance of {@link ROIRef }
     * 
     */
    public ROIRef createROIRef() {
        return new ROIRef();
    }

    /**
     * Create an instance of {@link Experiment }
     * 
     */
    public Experiment createExperiment() {
        return new Experiment();
    }

    /**
     * Create an instance of {@link MicrobeamManipulation }
     * 
     */
    public MicrobeamManipulation createMicrobeamManipulation() {
        return new MicrobeamManipulation();
    }

    /**
     * Create an instance of {@link LightSourceSettings }
     * 
     */
    public LightSourceSettings createLightSourceSettings() {
        return new LightSourceSettings();
    }

    /**
     * Create an instance of {@link Settings }
     * 
     */
    public Settings createSettings() {
        return new Settings();
    }

    /**
     * Create an instance of {@link Plate }
     * 
     */
    public Plate createPlate() {
        return new Plate();
    }

    /**
     * Create an instance of {@link Well }
     * 
     */
    public Well createWell() {
        return new Well();
    }

    /**
     * Create an instance of {@link WellSample }
     * 
     */
    public WellSample createWellSample() {
        return new WellSample();
    }

    /**
     * Create an instance of {@link ReagentRef }
     * 
     */
    public ReagentRef createReagentRef() {
        return new ReagentRef();
    }

    /**
     * Create an instance of {@link PlateAcquisition }
     * 
     */
    public PlateAcquisition createPlateAcquisition() {
        return new PlateAcquisition();
    }

    /**
     * Create an instance of {@link WellSampleRef }
     * 
     */
    public WellSampleRef createWellSampleRef() {
        return new WellSampleRef();
    }

    /**
     * Create an instance of {@link Reagent }
     * 
     */
    public Reagent createReagent() {
        return new Reagent();
    }

    /**
     * Create an instance of {@link Screen.PlateRef }
     * 
     */
    public Screen.PlateRef createScreenPlateRef() {
        return new Screen.PlateRef();
    }

    /**
     * Create an instance of {@link Experimenter }
     * 
     */
    public Experimenter createExperimenter() {
        return new Experimenter();
    }

    /**
     * Create an instance of {@link ExperimenterGroup }
     * 
     */
    public ExperimenterGroup createExperimenterGroup() {
        return new ExperimenterGroup();
    }

    /**
     * Create an instance of {@link Leader }
     * 
     */
    public Leader createLeader() {
        return new Leader();
    }

    /**
     * Create an instance of {@link Instrument }
     * 
     */
    public Instrument createInstrument() {
        return new Instrument();
    }

    /**
     * Create an instance of {@link Microscope }
     * 
     */
    public Microscope createMicroscope() {
        return new Microscope();
    }

    /**
     * Create an instance of {@link ManufacturerSpec }
     * 
     */
    public ManufacturerSpec createManufacturerSpec() {
        return new ManufacturerSpec();
    }

    /**
     * Create an instance of {@link LightSource }
     * 
     */
    public LightSource createLightSource() {
        return new LightSource();
    }

    /**
     * Create an instance of {@link Detector }
     * 
     */
    public Detector createDetector() {
        return new Detector();
    }

    /**
     * Create an instance of {@link Objective }
     * 
     */
    public Objective createObjective() {
        return new Objective();
    }

    /**
     * Create an instance of {@link FilterSet }
     * 
     */
    public FilterSet createFilterSet() {
        return new FilterSet();
    }

    /**
     * Create an instance of {@link FilterRef }
     * 
     */
    public FilterRef createFilterRef() {
        return new FilterRef();
    }

    /**
     * Create an instance of {@link DichroicRef }
     * 
     */
    public DichroicRef createDichroicRef() {
        return new DichroicRef();
    }

    /**
     * Create an instance of {@link Filter }
     * 
     */
    public Filter createFilter() {
        return new Filter();
    }

    /**
     * Create an instance of {@link TransmittanceRange }
     * 
     */
    public TransmittanceRange createTransmittanceRange() {
        return new TransmittanceRange();
    }

    /**
     * Create an instance of {@link Dichroic }
     * 
     */
    public Dichroic createDichroic() {
        return new Dichroic();
    }

    /**
     * Create an instance of {@link Image }
     * 
     */
    public Image createImage() {
        return new Image();
    }

    /**
     * Create an instance of {@link ExperimentRef }
     * 
     */
    public ExperimentRef createExperimentRef() {
        return new ExperimentRef();
    }

    /**
     * Create an instance of {@link InstrumentRef }
     * 
     */
    public InstrumentRef createInstrumentRef() {
        return new InstrumentRef();
    }

    /**
     * Create an instance of {@link ObjectiveSettings }
     * 
     */
    public ObjectiveSettings createObjectiveSettings() {
        return new ObjectiveSettings();
    }

    /**
     * Create an instance of {@link ImagingEnvironment }
     * 
     */
    public ImagingEnvironment createImagingEnvironment() {
        return new ImagingEnvironment();
    }

    /**
     * Create an instance of {@link StageLabel }
     * 
     */
    public StageLabel createStageLabel() {
        return new StageLabel();
    }

    /**
     * Create an instance of {@link Pixels }
     * 
     */
    public Pixels createPixels() {
        return new Pixels();
    }

    /**
     * Create an instance of {@link Channel }
     * 
     */
    public Channel createChannel() {
        return new Channel();
    }

    /**
     * Create an instance of {@link DetectorSettings }
     * 
     */
    public DetectorSettings createDetectorSettings() {
        return new DetectorSettings();
    }

    /**
     * Create an instance of {@link FilterSetRef }
     * 
     */
    public FilterSetRef createFilterSetRef() {
        return new FilterSetRef();
    }

    /**
     * Create an instance of {@link LightPath }
     * 
     */
    public LightPath createLightPath() {
        return new LightPath();
    }

    /**
     * Create an instance of {@link BinData }
     * 
     */
    public BinData createBinData() {
        return new BinData();
    }

    /**
     * Create an instance of {@link TiffData.UUID }
     * 
     */
    public TiffData.UUID createTiffDataUUID() {
        return new TiffData.UUID();
    }

    /**
     * Create an instance of {@link Plane }
     * 
     */
    public Plane createPlane() {
        return new Plane();
    }

    /**
     * Create an instance of {@link MicrobeamManipulationRef }
     * 
     */
    public MicrobeamManipulationRef createMicrobeamManipulationRef() {
        return new MicrobeamManipulationRef();
    }

    /**
     * Create an instance of {@link StructuredAnnotations }
     * 
     */
    public StructuredAnnotations createStructuredAnnotations() {
        return new StructuredAnnotations();
    }

    /**
     * Create an instance of {@link TextAnnotation }
     * 
     */
    public TextAnnotation createTextAnnotation() {
        return new TextAnnotation();
    }

    /**
     * Create an instance of {@link Annotation }
     * 
     */
    public Annotation createAnnotation() {
        return new Annotation();
    }

    /**
     * Create an instance of {@link XMLAnnotation.Value }
     * 
     */
    public XMLAnnotation.Value createXMLAnnotationValue() {
        return new XMLAnnotation.Value();
    }

    /**
     * Create an instance of {@link FileAnnotation }
     * 
     */
    public FileAnnotation createFileAnnotation() {
        return new FileAnnotation();
    }

    /**
     * Create an instance of {@link TypeAnnotation }
     * 
     */
    public TypeAnnotation createTypeAnnotation() {
        return new TypeAnnotation();
    }

    /**
     * Create an instance of {@link BinaryFile }
     * 
     */
    public BinaryFile createBinaryFile() {
        return new BinaryFile();
    }

    /**
     * Create an instance of {@link External }
     * 
     */
    public External createExternal() {
        return new External();
    }

    /**
     * Create an instance of {@link ListAnnotation }
     * 
     */
    public ListAnnotation createListAnnotation() {
        return new ListAnnotation();
    }

    /**
     * Create an instance of {@link LongAnnotation }
     * 
     */
    public LongAnnotation createLongAnnotation() {
        return new LongAnnotation();
    }

    /**
     * Create an instance of {@link NumericAnnotation }
     * 
     */
    public NumericAnnotation createNumericAnnotation() {
        return new NumericAnnotation();
    }

    /**
     * Create an instance of {@link BasicAnnotation }
     * 
     */
    public BasicAnnotation createBasicAnnotation() {
        return new BasicAnnotation();
    }

    /**
     * Create an instance of {@link DoubleAnnotation }
     * 
     */
    public DoubleAnnotation createDoubleAnnotation() {
        return new DoubleAnnotation();
    }

    /**
     * Create an instance of {@link CommentAnnotation }
     * 
     */
    public CommentAnnotation createCommentAnnotation() {
        return new CommentAnnotation();
    }

    /**
     * Create an instance of {@link BooleanAnnotation }
     * 
     */
    public BooleanAnnotation createBooleanAnnotation() {
        return new BooleanAnnotation();
    }

    /**
     * Create an instance of {@link TimestampAnnotation }
     * 
     */
    public TimestampAnnotation createTimestampAnnotation() {
        return new TimestampAnnotation();
    }

    /**
     * Create an instance of {@link TagAnnotation }
     * 
     */
    public TagAnnotation createTagAnnotation() {
        return new TagAnnotation();
    }

    /**
     * Create an instance of {@link TermAnnotation }
     * 
     */
    public TermAnnotation createTermAnnotation() {
        return new TermAnnotation();
    }

    /**
     * Create an instance of {@link MapAnnotation }
     * 
     */
    public MapAnnotation createMapAnnotation() {
        return new MapAnnotation();
    }

    /**
     * Create an instance of {@link ROI.Union }
     * 
     */
    public ROI.Union createROIUnion() {
        return new ROI.Union();
    }

    /**
     * Create an instance of {@link OME.BinaryOnly }
     * 
     */
    public OME.BinaryOnly createOMEBinaryOnly() {
        return new OME.BinaryOnly();
    }

    /**
     * Create an instance of {@link Laser }
     * 
     */
    public Laser createLaser() {
        return new Laser();
    }

    /**
     * Create an instance of {@link Arc }
     * 
     */
    public Arc createArc() {
        return new Arc();
    }

    /**
     * Create an instance of {@link Filament }
     * 
     */
    public Filament createFilament() {
        return new Filament();
    }

    /**
     * Create an instance of {@link LightEmittingDiode }
     * 
     */
    public LightEmittingDiode createLightEmittingDiode() {
        return new LightEmittingDiode();
    }

    /**
     * Create an instance of {@link GenericExcitationSource }
     * 
     */
    public GenericExcitationSource createGenericExcitationSource() {
        return new GenericExcitationSource();
    }

    /**
     * Create an instance of {@link Pump }
     * 
     */
    public Pump createPump() {
        return new Pump();
    }

    /**
     * Create an instance of {@link ChannelRef }
     * 
     */
    public ChannelRef createChannelRef() {
        return new ChannelRef();
    }

    /**
     * Create an instance of {@link ProjectRef }
     * 
     */
    public ProjectRef createProjectRef() {
        return new ProjectRef();
    }

    /**
     * Create an instance of {@link Shape }
     * 
     */
    public Shape createShape() {
        return new Shape();
    }

    /**
     * Create an instance of {@link Rectangle }
     * 
     */
    public Rectangle createRectangle() {
        return new Rectangle();
    }

    /**
     * Create an instance of {@link Mask }
     * 
     */
    public Mask createMask() {
        return new Mask();
    }

    /**
     * Create an instance of {@link Point }
     * 
     */
    public Point createPoint() {
        return new Point();
    }

    /**
     * Create an instance of {@link Ellipse }
     * 
     */
    public Ellipse createEllipse() {
        return new Ellipse();
    }

    /**
     * Create an instance of {@link Line }
     * 
     */
    public Line createLine() {
        return new Line();
    }

    /**
     * Create an instance of {@link Polyline }
     * 
     */
    public Polyline createPolyline() {
        return new Polyline();
    }

    /**
     * Create an instance of {@link Polygon }
     * 
     */
    public Polygon createPolygon() {
        return new Polygon();
    }

    /**
     * Create an instance of {@link Label }
     * 
     */
    public Label createLabel() {
        return new Label();
    }

    /**
     * Create an instance of {@link AffineTransform }
     * 
     */
    public AffineTransform createAffineTransform() {
        return new AffineTransform();
    }

    /**
     * Create an instance of {@link Map.M }
     * 
     */
    public Map.M createMapM() {
        return new Map.M();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LightSource }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link LightSource }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", name = "LightSourceGroup")
    public JAXBElement<LightSource> createLightSourceGroup(LightSource value) {
        return new JAXBElement<LightSource>(_LightSourceGroup_QNAME, LightSource.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", name = "MetadataOnly")
    public JAXBElement<Object> createMetadataOnly(Object value) {
        return new JAXBElement<Object>(_MetadataOnly_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Laser }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Laser }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", name = "Laser", substitutionHeadNamespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", substitutionHeadName = "LightSourceGroup")
    public JAXBElement<Laser> createLaser(Laser value) {
        return new JAXBElement<Laser>(_Laser_QNAME, Laser.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Arc }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Arc }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", name = "Arc", substitutionHeadNamespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", substitutionHeadName = "LightSourceGroup")
    public JAXBElement<Arc> createArc(Arc value) {
        return new JAXBElement<Arc>(_Arc_QNAME, Arc.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Filament }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Filament }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", name = "Filament", substitutionHeadNamespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", substitutionHeadName = "LightSourceGroup")
    public JAXBElement<Filament> createFilament(Filament value) {
        return new JAXBElement<Filament>(_Filament_QNAME, Filament.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LightEmittingDiode }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link LightEmittingDiode }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", name = "LightEmittingDiode", substitutionHeadNamespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", substitutionHeadName = "LightSourceGroup")
    public JAXBElement<LightEmittingDiode> createLightEmittingDiode(LightEmittingDiode value) {
        return new JAXBElement<LightEmittingDiode>(_LightEmittingDiode_QNAME, LightEmittingDiode.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GenericExcitationSource }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GenericExcitationSource }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", name = "GenericExcitationSource", substitutionHeadNamespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", substitutionHeadName = "LightSourceGroup")
    public JAXBElement<GenericExcitationSource> createGenericExcitationSource(GenericExcitationSource value) {
        return new JAXBElement<GenericExcitationSource>(_GenericExcitationSource_QNAME, GenericExcitationSource.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Shape }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Shape }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", name = "ShapeGroup")
    public JAXBElement<Shape> createShapeGroup(Shape value) {
        return new JAXBElement<Shape>(_ShapeGroup_QNAME, Shape.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Rectangle }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Rectangle }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", name = "Rectangle", substitutionHeadNamespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", substitutionHeadName = "ShapeGroup")
    public JAXBElement<Rectangle> createRectangle(Rectangle value) {
        return new JAXBElement<Rectangle>(_Rectangle_QNAME, Rectangle.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Mask }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Mask }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", name = "Mask", substitutionHeadNamespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", substitutionHeadName = "ShapeGroup")
    public JAXBElement<Mask> createMask(Mask value) {
        return new JAXBElement<Mask>(_Mask_QNAME, Mask.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Point }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Point }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", name = "Point", substitutionHeadNamespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", substitutionHeadName = "ShapeGroup")
    public JAXBElement<Point> createPoint(Point value) {
        return new JAXBElement<Point>(_Point_QNAME, Point.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Ellipse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Ellipse }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", name = "Ellipse", substitutionHeadNamespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", substitutionHeadName = "ShapeGroup")
    public JAXBElement<Ellipse> createEllipse(Ellipse value) {
        return new JAXBElement<Ellipse>(_Ellipse_QNAME, Ellipse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Line }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Line }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", name = "Line", substitutionHeadNamespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", substitutionHeadName = "ShapeGroup")
    public JAXBElement<Line> createLine(Line value) {
        return new JAXBElement<Line>(_Line_QNAME, Line.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Polyline }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Polyline }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", name = "Polyline", substitutionHeadNamespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", substitutionHeadName = "ShapeGroup")
    public JAXBElement<Polyline> createPolyline(Polyline value) {
        return new JAXBElement<Polyline>(_Polyline_QNAME, Polyline.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Polygon }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Polygon }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", name = "Polygon", substitutionHeadNamespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", substitutionHeadName = "ShapeGroup")
    public JAXBElement<Polygon> createPolygon(Polygon value) {
        return new JAXBElement<Polygon>(_Polygon_QNAME, Polygon.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Label }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Label }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", name = "Label", substitutionHeadNamespace = "http://www.openmicroscopy.org/Schemas/OME/2016-06", substitutionHeadName = "ShapeGroup")
    public JAXBElement<Label> createLabel(Label value) {
        return new JAXBElement<Label>(_Label_QNAME, Label.class, null, value);
    }

}
