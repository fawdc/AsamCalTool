/*
 * Creation : 20 févr. 2019
 */
package a2lobject;

import static constante.SecondaryKeywords.BYTE_ORDER;
import static constante.SecondaryKeywords.FIX_AXIS_PAR;
import static constante.SecondaryKeywords.FIX_AXIS_PAR_DIST;
import static constante.SecondaryKeywords.FIX_AXIS_PAR_LIST;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constante.SecondaryKeywords;

public final class AxisDescr {

    private Attribute attribute;
    private String inputQuantity;
    private String conversion;
    private int maxAxisPoints;
    private float lowerLimit;
    private float upperLimit;

    private CompuMethod compuMethod;
    private RecordLayout recordLayout;
    private AxisPts axisPts;

    private static final int nbMandatoryFields = 6;

    private final Map<SecondaryKeywords, Object> optionalsParameters = new HashMap<SecondaryKeywords, Object>() {
        private static final long serialVersionUID = 1L;

        {
            put(SecondaryKeywords.AXIS_PTS_REF, null);
            put(BYTE_ORDER, null);
            put(FIX_AXIS_PAR, null);
            put(FIX_AXIS_PAR_DIST, null);
            put(FIX_AXIS_PAR_LIST, null);
        }
    };

    public AxisDescr(List<String> parameters) {

        if (parameters.size() == 1 || parameters.size() >= 6) {
            for (int n = 0; n < parameters.size(); n++) {
                switch (n) {
                case 0:
                    this.attribute = Attribute.getAttribute(parameters.get(n));
                    // System.out.println(this.name);
                    break;
                case 1:
                    this.inputQuantity = parameters.get(n);
                    break;
                case 2:
                    this.conversion = parameters.get(n);
                    break;
                case 3:
                    this.maxAxisPoints = Integer.parseInt(parameters.get(n));
                    break;
                case 4:
                    this.lowerLimit = Float.parseFloat(parameters.get(n));
                    break;
                case 5:
                    this.upperLimit = Float.parseFloat(parameters.get(n));
                    break;
                default:

                    Set<SecondaryKeywords> keys = optionalsParameters.keySet();
                    for (int nPar = n; nPar < parameters.size(); nPar++) {
                        if (keys.contains(SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar)))) {
                            switch (parameters.get(nPar)) {
                            case "AXIS_PTS_REF":
                                optionalsParameters.put(SecondaryKeywords.AXIS_PTS_REF, parameters.get(nPar + 1));
                                break;
                            case "FIX_AXIS_PAR":
                                n = nPar + 1;
                                optionalsParameters.put(FIX_AXIS_PAR, new FixAxisPar(parameters.subList(n, n + 3)));
                                n = nPar + 3;
                                break;
                            case "FIX_AXIS_PAR_DIST":
                                n = nPar + 1;
                                optionalsParameters.put(FIX_AXIS_PAR_DIST, new FixAxisParDist(parameters.subList(n, n + 3)));
                                n += 3;
                                break;
                            case "FIX_AXIS_PAR_LIST":
                                n = nPar + 1;
                                optionalsParameters.put(FIX_AXIS_PAR_LIST, parameters.subList(n, n + 3));
                                n += 3;
                                break;
                            }

                        }
                    }
                    n = parameters.size();
                    break;
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

    public static int getNbMandatoryfields() {
        return nbMandatoryFields;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public final String getInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("Attribute : " + attribute + "\n");
        sb.append("InputQuantity : " + inputQuantity + "\n");
        sb.append("Conversion : " + conversion + "\n");
        sb.append("MaxAxisPoints : " + maxAxisPoints + "\n");
        sb.append("LowerLimit : " + lowerLimit + "\n");
        sb.append("UpperLimit : " + upperLimit + "\n");

        return sb.toString();
    }

    public void setCompuMethod(CompuMethod compuMethod) {
        this.compuMethod = compuMethod;
    }

    public void setAxisPts(AxisPts axisPts) {
        this.axisPts = axisPts;
    }

    public AxisPts getAxisPts() {
        return axisPts;
    }

    public String getConversion() {
        return conversion;
    }

    public CompuMethod getCompuMethod() {
        return compuMethod;
    }

    public RecordLayout getRecordLayout() {
        return recordLayout;
    }

    @Override
    public String toString() {
        return getInfo();
    }

    public int getMaxAxisPoints() {
        return maxAxisPoints;
    }

    public Map<SecondaryKeywords, Object> getOptionalsParameters() {
        return optionalsParameters;
    }

    public enum Attribute {
        CURVE_AXIS, COM_AXIS, FIX_AXIS, RES_AXIS, STD_AXIS;

        public static Attribute getAttribute(String name) {
            switch (name) {
            case "CURVE_AXIS":
                return CURVE_AXIS;
            case "COM_AXIS":
                return COM_AXIS;
            case "FIX_AXIS":
                return FIX_AXIS;
            case "RES_AXIS":
                return RES_AXIS;
            case "STD_AXIS":
                return STD_AXIS;
            default:
                return null;
            }
        }
    }
}