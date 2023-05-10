package br.com.framework.interfac.crud;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public interface InterfaceCrud<T> extends Serializable {
	
	// Salvar dados
	void save(T obj) throws Exception;
	
	void persist(T obj) throws Exception;
	
	// Salvar ou atualizar
	void saveOrUpdate(T obj) throws Exception;
	
	// Atualizar dados
	void update(T obj) throws Exception;
	
	// Deletar dados
	void delete(T obj) throws Exception;
	
	// Salvar/Atualizar e retornar o objeto
	T merge(T obj) throws Exception;
	
	// Carregar lista de dados de determinada classe
	List<T> findList(Class<T> objs) throws Exception;
	
	Object findById(Class<T> entidade, Long id) throws Exception;
	
	T tFindById(Class<T> entidade, Long id) throws Exception;

	List<T> findListByQuery(String s) throws Exception;
	
	// Executar update com HQL
	void executeUpdateQuery(String s) throws Exception;
	
	// Executar update com SQL
	void executeUpdateSQL(String s) throws Exception;
	
	// Limpar sessao do Hibernate
	void clearSession() throws Exception;
	
	// Retirar objeto da sessao do Hibernate
	void evict(Object obj) throws Exception;
	
	Session getSession() throws Exception;
	
	List<?> getListSQL(String sql) throws Exception;
	
	// JDBC do Spring
	JdbcTemplate getJdbcTemplate();
	
	SimpleJdbcTemplate getSimpleJdbcTemplate();
	
	SimpleJdbcInsert getSimpleJdbcInsert();
	
	Long totalRegistro(String table) throws Exception;
	
	Query obterQuery(String query) throws Exception;
	
	// Carregamento dinamico com JSF e PrimeFaces
	List<T> findListByQuery(String query, int iniciaNoRegistro, int maximoResultado) throws Exception;

}
