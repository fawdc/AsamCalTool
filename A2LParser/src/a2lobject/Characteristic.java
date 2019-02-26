/*
 * Creation : 2 mars 2018
 */
package a2lobject;

import static constante.SecondaryKeywords.ANNOTATION;
import static constante.SecondaryKeywords.AXIS_DESCR;
import static constante.SecondaryKeywords.BIT_MASK;
import static constante.SecondaryKeywords.BYTE_ORDER;
import static constante.SecondaryKeywords.CALIBRATION_ACCESS;
import static constante.SecondaryKeywords.COMPARISON_QUANTITY;
import static constante.SecondaryKeywords.DEPENDENT_CHARACTERISTIC;
import static constante.SecondaryKeywords.DISCRETE;
import static constante.SecondaryKeywords.DISPLAY_IDENTIFIER;
import static constante.SecondaryKeywords.ECU_ADDRESS_EXTENSION;
import static constante.SecondaryKeywords.EXTENDED_LIMITS;
import static constante.SecondaryKeywords.FORMAT;
import static constante.SecondaryKeywords.MATRIX_DIM;
import static constante.SecondaryKeywords.MAX_REFRESH;
import static constante.SecondaryKeywords.NUMBER;
import static constante.SecondaryKeywords.PHYS_UNIT;
import static constante.SecondaryKeywords.READ_ONLY;
import static constante.SecondaryKeywords.REF_MEMORY_SEGMENT;
import static constante.SecondaryKeywords.STEP_SIZE;
import static constante.SecondaryKeywords.VIRTUAL_CHARACTERISTIC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import constante.SecondaryKeywords;

/**
 * Parametre calibrable avec les proprietes suivantes : -nom - description - type : VALUE, ASCII, VAL_BLK, CURVE, MAP, CUBOID, CUBE_4, CUBE_5 - adress
 * - record layout - computation method - upper and lower calibration limits - format
 */

public final class Characteristic implements Comparable<Characteristic> {

    private String name;
    private String longIdentifier;
    private CharacteristicType type;
    private long adress; // 4-byte unsigned integer
    private String deposit; // Reference to RECORLAYOUT
    private float maxDiff;
    private String conversion; // Reference to COMPUTMETHOD
    private float lowerLimit;
    private float upperLimit;

    private CompuMethod compuMethod;
    private RecordLayout recordLayout;

    private List<AxisDescr> axisDescrs;

    private final Map<SecondaryKeywords, Object> optionalsParameters = new HashMap<SecondaryKeywords, Object>() {
        {
            put(ANNOTATION, null);
            put(AXIS_DESCR, null);
            put(BIT_MASK, null); // ToDo
            put(BYTE_ORDER, null); // ToDo
            put(CALIBRATION_ACCESS, null); // ToDo
            put(COMPARISON_QUANTITY, null); // ToDo
            put(DEPENDENT_CHARACTERISTIC, null); // ToDo
            put(DISCRETE, null); // ToDo
            put(DISPLAY_IDENTIFIER, null);
            put(ECU_ADDRESS_EXTENSION, null); // ToDo
            put(EXTENDED_LIMITS, null); // ToDo
            put(FORMAT, null);
            put(MATRIX_DIM, null);
            put(MAX_REFRESH, null);
            put(NUMBER, null);
            put(PHYS_UNIT, null);
            put(READ_ONLY, null); // Par defaut
            put(REF_MEMORY_SEGMENT, null);
            put(STEP_SIZE, null);
            put(VIRTUAL_CHARACTERISTIC, null);

        }
    };

