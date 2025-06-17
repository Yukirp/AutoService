
package br.com.autoservice;

import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class VendaService {

    public static List<Venda> listarVendas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Query<Venda> query = session.createQuery("FROM Venda", Venda.class);
            List<Venda> vendas = query.list();
            session.getTransaction().commit();
            return vendas;
        }
    }

    public static void limparHistorico() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.createQuery("DELETE FROM Venda").executeUpdate();
            session.getTransaction().commit();
        }
    }
    
    public static void limparVendas() {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        session.beginTransaction();
        session.createQuery("DELETE FROM Venda").executeUpdate();
        session.getTransaction().commit();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    
     public static void salvarVenda(Venda venda) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(venda);
            session.getTransaction().commit();
        }
    }



}
    