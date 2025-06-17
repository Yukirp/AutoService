package br.com.autoservice;

import javax.swing.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListarServicosFrame extends JFrame {

    private JTextField placaField;
    private JTextArea resultadoArea;
    private JButton gerarPdfBtn;

    private Carro carroAtual;
    private List<Servico> servicosAtual;

    public ListarServicosFrame() {
        setTitle("🔍 Consulta de Serviços por Carro");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Painel superior com campo de busca
        JPanel painelBusca = new JPanel();
        painelBusca.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JLabel placaLabel = new JLabel("Placa do carro:");
        placaLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        placaField = new JTextField(10);
        JButton buscarBtn = new JButton("🔍 Buscar");

        buscarBtn.addActionListener((ActionEvent e) -> listarServicos());

        painelBusca.add(placaLabel);
        painelBusca.add(placaField);
        painelBusca.add(buscarBtn);

        add(painelBusca, BorderLayout.NORTH);

        // Área de resultado
        resultadoArea = new JTextArea();
        resultadoArea.setEditable(false);
        resultadoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultadoArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(resultadoArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("📋 Detalhes dos Serviços"));

        add(scrollPane, BorderLayout.CENTER);

        // Botão de exportar
        gerarPdfBtn = new JButton("📄 Gerar PDF");
        gerarPdfBtn.setEnabled(false);
        gerarPdfBtn.addActionListener(ev -> {
            if (carroAtual != null && servicosAtual != null) {
                RelatorioPDF.gerarRelatorioServicos(carroAtual, servicosAtual);
                JOptionPane.showMessageDialog(this, "✅ PDF gerado com sucesso!");
            }
        });

        JPanel painelInferior = new JPanel();
        painelInferior.add(gerarPdfBtn);
        add(painelInferior, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void listarServicos() {
        String placa = placaField.getText().trim();

        if (placa.isBlank()) {
            JOptionPane.showMessageDialog(this, "Por favor, informe a placa do carro.");
            return;
        }

        Carro carro = CarroService.buscarCarroPorPlaca(placa);

        if (carro == null) {
            JOptionPane.showMessageDialog(this, "Carro não encontrado.");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Query<Servico> query = session.createQuery("FROM Servico WHERE carro.id = :carroId", Servico.class);
            query.setParameter("carroId", carro.getId());
            List<Servico> servicos = query.list();

            session.getTransaction().commit();

            if (servicos.isEmpty()) {
                resultadoArea.setText("🚫 Nenhum serviço encontrado para o carro " + placa);
                gerarPdfBtn.setEnabled(false);
            } else {
                StringBuilder sb = new StringBuilder();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                double total = 0;

                sb.append("📌 Serviços do carro ").append(placa.toUpperCase()).append(":\n\n");
                for (Servico servico : servicos) {
                    sb.append("• ").append(servico.getDescricao())
                      .append(" | 💰 R$ ").append(String.format("%.2f", servico.getValor()))
                      .append(" | 📅 ").append(servico.getData().format(formatter))
                      .append(" | 🛞 Km: ").append(servico.getQuilometragem())
                      .append("\n");
                    total += servico.getValor();
                }

                sb.append("\n💵 TOTAL: R$ ").append(String.format("%.2f", total));

                resultadoArea.setText(sb.toString());

                carroAtual = carro;
                servicosAtual = servicos;
                gerarPdfBtn.setEnabled(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Erro ao buscar serviços.");
        }
    }
}
