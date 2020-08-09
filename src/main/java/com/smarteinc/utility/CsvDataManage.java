package com.smarteinc.utility;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Marker;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public final class CsvDataManage {
	
	private final Path src;
    private final Path dest;

    public CsvDataManage(Path src, Path dest) {
        this.src = src;
        this.dest = dest;
    }

    private static final int ROW_NUMBER = 0;
    private static final int COLUMN_NAME = 1;
    private static final int COLUMN_VALUE_STRING = 2;
    private static final int COLUMN_VALUE_FLOAT = 3;
    private static final String[] COLUMN_NAMES = { "row_number", "column_name", "column_value_string", "column_value_float" };

    public void apply() throws IOException {
        // Use `try with recources` to close streams correctly
        try (CSVReader reader = new CSVReader(new FileReader(src.toFile())); 
             @SuppressWarnings("deprecation")
			CSVWriter writer = new CSVWriter(new FileWriter(dest.toFile()), ',', CSVWriter.NO_QUOTE_CHARACTER)) {
            // key - ordered list of columns in source file
            Map<String, Marker> columnNameFloatMarker = getSourceColumnNamesWithFloatMarker(reader.readNext());
            int posRowNumber = getRowNumberPosition(columnNameFloatMarker.keySet());

            if (columnNameFloatMarker.isEmpty())
                return;

            writer.writeNext(COLUMN_NAMES);

            // Create buffer only once for all lines; do not use string concatenation or replacing
            String[] buf = new String[COLUMN_NAMES.length];

            reader.forEach(values -> {
                buf[ROW_NUMBER] = values[posRowNumber];

                int col = 0;
                // this is just reference to buf[]; if `null` then no output
                String[] resultLine;

                for (Map.Entry<String, Marker> entry : columnNameFloatMarker.entrySet()) {
                    String columnName = entry.getKey();
                    Marker marker = entry.getValue();

                    if ((resultLine = marker.createResultLine(columnName, values[col], buf)) != null)
                        writer.writeNext(resultLine);

                    col++;
                }
            });
        }
    }
    
    private static final String FLOAT = "_float";
    private static final String STRING = "_string";
    
    private enum Marker {
        NONE {
            @Override
            public String[] createResultLine(String columnName, String value, String[] buf) {
                return null;
            }
        },
        STRING {
            @Override
            public String[] createResultLine(String columnName, String value, String[] buf) {
                buf[COLUMN_VALUE_STRING] = value;
                buf[COLUMN_VALUE_FLOAT] = " ";
                buf[COLUMN_NAME] = columnName;
                return buf;
            }
        },
        FLOAT {
            @Override
            public String[] createResultLine(String columnName, String value, String[] buf) {
                buf[COLUMN_VALUE_STRING] = " ";
                buf[COLUMN_VALUE_FLOAT] = value;
                buf[COLUMN_NAME] = columnName;
                return buf;
            }
        };

        public abstract String[] createResultLine(String columnName, String value, String[] buf);
    }

    // Source column preprocessing to avoid string comparision; do it only once
    private static Map<String, Marker> getSourceColumnNamesWithFloatMarker(String... columns) {
        if (columns == null || columns.length == 0)
            return Collections.emptyMap();

        Map<String, Marker> map = new LinkedHashMap<>();

        for (int i = 0; i < columns.length; i++) {
            String columnName = columns[i];
            Marker marker = Marker.NONE;

            if (columnName.endsWith(FLOAT)) {
                columnName = columnName.substring(0, columnName.length() - FLOAT.length());
                marker = Marker.FLOAT;
            } else if (columnName.endsWith(STRING)) {
                columnName = columnName.substring(0, columnName.length() - STRING.length());
                marker = Marker.STRING;
            }

            if (map.put(columnName, marker) != null)
                throw new IllegalArgumentException("Column duplication in the source file");
        }

        return map;
    }

    private static int getRowNumberPosition(Set<String> columnNames) {
        int i = 0;

        for (String columnName : columnNames) {
            if ("row_number".equals(columnName))
                return i;
            i++;
        }

        throw new IllegalArgumentException("Source file does not contain 'row_number' column");
    }
}



