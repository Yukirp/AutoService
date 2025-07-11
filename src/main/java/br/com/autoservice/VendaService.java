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
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // retorna lista vazia em caso de erro
        }
    }

    public static void limparTodasVendas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.createQuery("DELETE FROM Venda").executeUpdate();
            session.getTransaction().commit();
            System.out.println("Todas as vendas foram apagadas.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Métodos antigos para compatibilidade
    public static void limparHistorico() {
        limparTodasVendas();
    }

    public static void limparVendas() {
        limparTodasVendas();
    }

    public static void salvarVenda(Venda venda) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(venda);
            session.getTransaction().commit();
            System.out.println("Venda registrada com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