    public Characteristic(List<String> parameters) {

        parameters.remove("/begin"); // Remove /begin
        parameters.remove("CHARACTERISTIC"); // Remove CHARACTERISTIC

        if (parameters.size() == 1 || parameters.size() >= 9) {
            for (int n = 0; n < parameters.size(); n++) {
                switch (n) {
                case 0:
                    this.name = parameters.get(n);
                    // System.out.println(this.name);
                    break;
                case 1:
                    this.longIdentifier = parameters.get(n);
                    break;
                case 2:
                    this.type = CharacteristicType.getCharacteristicType(parameters.get(n));
                    break;
                case 3:
                    this.adress = Long.decode(parameters.get(n)) & 0xffffffffL;
                    break;
                case 4:
                    this.deposit = parameters.get(n);
                    break;
                case 5:
                    this.maxDiff = Float.parseFloat(parameters.get(n));
                    break;
                case 6:
                    this.conversion = parameters.get(n);
                    break;
                case 7:
                    this.lowerLimit = Float.parseFloat(parameters.get(n));
                    break;
                case 8:
                    this.upperLimit = Float.parseFloat(parameters.get(n));
                    break;

                default: // Cas de parametres optionels

                    Set<SecondaryKeywords> keys = optionalsParameters.keySet();
                    for (int nPar = n; nPar < parameters.size(); nPar++) {
                        if (keys.contains(SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar)))) {
                            switch (parameters.get(nPar)) {
                            case "ANNOTATION":
                                n = nPar + 1;
                                do {
                                } while (!parameters.get(++nPar).equals("ANNOTATION"));
                                optionalsParameters.put(ANNOTATION, new Annotation(parameters.subList(n, nPar - 3)));
                                n = nPar + 1;
                                break;
                            case "AXIS_DESCR":
                                if (axisDescrs == null) {
                                    axisDescrs = new ArrayList<AxisDescr>();
                                    optionalsParameters.put(AXIS_DESCR, axisDescrs);
                                }
                                n = nPar + 1;
                                do {
                                } while (!parameters.get(++nPar).equals("AXIS_DESCR"));
                                axisDescrs.add(new AxisDescr(parameters.subList(n, nPar - 1)));
                                n = nPar + 1;
                                break;
                            case "DISPLAY_IDENTIFIER":
                                optionalsParameters.put(DISPLAY_IDENTIFIER, parameters.get(nPar + 1));
                                break;
                            case "FORMAT":
                                optionalsParameters.put(FORMAT, new Format(parameters.get(nPar + 1).toString()));
                                break;

                            case "MATRIX_DIM":

                                break;
                            case "NUMBER":

                                break;
                            case "PHYS_UNIT":

                                break;
                            case "READ_ONLY":
                                optionalsParameters.put(READ_ONLY, true);
                                break;
                            default:
                                break;
                            }
                        }
                    }
                }
            }

            // On vide la MAP de parametre non utilise
            Iterator<Map.Entry<SecondaryKeywords, Object>> iter = optionalsParameters.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<SecondaryKeywords, Object> entry = iter.next();
                if (entry.getValue() == null) {
                    iter.remove();
                }
            }

        } else {
            throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
        }

    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getConversion() {
        return conversion;
    }

    public final void assignComputMethod(List<CompuMethod> compuMethods) {

        int idx = Collections.binarySearch(compuMethods, CompuMethod.createEmptyCompuMethod(conversion));

        if (idx > -1) {
            this.compuMethod = compuMethods.get(idx);
        }
    }

    public final void assignRecordLayout(List<RecordLayout> recordLayouts) {

        int idx = Collections.binarySearch(recordLayouts, RecordLayout.createEmptyRecordLayout(deposit));

        if (idx > -1) {
            this.recordLayout = recordLayouts.get(idx);
        }
    }

    public final String getInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("Name : " + name + "\n");
        sb.append("LongIdentifier : " + longIdentifier + "\n");
        sb.append("Type : " + type + "\n");
        sb.append("Adress : " + adress + "\n");
        sb.append("Deposit : " + deposit + "\n");
        sb.append("Conversion : " + conversion + "\n");
        sb.append("MaxDiff : " + maxDiff + "\n");
        sb.append("LowerLimit : " + lowerLimit + "\n");
        sb.append("UpperLimit : " + upperLimit + "\n");

        for (Entry<SecondaryKeywords, Object> entry : optionalsParameters.entrySet()) {
            if (entry.getValue() != null) {
                sb.append(entry.getKey() + " : " + entry.getValue() + "\n");
            }
        }

        return sb.toString();
    }

    public enum CharacteristicType {

        ASCII, CURVE, MAP, CUBOID, CUBE_4, CUBE_5, VAL_BLK, VALUE;

        private static CharacteristicType getCharacteristicType(String name) {
            switch (name) {
            case "ASCII":
                return ASCII;
            case "CURVE":
                return CURVE;
            case "MAP":
                return MAP;
            case "CUBOID":
                return CUBOID;
            case "CUBE_4":
                return CUBE_4;
            case "CUBE_5":
                return CUBE_5;
            case "VAL_BLK":
                return VAL_BLK;
            case "VALUE":
                return VALUE;
            default:
                return null;
            }
        }

    }

    @Override
    public int compareTo(Characteristic o) {
        return this.name.compareToIgnoreCase(o.toString());
    }

}
