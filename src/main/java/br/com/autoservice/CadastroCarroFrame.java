package br.com.autoservice;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.hibernate.Session;

public class CadastroCarroFrame extends JFrame {

    private JTextField placaField;
    private JTextField marcaField;
    private JTextField modeloField;
    private JTextField anoField;
    private JTextField clienteField;

    public CadastroCarroFrame() {
        setTitle("Cadastrar Carro");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel placaLabel = new JLabel("Placa:");
        placaLabel.setBounds(20, 20, 80, 25);
        placaField = new JTextField();
        placaField.setBounds(100, 20, 150, 25);

        JLabel marcaLabel = new JLabel("Marca:");
        marcaLabel.setBounds(20, 60, 80, 25);
        marcaField = new JTextField();
        marcaField.setBounds(100, 60, 150, 25);

        JLabel modeloLabel = new JLabel("Modelo:");
        modeloLabel.setBounds(20, 100, 80, 25);
        modeloField = new JTextField();
        modeloField.setBounds(100, 100, 150, 25);

        JLabel anoLabel = new JLabel("Ano:");
        anoLabel.setBounds(20, 140, 80, 25);
        anoField = new JTextField();
        anoField.setBounds(100, 140, 150, 25);

        JLabel clienteLabel = new JLabel("Cliente:");
        clienteLabel.setBounds(20, 180, 80, 25);
        clienteField = new JTextField();
        clienteField.setBounds(100, 180, 150, 25);

        JButton cadastrarBtn = new JButton("Cadastrar");
        cadastrarBtn.setBounds(90, 220, 120, 25);

        add(placaLabel);
        add(placaField);
        add(marcaLabel);
        add(marcaField);
        add(modeloLabel);
        add(modeloField);
        add(anoLabel);
        add(anoField);
        add(clienteLabel);
        add(clienteField);
        add(cadastrarBtn);

        cadastrarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastrarCarro();
            }
        });

        setVisible(true);
    }

    private void cadastrarCarro() {
        String placa = placaField.getText();
        String marca = marcaField.getText();
        String modelo = modeloField.getText();
        String cliente = clienteField.getText();
        int ano;

        try {
            ano = Integer.parseInt(anoField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ano inválido!");
            return;
        }

        Carro carro = new Carro(placa, marca, modelo, ano, cliente);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(carro);
            session.getTransaction().commit();
            JOptionPane.showMessageDialog(this, "Carro cadastrado com sucesso!");
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar o carro.");
        }
    }
}
