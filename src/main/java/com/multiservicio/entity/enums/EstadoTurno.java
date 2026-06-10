package com.multiservicio.entity.enums;

public enum EstadoTurno {
    PENDIENTE("PENDIENTE"),
    LLAMADO("LLAMADO"),
    EN_ATENCION("EN ATENCION"),
    FINALIZADO("FINALIZADO"),
    CANCELADO("CANCELADO"),
    NO_PRESENTADO("NO PRESENTADO"),
    REPROGRAMADO("REPROGRAMADO");

    private final String dbValue;

    EstadoTurno(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static EstadoTurno fromDb(String value) {
        for (EstadoTurno estado : values()) {
            if (estado.dbValue.equals(value)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de turno no válido: " + value);
    }
}
