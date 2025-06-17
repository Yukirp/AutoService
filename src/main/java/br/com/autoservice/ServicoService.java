
package br.com.autoservice;

import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.List;

public class ServicoService {

    public static List<Servico> listarServicos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Query<Servico> query = session.createQuery("FROM Servico", Servico.class);
            List<Servico> servicos = query.list();
            session.getTransaction().commit();
            return servicos;
        }
    }

    public static void limparServicos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.createQuery("DELETE FROM Servico").executeUpdate();
            session.getTransaction().commit();
        }
    }

    public static List<Servico> buscarServicosPorData(LocalDate data) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Query<Servico> query = session.createQuery("FROM Servico WHERE data = :data", Servico.class);
            query.setParameter("data", data);
            List<Servico> resultado = query.list();
            session.getTransaction().commit();
            return resultado;
        }
    }
    
    public static void limparHistorico() {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        session.beginTransaction();
        session.createQuery("DELETE FROM Servico").executeUpdate();
        session.getTransaction().commit();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

}
    