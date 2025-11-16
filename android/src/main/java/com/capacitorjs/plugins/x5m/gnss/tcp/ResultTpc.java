package com.capacitorjs.plugins.x5m.gnss.tcp;

// Archivo: ResultTpc.java

/**
 * Clase modelo para encapsular el resultado de una operación TCP
 * junto con su codificación.
 */
public class ResultTpc {

    // Propiedades privadas para almacenar los datos.
    private final String result;
    private final String encoding;

    /**
     * Constructor para crear una nueva instancia de ResultTpc.
     * Se asignan los valores en el momento de la creación.
     *
     * @param result El dato principal (ej. datos recibidos, en formato String).
     * @param encoding La codificación de los datos (ej. "UTF-8", "base64").
     */
    public ResultTpc(String result, String encoding) {
        // Se recomienda validar que los argumentos no sean nulos si es un requisito.
        if (result == null || encoding == null) {
            throw new IllegalArgumentException("El resultado y la codificación no pueden ser nulos.");
        }
        this.result = result;
        this.encoding = encoding;
    }

    // --- Métodos "Getter" para acceder a las propiedades ---

    /**
     * @return El resultado de la operación.
     */
    public String getResult() {
        return result;
    }

    /**
     * @return La codificación del resultado.
     */
    public String getEncoding() {
        return encoding;
    }

    public byte[] toBytes(){
        return result.getBytes();
    }

    // --- Métodos de utilidad (Opcionales pero recomendados) ---

    /**
     * Devuelve una representación en String del objeto, útil para depuración y logs.
     * @return Una cadena que describe el objeto.
     */
    @Override
    public String toString() {
        return "ResultTpc{" +
                "result='" + result + '\'' +
                ", encoding='" + encoding + '\'' +
                '}';
    }
}
