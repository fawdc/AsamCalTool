/*
 * Creation : 2 mars 2018
 */
package a2l;

import static constante.SecondaryKeywords.ANNOTATION;
import static constante.SecondaryKeywords.BYTE_ORDER;
import static constante.SecondaryKeywords.CALIBRATION_ACCESS;
import static constante.SecondaryKeywords.COMPARISON_QUANTITY;
import static constante.SecondaryKeywords.DEPOSIT;
import static constante.SecondaryKeywords.DISPLAY_IDENTIFIER;
import static constante.SecondaryKeywords.ECU_ADDRESS_EXTENSION;
import static constante.SecondaryKeywords.EXTENDED_LIMITS;
import static constante.SecondaryKeywords.FORMAT;
import static constante.SecondaryKeywords.MONOTONY;
import static constante.SecondaryKeywords.PHYS_UNIT;
import static constante.SecondaryKeywords.READ_ONLY;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import constante.ConversionType;
import constante.SecondaryKeywords;

public final class AxisPts extends AdjustableObject {

    @SuppressWarnings("unused")
    private char[] inputQuantity; // Reference to INPUT_QUANTITY
    private short maxAxisPoints;

    private List<Characteristic> characteristicsDependency;

    public AxisPts(List<String> parameters, int beginLine, int endLine) {

        initOptionalsParameters();

        build(parameters, beginLine, endLine);

        characteristicsDependency = new ArrayList<Characteristic>();
    }

    private final void initOptionalsParameters() {
        optionalsParameters = new EnumMap<SecondaryKeywords, Object>(SecondaryKeywords.class);
        optionalsParameters.put(ANNOTATION, null);
        optionalsParameters.put(BYTE_ORDER, null);
        optionalsParameters.put(CALIBRATION_ACCESS, null); // ToDo
        optionalsParameters.put(COMPARISON_QUANTITY, null); // ToDo
        optionalsParameters.put(DEPOSIT, null);
        optionalsParameters.put(DISPLAY_IDENTIFIER, null);
        optionalsParameters.put(ECU_ADDRESS_EXTENSION, null); // ToDo
        optionalsParameters.put(EXTENDED_LIMITS, null); // ToDo
        optionalsParameters.put(FORMAT, null);
        optionalsParameters.put(MONOTONY, null);
        optionalsParameters.put(PHYS_UNIT, null);
        optionalsParameters.put(READ_ONLY, false); // Par defaut
    }

    @Override
    public String toString() {
        return this.name;
    }

    public final short getMaxAxisPoints() {
        return maxAxisPoints;
    }

    public final String getDepositMode() {
        Object oDeposit = optionalsParameters.get(DEPOSIT);
        return oDeposit != null ? oDeposit.toString() : "";
    }

    public final String[] getStringValues() {
        String[] strValues = new String[this.values.getDimX()];
        for (short i = 0; i < strValues.length; i++) {
            strValues[i] = this.values.getValue(0, i);
        }

        return strValues;
    }

    @Override
    public final void assignComputMethod(HashMap<Integer, CompuMethod> compuMethods) {
        this.compuMethod = compuMethods.get(this.conversionId);
    }

    public final void assignCharacteristic(Characteristic characteristic) {
        characteristicsDependency.add(characteristic);
    }

    public final List<Characteristic> getCharacteristicsDependency() {
        return characteristicsDependency;
    }

    @Override
    public String[] getUnit() {
        return new String[] { this.compuMethod.getUnit() };
    }

    @Override
    public void build(List<String> parameters, int beginLine, int endLine) throws IllegalArgumentException {

        final int nbParams = parameters.size();

        if (nbParams >= 9) {

            this.name = parameters.get(2);
            this.longIdentifier = parameters.get(3).toCharArray();
            this.adress = Long.parseLong(parameters.get(4).substring(2), 16);
            this.inputQuantity = parameters.get(5).toCharArray();
            this.depositId = parameters.get(6).hashCode();
            this.maxDiff = Float.parseFloat(parameters.get(7));
            this.conversionId = parameters.get(8).hashCode();
            this.maxAxisPoints = Short.parseShort(parameters.get(9));
            this.lowerLimit = Float.parseFloat(parameters.get(10));
            this.upperLimit = Float.parseFloat(parameters.get(11));

            int n = 12;

            Set<SecondaryKeywords> keys = optionalsParameters.keySet();
            SecondaryKeywords keyWord;
            for (int nPar = n; nPar < nbParams; nPar++) {
                keyWord = SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar));
                if (keys.contains(keyWord)) {
                    switch (keyWord) {
                    case ANNOTATION:
                        n = nPar + 1;
                        do {
                        } while (!parameters.get(++nPar).equals(ANNOTATION.name()));
                        optionalsParameters.put(ANNOTATION, new Annotation(parameters.subList(n, nPar - 3)));
                        n = nPar + 1;
                        break;
                    case BYTE_ORDER:
                        optionalsParameters.put(BYTE_ORDER, parameters.get(nPar + 1));
                        nPar += 1;
                        break;
                    case DEPOSIT:
                        optionalsParameters.put(DEPOSIT, parameters.get(nPar + 1));
                        nPar += 1;
                        break;
                    case DISPLAY_IDENTIFIER:
                        optionalsParameters.put(DISPLAY_IDENTIFIER, parameters.get(nPar + 1).toCharArray());
                        nPar += 1;
                        break;
                    case FORMAT:
                        optionalsParameters.put(FORMAT, parameters.get(nPar + 1).toCharArray());
                        nPar += 1;
                        break;
                    case PHYS_UNIT:
                        break;
                    case READ_ONLY:
                        optionalsParameters.put(READ_ONLY, true);
                        break;
                    default:
                        break;
                    }
                }
            }

        } else {
            validParsing = false;
            throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
        }
        validParsing = true;
    }

    @Override
    protected void formatValues() {

        if (values != null) {
            final DecimalFormat df = new DecimalFormat();
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setDecimalSeparator('.');
            String separator = new String(new char[] { dfs.getGroupingSeparator() });
            df.setDecimalFormatSymbols(dfs);
            df.setMaximumFractionDigits(getNbDecimal());

            int nbValues = values.getDimX();

            for (int i = 0; i < nbValues; i++) {
                try {
                    double doubleValue = Double.parseDouble(values.getValue(0, i));
                    values.setValue(0, i, df.format(doubleValue).replace(separator, ""));
                } catch (Exception e) {
                    // Nothing
                }
            }
        }
    }

    @Override
    public double[] getResolution() {

        if (ConversionType.TAB_VERB.compareTo(this.compuMethod.getConversionType()) != 0) {
            double val0 = this.compuMethod.compute(1);
            double val1 = this.compuMethod.compute(2);
            double resol = val1 - val0;
            return new double[] { resol };
        }

        return new double[] { Double.NaN };
    }

    @Override
    public boolean isValid() {
        return validParsing;
    }

}
