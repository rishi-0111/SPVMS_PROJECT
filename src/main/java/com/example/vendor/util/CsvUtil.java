package com.example.vendor.util;

public class CsvUtil {
    
    /**
     * Escapes a CSV field value to prevent CSV injection and handle special characters
     * 
     * @param value The value to escape
     * @return Escaped value safe for CSV output
     */
    public static String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        
        // Prevent CSV injection by removing dangerous characters at the start
        String cleaned = value.trim();
        if (cleaned.startsWith("=") || cleaned.startsWith("+") || 
            cleaned.startsWith("-") || cleaned.startsWith("@") ||
            cleaned.startsWith("\t") || cleaned.startsWith("\r")) {
            cleaned = "'" + cleaned; // Prefix with single quote to neutralize
        }
        
        // Escape double quotes by doubling them
        cleaned = cleaned.replace("\"", "\"\"");
        
        // Wrap in quotes if contains comma, newline, or quote
        if (cleaned.contains(",") || cleaned.contains("\n") || 
            cleaned.contains("\"") || cleaned.contains("\r")) {
            cleaned = "\"" + cleaned + "\"";
        }
        
        return cleaned;
    }
    
    /**
     * Creates a CSV row from multiple values
     * 
     * @param values The values for the row
     * @return CSV formatted row
     */
    public static String createCsvRow(String... values) {
        StringBuilder row = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                row.append(",");
            }
            row.append(escapeCsvValue(values[i]));
        }
        return row.toString();
    }
}
