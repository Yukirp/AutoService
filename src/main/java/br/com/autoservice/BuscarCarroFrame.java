package br.com.autoservice;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class BuscarCarroFrame extends JFrame {

    private JComboBox<String> filtroCombo;
    private JTextField campoBusca;
    private JButton buscarBtn;
    private JTextArea resultadoArea;

    public BuscarCarroFrame() {
        setTitle("Buscar Carro");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel filtroLabel = new JLabel("Buscar por:");
        filtroLabel.setBounds(20, 20, 80, 25);

        filtroCombo = new JComboBox<>(new String[]{"Placa", "Cliente", "Data"});
        filtroCombo.setBounds(100, 20, 150, 25);

        campoBusca = new JTextField();
        campoBusca.setBounds(270, 20, 180, 25);

        buscarBtn = new JButton("Buscar");
        buscarBtn.setBounds(180, 60, 120, 30);

        resultadoArea = new JTextArea();
        resultadoArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultadoArea);
        scrollPane.setBounds(20, 100, 440, 240);

        add(filtroLabel);
        add(filtroCombo);
        add(campoBusca);
        add(buscarBtn);
        add(scrollPane);

        buscarBtn.addActionListener(this::realizarBusca);

        setVisible(true);
    }

    private void realizarBusca(ActionEvent e) {
        String filtro = (String) filtroCombo.getSelectedItem();
        String valor = campoBusca.getText().trim();

        if (valor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o valor de busca.");
            return;
        }

        switch (filtro) {
            case "Placa":
                Carro carro = CarroService.buscarCarroPorPlaca(valor);
                if (carro != null) {
                    resultadoArea.setText("Carro encontrado:\n" + formatarCarro(carro));
                } else {
                    resultadoArea.setText("Nenhum carro encontrado com a placa: " + valor);
                }
                break;

            case "Cliente":
                List<Carro> listaCarros = CarroService.buscarCarrosPorCliente(valor);
                if (listaCarros.isEmpty()) {
                    resultadoArea.setText("Nenhum carro encontrado para o cliente: " + valor);
                } else {
                    StringBuilder resultado = new StringBuilder();
                    for (Carro c : listaCarros) {
                        resultado.append(formatarCarro(c)).append("\n\n");
                    }
                    resultadoArea.setText(resultado.toString());
                }
                break;

            case "Data":
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate data = LocalDate.parse(valor, formatter);

                    List<Servico> servicos = ServicoService.buscarServicosPorData(data);
                    if (servicos == null || servicos.isEmpty()) {
                        resultadoArea.setText("Nenhum serviço encontrado para a data: " + valor);
                    } else {
                        StringBuilder sb = new StringBuilder("Serviços realizados na data " + valor + ":\n\n");
                        for (Servico s : servicos) {
                              sb .append("\nCliente: ").append(s.getCarro().getCliente())
                              .append("\nValor: R$ ").append(String.format("%.2f", s.getValor()))
                              .append("\nPlaca: ").append(s.getCarro().getPlaca())
                              .append("\nKm: ").append(s.getQuilometragem())
                              .append("\nDescrição: ").append(s.getDescricao())
                              .append("\n\n");
                        }
                        resultadoArea.setText(sb.toString());
                    }

                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Data inválida. Use o formato dd/MM/yyyy.");
                }
                break;
        }
    }

    private String formatarCarro(Carro carro) {
        return "ID: " + carro.getId() +
                "\nCliente: " + carro.getCliente() +
                "\nPlaca: " + carro.getPlaca() +
                "\nMarca: " + carro.getMarca() +
                "\nModelo: " + carro.getModelo() +
                "\nAno: " + carro.getAno();
    }
}
