package com.multiservicio.entity.enums;

public enum EstadoVentanilla {
    DISPONIBLE("DISPONIBLE"),
    OCUPADA("OCUPADA"),
    PAUSADA("PAUSADA");

    private final String dbValue;

    EstadoVentanilla(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static EstadoVentanilla fromDb(String value) {
        for (EstadoVentanilla estado : values()) {
            if (estado.dbValue.equals(value)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de ventanilla no válido: " + value);
    }
}
