package br.com.autoservice;

import javax.swing.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BuscarClienteFrame extends JFrame {

    private JTextField clienteField;
    private JTextArea resultadoArea;

    public BuscarClienteFrame() {
        setTitle("Buscar Carros por Cliente");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel clienteLabel = new JLabel("Nome do Cliente:");
        clienteLabel.setBounds(20, 20, 120, 25);
        clienteField = new JTextField();
        clienteField.setBounds(150, 20, 200, 25);

        JButton buscarBtn = new JButton("Buscar");
        buscarBtn.setBounds(370, 20, 90, 25);

        resultadoArea = new JTextArea();
        resultadoArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultadoArea);
        scrollPane.setBounds(20, 60, 440, 280);

        add(clienteLabel);
        add(clienteField);
        add(buscarBtn);
        add(scrollPane);

        buscarBtn.addActionListener((ActionEvent e) -> buscarCarros());

        setVisible(true);
    }

    private void buscarCarros() {
        String nomeCliente = clienteField.getText().trim();

        if (nomeCliente.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome do cliente.");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Query<Carro> query = session.createQuery(
                "FROM Carro WHERE cliente LIKE :nome", Carro.class);
            query.setParameter("nome", "%" + nomeCliente + "%");
            List<Carro> carros = query.list();

            session.getTransaction().commit();

            if (carros.isEmpty()) {
                resultadoArea.setText("Nenhum carro encontrado para o cliente: " + nomeCliente);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Carros encontrados para ").append(nomeCliente).append(":\n\n");

                for (Carro carro : carros) {
                    sb.append("• Placa: ").append(carro.getPlaca())
                      .append(" | Marca: ").append(carro.getMarca())
                      .append(" | Modelo: ").append(carro.getModelo())
                      .append(" | Ano: ").append(carro.getAno())
                      .append("\n");
                }

                resultadoArea.setText(sb.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao buscar carros.");
        }
    }
}
