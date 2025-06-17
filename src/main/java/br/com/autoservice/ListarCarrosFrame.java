package br.com.autoservice;

import javax.swing.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.awt.*;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class ListarCarrosFrame extends JFrame {

    public ListarCarrosFrame() {
        setTitle("🚗 Lista de Carros Cadastrados");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("🚘 Carros no Sistema", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(titulo, BorderLayout.NORTH);

        JPanel painelLista = new JPanel();
        painelLista.setLayout(new BoxLayout(painelLista, BoxLayout.Y_AXIS));
        painelLista.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JScrollPane scrollPane = new JScrollPane(painelLista);
        scrollPane.setBorder(BorderFactory.createTitledBorder("📋 Detalhes dos Carros"));
        add(scrollPane, BorderLayout.CENTER);

        List<Carro> carros = CarroService.listarCarros();

        // Ordena os carros pelo modelo, ignorando maiúsculas/minúsculas
        Collections.sort(carros, Comparator.comparing(c -> c.getModelo().toLowerCase()));

        if (carros.isEmpty()) {
            JLabel vazio = new JLabel("Nenhum carro encontrado.");
            vazio.setFont(new Font("SansSerif", Font.ITALIC, 16));
            painelLista.add(vazio);
        } else {
            for (Carro carro : carros) {
                String info = "<html>"
                            + "<b>Placa:</b> " + carro.getPlaca() + " | "
                            + "<b>Modelo:</b> " + carro.getModelo() + " | "
                            + "<b>Marca:</b> " + carro.getMarca() + " | "
                            + "<b>Cliente:</b> " + carro.getCliente()
                            + "</html>";
                JLabel label = new JLabel(info);
                label.setFont(new Font("SansSerif", Font.PLAIN, 14));
                label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
                painelLista.add(label);
            }
        }

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ListarCarrosFrame::new);
    }
}
