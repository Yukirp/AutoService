package br.com.autoservice;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class ProdutoService {

    public static void salvarProduto(String nome, Double preco, String descricao, String marca, String veiculosCompativeis, int quantidade) {
        Produto produto = new Produto(nome, preco, descricao, marca, veiculosCompativeis, quantidade);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(produto);
            session.getTransaction().commit();
            System.out.println("Produto salvo com sucesso: " + nome);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Produto> listarProdutos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Query<Produto> query = session.createQuery("FROM Produto WHERE ativo = true", Produto.class);
            List<Produto> produtos = query.list();

            session.getTransaction().commit();
            return produtos;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static List<Produto> buscarProdutoPorNome(String nome) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Query<Produto> query = session.createQuery("FROM Produto WHERE nome = :nome AND ativo = true", Produto.class);
            query.setParameter("nome", nome);

            List<Produto> produtos = query.list();
            session.getTransaction().commit();

            return produtos;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static Produto buscarProdutoPorNomeMarca(String nome, String marca) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Query<Produto> query = session.createQuery(
                "FROM Produto WHERE lower(nome) = :nome AND lower(marca) = :marca AND ativo = true", Produto.class
            );
            query.setParameter("nome", nome.toLowerCase().trim());
            query.setParameter("marca", marca.toLowerCase().trim());

            List<Produto> resultados = query.list();
            session.getTransaction().commit();

            return resultados.isEmpty() ? null : resultados.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void diminuirQuantidade(Produto produto, int quantidadeVendida) {
        if (produto.getQuantidade() >= quantidadeVendida) {
            produto.setQuantidade(produto.getQuantidade() - quantidadeVendida);

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();
                session.update(produto);
                session.getTransaction().commit();
                System.out.println("Quantidade atualizada com sucesso: " + produto.getQuantidade());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Quantidade insuficiente para a venda.");
        }
    }

    public static void excluirProduto(Produto produto) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            produto.setAtivo(false);  // Remoção lógica
            session.update(produto);
            session.getTransaction().commit();
            System.out.println("Produto ocultado com sucesso: " + produto.getNome());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void atualizarProduto(Produto produto) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.update(produto);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
