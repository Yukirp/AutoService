package br.com.autoservice;

import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;
import java.time.LocalDate;

public class CarroService {

    public static List<Carro> listarCarros() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Query<Carro> query = session.createQuery("FROM Carro WHERE ativo = true", Carro.class);
            List<Carro> carros = query.list();

            session.getTransaction().commit();

            if (carros.isEmpty()) {
                System.out.println("Nenhum carro encontrado.");
            } else {
                System.out.println("Lista de carros:");
                for (Carro carro : carros) {
                    System.out.println(
                        "ID: " + carro.getId() +
                        ", Cliente: " + carro.getCliente() +
                        ", Placa: " + carro.getPlaca() +
                        ", Marca: " + carro.getMarca() +
                        ", Modelo: " + carro.getModelo() +
                        ", Ano: " + carro.getAno()
                    );
                }
            }

            return carros;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static void salvarCarro(String placa, String marca, String modelo, int ano, String cliente) {
        Carro carro = new Carro(placa, marca, modelo, ano, cliente);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(carro);
            session.getTransaction().commit();
            System.out.println("Carro salvo com sucesso: " + placa);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void atualizarCarro(Long id, String novaPlaca, String novaMarca, String novoModelo, int novoAno) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Carro carro = session.get(Carro.class, id);
            if (carro != null) {
                carro.setPlaca(novaPlaca);
                carro.setMarca(novaMarca);
                carro.setModelo(novoModelo);
                carro.setAno(novoAno);

                session.update(carro);
                session.getTransaction().commit();
                System.out.println("Carro atualizado com sucesso!");
            } else {
                System.out.println("Carro com ID " + id + " não encontrado.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removerCarro(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Carro carro = session.get(Carro.class, id);
            if (carro != null) {
                carro.setAtivo(false); // Remoção lógica
                session.update(carro);
                session.getTransaction().commit();
                System.out.println("Carro removido com sucesso (removido logicamente).");
            } else {
                System.out.println("Carro com ID " + id + " não encontrado.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Carro buscarCarroPorPlaca(String placa) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Query<Carro> query = session.createQuery(
                "FROM Carro WHERE placa = :placa AND ativo = true", Carro.class);
            query.setParameter("placa", placa);
            Carro carro = query.uniqueResult();

            session.getTransaction().commit();

            if (carro != null) {
                System.out.println("Carro encontrado: " + carro.getModelo() + " (" + carro.getPlaca() + ")");
            } else {
                System.out.println("Nenhum carro ativo encontrado com a placa: " + placa);
            }

            return carro;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Carro> buscarCarrosPorCliente(String nomeCliente) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Query<Carro> query = session.createQuery(
                "FROM Carro WHERE cliente LIKE :nome AND ativo = true", Carro.class);
            query.setParameter("nome", "%" + nomeCliente + "%");

            List<Carro> carros = query.list();
            session.getTransaction().commit();

            return carros;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static String buscarServicosPorData(LocalDate data) {
        StringBuilder resultado = new StringBuilder();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Query<Servico> query = session.createQuery(
                "FROM Servico WHERE data = :data", Servico.class);
            query.setParameter("data", data);

            List<Servico> servicos = query.list();
            session.getTransaction().commit();

            if (servicos.isEmpty()) {
                resultado.append("Nenhum serviço encontrado para a data: ").append(data.toString());
            } else {
                resultado.append("Serviços encontrados para ").append(data.toString()).append(":\n\n");
                for (Servico s : servicos) {
                    resultado.append("Carro: ").append(s.getCarro().getPlaca())
                             .append("\nDescrição: ").append(s.getDescricao())
                             .append("\nValor: R$ ").append(String.format("%.2f", s.getValor()))
                             .append("\nKm: ").append(s.getQuilometragem())
                             .append("\n\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultado.append("Erro ao buscar serviços.");
        }

        return resultado.toString();
    }
}
