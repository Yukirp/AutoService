package br.com.autoservice;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Classe utilitária para fornecer uma única instância de SessionFactory
 * usada para gerenciar as sessões do Hibernate.
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            // Cria a SessionFactory a partir do arquivo hibernate.cfg.xml
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Em caso de erro, exibe no console e encerra o processo
            System.err.println("Erro ao inicializar SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Retorna a instância da SessionFactory.
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
