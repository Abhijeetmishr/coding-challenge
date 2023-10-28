package qr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class Version {

    private static Version[] VERSIONS;

    private final int version;
    private String informationBits;
    private final Map<Quality, Map<EncodingMode, Integer>> capacity;
    private final int size;
    private Map<Quality, MetaData> metaData = new HashMap<Quality, MetaData>();

    private List<Rect> alignmentPatterns;
    private List<Rect> finderPatterns;
    private List<Rect> timingPatterns;
    private Rect darkModule;

    static {
        initVersionCapacity();
        initCorrectionWordsAndBlocks();
        initVersionInformationString();
        initFinderPatterns();
        initAlignmentPatterns();
        initTimingPatterns();
        initDarkModule();
    }


    // https://www.thonky.com/qr-code-tutorial/data-encoding
    public Version(int version, Map<Quality, Map<EncodingMode, Integer>> capacity) {
        this.version = version;
        this.capacity = capacity;
        this.size = 21 + (this.version - 1) * 4;
    }
    public int capacity(EncodingMode mode, Quality quality) {
        return this.capacity.get(quality).get(mode);
    }
    public int bitCapacity(Quality quality) {
        return this.metaData(quality).bitCapacity;
    }

    public MetaData metaData(Quality quality) {
        return this.metaData.get(quality);
    }

    public int version() {
        return this.version;
    }
    public int qrSize() {
        return this.size;
    }

    public static Version bestFixByBits(Quality quality, int bitSize) {
        for (int i = 0; i < VERSIONS.length; i++) {
            var version = VERSIONS[i];
            if (version.bitCapacity(quality) >= bitSize) {
                return version;
            }
        }
        throw new IllegalArgumentException("Bit size "+bitSize+" is too large for quality "+quality);
    }


    public static Version bestFixByChars(int characterSize, EncodingMode mode, Quality quality) {
        for (int i = 0; i < VERSIONS.length; i++) {
            var version = VERSIONS[i];
            if (version.capacity(mode, quality) >= characterSize) {
                return version;
            }
        }
        throw new IllegalArgumentException("Character size "+characterSize+" is too big for "+mode+" / "+quality);
    }

    // https://www.thonky.com/qr-code-tutorial/character-capacities
    private static void initVersionCapacity() {
        var stream = Version.class.getResourceAsStream("/character-capacity.txt");
        if (stream != null) {
            var reader = new BufferedReader(new InputStreamReader(stream));
            var lines = reader.lines().toList();
            // remove header
            lines = lines.subList(1,lines.size());

            VERSIONS = new Version[41];
            var lastVersion = "0";
            Map<Quality, Map<EncodingMode, Integer>> capacityMap = null;
            Version versionElement = null;
            for (int i = 0; i < lines.size(); i++) {
                var line = lines.get(i);
                var splitted = line.split("\t");
                var version = splitted[0].trim();
                if (version.equals("")) {
                    version = lastVersion;
                } else {
                    capacityMap = new HashMap<Quality, Map<EncodingMode, Integer>>();
                    var versionInt = Integer.parseInt(version);
                    versionElement = new Version(versionInt, capacityMap);
                    VERSIONS[versionInt] = versionElement;
                }
                if (capacityMap == null || versionElement == null) {
                    throw new IllegalArgumentException("Something is wrong in the capacity file");
                }
                lastVersion = version;
                var quality = Quality.valueOf(splitted[1].trim());
                var numericCapacity = Integer.parseInt(splitted[2].trim());
                var alphaNumericCapacity = Integer.parseInt(splitted[3].trim());
                var byteCapacity = Integer.parseInt(splitted[4].trim());
                var kanjiCapacity = Integer.parseInt(splitted[5].trim());
                var encodingMap = new HashMap<EncodingMode, Integer>();
                encodingMap.put(EncodingMode.NUMERIC, numericCapacity);
                encodingMap.put(EncodingMode.ALPHA_NUMERIC, alphaNumericCapacity);
                encodingMap.put(EncodingMode.BYTE, byteCapacity);
                encodingMap.put(EncodingMode.KANJI, kanjiCapacity);
                capacityMap.put(quality,encodingMap);
            };
        }
    }

    // https://www.thonky.com/qr-code-tutorial/error-correction-table
    private static void initCorrectionWordsAndBlocks() {
        var stream = Version.class.getResourceAsStream("/error-correction-codewords-blocks.txt");
        if (stream != null) {
            var reader = new BufferedReader(new InputStreamReader(stream));
            var lines = reader.lines().toList();
            // remove header
            lines = lines.subList(1, lines.size());
            for (int i = 0; i < lines.size(); i++) {
                var line = lines.get(i);
                var splitted = line.split("\t");
                var versionQuality = splitted[0].trim();
                var versionSplitted = versionQuality.split("-");
                var version = Integer.parseInt(versionSplitted[0].trim());
                var quality = Quality.valueOf(versionSplitted[1].trim());

                var totalNumberOfDataCodeWords = Integer.parseInt(splitted[1].trim());
                var codewordsPerBlock = Integer.parseInt(splitted[2].trim());
                var numberOfBlocksInGroup1 = Integer.parseInt(splitted[3].trim());
                var numberOfDataCodewordsInEachOfGroup1sBlocks = Integer.parseInt(splitted[4].trim());
                var numberOfBlocksInGroup2 = splitted[5].equals("") ? 0 : Integer.parseInt(splitted[5].trim());
                var numberOfDataCodewordsInEachOfGroup2sBlocks = splitted[6].equals("") ? 0 : Integer.parseInt(splitted[6].trim());
                var totalCodeWords = Integer.parseInt(splitted[7].trim().split("=")[1].trim());
                var totalCodeBits = totalCodeWords * 8;
                version(version).metaData.put(quality,new MetaData(
                        totalNumberOfDataCodeWords,
                        codewordsPerBlock,
                        numberOfBlocksInGroup1,
                        numberOfDataCodewordsInEachOfGroup1sBlocks,
                        numberOfBlocksInGroup2,
                        numberOfDataCodewordsInEachOfGroup2sBlocks,
                        totalCodeWords,
                        totalCodeBits
                ));
            }
        }
    }

    private static void initVersionInformationString() {
        var stream = Version.class.getResourceAsStream("/version-information-string.txt");
        if (stream != null) {
            var reader = new BufferedReader(new InputStreamReader(stream));
            var lines = reader.lines().toList();
            // remove header
            lines = lines.subList(1, lines.size());
            for (int i = 0; i < lines.size(); i++) {
                var line = lines.get(i);
                var splitted = line.split("\t");
                var version = Integer.parseInt(splitted[0].trim());
                var information = splitted[1].trim();
                version(version).informationBits = information;
            }
        }
    }

    private static void initFinderPatterns() {
        /*
        The top-left finder pattern's top left corner is always placed at (0,0).
        The top-right finder pattern's top LEFT corner is always placed at ([(((V-1)*4)+21) - 7], 0)
        The bottom-left finder pattern's top LEFT corner is always placed at (0,[(((V-1)*4)+21) - 7])
         */
        for (var v : VERSIONS) {
            var size = 7 + 1;
            var pattern0 = new Rect(new Point2d(0, 0), size, size);
            var pattern1 = new Rect(new Point2d((((v.version-1)*4)+21) - 7, 0), size, size);
            var pattern2 = new Rect(new Point2d(0,(((v.version-1)*4)+21) - 7), size, size);
            v.finderPatterns = new ArrayList<>();
            v.finderPatterns.add(pattern0);
            v.finderPatterns.add(pattern1);
            v.finderPatterns.add(pattern2);
        }
    }

    private static void initTimingPatterns() {

        for (Version v: VERSIONS) {
            if (v.size > 0) {
                v.timingPatterns = new ArrayList<>();
                var p1a = v.finderPatterns.get(0);
                var p1b = v.finderPatterns.get(1);
                var horizontalTimingPattern = new Rect(new Point2d(p1a.rightBottom.x+1, 6), p1b.leftTop.x + 1 - p1a.rightBottom.x + 1, 1);
                v.timingPatterns.add(horizontalTimingPattern);

                var p2a = v.finderPatterns.get(0);
                var p2b = v.finderPatterns.get(2);
                var verticalTimingPattern = new Rect(new Point2d(6, p2a.rightBottom.y+1), 1, p2b.leftTop.y + 1 - p2a.rightBottom.y + 1);
                v.timingPatterns.add(verticalTimingPattern);
            }
        }

    }

    private static void initDarkModule() {

        for (Version v: VERSIONS) {
            if (v.size > 0) {
                /*
                ([(4 * V) + 9], 8)
                 */
                v.darkModule = new Rect(new Point2d((4 * v.version) + 9, 8), 1, 1);
            }
        }

    }

    private static void initAlignmentPatterns() {
        var stream = Version.class.getResourceAsStream("/alignment-pattners.txt");
        if (stream != null) {
            var reader = new BufferedReader(new InputStreamReader(stream));
            var lines = reader.lines().toList();
            // remove header
            lines = lines.subList(1, lines.size());
            for (int i = 0; i < lines.size(); i++) {
                var line = lines.get(i);
                var splitted = line.split("\t");
                var version = Integer.parseInt(splitted[0].trim());
                var v = VERSIONS[version];

                var alignmentPatternPosition = new ArrayList<Integer>();
                for (int j = 1; j < splitted.length; j++) {
                    var alignmentCenter = Integer.parseInt(splitted[j].trim());
                    alignmentPatternPosition.add(alignmentCenter);
                }
                var points = Point2d.createMatrix(alignmentPatternPosition);
                var rectangles = points.stream().map( center -> new Rect(center, 5)).toList();
                v.alignmentPatterns = rectangles.stream().filter(r ->
                        // only add alignment pattern if has not intersection with any of the finder patterns
                        r.intersection(v.finderPatterns).isEmpty()
                ).toList();
                //System.out.println("V: "+v.version+"="+v.alignmentPatterns.size()+" alignment patterns to draw");
            }
        }
    }

    public static Version version(int version) {
        return VERSIONS[version];
    }

    public static class MetaData {
        public final int totalNumberOfDataCodeWords;
        public final int codewordsPerBlock;

        public final int group1NumberOfBlocks;
        public final int group1NumberOfDataCodewordsInEachOfGroupBlocks;

        public final int group2NumberOfBlocks;
        public final int group2NumberOfDataCodewordsInEachOfGroupBlocks;

        public final int totalCodeWords;
        public final int bitCapacity;

        public  MetaData(int totalNumberOfDataCodeWords, int codewordsPerBlock, int group1NumberOfBlocks, int numberOfDataCodewordsInEachOfGroup1sBlocks, int numberOfBlocksInGroup2, int numberOfDataCodewordsInEachOfGroup2sBlocks, int totalCodeWords, int bitCapacity) {

            this.totalNumberOfDataCodeWords = totalNumberOfDataCodeWords;
            this.codewordsPerBlock = codewordsPerBlock;
            this.group1NumberOfBlocks = group1NumberOfBlocks;
            this.group1NumberOfDataCodewordsInEachOfGroupBlocks = numberOfDataCodewordsInEachOfGroup1sBlocks;
            this.group2NumberOfBlocks = numberOfBlocksInGroup2;
            this.group2NumberOfDataCodewordsInEachOfGroupBlocks = numberOfDataCodewordsInEachOfGroup2sBlocks;
            this.totalCodeWords = totalCodeWords;
            this.bitCapacity = bitCapacity;
        }
    }

}