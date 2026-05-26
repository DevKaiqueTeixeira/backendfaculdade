package com.cafe.projeto.dao.operacao;

public interface OperacaoDao<I, O> {
    O executar(I entrada);
}
