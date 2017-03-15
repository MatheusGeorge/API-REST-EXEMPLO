package com.tully.api.controller;

import com.auth0.jwt.JWTSigner;
import com.tully.api.dao.UsuarioDAO;
import com.tully.api.model.Usuario;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jonathan on 15/02/2017.
 */

@CrossOrigin
@RestController
public class UsuarioRestController {

    public static final String ISSUER = "tully.com";
    public static final String SECRET = "segredo";

    @Autowired
    private UsuarioDAO usuarioDAO;

    @RequestMapping(value = "/usuario", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Usuario> buscarTodos() {
        return usuarioDAO.encontrarTodos();
    }

    @RequestMapping(value = "/usuario/{usuarioID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Usuario> mostrar(@PathVariable long usuarioID) {
        Usuario usuario = usuarioDAO.encontrarPorId(usuarioID);
        if (usuario == null)
            return new ResponseEntity<Usuario>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
    }

    @RequestMapping(value = "/usuario", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Usuario> salvar(@RequestBody Usuario usuario) {
        try {
            usuarioDAO.salvar(usuario);
            URI local = new URI("/usuario/" + usuario.getId());
            return ResponseEntity.created(local).body(usuario);
        } catch (Exception e) {
            return new ResponseEntity<Usuario>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/usuario/{usuarioID}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Usuario> atualizar(@PathVariable long usuarioID, @RequestBody Usuario usuario) {
        try {
            Usuario usuarioOLD = usuarioDAO.encontrarPorId(usuarioID);

            if (usuarioOLD == null)
                return new ResponseEntity<Usuario>(HttpStatus.NOT_FOUND);

            usuarioDAO.atualizar(usuario);

            HttpHeaders responseHeader = new HttpHeaders();
            URI local = new URI("/usuario/" + usuario.getId());
            responseHeader.setLocation(local);

            return new ResponseEntity<Usuario>(responseHeader, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Usuario>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/usuario/{usuarioID}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> destruir(@PathVariable long usuarioID) {
        usuarioDAO.deletar(usuarioID);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> login(@RequestBody String credenciais) {
        JSONObject jsonCredenciais = new JSONObject(credenciais);

        Usuario usuario = usuarioDAO.login(jsonCredenciais.getString("login"), jsonCredenciais.getString("senha"));

        if (usuario != null) {
            // chave criptografada
            String jwt;
            // objeto json que vai receber a chave
            JSONObject token = new JSONObject();
            // objeto que cria a chave
            // SECRET serve para criptografar
            JWTSigner signer = new JWTSigner(SECRET);

            // signer precisa de informações para criptografar
            // ISSUER = quem está emitindo a chave
            // issued at = momento de emissão
            long iat = System.currentTimeMillis() / 1000;
            // expiration = momento de expiração
            long exp = iat + 120;

            // o signer precisa de um mapa de informações com todas as informações acima
            HashMap<String, Object> claims = new HashMap<String, Object>();
            claims.put("iss", ISSUER);
            claims.put("iat", iat);
            claims.put("exp", exp);

            // signer cria uma chave com base no mapa que foi dado
            jwt = signer.sign(claims);

            // colocando a chave criptografada dentro do objeto token de resposta
            token.put("token", jwt);

            return ResponseEntity.ok(token.toString());
        } else {
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }
    }
}
