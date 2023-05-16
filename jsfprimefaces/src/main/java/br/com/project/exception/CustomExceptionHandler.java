package br.com.project.exception;

import java.util.Iterator;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.hibernate.SessionFactory;
import org.primefaces.context.RequestContext;

import br.com.framework.hibernate.session.HibernateUtil;

public class CustomExceptionHandler extends ExceptionHandlerWrapper {

	private ExceptionHandler wrapperd;
	
	final FacesContext facesContext = FacesContext.getCurrentInstance();
	
	final Map<String, Object> requestMap = facesContext.getExternalContext().getRequestMap();
	
	final NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();
	
	
	public CustomExceptionHandler(ExceptionHandler exceptionHandler) {
		this.wrapperd = exceptionHandler;
	}
	
	// Sobrescreve o metodo ExceptionHandler que retorna a pilha de excecoes
	@Override
	public ExceptionHandler getWrapped() {
		return wrapperd;
	}

	// Sobrescrever o metodo handle que e' responsavel por manipular as excecoes do JSF
	@Override
	public void handle() throws FacesException {
		final Iterator<ExceptionQueuedEvent> iterator = getUnhandledExceptionQueuedEvents().iterator();
	
		while (iterator.hasNext()) {
			ExceptionQueuedEvent event = iterator.next();
			ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
			
			// Recuperar a excecao do contexto
			Throwable exception = context.getException();
			
			// Aqui trabalhamos a excecao
			try {
				requestMap.put("exceptionMessage", exception.getMessage());
				
				if (exception != null && exception.getMessage() != null
					&& exception.getMessage().indexOf("ConstraintViolationException") != -1) {
					
					FacesContext.getCurrentInstance().addMessage("msg",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Registro não pode ser removido por estar associado.", ""));
					
				} else if (exception != null && exception.getMessage() != null
						&& exception.getMessage().indexOf("org.hibernate.StaleObjectStateException") != -1) {
					
						FacesContext.getCurrentInstance().addMessage("msg",
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Registro foi atualizado ou excluído por outro usuário. Consulte novamente.", ""));
						
				} else {

					FacesContext.getCurrentInstance().addMessage("msg",
					new FacesMessage(FacesMessage.SEVERITY_FATAL,
					"Ocorreu um erro inesperado:\n" + exception.getMessage(), ""));

					// Alert exibido apenas se a pagina nao redirecionar
					RequestContext.getCurrentInstance().execute("alert('Ocorreu um erro inesperado')");
				
					RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage
								   (FacesMessage.SEVERITY_INFO, "erro",
									"Ocorreu um erro inesperado."));
					
					// Redirecionar para a pagina de erro
					navigationHandler.handleNavigation(facesContext, null, "/error/error.jsf?faces=redirect=true&expired=true");
				}
				
				// Renderizar a pagina de erro e exibir as mensagens
				facesContext.renderResponse();
				
			} finally {
				SessionFactory sf = HibernateUtil.getSessionFactory();
				
				if (sf.getCurrentSession().getTransaction().isActive()) {
					sf.getCurrentSession().getTransaction().rollback();
				}
				
				// Imprimir o erro no console
				exception.printStackTrace();
				
				iterator.remove();
			}
		}
		
		getWrapped().handle();
		
	}
	
}
