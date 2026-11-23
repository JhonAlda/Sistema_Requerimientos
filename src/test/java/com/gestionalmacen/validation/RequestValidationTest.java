package com.gestionalmacen.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gestionalmacen.dto.request.AjusteInventarioRequest;
import com.gestionalmacen.dto.request.DetalleRequerimientoRequest;
import com.gestionalmacen.dto.request.UsuarioRequest;
import com.gestionalmacen.validation.OnCreate;
import com.gestionalmacen.validation.OnUpdate;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RequestValidationTest {
    private static Validator validator;

    @BeforeAll
    static void crearValidador() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void aceptaCantidadesDecimalesPositivas() {
        var request = new DetalleRequerimientoRequest(1L, new BigDecimal("0.125"));
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void rechazaCantidadesNegativasOCero() {
        var request = new DetalleRequerimientoRequest(1L, new BigDecimal("-0.125"));
        assertFalse(validator.validate(request).isEmpty());
        assertFalse(validator.validate(new DetalleRequerimientoRequest(1L, BigDecimal.ZERO)).isEmpty());
    }

    @Test
    void exigeJustificacionParaAjustarInventario() {
        var request = new AjusteInventarioRequest(1L, 1L, BigDecimal.ONE,
            "AJUSTE_POSITIVO", null, " ");
        assertTrue(validator.validate(request).stream().anyMatch(error -> error.getPropertyPath().toString().equals("observacion")));
    }

    @Test
    void contrasenaEsObligatoriaAlCrearPeroOpcionalAlActualizar() {
        var request = new UsuarioRequest("Usuario de prueba", "prueba", "prueba@example.test", null, 2L, null, null);
        assertFalse(validator.validate(request, OnCreate.class).isEmpty());
        assertTrue(validator.validate(request, OnUpdate.class).isEmpty());
    }
}
