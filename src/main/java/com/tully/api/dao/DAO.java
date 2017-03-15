package com.tully.api.dao;

import java.util.List;

/**
 * Created by jonathan on 15/02/2017.
 */
public interface DAO<T> {
    void salvar(T entidade);
    void atualizar(T entidade);
    void deletar(Long primaryKey);
    List<T> encontrarTodos();
    T encontrarPorId(Long primaryKey);
}
