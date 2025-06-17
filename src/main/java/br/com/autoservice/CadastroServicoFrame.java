package br.com.autoservice;

import javax.swing.*;
import org.hibernate.Session;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class CadastroServicoFrame extends JFrame {

    private JTextField placaField;
    private JTextField descricaoField;
    private JTextField valorField;
    private JTextField dataField;
    private JTextField kmField;

    public CadastroServicoFrame() {
        setTitle("Cadastrar Serviço");
        setSize(350, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel placaLabel = new JLabel("Placa do carro:");
        placaLabel.setBounds(20, 20, 120, 25);
        placaField = new JTextField();
        placaField.setBounds(140, 20, 150, 25);

        JLabel descricaoLabel = new JLabel("Descrição:");
        descricaoLabel.setBounds(20, 60, 120, 25);
        descricaoField = new JTextField();
        descricaoField.setBounds(140, 60, 150, 25);

        JLabel valorLabel = new JLabel("Valor (R$):");
        valorLabel.setBounds(20, 100, 120, 25);
        valorField = new JTextField();
        valorField.setBounds(140, 100, 150, 25);

        JLabel dataLabel = new JLabel("Data (yyyy-MM-dd):");
        dataLabel.setBounds(20, 140, 140, 25);
        dataField = new JTextField();
        dataField.setBounds(160, 140, 130, 25);

        JLabel kmLabel = new JLabel("Km atual:");
        kmLabel.setBounds(20, 180, 120, 25);
        kmField = new JTextField();
        kmField.setBounds(140, 180, 150, 25);

        JButton cadastrarBtn = new JButton("Cadastrar");
        cadastrarBtn.setBounds(100, 230, 120, 30);

        add(placaLabel); add(placaField);
        add(descricaoLabel); add(descricaoField);
        add(valorLabel); add(valorField);
        add(dataLabel); add(dataField);
        add(kmLabel); add(kmField);
        add(cadastrarBtn);

        cadastrarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastrarServico();
            }
        });

        setVisible(true);
    }

    private void cadastrarServico() {
        String placa = placaField.getText();
        String descricao = descricaoField.getText();
        double valor;
        int km;
        LocalDate data;

        try {
            valor = Double.parseDouble(valorField.getText());
        }catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor inválido.");
            return;
        }

        try {
            km = Integer.parseInt(kmField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quilometragem inválida.");
            return;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            data = LocalDate.parse(dataField.getText(), formatter);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use o formato dd/MM/yyyy.");
            return;
        }

        Carro carro = CarroService.buscarCarroPorPlaca(placa); // aqui a substituição correta

        if (carro == null) {
            JOptionPane.showMessageDialog(this, "Carro não encontrado.");
            return;
        }

        Servico servico = new Servico(descricao, valor, data, km, carro);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(servico);
            session.getTransaction().commit();

            int opcao = JOptionPane.showConfirmDialog(
                this,
                "Serviço cadastrado com sucesso!\nDeseja cadastrar outro serviço para este carro?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
            );

            if (opcao == JOptionPane.YES_OPTION) {
                // Limpa os campos (mantém a placa)
                descricaoField.setText("");
                valorField.setText("");
                dataField.setText("");
                kmField.setText("");
                descricaoField.requestFocus();
            } else {
                dispose();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar serviço.");
        }
}

}
