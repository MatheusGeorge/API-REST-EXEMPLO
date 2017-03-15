package com.tully.api.dao;

import com.tully.api.model.Usuario;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by jonathan on 15/02/2017.
 */

@Repository
public class UsuarioDAO implements DAO<Usuario>{

    @PersistenceContext
    private EntityManager manager;

    @Transactional
    public void salvar(Usuario entidade) {
        manager.persist(entidade);
    }

    @Transactional
    public void atualizar(Usuario entidade) {
        manager.merge(entidade);
    }

    @Transactional
    public void deletar(Long primaryKey) {
        Usuario usuario = manager.find(Usuario.class, primaryKey);
        manager.remove(usuario);
    }

    public List<Usuario> encontrarTodos() {
        TypedQuery<Usuario> query = manager.createQuery("SELECT u FROM Usuario u", Usuario.class);
        return query.getResultList();
    }

    public Usuario encontrarPorId(Long primaryKey) {
        Usuario usuario = manager.find(Usuario.class, primaryKey);
        return usuario;
    }

    public Usuario login(String login, String senha) {
        TypedQuery<Usuario> query = manager.createQuery("SELECT u FROM Usuario u WHERE u.login = :login AND u.senha = :senha", Usuario.class);
        query.setParameter("login", login);
        query.setParameter("senha", senha);
        try {
            Usuario usuario = query.getSingleResult();
            return usuario;
        } catch (Exception e) {
            return null;
        }
    }
}
