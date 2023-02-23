package de.qbic.xml_edit;

import loci.common.DataTools;
import loci.common.DebugTools;
import loci.common.Location;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.*;
import loci.formats.gui.AWTImageTools;
import loci.formats.gui.BufferedImageReader;
import loci.formats.gui.BufferedImageWriter;
import loci.formats.gui.ImageViewer;
import loci.formats.in.DynamicMetadataOptions;
import loci.formats.in.MetadataLevel;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.out.OMETiffWriter;
import loci.formats.services.OMEXMLService;
import loci.formats.services.OMEXMLServiceImpl;
import loci.formats.tools.AsciiImage;
import net.imagej.ImageJ;
import net.imglib2.type.numeric.RealType;
import ome.xml.meta.MetadataRoot;
import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.OMEModel;
import ome.xml.model.OMEModelImpl;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.StringTokenizer;

@Plugin(type = Command.class, menuPath = "Plugins>XML-Editor")
public class XMLEditor<T extends RealType<T>> implements Command {

    // -- Constants --

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLEditor.class);
    private static final String NEWLINE = System.getProperty("line.separator");

    // -- Fields --

    private String id = null;
    private boolean printVersion = false;
    private boolean pixels = true;
    ImageViewer viewer = new ImageViewer(false);
    private boolean doCore = true;

    private boolean doMeta = true;
    private boolean filter = true;
    private boolean thumbs = false;
    private boolean minmax = false;
    private boolean merge = false;
    private boolean stitch = false;
    private boolean group = true;
    private boolean separate = false;
    private boolean expand = false;
    private boolean omexml = false;
    private boolean cache = false;
    private boolean originalMetadata = true;
    private boolean normalize = false;
    private boolean fastBlit = false;
    private boolean autoscale = false;
    private boolean preload = false;
    private boolean ascii = false;
    private boolean usedFiles = true;
    private boolean omexmlOnly = false;
    private boolean validate = true;
    private boolean flat = true;
    private String omexmlVersion = null;
    private int start = 0;
    private int end = Integer.MAX_VALUE;
    private int series = 0;
    private int resolution = 0;
    private int xCoordinate = 0, yCoordinate = 0, width = 0, height = 0;
    private String swapOrder = null, shuffleOrder = null;
    private String map = null;
    private String format = null;
    private String cachedir = null;
    public LinkedList<XMLChange> changeHistory;
    public Document xml_doc;

    public Element xmlElement;
    private int xmlSpaces = 3;
    private DynamicMetadataOptions options = new DynamicMetadataOptions();

    private IFormatReader reader;
    private IFormatReader baseReader;
    private MinMaxCalculator minMaxCalc;
    private DimensionSwapper dimSwapper;
    private BufferedImageReader biReader;
    private GUI myGUI;

    // -- ImageInfo methods --

    public boolean parseArgs(String[] args) {
        id = null;
        printVersion = false;
        pixels = true;
        doCore = true;
        doMeta = true;
        filter = true;
        thumbs = false;
        minmax = false;
        merge = false;
        stitch = false;
        group = true;
        separate = false;
        expand = false;
        omexml = false;
        cache = false;
        originalMetadata = true;
        normalize = false;
        fastBlit = false;
        autoscale = false;
        preload = false;
        usedFiles = true;
        omexmlOnly = false;
        validate = true;
        flat = true;
        omexmlVersion = null;
        xmlSpaces = 3;
        start = 0;
        end = Integer.MAX_VALUE;
        series = 0;
        resolution = 0;
        xCoordinate = 0;
        yCoordinate = 0;
        width = 0;
        height = 0;
        swapOrder = null;
        shuffleOrder = null;
        map = null;
        cachedir = null;
        if (args == null) return false;
        for (int i=0; i<args.length; i++) {
            if (args[i].startsWith("-")) {
                if (args[i].equals("CommandLineTools.VERSION")){
                    printVersion = true;
                    return true;
                }
                else if (args[i].equals("-nopix")) pixels = false;
                else if (args[i].equals("-nocore")) doCore = false;
                else if (args[i].equals("-nometa")) doMeta = false;
                else if (args[i].equals("-nofilter")) filter = false;
                else if (args[i].equals("-thumbs")) thumbs = true;
                else if (args[i].equals("-minmax")) minmax = true;
                else if (args[i].equals("-merge")) merge = true;
                else if (args[i].equals("-stitch")) stitch = true;
                else if (args[i].equals("-nogroup")) group = false;
                else if (args[i].equals("-separate")) separate = true;
                else if (args[i].equals("-expand")) expand = true;
                else if (args[i].equals("-cache")) cache = true;
                else if (args[i].equals("-omexml")) omexml = true;
                else if (args[i].equals("-no-sas")) originalMetadata = false;
                else if (args[i].equals("-normalize")) normalize = true;
                else if (args[i].equals("-fast")) fastBlit = true;
                else if (args[i].equals("-autoscale")) {
                    fastBlit = true;
                    autoscale = true;
                }
                else if (args[i].equals("-novalid")) validate = false;
                else if (args[i].equals("-validate")) validate = true;
                else if (args[i].equals("-noflat")) flat = false;
                else if (args[i].equals("-debug")) {
                    DebugTools.setRootLevel("DEBUG");
                }
                else if (args[i].equals("-trace")) {
                    DebugTools.setRootLevel("TRACE");
                }
                else if (args[i].equals("-omexml-only")) {
                    omexmlOnly = true;
                    omexml = true;
                    DebugTools.setRootLevel("OFF");
                }
                else if (args[i].equals("-preload")) preload = true;
                else if (args[i].equals("-ascii")) ascii = true;
                else if (args[i].equals("-nousedfiles")) usedFiles = false;
                else if (args[i].equals("-xmlversion")) omexmlVersion = args[++i];
                else if (args[i].equals("-xmlspaces")) {
                    xmlSpaces = Integer.parseInt(args[++i]);
                }
                else if (args[i].equals("-crop")) {
                    StringTokenizer st = new StringTokenizer(args[++i], ",");
                    xCoordinate = Integer.parseInt(st.nextToken());
                    yCoordinate = Integer.parseInt(st.nextToken());
                    width = Integer.parseInt(st.nextToken());
                    height = Integer.parseInt(st.nextToken());
                }
                else if (args[i].equals("-range")) {
                    try {
                        start = Integer.parseInt(args[++i]);
                        end = Integer.parseInt(args[++i]);
                    }
                    catch (NumberFormatException exc) { }
                }
                else if (args[i].equals("-series")) {
                    try {
                        series = Integer.parseInt(args[++i]);
                    }
                    catch (NumberFormatException exc) { }
                }
                else if (args[i].equals("-resolution")) {
                    try {
                        resolution = Integer.parseInt(args[++i]);
                    }
                    catch (NumberFormatException exc) { }
                }
                else if (args[i].equals("-swap")) {
                    swapOrder = args[++i].toUpperCase();
                }
                else if (args[i].equals("-shuffle")) {
                    shuffleOrder = args[++i].toUpperCase();
                }
                else if (args[i].equals("-map")) map = args[++i];
                else if (args[i].equals("-format")) format = args[++i];
                else if (args[i].equals("-cache-dir")) {
                    cache = true;
                    cachedir = args[++i];
                }
                else if (args[i].equals("-option")) {
                    options.set(args[++i], args[++i]);
                }
                else if (!args[i].equals("CommandLineTools.NO_UPGRADE_CHECK")) {
                    LOGGER.error("Found unknown command flag: {}; exiting.", args[i]);
                    return false;
                }
            }
            else {
                if (id == null) id = args[i];
                else {
                    LOGGER.error("Found unknown argument: {}; exiting.", args[i]);
                    return false;
                }
            }
        }
        return true;
    }

    public void createReader() {
        if (reader != null) return; // reader was set programmatically
        if (format != null) {
            // create reader of a specific format type
            try {
                Class<?> c = Class.forName("loci.formats.in." + format + "Reader");
                reader = (IFormatReader) c.newInstance();
            }
            catch (ClassNotFoundException exc) {
                LOGGER.warn("Unknown reader: {}", format);
                LOGGER.debug("", exc);
            }
            catch (InstantiationException exc) {
                LOGGER.warn("Cannot instantiate reader: {}", format);
                LOGGER.debug("", exc);
            }
            catch (IllegalAccessException exc) {
                LOGGER.warn("Cannot access reader: {}", format);
                LOGGER.debug("", exc);
            }
        }
        if (reader == null) reader = new ImageReader();
        baseReader = reader;
    }

    public void configureReaderPreInit() throws FormatException, IOException {
        if (omexml) {
            reader.setOriginalMetadataPopulated(originalMetadata);
            try {
                ServiceFactory factory = new ServiceFactory();
                OMEXMLService service = factory.getInstance(OMEXMLService.class);
                reader.setMetadataStore(
                        service.createOMEXMLMetadata(null, omexmlVersion));
            }
            catch (DependencyException de) {
                throw new MissingLibraryException(OMEXMLServiceImpl.NO_OME_XML_MSG, de);
            }
            catch (ServiceException se) {
                throw new FormatException(se);
            }
        }

        // check file format
        if (reader instanceof ImageReader) {
            // determine format
            ImageReader ir = (ImageReader) reader;
            if (new Location(id).exists()) {
                LOGGER.info("Checking file format [{}]", ir.getFormat(id));
            }
        }
        else {
            // verify format
            LOGGER.info("Checking {} format [{}]", reader.getFormat(),
                    reader.isThisType(id) ? "yes" : "no");
        }

        LOGGER.info("Initializing reader");
        if (stitch) {
            reader = new FileStitcher(reader, true);
            Location f = new Location(id);
            String pat = null;
            if (!f.exists()) {
                ((FileStitcher) reader).setUsingPatternIds(true);
                pat = id;
            }
            else {
                pat = FilePattern.findPattern(f);
            }
            if (pat != null) id = pat;
        }
        if (expand) reader = new ChannelFiller(reader);
        if (separate) reader = new ChannelSeparator(reader);
        if (merge) reader = new ChannelMerger(reader);
        if (cache) {
            if (cachedir != null) {
                reader  = new Memoizer(reader, 0, new File(cachedir));
            } else {
                reader = new Memoizer(reader, 0);
            }
        }
        minMaxCalc = null;
        if (minmax || autoscale) reader = minMaxCalc = new MinMaxCalculator(reader);
        dimSwapper = null;
        if (swapOrder != null || shuffleOrder != null) {
            reader = dimSwapper = new DimensionSwapper(reader);
        }
        reader = biReader = new BufferedImageReader(reader);

        reader.close();
        reader.setNormalized(normalize);
        reader.setMetadataFiltered(filter);
        reader.setGroupFiles(group);
        options.setMetadataLevel(
                doMeta ? MetadataLevel.ALL : MetadataLevel.MINIMUM);
        options.setValidate(validate);
        reader.setMetadataOptions(options);
        reader.setFlattenedResolutions(flat);
    }

    public void configureReaderPostInit() {
        if (swapOrder != null) dimSwapper.swapDimensions(swapOrder);
        if (shuffleOrder != null) dimSwapper.setOutputOrder(shuffleOrder);
    }
    public void checkWarnings() {
        if (!normalize && (reader.getPixelType() == FormatTools.FLOAT ||
                reader.getPixelType() == FormatTools.DOUBLE))
        {
            LOGGER.warn("");
            LOGGER.warn("Java does not support " +
                    "display of unnormalized floating point data.");
            LOGGER.warn("Please use the '-normalize' option " +
                    "to avoid receiving a cryptic exception.");
        }

        if (reader.isRGB() && reader.getRGBChannelCount() > 4) {
            LOGGER.warn("");
            LOGGER.warn("Java does not support merging more than 4 channels.");
            LOGGER.warn("Please use the '-separate' option " +
                    "to avoid losing channels beyond the 4th.");
        }
    }

    public void readCoreMetadata() throws FormatException, IOException {
        if (!doCore) return; // skip core metadata printout

        // read basic metadata
        LOGGER.info("");
        LOGGER.info("Reading core metadata");
        LOGGER.info("{} = {}", stitch ? "File pattern" : "filename",
                stitch ? id : reader.getCurrentFile());
        if (map != null) LOGGER.info("Mapped filename = {}", map);
        if (usedFiles) {
            String[] used = reader.getUsedFiles();
            boolean usedValid = used != null && used.length > 0;
            if (usedValid) {
                for (int u=0; u<used.length; u++) {
                    if (used[u] == null) {
                        usedValid = false;
                        break;
                    }
                }
            }
            if (!usedValid) {
                LOGGER.warn("************ invalid used files list ************");
            }
            if (used == null) {
                LOGGER.info("Used files = null");
            }
            else if (used.length == 0) {
                LOGGER.info("Used files = []");
            }
            else if (used.length > 1) {
                LOGGER.info("Used files:");
                for (int u=0; u<used.length; u++) LOGGER.info("\t{}", used[u]);
            }
            else if (!id.equals(used[0])) {
                LOGGER.info("Used files = [{}]", used[0]);
            }
        }
        int seriesCount = reader.getSeriesCount();
        LOGGER.info("Series count = {}", seriesCount);
        MetadataStore ms = reader.getMetadataStore();
        MetadataRetrieve mr = ms instanceof MetadataRetrieve ?
                (MetadataRetrieve) ms : null;
        for (int j=0; j<seriesCount; j++) {
            reader.setSeries(j);

            // read basic metadata for series #i
            int imageCount = reader.getImageCount();
            int resolutions = reader.getResolutionCount();
            boolean rgb = reader.isRGB();
            int sizeX = reader.getSizeX();
            int sizeY = reader.getSizeY();
            int sizeZ = reader.getSizeZ();
            int sizeC = reader.getSizeC();
            int sizeT = reader.getSizeT();
            int pixelType = reader.getPixelType();
            int validBits = reader.getBitsPerPixel();
            int effSizeC = reader.getEffectiveSizeC();
            int rgbChanCount = reader.getRGBChannelCount();
            boolean indexed = reader.isIndexed();
            boolean falseColor = reader.isFalseColor();
            byte[][] table8 = reader.get8BitLookupTable();
            short[][] table16 = reader.get16BitLookupTable();
            Modulo moduloZ = reader.getModuloZ();
            Modulo moduloC = reader.getModuloC();
            Modulo moduloT = reader.getModuloT();
            int thumbSizeX = reader.getThumbSizeX();
            int thumbSizeY = reader.getThumbSizeY();
            int tileSizeX = reader.getOptimalTileWidth();
            int tileSizeY = reader.getOptimalTileHeight();
            boolean little = reader.isLittleEndian();
            String dimOrder = reader.getDimensionOrder();
            boolean orderCertain = reader.isOrderCertain();
            boolean thumbnail = reader.isThumbnailSeries();
            boolean interleaved = reader.isInterleaved();
            boolean metadataComplete = reader.isMetadataComplete();

            // output basic metadata for series #i
            String seriesName = mr == null ? null : mr.getImageName(j);
            LOGGER.info("Series #{}{}{}:",
                    new Object[] {j, seriesName == null ? " " : " -- ",
                            seriesName == null ? "" : seriesName});

            if (flat == false && resolutions > 1) {
                LOGGER.info("\tResolutions = {}", resolutions);
                for (int i = 0; i < resolutions; i++) {
                    reader.setResolution(i);
                    LOGGER.info("\t\tsizeX[{}] = {}", i, reader.getSizeX());
                }
                reader.setResolution(0);
            }
            LOGGER.info("\tImage count = {}", imageCount);
            LOGGER.info("\tRGB = {} ({}) {}", new Object[] {rgb, rgbChanCount,
                    merge ? "(merged)" : separate ? "(separated)" : ""});
            if (rgb != (rgbChanCount != 1)) {
                LOGGER.warn("\t************ RGB mismatch ************");
            }
            LOGGER.info("\tInterleaved = {}", interleaved);

            StringBuilder sb = new StringBuilder();
            sb.append("\tIndexed = ");
            sb.append(indexed);
            sb.append(" (");
            sb.append(!falseColor);
            sb.append(" color");
            if (table8 != null) {
                sb.append(", 8-bit LUT: ");
                sb.append(table8.length);
                sb.append(" x ");
                sb.append(table8[0] == null ? "null" : "" + table8[0].length);
            }
            if (table16 != null) {
                sb.append(", 16-bit LUT: ");
                sb.append(table16.length);
                sb.append(" x ");
                sb.append(table16[0] == null ? "null" : "" + table16[0].length);
            }
            sb.append(")");
            LOGGER.info(sb.toString());

            if (table8 != null && table16 != null) {
                LOGGER.warn("\t************ multiple LUTs ************");
            }
            LOGGER.info("\tWidth = {}", sizeX);
            LOGGER.info("\tHeight = {}", sizeY);

            if (imageCount != sizeZ * effSizeC * sizeT) {
                LOGGER.info("\t************ ZCT mismatch ************");
            }
            LOGGER.info("\tTile size = {} x {}", tileSizeX, tileSizeY);
            LOGGER.info("\tThumbnail size = {} x {}", thumbSizeX, thumbSizeY);
            LOGGER.info("\tEndianness = {}",
                    little ? "intel (little)" : "motorola (big)");
            LOGGER.info("\tDimension order = {} ({})", dimOrder,
                    orderCertain ? "certain" : "uncertain");
            LOGGER.info("\tPixel type = {}",
                    FormatTools.getPixelTypeString(pixelType));
            LOGGER.info("\tValid bits per pixel = {}", validBits);
            LOGGER.info("\tMetadata complete = {}", metadataComplete);
            LOGGER.info("\tThumbnail series = {}", thumbnail);
            if (doMeta) {
                LOGGER.info("\t-----");
                int[] indices;
                if (imageCount > 6) {
                    int q = imageCount / 2;
                    indices = new int[] {
                            0, q - 2, q - 1, q, q + 1, q + 2, imageCount - 1
                    };
                }
                else if (imageCount > 2) {
                    indices = new int[] {0, imageCount / 2, imageCount - 1};
                }
                else if (imageCount > 1) indices = new int[] {0, 1};
                else indices = new int[] {0};
                int[][] zct = new int[indices.length][];
                int[] indices2 = new int[indices.length];

                sb.setLength(0);
                for (int i=0; i<indices.length; i++) {
                    zct[i] = reader.getZCTCoords(indices[i]);
                    indices2[i] = reader.getIndex(zct[i][0], zct[i][1], zct[i][2]);
                    sb.append("\tPlane #");
                    sb.append(indices[i]);
                    sb.append(" <=> Z ");
                    sb.append(zct[i][0]);
                    sb.append(", C ");
                    sb.append(zct[i][1]);
                    sb.append(", T ");
                    sb.append(zct[i][2]);
                    if (indices[i] != indices2[i]) {
                        sb.append(" [mismatch: ");
                        sb.append(indices2[i]);
                        sb.append("]");
                        sb.append(NEWLINE);
                    }
                    else sb.append(NEWLINE);
                }
                LOGGER.info(sb.toString());
            }
        }
    }
    public void readPixels() throws FormatException, IOException {
        String seriesLabel = reader.getSeriesCount() > 1 ?
                (" series #" + series) : "";
        LOGGER.info("");

        int num = reader.getImageCount();
        if (start < 0) start = 0;
        if (start >= num) start = num - 1;
        if (end < 0) end = 0;
        if (end >= num) end = num - 1;
        if (end < start) end = start;

        LOGGER.info("Reading{} pixel data ({}-{})",
                new Object[] {seriesLabel, start, end});

        int sizeX = reader.getSizeX();
        int sizeY = reader.getSizeY();
        if (width == 0) width = sizeX;
        if (height == 0) height = sizeY;

        int pixelType = reader.getPixelType();

        BufferedImage[] images = new BufferedImage[end - start + 1];
        long s = System.currentTimeMillis();
        long timeLastLogged = s;
        for (int i=start; i<=end; i++) {
            if (!fastBlit) {
                images[i - start] = thumbs ? biReader.openThumbImage(i) :
                        biReader.openImage(i, xCoordinate, yCoordinate, width, height);
            }
            else {
                byte[] b = thumbs ? reader.openThumbBytes(i) :
                        reader.openBytes(i, xCoordinate, yCoordinate, width, height);
                Object pix = DataTools.makeDataArray(b,
                        FormatTools.getBytesPerPixel(pixelType),
                        FormatTools.isFloatingPoint(pixelType),
                        reader.isLittleEndian());
                Double min = null, max = null;

                if (autoscale) {
                    Double[] planeMin = minMaxCalc.getPlaneMinimum(i);
                    Double[] planeMax = minMaxCalc.getPlaneMaximum(i);
                    if (planeMin != null && planeMax != null) {
                        min = planeMin[0];
                        max = planeMax[0];
                        for (int j=1; j<planeMin.length; j++) {
                            if (planeMin[j].doubleValue() < min.doubleValue()) {
                                min = planeMin[j];
                            }
                            if (planeMax[j].doubleValue() > max.doubleValue()) {
                                max = planeMax[j];
                            }
                        }
                    }
                }
                else if (normalize) {
                    min = Double.valueOf(0);
                    max = Double.valueOf(1);
                }

                if (normalize) {
                    if (pix instanceof float[]) {
                        pix = DataTools.normalizeFloats((float[]) pix);
                    }
                    else if (pix instanceof double[]) {
                        pix = DataTools.normalizeDoubles((double[]) pix);
                    }
                }
                if (thumbs) {
                    images[i - start] = AWTImageTools.makeImage(ImageTools.make24Bits(pix,
                                    sizeX, sizeY, reader.isInterleaved(), false, min, max),
                            sizeX, sizeY, FormatTools.isSigned(pixelType));
                }
                else {
                    images[i - start] = AWTImageTools.makeImage(ImageTools.make24Bits(pix,
                                    width, height, reader.isInterleaved(), false, min, max),
                            width, height, FormatTools.isSigned(pixelType));
                }
            }
            if (images[i - start] == null) {
                LOGGER.warn("\t************ Failed to read plane #{} ************", i);
            }
            if (reader.isIndexed() && reader.get8BitLookupTable() == null &&
                    reader.get16BitLookupTable() == null)
            {
                LOGGER.warn("\t************ no LUT for plane #{} ************", i);
            }

            // check for pixel type mismatch
            int pixType = AWTImageTools.getPixelType(images[i - start]);
            if (pixType != pixelType && pixType != pixelType + 1 && !fastBlit) {
                LOGGER.info("\tPlane #{}: pixel type mismatch: {}/{}",
                        new Object[] {i, FormatTools.getPixelTypeString(pixType),
                                FormatTools.getPixelTypeString(pixelType)});
            }
            else {
                // log number of planes read every second or so
                long t = System.currentTimeMillis();
                if (i == end || (t - timeLastLogged) / 1000 > 0) {
                    int current = i - start + 1;
                    int total = end - start + 1;
                    int percent = 100 * current / total;
                    LOGGER.info("\tRead {}/{} planes ({}%)", new Object[] {
                            current, total, percent
                    });
                    timeLastLogged = t;
                }
            }
        }
        long e = System.currentTimeMillis();

        LOGGER.info("[done]");

        // output timing results
        float sec = (e - s) / 1000f;
        float avg = (float) (e - s) / images.length;
        LOGGER.info("{}s elapsed ({}ms per plane)", sec, avg);

        // display pixels in image viewer
        if (ascii) {
            for (int i=0; i<images.length; i++) {
                final BufferedImage img = images[i];
                LOGGER.info("");
                LOGGER.info("Image #{}:", i);
                LOGGER.info(new AsciiImage(img).toString());
            }
        }
        else {
            LOGGER.info("");
            LOGGER.info("Launching image viewer");
            // ImageViewer viewer = new ImageViewer(false);
            viewer.setImages(reader, images);
            viewer.setVisible(true);
        }
    }

    public BufferedImage[] readPixels2() throws FormatException, IOException {
        String seriesLabel = reader.getSeriesCount() > 1 ?
                (" series #" + series) : "";
        LOGGER.info("");

        int num = reader.getImageCount();
        if (start < 0) start = 0;
        if (start >= num) start = num - 1;
        if (end < 0) end = 0;
        if (end >= num) end = num - 1;
        if (end < start) end = start;

        LOGGER.info("Reading{} pixel data ({}-{})",
                new Object[] {seriesLabel, start, end});

        int sizeX = reader.getSizeX();
        int sizeY = reader.getSizeY();
        if (width == 0) width = sizeX;
        if (height == 0) height = sizeY;

        int pixelType = reader.getPixelType();

        BufferedImage[] images = new BufferedImage[end - start + 1];
        long s = System.currentTimeMillis();
        long timeLastLogged = s;
        for (int i=start; i<=end; i++) {
            if (!fastBlit) {
                images[i - start] = thumbs ? biReader.openThumbImage(i) :
                        biReader.openImage(i, xCoordinate, yCoordinate, width, height);
            }
            else {
                byte[] b = thumbs ? reader.openThumbBytes(i) :
                        reader.openBytes(i, xCoordinate, yCoordinate, width, height);
                Object pix = DataTools.makeDataArray(b,
                        FormatTools.getBytesPerPixel(pixelType),
                        FormatTools.isFloatingPoint(pixelType),
                        reader.isLittleEndian());
                Double min = null, max = null;

                if (autoscale) {
                    Double[] planeMin = minMaxCalc.getPlaneMinimum(i);
                    Double[] planeMax = minMaxCalc.getPlaneMaximum(i);
                    if (planeMin != null && planeMax != null) {
                        min = planeMin[0];
                        max = planeMax[0];
                        for (int j=1; j<planeMin.length; j++) {
                            if (planeMin[j].doubleValue() < min.doubleValue()) {
                                min = planeMin[j];
                            }
                            if (planeMax[j].doubleValue() > max.doubleValue()) {
                                max = planeMax[j];
                            }
                        }
                    }
                }
                else if (normalize) {
                    min = Double.valueOf(0);
                    max = Double.valueOf(1);
                }

                if (normalize) {
                    if (pix instanceof float[]) {
                        pix = DataTools.normalizeFloats((float[]) pix);
                    }
                    else if (pix instanceof double[]) {
                        pix = DataTools.normalizeDoubles((double[]) pix);
                    }
                }
                if (thumbs) {
                    images[i - start] = AWTImageTools.makeImage(ImageTools.make24Bits(pix,
                                    sizeX, sizeY, reader.isInterleaved(), false, min, max),
                            sizeX, sizeY, FormatTools.isSigned(pixelType));
                }
                else {
                    images[i - start] = AWTImageTools.makeImage(ImageTools.make24Bits(pix,
                                    width, height, reader.isInterleaved(), false, min, max),
                            width, height, FormatTools.isSigned(pixelType));
                }
            }
            if (images[i - start] == null) {
                LOGGER.warn("\t************ Failed to read plane #{} ************", i);
            }
            if (reader.isIndexed() && reader.get8BitLookupTable() == null &&
                    reader.get16BitLookupTable() == null)
            {
                LOGGER.warn("\t************ no LUT for plane #{} ************", i);
            }

            // check for pixel type mismatch
            int pixType = AWTImageTools.getPixelType(images[i - start]);
            if (pixType != pixelType && pixType != pixelType + 1 && !fastBlit) {
                LOGGER.info("\tPlane #{}: pixel type mismatch: {}/{}",
                        new Object[] {i, FormatTools.getPixelTypeString(pixType),
                                FormatTools.getPixelTypeString(pixelType)});
            }
            else {
                // log number of planes read every second or so
                long t = System.currentTimeMillis();
                if (i == end || (t - timeLastLogged) / 1000 > 0) {
                    int current = i - start + 1;
                    int total = end - start + 1;
                    int percent = 100 * current / total;
                    LOGGER.info("\tRead {}/{} planes ({}%)", new Object[] {
                            current, total, percent
                    });
                    timeLastLogged = t;
                }
            }
        }
        long e = System.currentTimeMillis();

        LOGGER.info("[done]");

        // output timing results
        float sec = (e - s) / 1000f;
        float avg = (float) (e - s) / images.length;
        LOGGER.info("{}s elapsed ({}ms per plane)", sec, avg);

        // display pixels in image viewer
        return images;
    }
    public void printGlobalMetadata() {
        LOGGER.info("");
        LOGGER.info("Reading global metadata");
        Hashtable<String, Object> meta = reader.getGlobalMetadata();
        String[] keys = MetadataTools.keys(meta);
        for (String key : keys) {
            LOGGER.info("{}: {}", key,  meta.get(key));
        }
    }
    public Hashtable<String, Object> printOriginalMetadata() {
        String seriesLabel = reader.getSeriesCount() > 1 ?
                (" series #" + series) : "";
        LOGGER.info("");
        LOGGER.info("Reading{} metadata", seriesLabel);
        Hashtable<String, Object> meta = reader.getSeriesMetadata();
        String[] keys = MetadataTools.keys(meta);
        for (int i=0; i<keys.length; i++) {
            LOGGER.info("{}: {}", keys[i], meta.get(keys[i]));
        }
        return meta;
    }
    public String getOMEXML() throws MissingLibraryException, ServiceException, IOException, ParserConfigurationException, SAXException {
        String xml = "";
        LOGGER.info("");
        MetadataStore ms = reader.getMetadataStore();

        if (baseReader instanceof ImageReader) {
            baseReader = ((ImageReader) baseReader).getReader();
        }

        OMEXMLService service;
        try {
            ServiceFactory factory = new ServiceFactory();
            service = factory.getInstance(OMEXMLService.class);
        }
        catch (DependencyException de) {
            throw new MissingLibraryException(OMEXMLServiceImpl.NO_OME_XML_MSG, de);
        }
        String version = service.getOMEXMLVersion(ms);
        if (version == null) LOGGER.info("Generating OME-XML");
        else {
            LOGGER.info("Generating OME-XML (schema version {})", version);
        }
        if (ms instanceof MetadataRetrieve) {
            if (omexmlOnly) {
                DebugTools.setRootLevel("INFO");
            }
            xml = service.getOMEXML((MetadataRetrieve) ms);
            //LOGGER.info("First XML-Output");
            //LOGGER.info("{}", XMLTools.indentXML(xml, xmlSpaces, true));


            if (omexmlOnly) {
                DebugTools.setRootLevel("OFF");
            }


        }
        else {
            LOGGER.info("The metadata could not be converted to OME-XML.");
            if (omexmlVersion == null) {
                LOGGER.info("The OME-XML Java library is probably not available.");
            }
            else {
                LOGGER.info("{} is probably not a legal schema version.",
                        omexmlVersion);
            }
        }
        return xml;
    }
    public byte[][] readImage() throws FormatException, IOException {

        System.out.println("reader open bytes 0: " + reader.openBytes(0));
        int x = reader.getSizeX();
        int y = reader.getSizeY();
        int ind = reader.getImageCount() - 1;
        int w = reader.getSizeZ();
        int h = reader.getSizeC();
        System.out.println("options: " + ind + "" + x + " " + y + " " + w + " " + h);
        System.out.println("SizeX: " + reader.getSizeX());
        System.out.println("No Images: " + reader.getImageCount());
        byte[][] pixelData = new byte[reader.getImageCount()][];

        for (int i=0; i< reader.getImageCount(); i++) {
            pixelData[i] = reader.openBytes(i);

            /*
            byte[] img = new byte[reader.getSizeX() * reader.getSizeX() * 1 * reader.getBitsPerPixel()];
            // fill with random data
            for (int ii=0; ii<img.length; ii++) img[ii] = (byte) (256 * Math.random());

            pixelData[i] = img;

             */
        }
        return pixelData;
    }

    public void applyChange(XMLChange change, Document root, Node n, LinkedList<String> query) {
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println("Remaining Query: " + query.toString());
        System.out.println("Current Node: " + n.getNodeName());

        if (change.modificationType == "add" && query.size()==0 && change.getNewContent().startsWith("@")) {
            String attrName = change.getNewContent().split("=")[0].replace("@", "");
            String attrValue = change.getNewContent().split("=")[1].replace(":", "");
            ((Element) n).setAttribute(attrName, attrValue);
            System.out.println("Attributes old: " + n.getAttributes().getNamedItem(attrName));
            return;
        }
        else if (change.modificationType == "edit" && query.get(0).startsWith("@")) {
            String oldNodeName = query.get(0).replace("@", "");
            String newNodeValue = change.getNewContent();
            System.out.println("Attributes old: " + n.getAttributes().getNamedItem(oldNodeName));
            n.getAttributes().getNamedItem(oldNodeName).setNodeValue(newNodeValue);
            System.out.println("Attributes new: " + n.getAttributes().getNamedItem(oldNodeName));
            return;
        }
        else if (change.modificationType == "del" && query.get(0).startsWith("@")) {
            ((Element) n).removeAttribute(query.get(0).replace("@", ""));
            return;
        }
        else if (query.get(0).startsWith("#")) {
            String newNodeValue = change.getNewContent();
            System.out.println("Attributes old: " + n.getTextContent());
            n.setTextContent(newNodeValue);
            System.out.println("Attributes new: " + n.getTextContent());
            return;
        }
        else if (query.get(0).startsWith(":")) {
            return;

        }
        else {
            // GO DEEPER
            for (int c=0; c<n.getChildNodes().getLength(); c++) {
                System.out.println("Next Query item: " + query.get(0).replace("@", ""));
                System.out.println("Node Child: " + n.getChildNodes().item(c).getNodeName());
                if (query.get(0).equals(n.getChildNodes().item(c).getNodeName())) {
                    System.out.println("Next Part found");
                    query.remove(0);
                    applyChange(change, root, n.getChildNodes().item(c), query);
                    return;
                }
            }
        }
        // query not in graph --> make new node containing the change
        System.out.println("Query couldnt be found, no change was made");
    }
    public void exportToOmeTiff(String path) throws Exception {

        int dot = path.lastIndexOf(".");
        String outId = (dot >= 0 ? path.substring(0, dot) : path) + ".ome.tif";
        System.out.println("Converting " + path + " to " + outId + " ");

        // record metadata to OME-XML format
        ServiceFactory factory = new ServiceFactory();
        OMEXMLService service = factory.getInstance(OMEXMLService.class);
        IMetadata omexmlMeta = service.createOMEXMLMetadata();


        System.out.println("XML SCHEMA: ");
        System.out.println(XMLTools.indentXML(omexmlMeta.getRoot().toString()));

        for (XMLChange c : changeHistory) {
            System.out.println("apply Change: ");
            LinkedList<String> query = new LinkedList<>();
            query.addAll(c.getLocation());
            applyChange(c, xml_doc, xml_doc, query);
        }

        System.out.println("Second XML-Output");
        System.out.println(XMLTools.indentXML(XMLTools.getXML(xml_doc)));

        xmlElement = xml_doc.getDocumentElement();
        OMEModel xmlModel = new OMEModelImpl();
        MetadataRoot mdr = new OMEXMLMetadataRoot(xmlElement, xmlModel);
        omexmlMeta.setRoot(mdr);


        BufferedImageWriter biwriter = new BufferedImageWriter(new OMETiffWriter());
        BufferedImage[] testimage = readPixels2();

        /*
        writer.setMetadataRetrieve(omexmlMeta);
        writer.setId("test.ome.tif");

        byte[][] imageInByte = getByteArrays(testimage);
        System.out.println("byte length: " + imageInByte.length);
        for (int p=0; p<imageInByte.length;p++) {
            writer.saveBytes(p, imageInByte[p]);
        }
         */

        biwriter.setMetadataRetrieve(omexmlMeta);
        biwriter.setId(outId);

        for (int i = 0; i < testimage.length; i++) {
            biwriter.saveImage(i, testimage[i]);
        }

        biwriter.close();
        System.out.println("[done]");
    }

    public void printOMEXML() throws Exception {

        // record metadata to OME-XML format
        ServiceFactory factory = new ServiceFactory();
        OMEXMLService service = factory.getInstance(OMEXMLService.class);
        IMetadata omexmlMeta = service.createOMEXMLMetadata();

        System.out.println("XML SCHEMA: ");
        System.out.println(XMLTools.indentXML(omexmlMeta.getRoot().toString()));
        Document example_xml_doc = xml_doc;

        for (XMLChange c : changeHistory) {
            System.out.println("apply Change: ");
            LinkedList<String> query = new LinkedList<>();
            query.addAll(c.getLocation());
            applyChange(c, example_xml_doc, example_xml_doc, query);
        }

        System.out.println("###### Second XML-Output ################################################################");
        System.out.println(XMLTools.indentXML(XMLTools.getXML(example_xml_doc)));
        myGUI.setVisible(true);
    }

    public void readXML(String path) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        xml_doc = db.parse(new File(path));
        String xml = XMLTools.getXML(xml_doc);
        System.out.println(xml);
        changeHistory = new LinkedList<>();
        myGUI.makeTree(xml_doc);
    }

    public void readImage(String path) throws IOException, FormatException, ServiceException, ParserConfigurationException, SAXException {
        String[] args = new String[2];
        args[0] = path; // the id parameter
        args[1] = "-omexml-only";

        parseArgs(args);
        createReader();
        configureReaderPreInit();
        reader.setId(path);

        configureReaderPostInit();
        checkWarnings();
        readCoreMetadata();

        reader.setSeries(series);
        readPixels();

        printGlobalMetadata();
        printOriginalMetadata();
        String xml = getOMEXML();
        xml_doc = XMLTools.parseDOM(xml);
        changeHistory = new LinkedList<>();
        myGUI.makeTree(xml_doc);
    }
    public void readSchema(String path) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        xml_doc = db.parse(new File(path));
        String xml = XMLTools.getXML(xml_doc);
        System.out.println(xml);
        changeHistory = new LinkedList<>();
        myGUI.makeTree(xml_doc);
    }
    public void testEdit() {
        myGUI = new GUI(this);
        myGUI.setVisible(true);
    }

    // -- SaveFileDialogExample method --
    @Override
    public void run() {
        DebugTools.enableLogging("INFO");
        new XMLEditor().testEdit();
    }
    public static void main(String[] args) throws Exception {
        final ImageJ ij = new ImageJ();
        ij.command().run(XMLEditor.class, true);
    }

}
