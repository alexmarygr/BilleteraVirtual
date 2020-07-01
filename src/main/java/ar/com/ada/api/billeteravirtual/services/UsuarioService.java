package ar.com.ada.api.billeteravirtual.services;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.com.ada.api.billeteravirtual.entities.*;
import ar.com.ada.api.billeteravirtual.repos.UsuarioRepository;
import ar.com.ada.api.billeteravirtual.security.Crypto;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository repo;

    @Autowired
    PersonaService personaService;

    @Autowired
    BilleteraService billeteraService;

	public Usuario buscarPorUsername(String username) {
		return repo.findByUsername(username);
	}

	public void login(String username, String password) {
	}
     
    /*1.Metodo: Crear Usuario
    1.1-->Crear una Persona(setearle un usuario)
    1.2-->crear un usuario
    1.3-->Crear una billetera(setearle una persona)
    1.4-->Crear una cuenta en pesos y otra en dolares*/

    /* 2. Metodo: Iniciar Sesion 
    2.1-- recibe el username y la password
    2.2-- vamos a validar los datos
    2.3-- devolver un verdadero o falso
    */
    public Usuario crearUsuario(String nombre, int pais, int tipoDocumento, String documento, Date fechaNacimiento,
            String email, String password) {

        Persona persona = new Persona();
        persona.setNombre(nombre);
        persona.setPaisId(pais);
        persona.setTipoDocumentoId(tipoDocumento);
        persona.setDocumento(documento);
        persona.setFechaNacimiento(fechaNacimiento);

        Usuario usuario = new Usuario();
        usuario.setUsername(email);
        usuario.setEmail(email);
        usuario.setPassword(Crypto.encrypt(password, email));

        persona.setUsuario(usuario);

        Billetera billetera = new Billetera(); // Se crea la billetera

        BigDecimal saldoInicial = new BigDecimal(0);

        Cuenta cuentaPesos = new Cuenta(); // Se crea cuenta en pesos
        cuentaPesos.setSaldo(saldoInicial);
        cuentaPesos.setMoneda("ARS");

        Cuenta cuentaDolares = new Cuenta(); // Se crea cuenta en dolares
        cuentaDolares.setSaldo(saldoInicial);
        cuentaDolares.setMoneda("USD");

        // Les seteo las cuentas a billetera
        billetera.agregarCuenta(cuentaPesos);
        billetera.agregarCuenta(cuentaDolares);

        persona.setBilletera(billetera);// Se le da la billetera a la persona

        personaService.grabar(persona);

        billeteraService.grabar(billetera);

        billeteraService.cargarSaldo(new BigDecimal(500), "ARS", billetera.getBilleteraId(), "regalo",
                "Bienvenida por creacion de usuario");

        return usuario;
    }


}