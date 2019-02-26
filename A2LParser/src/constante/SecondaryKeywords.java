/*
 * Creation : 2 mars 2018
 */
package constante;

public enum SecondaryKeywords {
    ALIGNMENT_BYTE, ALIGNMENT_FLOAT16_IEEE, ALIGNMENT_FLOAT32_IEEE, ALIGNMENT_FLOAT64_IEEE, ALIGNMENT_INT64, ALIGNMENT_LONG, ALIGNMENT_WORD, ANNOTATION, ANNOTATION_LABEL, ANNOTATION_ORIGIN, ANNOTATION_TEXT, AXIS_DESCR, AXIS_PTS_REF, AXIS_PTS_X, AXIS_PTS_Y, AXIS_PTS_Z, AXIS_RESCALE_X, BIT_MASK, BYTE_ORDER, CALIBRATION_ACCESS, COEFFS, COEFFS_LINEAR, COMPARISON_QUANTITY, COMPU_TAB_REF, CURVE_AXIS_REF, DEFAULT_VALUE, DEFAULT_VALUE_NUMERIC, DEPENDENT_CHARACTERISTIC, DEPOSIT, DISCRETE, DISPLAY_IDENTIFIER, DIST_OP_X, DIST_OP_Y, DIST_OP_Z, ECU_ADDRESS_EXTENSION, EXTENDED_LIMITS, FIX_AXIS_PAR, FIX_AXIS_PAR_DIST, FIX_AXIS_PAR_LIST, FIX_NO_AXIS_PTS_X, FIX_NO_AXIS_PTS_Y, FIX_NO_AXIS_PTS_Z, FNC_VALUES, FORMAT, FORMULA, FORMULA_INV, IDENTIFICATION, MATRIX_DIM, MAX_GRAD, MAX_REFRESH, MONOTONY, NUMBER, PHYS_UNIT, PROJECT_NO, READ_ONLY, REF_MEMORY_SEGMENT, REF_UNIT, STATIC_RECORD_LAYOUT, STATUS_STRING_REF, STEP_SIZE, VERSION, VIRTUAL_CHARACTERISTIC;

    public static final SecondaryKeywords getSecondaryKeyWords(String name) {
        for (SecondaryKeywords enumKeyword : SecondaryKeywords.values()) {
            if (enumKeyword.name().equals(name)) {
                return enumKeyword;
            }
        }
        return null;
    }

}
